# Spring Boot Deployment on Ubuntu using systemd

This guide explains how to deploy and manage the **Healthcare Equipment Assistant** Spring Boot application on an Ubuntu server using `systemd`.

---

## 1. Prepare the Application JAR

### 1.1 Build the JAR file locally
```bash
mvn clean package
```
The resulting file should be in:
```
target/healthcare-equipment-assistant-0.0.1-SNAPSHOT.jar
```

### 1.2 Upload the JAR to the server
```bash
scp target/healthcare-equipment-assistant-0.0.1-SNAPSHOT.jar aws:/app/backend/
```

### 1.3 Upload the `application.properties` file
```bash
scp src/main/resources/application.properties aws:/app/backend/
```

---

## 2. Create the systemd Service File

### 2.1 Create and open the service file
```bash
sudo nano /etc/systemd/system/healthcare-app.service
```

### 2.2 Add the following content
```ini
[Unit]
Description=Healthcare Equipment Assistant Spring Boot Application
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/app/backend

ExecStart=/usr/bin/java -jar /app/backend/healthcare-equipment-assistant-0.0.1-SNAPSHOT.jar --spring.config.location=/app/backend/application.properties

SuccessExitStatus=143
Restart=always
RestartSec=10

StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Save and close the file.

---

## 3. Reload systemd and Start the Application

### 3.1 Reload systemd configuration
```bash
sudo systemctl daemon-reexec
sudo systemctl daemon-reload
```

### 3.2 Start the service
```bash
sudo systemctl start healthcare-app.service
```

### 3.3 Enable auto-start on boot
```bash
sudo systemctl enable healthcare-app.service
```

---

## 4. Managing the Application

### 4.1 Check service status
```bash
sudo systemctl status healthcare-app.service
```

### 4.2 Restart the application
```bash
sudo systemctl restart healthcare-app.service
```

### 4.3 Stop the application
```bash
sudo systemctl stop healthcare-app.service
```

### 4.4 View logs
```bash
journalctl -u healthcare-app.service -f
```

---

## 5. Deploying New Versions

### 5.1 Upload the new JAR
```bash
scp target/healthcare-equipment-assistant-0.0.1-SNAPSHOT.jar aws:/app/backend/
```

### 5.2 Restart the service
```bash
sudo systemctl restart healthcare-app.service
```

---

## 6. Notes
- Ensure `/app/backend/` exists and has the correct permissions for the `ubuntu` user.
- Avoid using `nohup` or `&` in `systemd` services — let `systemd` handle process management.
- All logs are accessible via:
```bash
journalctl -u healthcare-app.service
```
