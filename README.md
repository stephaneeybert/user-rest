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
curl -i -H "Accept:application/json" -H "Content-Type: application/json" -H "Authorization:Bearer eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1MzQwMDQyNDIsInN1YiI6Im1pdHRpcHJvdmVuY2VAeWFob28uc2UifQ.7M4gdjgpjEcxof7_iowasdQQiFy6nj4-ylhrNnYXaPE" "http://localhost:8080/api/users/1/password" -X PUT -d "\"mignet\""
