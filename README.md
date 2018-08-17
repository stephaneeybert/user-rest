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
curl -i -H "Accept:application/json" -H "Content-Type: application/json" "http://localhost:8080/api/users/login" -X POST -d "{ \"email\" : \"mittiprovence@yahoo.se\", \"password\" : \"mignet\" }"
curl -i -H "Accept:application/json" -H "Content-Type: application/json" -H "Authorization:Bearer eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MzUwOTU2OTMsInN1YiI6Im1pdHRpcHJvdmVuY2VAeWFob28uc2UifQ.a0m4XSpJsQJTslqJJ2ElhyBqqT8CpUnPeAZ64l-T05M3s8E2C34hwEyjujJ6HO4jKITr_ubNX17vAQg4COGq4g" "http://localhost:8080/api/users/1/password" -X PUT -d "\"mignet\""
