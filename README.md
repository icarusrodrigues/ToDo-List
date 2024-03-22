# Task (To-do list) Management

A Java application with Spring Boot for managing To-do list tasks.

## Instructions for running

Below are the steps to run the application on your machine:

1. Clone the repository;
2. Install PostgreSQL, configure it, and create a database with name "database" (or another name of your choice);
3. In file [application.yml](https://github.com/icarusrodrigues/ToDo-List/blob/main/src/main/resources/application.yml) put your database credentials (user, password and name of database in url field if you create a database with another name);
4. Run the application.

> [!NOTE]  
> The application uses flyway to manage changes to the database, so when you run the application for the first time, the necessary tables will be automatically created.

## Swagger UI

The application has the Swagger tool to document it. To access it, run the application and access the url: (http://localhost:8080/swagger-ui/index.html)

In Swagger you can access all endpoints and generate requests:
![Swagger ui](https://github.com/icarusrodrigues/ToDo-List/assets/68354040/461618e7-0819-4fa8-8302-894e3e3b8abc)

And on each endpoint, you can see the type of response you might receive:
![Responses example](https://github.com/icarusrodrigues/ToDo-List/assets/68354040/9c3305aa-ae18-4267-b7f0-1844e0f50d56)

## Postman

In the application there is also a [postman collection](https://github.com/icarusrodrigues/ToDo-List/blob/main/To_Do_List%20_Collection.postman_collection.json) with all endpoints.

## Tests

To run the tests, open the test folder, right-click on the java folder, and select "Run 'Tests' in 'java'" if you use IntelliJ, or "Run Tests" if you use VS Code.
