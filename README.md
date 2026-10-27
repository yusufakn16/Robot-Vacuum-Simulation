# Robot Vacuum Cleaning Simulation

This project is an interactive JavaFX simulation that models an autonomous robot vacuum cleaner navigating a room, avoiding obstacles, cleaning various types of dirt, managing its battery life, and returning to its charging station.

---

## 👥 Development Team

- Muhammed Enes Erkoç
- Abidin Yılmazer
- Yusuf AKIN

---

## 📌 Key Features

- **MVC Architecture:** Strict separation between Model, View, and Controller layers.
- **Room & Obstacle Management:** Multiple floor types (parquet, tile, carpet) along with obstacles such as beds, coffee tables, and kitchen furniture.
- **Dirt Types & Durations:** Distinct cleaning durations and battery consumption rates for dust, liquids, and stains.
- **Navigation & Cleaning Algorithms:**
  - Random Walk
  - Spiral Cleaning
  - Wall-Following
  - Smart Return: Automatic return to the charging station using shortest pathfinding (BFS / A*) when the battery is low or requested by the user.
- **Real-Time Statistics:** Live dashboard tracking cleaned area percentage, remaining dirt, battery status, and elapsed time.
- **Visual & Audio Feedback:** Animations and sound effects for collisions, cleaning actions, and charging station arrival.

---

## 🛠️ Tech Stack

- **Language:** Java 21
- **GUI Framework:** JavaFX 21 (Controls, FXML)
- **Build Tool:** Apache Maven
- **Testing:** JUnit 5

---

## 🚀 How to Run

To run the application locally, execute the following Maven command in the project root directory:

mvn clean javafx:run

---

## 📊 Architecture & Design

The class structures, relationships, and use-case scenarios are documented in the UML diagrams included in the root directory:
- `UML Diyagramı 1.png`
- `Uml Diyagram 2.png`
- `Uml Diyagram 3.png`

---

## 📜 Public Sharing & Acknowledgment

> This project was developed as part of the **BZ 214 Visual Programming** course at Erciyes University. Special thanks to the course instructor, contributors, and **Gökhan AZİZOĞLU**.
