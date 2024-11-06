# Exchange App

## Overview
The **Exchange App** is a RESTful service developed for handling currency exchange transactions. It enables users (referred to as `appUser`) to sign up, log in, view account details, and conduct currency exchanges.

## Key Features
- **User Registration**: Enables new users to create an account.
- **User Login**: Allows existing users to access their accounts.
- **Retrieve User Details**: Provides information about the logged-in user.
- **Currency Exchange**: Facilitates currency exchange operations for registered users.

## API Documentation
The API is documented with **Swagger** and is accessible at the `/swagger-ui.html` endpoint.

### Endpoints

1. **Register user**
    - **URL**: `/api/v1/security/register`
    - **Method**: `POST`
    - **Request Body**:
      ```json
      {
        "name": "string",
        "lastName": "string",
        "password": "string",
        "startingBalancePln": "number"
      }
      ```
2. **Login user**
    - **URL**: `/api/v1/security/login`
    - **Method**: `POST`
    - **Request Body**:
      ```json
      {
        "username": "string",
        "password": "string"
      }
      ```

3. **Get user details**
    - **URL**: `/api/v1/appuser`
    - **Method**: `GET`

4. **Exchange currency**
    - **URL**: `/api/v1/appuser/exchange`
    - **Method**: `POST`
   - **Request Params**:
       - `currency`: currency enum.
       - `amount`: amount.

## Resilience4j Integration
The service integrates **Resilience4j** to enhance stability and reliability. The following resilience mechanisms are implemented:

- **Circuit Breaker**: Shields the service from repeated failures of external APIs by temporarily halting additional requests when a threshold of errors is reached.
- **Retry**: Attempts to resend failed requests to the external API before activating the circuit breaker.

These settings are configured in the `application.yaml` file.

```yaml
resilience4j:
  circuitbreaker:
    instances:
      geoLocationService:
        sliding-window-size: 20
        minimum-number-of-calls: 10
        failure-rate-threshold: 50
        slow-call-rate-threshold: 60
        slow-call-duration-threshold: 2s
        wait-duration-in-open-state: 20s
        permitted-number-of-calls-in-half-open-state: 5
        automatic-transition-from-open-to-half-open-enabled: true
  retry:
    instances:
      geoLocationServiceRetry:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 1.5
        retry-exceptions:
          - org.springframework.web.client.HttpServerErrorException
          - org.springframework.web.client.ResourceAccessException
```

## Running the Application

To launch the application locally with Docker:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/exchange-app.git
   cd exchange-app
   mvn clean package
   ```

2. Verify Docker and Docker Compose are Installed on your machine.

3. Build and Start the Docker Containers: Run the following command to create the Docker image for the application and start both the PostgreSQL and application containers.
   ```bash
   docker-compose up --build
   ```

4. Access the Application: Once the containers are up and running, view the API documentation through Swagger at: http://localhost:8080/swagger-ui.html.

5. Shutting Down the Containers: To stop the running containers, execute:
   ```bash
   docker-compose down
   ```

### Docker Configuration

- **PostgreSQL Container**:
    - **Image**: `postgres:15`
    - **Environment Settings**:
        - `POSTGRES_USER`: `user`
        - `POSTGRES_PASSWORD`: `password`
        - `POSTGRES_DB`: `exchange_db`
    - **Port Mapping**: `5432:5432`
    - **Data Volume**: Persists data in `postgres-data:/var/lib/postgresql/data`

- **Spring Boot Application Container**:
    - **Build Context**: Current directory (contains the Dockerfile)
    - **Environment Settings**:
        - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://postgres:5432/exchange_db`
        - `SPRING_DATASOURCE_USERNAME`: `user`
        - `SPRING_DATASOURCE_PASSWORD`: `password`
    - **Port Mapping**: `8080:8080`
    - **Startup Dependency**: The application container is configured to wait for PostgreSQL to be fully initialized before starting.


### Additional Notes

- The `application.yaml` file is preconfigured to connect to the database provided by the Docker container.
- Database migrations are automatically handled by Flyway upon application startup.
- Confirm that Docker Desktop or Docker Engine is running before using the `docker-compose up` command.
- For local development without Docker, use the `application-dev.yaml` profile, which connects to a local PostgreSQL instance.
