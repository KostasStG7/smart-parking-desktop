# Smart Parking Desktop Application

A Java Swing desktop application for finding and managing parking spaces, developed as an academic team project at the University of West Attica.

## Features

- User registration, login and profile management.
- Parking space listings with location, vehicle type and availability information.
- Interactive maps using JMapViewer and OpenStreetMap, with Nominatim reverse geocoding.
- Availability periods, reservations and cancellation workflows.
- Pricing rules, booking options and a simulated payment workflow.
- Notifications and separate views for drivers and parking space owners.

## Technology

Java, Swing, JDBC, MySQL, JMapViewer and OpenStreetMap.

## Project structure

| Path | Purpose |
| --- | --- |
| `src/gui/` | Swing screens, dialogs and visual theme |
| `src/service/` | Application logic and validation |
| `src/dao/` | Database queries and persistence |
| `src/model/` | Application data models |
| `src/db/` | JDBC connection configuration |
| `src/main/Main.java` | Application entry point |
| `database_create.sql` | Database schema with five tables and relationships |
| `lib/` | JMapViewer and MySQL JDBC driver from the original project |
| `config.example.ps1` | Example local connection settings |
| `run.ps1` | Windows PowerShell build and launch helper |

## Requirements

- A JDK, with `java` and `javac` available on `PATH`. Use JDK 21 for the setup below.
- A local MySQL installation; MySQL 8.x is the suggested evaluation environment.
- A graphical desktop for the Swing interface.
- Internet access for online map tiles and reverse geocoding.

## Run on Windows

1. Download or clone the repository.
2. In MySQL Workbench or a MySQL client, run `database_create.sql` against your local server. Use a fresh, empty `smart_parking_db` schema. The script creates tables and should only be run once; it does not reset an existing database.
3. Open PowerShell in the repository folder and create your local settings file:

```powershell
Copy-Item config.example.ps1 config.local.ps1
```

4. Edit `config.local.ps1` with your MySQL username and password. `SMART_PARKING_DB_URL` defaults to the local `smart_parking_db` database. Keep this file private.
5. Build and launch:

```powershell
./run.ps1
```

If PowerShell blocks local scripts, use your IDE instead of changing machine-wide execution policy: open `src` as the source folder, add the two JAR files in `lib` to the project classpath, set the three `SMART_PARKING_DB_*` environment variables in the run configuration, and launch `main.Main`.

The schema contains no pre-created user accounts. Register a disposable test account through the application.

## Scope

This is an academic desktop prototype. The payment interface simulates payment status changes; it does not connect to a payment processor. Account passwords are currently stored directly in the database, so use test credentials only. Production use would require password hashing and further security and reliability work. Online map and geocoding services remain subject to their providers' usage policies.

## Publication preparation

The team application code is preserved, with database credentials moved to environment variables. The database initialization script no longer drops an existing database. This repository omits compiled output, IDE settings, database contents and the original Git history. Build instructions and the PowerShell launcher were added for local setup.

The publication package was inspected for embedded connection credentials and file completeness. Compilation, database integration and graphical execution have not been verified in the preparation environment because a JDK and MySQL were unavailable.

Third-party JAR files are provided as they appeared in the original project; their own license terms and notices apply. No license for the team's original code is assigned by this packaging step.
