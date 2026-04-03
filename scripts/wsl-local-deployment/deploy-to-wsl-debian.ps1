[CmdletBinding()]
param(
    [string]$Distro = "Debian",
    [string]$LinuxUser = "asanso",
    [string]$InstallDir = "/opt/tt-data-league-api",
    [string]$ServiceName = "tt-data-league-api",
    [string]$DbUrl = "jdbc:postgresql://localhost:5432/ttleaguedata",
    [string]$DbUsername = "ttleagueuser",
    [string]$DbPassword = "ttleaguepass",
    [string]$LogFile,
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

function Invoke-Checked {
    param(
        [string]$FilePath,
        [string[]]$ArgumentList
    )

    & $FilePath @ArgumentList
    if ($LASTEXITCODE -ne 0) {
        throw "Command failed: $FilePath $($ArgumentList -join ' ')"
    }
}

function Invoke-CheckedAndCapture {
    param(
        [string]$FilePath,
        [string[]]$ArgumentList
    )

    $output = & $FilePath @ArgumentList
    if ($LASTEXITCODE -ne 0) {
        throw "Command failed: $FilePath $($ArgumentList -join ' ')"
    }

    return @($output)
}

function Normalize-DistroName {
    param([string]$Name)

    if (-not $Name) {
        return ""
    }

    return ($Name -replace "`0", "" -replace "\(Default\)", "" -replace "^\*\s*", "").Trim()
}

function ConvertTo-BashSingleQuoted {
    param([string]$Value)

    if ($null -eq $Value) {
        return "''"
    }

    $singleQuote = "'"
    $singleQuoteEscape = $singleQuote + '"' + $singleQuote + '"' + $singleQuote
    return $singleQuote + $Value.Replace($singleQuote, $singleQuoteEscape) + $singleQuote
}

function Convert-WindowsPathToWsl {
    param(
        [string]$DistroName,
        [string]$WindowsPath
    )

    try {
        $convertedPath = (Invoke-CheckedAndCapture -FilePath "wsl.exe" -ArgumentList @("-d", $DistroName, "--", "wslpath", "-a", $WindowsPath) | Out-String).Trim()
        return ($convertedPath -replace "\\", "/")
    }
    catch {
        # Fallback for environments where wslpath is unavailable/fails.
        if ($WindowsPath -match '^[A-Za-z]:') {
            $drive = $WindowsPath.Substring(0, 1).ToLowerInvariant()
            $rest = ($WindowsPath.Substring(2) -replace "\\", "/").TrimStart('/')
            return "/mnt/$drive/$rest"
        }

        throw "Unable to convert Windows path '$WindowsPath' to a WSL path."
    }
}

function Test-IsExecutableJar {
    param([string]$JarPath)

    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $zip = $null
    try {
        $zip = [System.IO.Compression.ZipFile]::OpenRead($JarPath)
        $manifestEntry = $zip.Entries | Where-Object { $_.FullName -eq "META-INF/MANIFEST.MF" } | Select-Object -First 1
        if ($null -eq $manifestEntry) {
            return $false
        }

        $reader = New-Object System.IO.StreamReader($manifestEntry.Open())
        try {
            $manifestContent = $reader.ReadToEnd()
        }
        finally {
            $reader.Dispose()
        }

        return $manifestContent -match "(?m)^Main-Class:\s*.+$"
    }
    catch {
        return $false
    }
    finally {
        if ($null -ne $zip) {
            $zip.Dispose()
        }
    }
}

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$runtimeTarget = Join-Path $repoRoot "tt-data-league-api-runtime\target"

if (-not (Get-Command wsl.exe -ErrorAction SilentlyContinue)) {
    throw "wsl.exe is required but was not found on this machine."
}

Invoke-Checked -FilePath "wsl.exe" -ArgumentList @("--status")

$installedDistros = Invoke-CheckedAndCapture -FilePath "wsl.exe" -ArgumentList @("--list", "--quiet") |
    ForEach-Object { Normalize-DistroName $_ } |
    Where-Object { $_ }

$targetDistro = Normalize-DistroName $Distro
if (-not ($installedDistros | Where-Object { $_.ToLowerInvariant() -eq $targetDistro.ToLowerInvariant() })) {
    throw "WSL distro '$Distro' was not found. Installed distros: $($installedDistros -join ', ')"
}

if (-not $SkipBuild) {
    $mavenCommand = if (Test-Path (Join-Path $repoRoot "mvnw.cmd")) {
        Join-Path $repoRoot "mvnw.cmd"
    } elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
        "mvn"
    } else {
        throw "Neither mvnw.cmd nor mvn command is available. Install Maven or restore Maven Wrapper."
    }

    Push-Location $repoRoot
    try {
        Invoke-Checked -FilePath $mavenCommand -ArgumentList @("-pl", "tt-data-league-api-runtime", "-am", "clean", "package", "-DskipTests")
    }
    finally {
        Pop-Location
    }
}

