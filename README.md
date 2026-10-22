# Planets and Star Systems

A full-stack app that allows users to browse, create and manage planets and star systems. Content can be public or private, and role-based controls allow administrators to manage all records.

## Features

- Browse planets and star systems
- View public content without signing in
- Create, edit, and delete user-owned planets and star systems
- Mark content as public or private
- Search and sort planet and star system listings
- Authenticate users and apply role-based authorization
- Allow administrators to manage content across users

## Tech Stack

- Frontend: React, Vite, React Router, Bootstrap
- Backend: Java, Spring Boot
- Database: MySQL
- Testing: JUnit and Jest

## Architecture

The React frontend communicates with a Spring Boot REST API. The backend applies authentication and authorization rules, accesses MySQL through the data-access layer, and returns JSON responses to the client.

```text
React + Vite
    |
 REST API
    |
Spring Boot
    |
  MySQL
```

## Project Status

Originally completed as a LaunchCode FlexPath final project, this application is now under continued development. The current focus is improving documentation, modernizing tooling, and expanding the project through independent experiments.

## Future Improvements

- Improve local setup and configuration documentation
- Modernize frontend testing and development tooling
- Containerize and deploy the application
- Add additional astronomy data and usability improvements
- Explore optional companion modules in other programming languages