# MyCards Backend

This is a backend for the "MyCards" - designed to provide valuable services to credit and debit card holders.

## Installation

#### Prerequisites
1. Java 17
2. MySQL database.
3. Gradle (for dependency management)

#### Installation process
1. Clone the repository

```bash
git clone https://github.com/impritichouhan/mycards.git
cd your-path/mycards
```
2. MySQL setup
```sql
CREATE DATABASE mycards;
```
3. Update application.properties file
- Navigate to src/main/resources
- Edit application.properties file present there.

```
spring.datasource.url=jdbc:mysql://localhost:3306/card_services_db
spring.datasource.username=your-username
spring.datasource.password=your-password

```

4. Build the application
```bash
./gradlew clean build
```

5. Run the application
```bash
./gradlew bootRun
```






## Usage

When started, the app runs on ```localhost:8080``` and exposes following endpoints to the user:

### User Endpoints:
1. POST - ```/users/signIn```
    - Authenticate user and sign in

2. POST - ```/users/password/reset```
    - Request password reset

3. PUT - ```/users/profile```	
   - Update user profile information

4. POST - ```/users/signUp```	
   - Create a new user account

5. POST - ```/users/activity```	
   - Create user activity

6. GET - ```/users/activity/{userId/emailId/mobile}```
   - Retrieve specific user activity details
   - Filters can be applied - Date-range, eventType
   - Parameters are used to find the user and then sorting attributes are used as criterias in a query.

7. GET - ```/users/activity```
   - Retrieve all user activity details

8. POST - ```/users/otp/generate```	
   - Generate OTP for user verification

9. POST - ```/users/otp/verify```	
   - Verify the generated OTP





### Card Endpoints:
1. POST - ```/cards/add	```
   - Add a new card

2. PUT - ```/cards/update```	
   - Update existing card details

3. DELETE - ```/cards/delete```	
   - Delete a card




### LLM - Chat Endpoints:

1. POST - ```/chat```
  - Send prompt to the LLM and get generated response back






## Authorization and Authentication

This application uses a secure authentication mechanism with mobile/email and password. The user credentials are managed as follows:

### Password Encryption: 
Passwords are encrypted using Argon2, a highly secure hashing algorithm, before being stored in the database. When a user logs in, their password is hashed using Argon2 and compared to the stored hash, ensuring safe and secure password handling.

### JWT Token-Based Authorization: 
Upon successful login, a JSON Web Token (JWT) is generated and returned to the user. This token must be included as a Bearer token in the authToken header for subsequent authenticated requests, allowing the server to validate the authenticity of each request securely.

### OTP based authentication

When a ```/users/otp/generate``` request is made, OTP is generated and sent to the user's registered mobile number (and email, if present). The OTP is stored securely in the database and is valid for 5 minutes. user can then use ```/users/otp/verify``` endpoint to verify the OTP and get authToken to make a secure request.




