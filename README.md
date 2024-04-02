# Recipe Sharing Platform
**Backend Coding Challenge**

## Introduction

Your task is to build the backend functionality for a Recipe Sharing Platform. The platform allows users to create, share, and search for recipes. Users can create an account, log in, and perform various operations such as creating, updating, and deleting recipes, as well as searching for recipes based on different criteria.

## Requirements

### User Authentication

1. Implement user registration with email, username, and password.
2. All email addresses are expected to be unique in the database
3. The user table contains a key column with unique keys. 
4. Ensure password hashing and storage for security purposes.
 

### Recipe Management

1. Implement CRUD (Create, Read, Update, Delete) operations for recipes.
2. A recipe should have the following attributes:
    * Title (required)
    * User-ID (required)
    * Description (optional)
    * Ingredients (required, comma-separated values)
    * Instructions (required)
    * Servings (optional)
3. Ensure that the ingredients use the metric system.
4. Provide an endpoint to retrieve a list of recipes created by a specific user.
5. All recipe management endpoints require authentication.

#### Ingredients

When looking at the ingredients, we see a 1:n relationship between the recipe and ingredients. Therefore, it's recommended to manage ingredients in a second table with at least four columns:
* recipe_id
* value
* unit
* type

_Useful Units_

| unit  | comment    |
|-------|------------|
| g     | Gram       |
| kg    | Kilogram   |
| ml    | Milliliter |
| l     | Liter      |
| pc    | Piece      |
| tsp   | Teaspoon   |
| tbsp  | Tablespoon |
| pinch | A dash     |

_Examples_

| value | unit | type |
|-------|------|------|
| 8     | pcs  | egg  |
| .05   | l    | milk |
| 1     | dash | salt |

## Recipe Search

1. Implement a search functionality that allows users to search for recipes based on the following criteria:
    * Username (exact match or partial match)
    * Title (exact match or partial match)
2. Return a list of matching recipes based on the search criteria.
3. All recipe search endpoints require authentication.

## Recipe Recommendations

We want you to implement a special endpoint that returns a single recipe recommendation based on the current weather conditions.

### Setting up an account

To enable this feature you first need to setup an account on https://www.visualcrossing.com/

There are multiple free weather APIs on the market. This one allows free registration without any payment details required for the free tier.
Also the limit of 1000 free records per day should be sufficient for all evaluation purposes.

### The algorithm

Implement a "recommendations" functionality that recommends a recipe based on the current weather forecast. The recommendations work as follows:

1. Fetch the current weather situation in BERLIN (Germany) from the API
2. Avoid recipes that require baking if the outside temperature is above t1 degrees Celsius.
3. Avoid recipes using frozen ingredients if the outside temperature is below t2 degrees Celsius.
4. From the remaining recipes do a random choice.

The following constant values for t1,t2 shall be used, encoded in the application.yml:

| Variable | value |
|----------|-------|
| t1       | 28°C  |
| t2       | 2°C   |

Alternatively the values for t1 and t2 can also be given as parameters to the API call.
In this case the query parameters override the default values.

## Implementation

* Use Kotlin and the Spring Boot Framework for the implementation.
* Use a database of your choice to store the user and recipe information.
* Pay attention to error handling and validation of incoming data.

## API Documentation

Include clear and concise documentation for all endpoints, including request/response examples and any additional information required to understand and use the API.

## Testing

Write unit tests for the implemented functionality to ensure proper operation and behavior.

## Submission

* Provide a GitHub repository containing your code.
* Include a `SETUP.md` file with instructions on how to set up and run your application.
* Optionally, you can deploy your application to a hosting service and provide a link to the live demo.

## Evaluation Criteria

* Implementation of all the required features and functionalities.
* Code structure, readability, and maintainability.
* Proper usage of HTTP status codes and RESTful principles.
* Error handling and validation of incoming data.
* Test coverage and quality of unit tests.
* Documentation clarity and completeness.

## Note

* Focus on implementing the backend functionality only. No need to spend time on frontend/UI development for this challenge.
* Read the `ABOUT.md` for more details about this source code.
 