# pgAdmin Port Redirection to Windows Host

Since pgAdmin is running in WSL via Apache (default port 80), it's actually already accessible from Windows at `http://localhost/pgadmin4` — WSL2 automatically forwards ports to the Windows host.

If it's **not working**, here are the fixes:

---

## Check Apache is running and which port it's on

```bash
sudo service apache2 status
sudo ss -tlnp | grep apache
```

---

## Change to a custom port (e.g. 8080)

### 1. Edit the Apache ports config

```bash
sudo nano /etc/apache2/ports.conf
```

Change:

```
Listen 80
```

To:

```
Listen 8080
```

### 2. Update the virtual host

```bash
sudo nano /etc/apache2/sites-enabled/000-default.conf
```

Change `<VirtualHost *:80>` to `<VirtualHost *:8080>`, then restart:

```bash
sudo service apache2 restart
```

Access at `http://localhost:8080/pgadmin4`.

---

## If localhost still doesn't work from Windows

WSL2 sometimes gets a different internal IP. Find it:

```bash
ip addr show eth0 | grep "inet "
```

Then access via that IP from Windows, e.g. `http://172.x.x.x/pgadmin4`.

### Add a port proxy in PowerShell (run as Administrator)

```powershell
netsh interface portproxy add v4tov4 `
  listenport=80 `
  listenaddress=0.0.0.0 `
  connectport=80 `
  connectaddress=$(wsl hostname -I)
```

This makes Windows forward port 80 to the WSL2 instance regardless of its IP.

---

## Remove the portproxy later

```powershell
netsh interface portproxy delete v4tov4 listenport=80 listenaddress=0.0.0.0
```
