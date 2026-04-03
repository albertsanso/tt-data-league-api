#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage: deploy_service.sh --jar-path <path> [options]

Options:
  --jar-path <path>         Source JAR path inside WSL (required)
  --install-dir <path>      Install directory (default: /opt/tt-data-league-api)
  --service-name <name>     systemd service name (default: tt-data-league-api)
  --app-user <name>         Linux user running the service (default: current user)
  --db-url <value>          DB_TTLEAGUEDATA_JDBC_URL value
  --db-user <value>         DB_TTLEAGUEDATA_CREDENTIAL_USERNAME value
  --db-password <value>     DB_TTLEAGUEDATA_CREDENTIAL_PASSWORD value
  --log-file <path>         Log file for stdout/stderr (default: /var/log/<service-name>/app.log)
  --help                    Show this help message
EOF
}

JAR_PATH=""
INSTALL_DIR="/opt/tt-data-league-api"
SERVICE_NAME="tt-data-league-api"
APP_USER="${USER}"
DB_URL="jdbc:postgresql://localhost:15432/ttleaguedata"
DB_USER="ttleagueuser"
DB_PASSWORD="ttleaguepass"
LOG_FILE=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --jar-path)
      JAR_PATH="$2"
      shift 2
      ;;
    --install-dir)
      INSTALL_DIR="$2"
      shift 2
      ;;
    --service-name)
      SERVICE_NAME="$2"
      shift 2
      ;;
    --app-user)
      APP_USER="$2"
      shift 2
      ;;
    --db-url)
      DB_URL="$2"
      shift 2
      ;;
    --db-user)
      DB_USER="$2"
      shift 2
      ;;
    --db-password)
      DB_PASSWORD="$2"
      shift 2
      ;;
    --log-file)
      LOG_FILE="$2"
      shift 2
      ;;
    --help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage
      exit 1
      ;;
  esac
done

if [[ -z "$JAR_PATH" ]]; then
  echo "Missing required --jar-path argument." >&2
  usage
  exit 1
fi

if [[ ! -f "$JAR_PATH" ]]; then
  echo "JAR not found: $JAR_PATH" >&2
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "java was not found in WSL. Install Java 21 before deploying." >&2
  exit 1
fi

if ! command -v systemctl >/dev/null 2>&1; then
  echo "systemctl was not found. Enable systemd in WSL before deploying." >&2
  exit 1
fi

if ! id -u "$APP_USER" >/dev/null 2>&1; then
  echo "Linux user '$APP_USER' does not exist in this distro." >&2
  exit 1
fi

ENV_FILE="/etc/${SERVICE_NAME}.env"
UNIT_FILE="/etc/systemd/system/${SERVICE_NAME}.service"
TARGET_JAR="${INSTALL_DIR}/app.jar"

if [[ -z "$LOG_FILE" ]]; then
  LOG_FILE="/var/log/${SERVICE_NAME}/app.log"
fi

LOG_DIR="$(dirname "$LOG_FILE")"

sudo mkdir -p "$INSTALL_DIR"
sudo cp "$JAR_PATH" "$TARGET_JAR"
sudo chown "$APP_USER":"$APP_USER" "$TARGET_JAR"
sudo chmod 640 "$TARGET_JAR"

sudo mkdir -p "$LOG_DIR"
sudo touch "$LOG_FILE"
sudo chown "$APP_USER":"$APP_USER" "$LOG_DIR" "$LOG_FILE"
sudo chmod 750 "$LOG_DIR"
sudo chmod 640 "$LOG_FILE"

sudo tee "$ENV_FILE" >/dev/null <<EOF
DB_TTLEAGUEDATA_JDBC_URL=${DB_URL}
DB_TTLEAGUEDATA_CREDENTIAL_USERNAME=${DB_USER}
DB_TTLEAGUEDATA_CREDENTIAL_PASSWORD=${DB_PASSWORD}
EOF
sudo chmod 600 "$ENV_FILE"

sudo tee "$UNIT_FILE" >/dev/null <<EOF
[Unit]
Description=tt-data-league-api Spring Boot service
After=network.target

[Service]
Type=simple
User=${APP_USER}
WorkingDirectory=${INSTALL_DIR}
EnvironmentFile=${ENV_FILE}
ExecStart=/usr/bin/java -jar ${TARGET_JAR}
StandardOutput=append:${LOG_FILE}
StandardError=append:${LOG_FILE}
SuccessExitStatus=143
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable "$SERVICE_NAME"
sudo systemctl restart "$SERVICE_NAME"
sudo systemctl --no-pager --full status "$SERVICE_NAME"

echo "Deployment completed for service '${SERVICE_NAME}'."

