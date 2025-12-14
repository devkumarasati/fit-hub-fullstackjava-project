# FitHub

FitHub is a web application designed to connect fitness trainers with health enthusiasts. The platform allows trainers to share their expertise by creating structured workout plans, while users can easily discover and subscribe to the plans that match their fitness goals.

## Project Overview

We built this project to make fitness accessible and organized. Whether you are a professional trainer looking to reach more clients or someone starting their fitness journey, FitPlanHub provides the tools you need. The interface is clean, responsive, and focuses on what matters most—your health and progress.

## Key Features

### For Trainers
*   **Create Plans:** Easily design and publish new workout routines.
*   **Manage Content:** Edit or remove plans as your training methods evolve.
*   **Track Success:** See how many users are engaging with your content.

### For Users
*   **Discover Plans:** Browse a variety of fitness plans with clear pricing in Rupees (₹).
*   **Subscribe:** Get full access to plan details by subscribing to your favorite trainers.
*   **Personalized Feed:** Follow trainers to see their latest updates in your feed.
*   **Dashboard:** Keep track of all your active subscriptions in one place.

### General
*   **Secure Access:** Safe login and signup process for all users.
*   **Responsive Design:** Works smoothly on both desktop and mobile devices.

## Technology Stack

*   **Backend:** Java (Servlets, JDBC)
*   **Frontend:** HTML, CSS, JavaScript
*   **Database:** Oracle Database
*   **Server:** Apache Tomcat (recommended)

## Getting Started

Follow these steps to set up the project on your local machine:

1.  **Clone the Repository:** Download the project files to your computer.
2.  **Database Setup:**
    *   Make sure you have an Oracle Database instance running.
    *   Run the `database_schema.sql` script to create the necessary tables and sample data.
3.  **Configuration:**
    *   Open `src/main/java/com/fitplanhub/util/DatabaseUtil.java`.
    *   Update the database connection details (username and password) to match your local setup.
4.  **Run the Application:**
    *   Import the project into your IDE (like Eclipse).
    *   Run the project on a server like Apache Tomcat.
    *   Access the application at `http://localhost:8080/FitPlanHub`.

## License

This project is open for personal and educational use.
