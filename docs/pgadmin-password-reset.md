# Reset pgAdmin Password

## Option 1 — Re-run the setup script

```bash
sudo /usr/pgadmin4/bin/setup-web.sh
```

It will ask if you want to reconfigure an existing installation — say **yes**, then enter your new email and password.

---

## Option 2 — Manual reset

If the setup script doesn't work, reset it manually.

### 1. Find the pgAdmin user database

```bash
sudo find / -name "pgadmin4.db" 2>/dev/null
```

Usually located at `/var/lib/pgadmin/pgadmin4.db`.

### 2. Delete the database

```bash
sudo rm /var/lib/pgadmin/pgadmin4.db
```

### 3. Re-run setup

```bash
sudo /usr/pgadmin4/bin/setup-web.sh
```

This creates a fresh database and prompts you to set a new email and password.

### 4. Restart Apache

```bash
sudo service apache2 restart
```

---

> **Note:** Deleting `pgadmin4.db` removes all saved servers, queries, and preferences — just the pgAdmin app config, not your actual PostgreSQL databases. Those are safe.
