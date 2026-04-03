# WSL Debian Setup Guide

## 1. Install Debian on D: Drive

WSL doesn't support `--install --location` directly. The reliable approach: install normally, then export/import to D:.

### Step 1 — Install Debian (default location)

Open PowerShell as Administrator:

```powershell
wsl --install -d Debian
```

Restart when prompted, then complete the Debian first-run setup (username + password).

### Step 2 — Export the distro to a .tar file

```powershell
mkdir D:\WSL
wsl --export Debian D:\WSL\debian-backup.tar
```

### Step 3 — Unregister the original install

```powershell
wsl --unregister Debian
```

> ⚠ This removes the C: copy. Your .tar backup on D: is intact.

### Step 4 — Import from D:

```powershell
wsl --import Debian D:\WSL\Debian D:\WSL\debian-backup.tar
```

Format: `wsl --import <Name> <InstallDir> <TarFile>`

### Step 5 — Set default user

Imported distros default to root. Launch Debian as root first:

```powershell
wsl -d Debian
```

Then inside the shell:

```bash
echo -e "[user]\ndefault=yourusername" >> /etc/wsl.conf
```

Exit and restart the distro:

```powershell
wsl --terminate Debian
wsl -d Debian
```

### Step 6 — Verify

```powershell
wsl -l -v
```

You should see **Debian** listed. The VHDX file lives at `D:\WSL\Debian\ext4.vhdx`.

---

## 2. Install Java

Run these inside your Debian WSL terminal:

### Update package list

```bash
sudo apt update
```

### Install JDK

For the latest LTS:

```bash
sudo apt install default-jdk
```

Or pick a specific version:

```bash
sudo apt install openjdk-21-jdk   # Java 21
sudo apt install openjdk-17-jdk   # Java 17
sudo apt install openjdk-11-jdk   # Java 11
```

### Verify

```bash
java -version
javac -version
```

### Switch between multiple versions

```bash
sudo update-alternatives --config java
```

---

## 3. Install PostgreSQL

### Install

```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

### Start the service

```bash
sudo service postgresql start
```

> To enable systemd (so PostgreSQL starts automatically), add this to `/etc/wsl.conf`:
> ```ini
> [boot]
> systemd=true
> ```
> Then restart WSL: run `wsl --shutdown` from PowerShell and relaunch Debian.

### Verify

```bash
sudo service postgresql status
```

### Connect

```bash
sudo -u postgres psql
```

### Useful psql commands

```sql
-- Set a password for the postgres user
ALTER USER postgres PASSWORD 'yourpassword';

-- Create a new database
CREATE DATABASE mydb;

-- Create a new user
CREATE USER myuser WITH PASSWORD 'mypassword';
GRANT ALL PRIVILEGES ON DATABASE mydb TO myuser;

-- Exit
\q
```

### Auto-start on WSL launch (without systemd)

Add to `/etc/wsl.conf`:

```ini
[boot]
command = service postgresql start
```

---

## 4. Install pgAdmin and Apache

### Update and install dependencies

```bash
sudo apt update
sudo apt install -y curl gnupg2 lsb-release
```

### Add the pgAdmin apt repo

```bash
curl -fsS https://www.pgadmin.org/static/packages_pgadmin_org.pub | sudo gpg --dearmor -o /usr/share/keyrings/packages-pgadmin-org.gpg

sudo sh -c 'echo "deb [signed-by=/usr/share/keyrings/packages-pgadmin-org.gpg] https://ftp.postgresql.org/pub/pgadmin/pgadmin4/apt/$(lsb_release -cs) pgadmin4 main" > /etc/apt/sources.list.d/pgadmin4.list'
```

### Install pgAdmin (web mode) + Apache

```bash
sudo apt update
sudo apt install -y pgadmin4-web apache2
```

### Run the pgAdmin setup script

```bash
sudo /usr/pgadmin4/bin/setup-web.sh
```

It will ask for an **email** and **password** — these are your pgAdmin login credentials. The script automatically configures Apache.

### Start Apache

```bash
sudo service apache2 start
```

### Open in browser

```
http://localhost/pgadmin4
```

Log in with the email and password you set above.

### Auto-start Apache with WSL

Add to `/etc/wsl.conf`:

```ini
[boot]
command = service postgresql start && service apache2 start
```

### Useful Apache commands

```bash
sudo service apache2 start
sudo service apache2 stop
sudo service apache2 restart
sudo service apache2 status
```
