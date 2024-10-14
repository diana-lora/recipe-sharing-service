
Security

- Strong password policy
- Hashing and salting of passwords

Assumptions

- Users can see other users' recipes but not change them

Time Limitation Decisions - Possible Improvements

- Secrets and API keys should be encrypted and passed as environment variables.
- Gradle version catalog
- Use JPA for the database because it is faster to implement than JDBC. Otherwise, I had to do a lot
of manual work to map the database to the objects. Many developers prefer JDBC because it gives them more
control over the database, but it is more time-consuming to implement.
- Search endpoints normally should be paginated and sorted. I did not implement this because of time.

Questions

- "Ingredients (required, comma-separated values)" Is this for the API or for the database?
- "All recipe search endpoints require authentication." does this mean I have to create one endpoint per
attribute(title and username) or is okay to implement one endpoint with two query parameters.
