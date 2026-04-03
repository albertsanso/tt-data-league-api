# Deploying and Running a Spring Boot JAR

The simplest way to deploy and run a Spring Boot JAR is a few steps:

## 1. Build the fat/uber JAR

Spring Boot's Maven or Gradle plugin packages everything (dependencies included) into one executable JAR:

```bash
# Maven
./mvnw clean package

# Gradle
./gradlew bootJar
```

This produces something like `target/myapp-1.0.0.jar`.

---

## 2. Run it directly with Java

```bash
java -jar myapp-1.0.0.jar
```

That's it for the most basic case. Spring Boot embeds Tomcat (or Jetty/Undertow), so no external server is needed.

---

## Common additions you'll want in practice

**Set the active profile / environment config:**
```bash
java -jar myapp.jar --spring.profiles.active=prod
```

**Pass environment variables or override properties:**
```bash
java -Dserver.port=9090 -jar myapp.jar
# or
java -jar myapp.jar --server.port=9090
```

**Run in the background (Linux):**
```bash
nohup java -jar myapp.jar > app.log 2>&1 &
```

---

## Make it a systemd service (Linux)

If you want it to survive reboots, create `/etc/systemd/system/myapp.service`:

```ini
[Unit]
Description=My Spring Boot App

[Service]
ExecStart=/usr/bin/java -jar /opt/myapp/myapp.jar
Restart=always
User=myappuser

[Install]
WantedBy=multi-user.target
```

Then:
```bash
sudo systemctl enable myapp
sudo systemctl start myapp
```

---

## Quick decision guide

| Scenario | Approach |
|---|---|
| Local dev / quick test | `java -jar myapp.jar` |
| Long-running on a Linux VM | systemd service |
| Containerized / cloud | Wrap in a `Dockerfile`, deploy to Docker/K8s |
| PaaS (Heroku, Railway, Render) | Push the JAR or repo — they handle the rest |

For most straightforward server deployments, **a systemd service on a Linux VM** is the sweet spot between simplicity and reliability.
