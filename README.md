# HappyPlants 🌱

A full-stack web application that helps you discover and learn about plants. Search for plants by name and get detailed information from the Perenual API.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## 🌟 Overview

HappyPlants is a personal online plant caregiver application that allows users to search for plants and retrieve detailed botanical information. The application integrates with the [Perenual API](https://perenual.com/docs/api) to provide comprehensive plant data including common names, scientific names, genus, and family information.

## ✨ Features

- **Plant Search**: Search for plants by common or scientific name
- **Real-time Results**: Get instant results from the Perenual plant database
- **Responsive UI**: Modern, user-friendly interface with loading indicators
- **REST API**: Clean backend architecture with Spring Boot
- **Cross-Origin Support**: Separate frontend and backend with proxy configuration

## 🛠 Technology Stack

### Backend
- **Java 17**: Programming language
- **Spring Boot 3.4.2**: Framework for building the REST API
  - `spring-boot-starter-web`: Web application support
  - `spring-boot-starter-test`: Testing support
- **PostgreSQL**: Relational database for storing user and plant data
- **Maven**: Build and dependency management
- **dotenv-java 3.0.0**: Environment variable management
- **Jackson**: JSON serialization/deserialization

### Frontend
- **React 19.2.4**: UI library
- **Vike**: Server-Side Rendering (SSR) and routing framework
- **Vite 7.3.1**: Build tool and development server
- **ESLint**: Code linting
- **Lucide React**: Icon library
- **Tailwind CSS 4**: Styling

## 🏗 Architecture

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   Browser   │────────▶│  Vite Dev   │────────▶│   Spring    │
│  (React)    │         │   Server    │         │    Boot     │
│             │◀────────│  :5173      │◀────────│   :8080     │
└─────────────┘         └─────────────┘         └─────────────┘
                              │                        │
                              │                        │
                              ▼                        ▼
                        Proxy /api/*            Perenual API
```

The application uses a proxy configuration where Vite forwards API requests to the Spring Boot backend, which then communicates with the external Perenual API.

## 📦 Prerequisites

Before installing and running HappyPlants, ensure you have the following installed on your system:

### Required Software

1. **Java Development Kit (JDK) 17 or higher**
   - Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify installation:
     ```bash
     java -version
     ```
   - Expected output: `java version "17.0.x"` or higher

2. **Apache Maven 3.6 or higher**
   - Download from: [Maven Download](https://maven.apache.org/download.cgi)
   - Or install via package manager:
     ```bash
     # macOS
     brew install maven

     # Windows (using Chocolatey)
     choco install maven
     ```
   - Verify installation:
     ```bash
     mvn -version
     ```

3. **Node.js 18.x or higher (with npm)**
   - Download from: [Node.js Official Website](https://nodejs.org/)
   - Or install via package manager:
     ```bash
     # macOS
     brew install node

     # Windows (using Chocolatey)
     choco install nodejs
     ```
   - Verify installation:
     ```bash
     node -version
     npm -version
     ```

4. **Perenual API Key**
   - Sign up for a free API key at: [Perenual API](https://perenual.com/docs/api)
   - Free tier includes limited requests per month

### Recommended Tools

- **Git**: For version control
- **IDE**: IntelliJ IDEA, VS Code, or any Java/JavaScript IDE
- **Postman**: For API testing (optional)

## 📥 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/HappyPlantsDA489A/HappyPlants.git
cd HappyPlants
```

### 2. Backend Setup

Navigate to the backend directory and install dependencies:

```bash
cd backend
mvn clean install
```

This will:
- Download all Maven dependencies
- Compile the Java source code
- Run tests
- Create the executable JAR file in the `target` directory

### 3. Frontend Setup

Navigate to the frontend directory and install dependencies:

```bash
cd ../frontend
npm install
```

This will:
- Download all npm packages
- Create the `node_modules` directory
- Set up the React development environment

## ⚙️ Configuration

### Backend Configuration

1. **Create Environment File**

   Create a `.env` file in the `backend` directory:

   ```bash
   cd backend
   touch .env
   ```

2. **Add API Key and Database Credentials**

  Edit the `.env` file and add your Perenual API key along with the PostgreSQL database configuration:

  ```env
  PERENUAL_KEY=your_api_key_here
  DB_URL=jdbc:postgresql://localhost:5432/happyplants
  DB_USERNAME=your_db_username
  DB_PASSWORD=your_db_password
  ```

  **Example:**
  ```env
  PERENUAL_KEY=sk-q4a7697ccedf51ef314587
  DB_URL=jdbc:postgresql://localhost:5432/happyplants
  DB_USERNAME=postgres
  DB_PASSWORD=secret
  ```

  **⚠️ Security Note**: Never commit the `.env` file to version control. It's already included in `.gitignore`.

3. **Application Properties** (Optional)

   The `backend/src/main/resources/application.properties` file is already configured:
   ```properties
   plant.api.token=${PERENUAL_KEY}
   ```

   You can add additional configurations here:
   ```properties
   # Server port (default: 8080)
   server.port=8080

   # Logging level
   logging.level.com.happyplants=DEBUG
   ```

### Frontend Configuration

The Vite configuration (`frontend/vite.config.js`) is already set up with a proxy:

```javascript
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
```

This ensures that all requests to `/api/*` are forwarded to the backend server.

## 🚀 Running the Application

You need to run both the backend and frontend servers simultaneously.

### Option 1: Using Two Terminal Windows

**Terminal 1 - Backend:**
```bash
cd backend
mvn spring-boot:run
```

The backend server will start on `http://localhost:8080`

Wait for the message:
```
Started Application in X.XXX seconds
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm run dev
```

The frontend development server will start on `http://localhost:5173`

You should see:
```
  VITE v7.3.1  ready in XXX ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

### Option 2: Using Background Processes (macOS/Linux)

```bash
# Start backend in background
cd backend && mvn spring-boot:run &

# Start frontend in background
cd frontend && npm run dev &
```

### Accessing the Application

Open your browser and navigate to:
```
http://localhost:5173
```

## 📁 Project Structure

```
HappyPlants/
├── README.md
├── .gitignore
│
├── backend/
│   ├── pom.xml                      # Maven configuration
│   ├── package.json                 # Backend metadata
│   ├── .env                         # Environment variables (not in Git)
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── happyplants/
│   │       │           ├── Application.java              # Main Spring Boot application
│   │       │           ├── controller/
│   │       │           │   ├── APIConnection.java        # REST controller
│   │       │           │   └── ApiResponse.java          # API response wrapper
│   │       │           └── model/
│   │       │               └── PlantDTO.java             # Plant data transfer object
│   │       └── resources/
│   │           └── application.properties                # Spring configuration
│   └── target/                      # Compiled classes (generated)
│
└── frontend/
    ├── package.json                 # npm dependencies
    ├── vite.config.ts              # Vite configuration
    ├── eslint.config.js            # ESLint configuration
    ├── index.html                  # HTML entry point
    ├── .gitignore
    ├── public/
    │   └── vite.svg                # Public assets
    └── src/
        ├── config.ts               # API base URL configuration
        ├── components/             # Reusable React components
        ├── index.css               # Global styles
        ├── style.css               # Component styles
        ├── pages/                  # Vike route files (+Page.tsx, +guard.ts)
        └── assets/                 # Images and static assets
            ├── happy-plant-background.jpg
            ├── pexels-background.jpg
            └── react.svg
```

## 🔌 API Endpoints

### Backend REST API

Base URL: `http://localhost:8080/api`

#### 1. Test Connection

**Endpoint:** `GET /api/test`

**Description:** Verifies backend connectivity

**Response:**
```json
"Backend svarar svar: Koppling fungerar!"
```

**cURL Example:**
```bash
curl http://localhost:8080/api/test
```

#### 2. Search Plants

**Endpoint:** `GET /api/plants/search`

**Description:** Search for plants by name

**Query Parameters:**
- `name` (required): The plant name to search for

**Response:**
```json
[
  {
    "id": 1,
    "common_name": "Philodendron",
    "scientific_name": ["Philodendron hederaceum"],
    "genus": "Philodendron",
    "family": "Araceae"
  }
]
```

**cURL Example:**
```bash
curl "http://localhost:8080/api/plants/search?name=Philodendron"
```

#### 3. Plant Details

**Endpoint:** `GET /api/plants/{id}`

**Description:** Get detailed information for a specific plant.

**Path Parameters:**
- `id` (required): The Perenual plant ID.

#### 4. Authentication Endpoints

- `POST /api/auth/log-in`: Authenticate user and setup session cookie (`HAPPY_COOKIE`).
- `POST /api/auth/register`: Register a new user account.
- `DELETE /api/auth/log-out`: Invalidate session and log user out.
- `GET /api/auth/check-auth`: Verify current session status.

#### 5. User Profile Endpoints

- `GET /api/user/user-info`: Get current user details.
- `PATCH /api/user/display-name`: Update user display name.
- `PATCH /api/user/change-password`: Update user password.
- `DELETE /api/user`: Delete user account.

#### 6. User Plant Library Endpoints

- `POST /api/user/plants/{perenualId}`: Add a plant to the library.
- `GET /api/user/plants`: Retrieve library plants (supports sorting and filtering).
- `GET /api/user/plants/{plantId}`: Retrieve a specific library plant.
- `DELETE /api/user/plants/{userPlantId}`: Remove a plant from the library.
- `PATCH /api/user/plants/{userPlantId}/dead`: Mark plant as dead.
- `PATCH /api/user/plants/{userPlantId}/watering-frequency`: Update watering interval.
- `POST /api/user/plants/{userPlantId}/water`: Log a watering event.
- `GET /api/user/plants/{userPlantId}/waterings`: History of watering events.
- `DELETE /api/user/plants/{userPlantId}/waterings`: Undo a specific watering event.
- `PATCH /api/user/plants/{userPlantId}/nickname`: Set a custom nickname.
- `PATCH /api/user/plants/{userPlantId}/image-url`: Update plant picture.

### External API Integration

The backend integrates with the Perenual API:

**API Endpoint:** `https://perenual.com/api/v2/species-list`

**Parameters:**
- `q`: Search query
- `page`: Page number (default: 1)
- `hardiness`: Hardiness zones (4-8)
- `key`: API key

## 🐛 Troubleshooting

### Common Issues and Solutions

#### 1. Backend Won't Start

**Problem:** `PERENUAL_KEY` not found

**Solution:**
- Ensure `.env` file exists in the `backend` directory
- Check that `PERENUAL_KEY` is correctly set
- Verify no extra spaces in the `.env` file

**Problem:** Port 8080 already in use

**Solution:**
```bash
# Find and kill process using port 8080
# macOS/Linux:
lsof -ti:8080 | xargs kill -9

# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

Or change the port in `application.properties`:
```properties
server.port=8081
```

#### 2. Frontend Issues

**Problem:** Cannot connect to backend

**Solution:**
- Verify backend is running on port 8080
- Check proxy configuration in `vite.config.js`
- Clear browser cache and restart frontend

**Problem:** `npm install` fails

**Solution:**
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### 3. API Issues

**Problem:** "Invalid API key" error

**Solution:**
- Verify your Perenual API key is active
- Check if you've exceeded API rate limits
- Ensure the key is correctly copied without extra characters

**Problem:** CORS errors

**Solution:**
- The Vite proxy should handle CORS
- Ensure you're accessing the app through `http://localhost:5173` (not directly through port 8080)

#### 4. Maven Issues

**Problem:** Maven dependencies won't download

**Solution:**
```bash
# Force update dependencies
mvn clean install -U

# Use Maven wrapper if available
./mvnw clean install
```

#### 5. Java Version Issues

**Problem:** "Unsupported class file major version"

**Solution:**
- Ensure Java 17 or higher is installed
- Set JAVA_HOME environment variable:

```bash
# macOS/Linux
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Windows (PowerShell)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
```

### Debug Mode

Enable debug logging in `application.properties`:

```properties
logging.level.com.happyplants=DEBUG
logging.level.org.springframework.web=DEBUG
```

## 🧪 Testing

### Backend Tests

Run Maven tests:
```bash
cd backend
mvn test
```

### Frontend Tests

Run React test:
```bash
cd frontend
npm run test
```

Run React linting:
```bash
cd frontend
npm run lint
```

### Manual Testing

1. **Test backend connectivity:**
   ```bash
   curl http://localhost:8080/api/test
   ```

2. **Test plant search:**
   ```bash
   curl "http://localhost:8080/api/plants/search?name=rose"
   ```

3. **Test frontend:** Open `http://localhost:5173` and search for a plant

## 📝 Development

### Building for Production

**Backend:**
```bash
cd backend
mvn clean package
java -jar target/happy-plant-1.0-SNAPSHOT.jar
```

**Frontend:**
```bash
cd frontend
npm run build
npm run preview
```

### Code Formatting

**Frontend:**
```bash
npm run lint
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is part of the Sys2 DA489A course. All rights reserved.

## 👥 Authors

- HappyPlants Team
- Course: Sys2 DA489A
- University Project

## 🙏 Acknowledgments

- [Perenual API](https://perenual.com/) for providing plant data
- [Spring Boot](https://spring.io/projects/spring-boot) for the backend framework
- [React](https://react.dev/) for the frontend library
- [Vite](https://vite.dev/) for the amazing build tool
- [Font Awesome](https://fontawesome.com/) for icons

## 📞 Support

For issues and questions:
- Create an issue in the GitHub repository
- Contact the development team

---

**Happy Planting! 🌿**
