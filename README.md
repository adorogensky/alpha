Prerequisites:
1. Java 8
2. Maven
3. Docker

Before running tests run 'chmod +x ./create-alphadb && ./create-alphadb junit'. 
This will pull the latest postgres image, create a new docker network and start a container 
with 'alphadb-junit' database and 'alphadb-junit' role, publishing on port 5433.

Run 'mvn clean test' for tests.

