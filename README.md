# Automated tests for Fake Rest API

This project is a testing framework based on Java using TestNG, REST Assured and Allure.
It contains automated tests for a fake REST API.

1) TestNG is used for test execution and management
2) RESTAssured is used for sending requests and validating the responses
3) Allure is used for generating test reports

## Project structure

├── main
│   ├── java
│       ├── configs              # Class for loading the configuration properties
│       ├── models               # POJO classes 
│       ├── repository           # Repositories to provide test data for tests
│       ├── requests             # Classes encapsulating GET, POST, PUT, DELETE requests
│   
│       
├── test
├── java
│   ├── constants                # Error constants used in the tests
│   ├── error_messages           # Class with methods for verification of the errors
│   ├── tests                    # Classes with tests for authors and books
└── resources
    ├── data                     # Valid data for authors and books
    ├── application.properties   # Configurations used for the tests
    └── testng.xml               # TestNG configuration


## Running the Tests

Prerequisites:
Java: Version 17
Maven: For project build and dependency management
Allure: For generating the test reports

1. **Clone the Repository**:
   
   git clone https://github.com/zvezdelinaZheleva/fake-rest-api-tests.git
   
2. **Navigate to the directory**:
   
   cd fake-rest-api-tests

3. **Execute Tests with Maven:**:
   
   mvn clean test
   
4. **Generate and view the allure report:**:

   mvn allure:serve

## CI/CD

 The project uses Github Actions for execution of the tests on every push and pull request to the master or feature branch.
 It also generates allure reports and deploy them to Github Pages.
 


   
