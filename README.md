# user-registration
I build this backend application that will feature User Registration & Authentication (**OTP based**).


## Features
 1. User Registration
 2. OTP Validation
 3. User Login
 4. User Profile Details
 5. User Logout

## Requirements

For building and running the application you need:

 - JDK 1.8
 - Maven 3

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the  `main`  method in the  root directory `/user/src/main/java/com/user/UserApplication.java`  class from your IDE.

Alternatively you can use the `Spring Tools 4 for Eclipse`

 - **Steps for Running the application:**
	> 1. Pull the code from github.
	> 2. Update the maven project once.
	> 3. **Run the ddl.sql Path:**[`/user/src/main/resources/db_scripts/ddl.sql`](https://github.com/akashpal007/user-registration/blob/main/src/main/resources/db_scripts/ddl.sql)
	> 4. **Update `partner table` 2Factor API KEY with your own API key. If you does not have any API key fill free to visit** [2FACTOR](https://2factor.in/v3/index)
	> 5. Please update the database credentials as per yours.
	> 6. Run the application.
	> 7. You can use Swagger for checking the API's. [Localhost Swagger](http://localhost:8888/swagger-ui/)

## Future changes

 - I will implement spring security and Oauth for user verification and authentication.
 - I will implement resend OTP mechanism.
