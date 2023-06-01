#recipe-sharing-challenge-java

This bare-bone project provides you with some components and dependencies
to support you through your journey. Hopefully they can help you speed up
the development of your challenge!

## Project Details

This project requires:

1. Java 11

This project contains:

1. Kotlin
2. Spring Boot 2.7.X
3. Maven (incl. wrapper)
4. Lombok
5. Spring Security
6. Spring Data JPA
7. H2 Database

Feel free to change anything, e.g. you can use Gradle, any other (real) database, 
Spring JDBC, Java 17, Spring Boot 3.X.X.

### Project Structure

This source code also provides these application components:

1. A **SecurityConfiguration** that you can extend to enable user authentication.
2. A **UserManagement** service interface along with some stubs.
3. A **RecipeSearch** and **RecipeManagement** service interfaces along with some stubs.

Feel free to use/change them to your advantage.

## Running the Project

To run the project, use the maven wrapper executable:

```
./mvnw clean spring-boot:run
```

The project's tests can also be executed through the maven wrapper:

```
./mvnw test
```