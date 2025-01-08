# API - Forum Hub

## Description
**Forum Hub** is a RESTful API designed to replicate the backend of a forum where participants can create and respond to questions on various topics. Inspired by the Alura platform, this project encourages collaboration among students, teachers, and moderators.

The API allows for managing topics, users, and replies through CRUD (Create, Read, Update, Delete) operations, implementing best development practices and utilizing modern tools such as **Spring Boot**.

---

## Features

- User registration and authentication
- Topics, Users, Courses, and Replies management
- Show Topics ordered by date
- Documentation with Swagger
- Data persistence with MySQL
- Authentication with JWT Library

## Prerequisites
- **Java 17 or higher**
- **Maven 3.8**
- **Spring Boot 3.4**
- **MySQL Database**

## Technologies Used
- **Spring Boot**: Rapid and robust application development.
- **JWT**: Secure authentication.
- **Flyway**: Database migration and management.
- **MySQL**: Relational database management system.
- **Swagger**: Interactive API documentation.
- **Lombok**: Reduction of boilerplate code.
---

## Installation and Configuration

1. Clon Repository
     
    ```bash
     git clone https://github.com/Mad-CaTs/appForoHub.git
     cd appForoHub
    
2. Configure enviroment or edit application.properties for you database connection

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/forohub
spring.datasource.username=user
spring.datasource.password=password
```

Create a new database called `forohub`:
```sql
CREATE DATABASE forohub;
```
3. Compile and run project

     ```bash
     mvn clean install
     mvn spring-boot:run

The application will be available on: `http://localhost:8080`

---

## Principal Endpoints
- **Authentication**:
  - `POST /auth/login`: Logs in and retrieves a JWT token.
- **Topics**:
  - `GET /topics`: Lists all topics.
  - `GET /topics/{id}`: Shows a specific topic.
  - `POST /topics`: Creates a new topic.
  - `PUT /topics/{id}`: Updates an existing topic.
  - `DELETE /topics/{id}`: Deletes a topic.
 
## How to Contribute

Contributions are welcome! If you want to improve this project, follow these steps to contribute via GitHub:

1. **Fork the repository**:
   Click the "Fork" button at the top of the repository page.

2. **Clone your fork locally**:

   ```bash
   git clone https://github.com/Mad-CaTs/appForoHub.git
   ```
3. **Create a new branch for your change**
   ```bash
   git checkout -b feature/new-feature
   ```
4. **Make your changes and commit with descriptive messages**
   ```bash
   git commit -m "Detailed description of what you've changed"
   ```
5. **Push your branch to the remote repository**
   ```bash
   git push origin feature/new-feature
   ```
6. **Open a Pull Request**
   - Go to the "Pull requests" tab in the original repository.
   - Click "New pull request" and select the branch you've pushed.
   - Provide a clear description of the changes you've made.
7. **Wait for the review** Your Pull Request will be reviewed, and if everything is in order, it will be merged into the project's main branch.



### Contact

If you have any questions or suggestions, feel free to open an issue in the repository or contact us via markusperezch1@gmail.com.

---


## License
This project is licensed under the MIT License. See the `LICENSE` file for more details.
