To only build the project
mvn clean install

To build and run some integration tests
mvn clean install -Denv="test" -Ddb="h2"

The data layer is compatible with MySQL and Oracle
-Denv="prod" (an empty env string is considered as prod)
-Denv="preprod"
-Denv="test"
-Ddb="mysql"
-Ddb="oracle"
-Ddb="postgresql"
-Ddb="h2"

To run the application
mvn clean spring-boot:run

Running with the debugger
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
mvn clean install -Denv="test" -Ddb="h2" -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 -Xnoagent -Djava.compiler=NONE"

Some example API requests
curl -i -H "Accept:application/json" http://localhost:8080/api/error
curl -i -H "Accept:application/json" -H "Content-Type: application/json" "http://localhost:8080/api/auth/login" -X POST -d "{ \"email\" : \"mittiprovence@yahoo.se\", \"password\" : \"mignet\" }"
curl -i -H "Accept:application/json" -H "Content-Type: application/json" -H "Authorization:Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaXR0aXByb3ZlbmNlQHlhaG9vLnNlIiwic2NvcGVzIjpbIlJPTEVfQURNSU4iXSwiaXNzIjoiaHR0cDovL3RoYWxhc29mdC5jb20iLCJpYXQiOjE1MzU2MDgyMjAsImV4cCI6MTUzNTYwOTEyMH0.RXopXBufbzit9StoAvB_khA_lF9-UV0NwXSGVm_B9k5ijY89N0IMWDMeZyfE2AmJN_AAQ0doRni8d0RvDpGldQ" "http://localhost:8080/api/users/1/password" -X PUT -d "\"mignet\""

To do list
Create the refresh token in the credentials filter and add it to the response.
In the angular interceptor, if the access token is expired then try to renew it with the refresh token and try to authenticate with it.
Add a "remember me" angular interceptor to decide if the refresh token must be used if the access token expired.
Add a refresh token and test it

