# Beauty Salon

A web application for managing a beauty salon's work schedule, is going to be built using Spring
Boot.

## Roles

- Guest
- Client
- Administrator
- Hairdresser

## Installation

1. Clone the repository: git clone https://github.com/edoyou/k2s-project.git
2. Import the project into your favorite IDE (Eclipse, IntelliJ, etc.).
3. Configure the database connection settings in the `application.properties` file.
4. Run the application using the IDE or by executing the following command in the project
   directory: `./mvnw spring-boot:run`

## Running

1. Open the browser and navigate to http://localhost:8080/.
2. Use the application with the available features according to the assigned role.

## Code style

Link to the Google Java code style
XML: https://google.github.io/styleguide/intellij-java-google-style.xml:

## Plan:

1. Designing the data model and creating the database:

- Designing the data model, including entities and relationships.
- Creating the database MySQL.
- The database connection configuration in the application.

2. Implementing the data access layer:

- Creating the model classes (entities).
- Creating the repository interfaces.
- Testing the data access layer with CRUD operations.

3. Implementing the service layer:

- Creating the service classes.
- Implementing the business logic for each service.
- Writing unit tests for the service layer using Mockito.

4. Implementing the controller layer:

- Creating the controller classes.
- Implementing the RESTful API endpoints for each controller.
- Testing the API endpoints using tools like Postman or curl.

5. Implementing the security and authentication features:

- Spring Security configuration for authentication and authorization.
- Implementing JWT token generation and validation.
- Creating roles and assign them to users.
- Password encryption.

6. Implementing internationalization (i18n) support:

- Configuring i18n support with Spring Boot Starter I18N.
- Adding language-specific message files.
- Implementing language switching in the application.

7. Implementing the front-end user interface:

- Choosing a front-end framework (Bootstrap, Materialize, etc.).
- Creating the HTML templates using Thymeleaf.
- Implementing the necessary JavaScript and CSS for the user interface.

8. Implementing error handling and exception handling:

- Creating custom exception classes.
- Implementing exception handling with Spring for REST APIs.
- Ensuring that end-users do not see stack traces on the client-side.

9. Implementing additional features as needed:

- Implementing pagination for data pages.
- Adding data validation for input fields.
- Implementing the event log using the log4j library.
- Writing integration tests in order to ensure complete functionality.

10. Performing final testing and bug fixing:

- Testing the application thoroughly to identify any issues.
- Fixing any bugs or issues found during testing.

11. Deployment and maintenance:

- Deploying the application to a hosting platform or server.
- Monitoring the application and performing necessary maintenance.