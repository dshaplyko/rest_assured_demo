# RestAssured Sample Project

This project is a sample setup for testing REST APIs using RestAssured and TestNG.

## Prerequisites

- Java 8 or higher
- Maven 3.6.0 or higher

## Project Structure

```
.
├── .gitignore
├── pom.xml
├── src
│   ├── main
│   │   └── java
│   └── test
│       ├── java
│       │   └── com
│       │       └── example
│       │           └── AppTest.java
│       └── resources
│           └── config.properties
└── target
    ├── classes
    ├── generated-sources
    ├── generated-test-sources
    ├── maven-status
    ├── surefire-reports
    └── test-classes
```

## Dependencies

The project uses the following dependencies:

- `commons-io:2.6`
- `io.rest-assured:4.4.0`
- `org.testng:7.1.0`
- `org.json:20210307`

These dependencies are defined in the [pom.xml](pom.xml) file.

## Running Tests

To run the tests, execute the following command:

```sh
mvn clean test
```

This will clean the project and run all the tests defined in the `src/test/java` directory.

## Configuration

The base URL and other configurations are defined in the `src/test/resources/config.properties` file. Make sure to update this file with the correct values before running the tests.

## Test Reports

After running the tests, the reports can be found in the `target/surefire-reports` directory. The reports include detailed information about the test execution and results.

## License

This project is licensed under the MIT License.

This `README.md` file provides an overview of the project, its structure, dependencies, and instructions on how to run the tests.
