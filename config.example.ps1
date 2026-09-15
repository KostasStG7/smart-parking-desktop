# Copy to config.local.ps1 and set your own MySQL connection details.
$env:SMART_PARKING_DB_URL = "jdbc:mysql://localhost:3306/smart_parking_db"
$env:SMART_PARKING_DB_USER = "REPLACE_WITH_MYSQL_USER"
$env:SMART_PARKING_DB_PASSWORD = "REPLACE_WITH_MYSQL_PASSWORD"
