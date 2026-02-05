# An Intelligent System for Predictive Travel Assistance and Journey Safety Monitoring

This project is an end-to-end safety solution that monitors user journeys in real-time and detects deviations using AI.

## 🏗️ Architecture
The system follows a microservices architecture:
* **Mobile App**: Built with Ionic/Angular for real-time tracking and Leaflet maps.
* **Backend**: Java Spring Boot (Port 8081) managing safety timers and alerts.
* **AI Service**: Python FastAPI (Port 8001) using geodesic math for deviation detection.
* **Database**: MongoDB for historical journey logs.

## 🧠 Key Features
* **Geodesic Monitoring**: Calculates real-time distance from the planned path.
* **Safety Timer**: Automatically starts a 10-second countdown if an anomaly is detected.
* **Emergency Alerts**: Automatically shares live location with emergency contacts if the user doesn't respond.

## 🚀 Setup
1.  **Python**: Run `uvicorn main:app --port 8001` in `/nextstop-ml-service`.
2.  **Java**: Run `BackendApplication` in IntelliJ (Port 8081).
3.  **Mobile**: Run `ionic serve` in `/nextstop-mobile-app`.