$jarCandidates = Get-ChildItem -Path $runtimeTarget -Filter "*.jar" -File |
    Where-Object { $_.Name -notmatch "(sources|javadoc|original)" } |
    Sort-Object LastWriteTime -Descending

if (-not $jarCandidates) {
    throw "No runnable JAR found under '$runtimeTarget'."
}

$executableJar = $jarCandidates | Where-Object { Test-IsExecutableJar -JarPath $_.FullName } | Select-Object -First 1
if ($null -eq $executableJar) {
    $candidateNames = $jarCandidates | ForEach-Object { $_.Name }
    throw "No executable JAR with Main-Class manifest was found under '$runtimeTarget'. Candidates: $($candidateNames -join ', '). Build runtime module with spring-boot repackage enabled."
}

$jarPath = $executableJar.FullName
$deployScriptWindowsPath = Join-Path $repoRoot "scripts\wsl-local-deployment\wsl\deploy_service.sh"

if (-not (Test-Path $deployScriptWindowsPath)) {
    throw "WSL deployment script not found: $deployScriptWindowsPath"
}

$jarPathWsl = Convert-WindowsPathToWsl -DistroName $targetDistro -WindowsPath $jarPath
$deployScriptWsl = Convert-WindowsPathToWsl -DistroName $targetDistro -WindowsPath $deployScriptWindowsPath

if (-not $LinuxUser) {
    $LinuxUser = (Invoke-CheckedAndCapture -FilePath "wsl.exe" -ArgumentList @("-d", $targetDistro, "--", "bash", "-lc", "whoami") | Out-String).Trim()
}

$escapedArgList = @(
    "--jar-path $(ConvertTo-BashSingleQuoted $jarPathWsl)",
    "--install-dir $(ConvertTo-BashSingleQuoted $InstallDir)",
    "--service-name $(ConvertTo-BashSingleQuoted $ServiceName)",
    "--app-user $(ConvertTo-BashSingleQuoted $LinuxUser)",
    "--db-url $(ConvertTo-BashSingleQuoted $DbUrl)",
    "--db-user $(ConvertTo-BashSingleQuoted $DbUsername)",
    "--db-password $(ConvertTo-BashSingleQuoted $DbPassword)"
)

if (-not [string]::IsNullOrWhiteSpace($LogFile)) {
    $escapedArgList += "--log-file $(ConvertTo-BashSingleQuoted $LogFile)"
}

$escapedArgs = $escapedArgList -join " "

$bashCommand = "chmod +x $(ConvertTo-BashSingleQuoted $deployScriptWsl) && $(ConvertTo-BashSingleQuoted $deployScriptWsl) $escapedArgs"

Write-Host "Deploying '$jarPath' to WSL distro '$Distro' as service '$ServiceName'..."
Invoke-Checked -FilePath "wsl.exe" -ArgumentList @("-d", $targetDistro, "--", "bash", "-lc", $bashCommand)
Write-Host "Done."

