# URL Shortener 🌐

A lightweight, no-framework Java-based URL shortening service built with Java 17, Maven, and H2 Database. This project allows users to shorten URLs anonymously, register/login to create custom short URLs, and redirect to original URLs using a simple HTTP server.

![URL Shortener Demo](https://via.placeholder.com/800x400.png?text=URL+Shortener+Demo)

## ✨ Features

- Anonymous URL Shortening: Shorten URLs without logging in.
- User Authentication: Register and login to create custom short URLs.
- Custom Short URLs: Authenticated users can specify custom aliases.
- Persistent Storage: Uses H2 Database to store users and URLs.
- Simple UI: Clean, responsive web interface with HTML, CSS, and JavaScript.
- CI Pipeline: Automated build and test with GitHub Actions.
- Logging: Detailed logs using SLF4J and Logback.

## 🛠️ Tech Stack

- Backend: Java 17, `com.sun.net.httpserver`
- Database: H2 (file-based)
- Build Tool: Maven
- Frontend: HTML, CSS, JavaScript (no frameworks)
- Testing: JUnit 5, Mockito
- CI/CD: GitHub Actions
- Logging: SLF4J, Logback

## 📋 Prerequisites

Install the required tools:
```bash
# Check Java 17
java -version
# Install Java 17 if needed (example for Windows, use package manager for Linux/macOS)
# Download from https://adoptium.net/ and set JAVA_HOME

# Check Maven
mvn -version
# Install Maven if needed (example for Windows)
# Download from https://maven.apache.org/download.cgi and set PATH

# Check Git
git --version
# Install Git if needed (example for Windows)
# Download from https://git-scm.com/downloads

## 🚀 Getting Started

1. Clone the Repository

git clone ```https://github.com/your-username/url-shortener.git```
cd url-shortener

2. Build the Project

```mvn clean package```

3. Run the Application

java -jar target/url-shortener-1.0-SNAPSHOT-jar-with-dependencies.jar

4. Access the Application

# Open in browser (Windows)
start http://localhost:8000
# On Linux: xdg-open http://localhost:8000
# On macOS: open http://localhost:8000

Use the web interface to:

# Shorten URLs anonymously
# Register or login to create custom short URLs
# Access shortened URLs (e.g., http://localhost:8000/abc123)

5. Check the Database

Access the H2 Database Console:

# Ensure server is running
# Open H2 Console (Windows)
start http://localhost:8082
# On Linux: xdg-open http://localhost:8082
# On macOS: open http://localhost:8082
# Login with:
# JDBC URL: jdbc:h2:./urlshortener
# Username: sa
# Password: (blank)

Query tables in the H2 Console:

# List users
echo "SELECT * FROM USERS;"
# List URLs
echo "SELECT * FROM URLS;"

Alternatively, query programmatically:

cat << EOF > src/main/java/com/urlshortener/server/DatabaseCheck.java
package com.urlshortener.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseCheck {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:./urlshortener;DB_CLOSE_DELAY=-1", "sa", "")) {
            System.out.println("USERS table:");
            ResultSet rsUsers = conn.createStatement().executeQuery("SELECT * FROM USERS");
            while (rsUsers.next()) {
                System.out.println("Username: " + rsUsers.getString("username") + ", Password: " + rsUsers.getString("password"));
            }
            System.out.println("\nURLS table:");
            ResultSet rsUrls = conn.createStatement().executeQuery("SELECT * FROM URLS");
            while (rsUrls.next()) {
                System.out.println("Short URL: " + rsUrls.getString("short_url") + ", Long URL: " + rsUrls.getString("long_url") + ", Username: " + rsUrls.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
EOF
mvn clean package
java -cp target/url-shortener-1.0-SNAPSHOT-jar-with-dependencies.jar com.urlshortener.server.DatabaseCheck

🗂️ Project Structure

tree .
# Output:
# url-shortener/
# ├── src/
# │   ├── main/
# │   │   ├── java/com/urlshortener/
# │   │   │   ├── handler/          # HTTP handlers
# │   │   │   ├── server/           # Main server logic
# │   │   │   └── service/          # Business logic
# │   │   └── resources/static/     # HTML, CSS, JS files
# │   └── test/                     # Unit tests
# ├── .github/workflows/ci.yml      # GitHub Actions CI pipeline
# ├── pom.xml                       # Maven configuration
# ├── app.log                       # Application logs
# └── urlshortener.mv.db           # H2 Database file

🔧 API Endpoints







Endpoint



Method



Description



Authentication





/api/shorten



POST



Shorten a URL anonymously



None





/api/custom



POST



Create a custom short URL



Bearer Token





/api/register



POST



Register a new user



None





/api/login



POST



Login and get a token



None





/<shortUrl>



GET



Redirect to the original URL



None

Test API endpoints:

# Register a user
curl -X POST -H "Content-Type: application/json" -d '{"username":"testuser","password":"testpass"}' http://localhost:8000/api/register

# Login
curl -X POST -H "Content-Type: application/json" -d '{"username":"testuser","password":"testpass"}' http://localhost:8000/api/login

# Shorten a URL
curl -X POST -H "Content-Type: application/json" -d '{"longUrl":"https://example.com"}' http://localhost:8000/api/shorten

🧪 Running Tests

mvn test

🔍 Troubleshooting

Buttons Not Clickable

# Check if script.js exists
ls src/main/resources/static/script.js
# If named scripts.js, rename it
mv src/main/resources/static/scripts.js src/main/resources/static/script.js
# Verify index.html references correct file
grep "script.js" src/main/resources/static/index.html
# If incorrect, update index.html
sed -i 's|/static/scripts.js|/static/script.js|' src/main/resources/static/index.html
# Rebuild and run
mvn clean package
java -jar target/url-shortener-1.0-SNAPSHOT-jar-with-dependencies.jar
# Check browser Console (F12) for errors
start http://localhost:8000

Database Issues

# Verify database file exists
ls urlshortener.mv.db
# If missing, run server to initialize
java -jar target/url-shortener-1.0-SNAPSHOT-jar-with-dependencies.jar &
# Access H2 Console
start http://localhost:8082
# Use JDBC URL: jdbc:h2:./urlshortener or jdbc:h2:C:/Users/anike/OneDrive/Desktop/URL-Shortener/URL-shortener/urlshortener

Server Errors

# Check logs
cat app.log
# Check if port 8000 is in use
netstat -aon | findstr :8000  # Windows
# If port is in use, change in Main.java and rebuild
sed -i 's/InetSocketAddress(8000)/InetSocketAddress(8080)/' src/main/java/com/urlshortener/server/Main.java
mvn clean package
java -jar target/url-shortener-1.0-SNAPSHOT-jar-with-dependencies.jar

Build Issues

# Resolve dependency issues
mvn clean install
# Clear Maven cache if needed
rm -rf ~/.m2/repository
mvn clean install

📜 License

# View license (MIT License assumed)
cat << EOF
MIT License
Copyright (c) 2025 Your Name
EOF

🙌 Contributing

# Fork and submit pull requests
git clone https://github.com/your-username/url-shortener.git
git checkout -b feature/your-feature
git commit -m "Add your feature"
git push origin feature/your-feature

📬 Contact

# Contact via GitHub Issues or email
echo "For support, open an issue at https://github.com/your-username/url-shortener/issues or email your-email@example.com"



⭐ Star this repository if you find it useful!