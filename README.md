To only build the project
```
mvn clean install
```

To build and run some integration tests
```
mvn clean install -Denv="test" -Ddb="h2"
```

The data layer is compatible with MySQL and Oracle
```
-Denv="prod" (an empty env string is considered as prod)
-Denv="preprod"
-Denv="test"
-Ddb="mysql"
-Ddb="oracle"
-Ddb="postgresql"
-Ddb="h2"
```

To run the application
```
mvn clean spring-boot:run
```

Running with the debugger
```
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
mvn clean install -Denv="test" -Ddb="h2" -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 -Xnoagent -Djava.compiler=NONE"
```

Some example API requests
```
curl -i -H "Accept:application/json" http://localhost:8080/api/error
curl -i -H "Accept:application/json" -H "Content-Type: application/json" "http://localhost:8080/api/auth/login" -X POST -d "{ \"email\" : \"mittiprovence@yahoo.se\", \"password\" : \"mignet\" }"
curl -i -H "Accept:application/json" -H "Content-Type: application/json" -H "TokenRefresh: Bearer eyJhbGciOiJIUzUxMiJ9.eyJlbWFpbCI6Im1pdHRpcHJvdmVuY2VAeWFob28uc2UiLCJzdWIiOiJORy1aRVJPIiwianRpIjoiNzBjYmYzZmItYmJhMC00Y2E3LWI4MTItZjNmYmM5NjMzNjU3IiwiaXNzIjoiaHR0cDovL3RoYWxhc29mdC5jb20iLCJpYXQiOjE1NDE0MTEyNzcsImV4cCI6MTU0MjAxNjA3N30.7soHggVsKkOU8SwepvF99c0JKltEWzgsOOyrtTWpPCODdH_TPyXFIVog_NWOOM1gmcFyqwbbZ3HDUQ6rgzdIaA" -H "ClientId: ng-zero" "http://localhost:8080/api/auth/token-refresh" -X POST
export TOKEN=...
curl -i -H "Accept:application/json" -H "Content-Type: application/json" -H "Authorization:Bearer eyJhbGciOiJIUzUxMiJ9.eyJlbWFpbCI6Im1pdHRpcHJvdmVuY2VAeWFob28uc2UiLCJmdWxsbmFtZSI6IlN0ZXBoYW5lIEV5YmVydCIsInNjb3BlcyI6WyJST0xFX1VTRVIiXSwic3ViIjoibWl0dGlwcm92ZW5jZUB5YWhvby5zZSIsImlzcyI6Imh0dHA6Ly90aGFsYXNvZnQuY29tIiwiaWF0IjoxNTQwNzQwMDA5LCJleHAiOjE1NDA3NDM2MDl9.jmfp22i1DQOaIlcgmCOB-g1m-i9PqWcXcZiztcPV3Juweivn1EvWq9LFzaTMt_XFECw-u6Sfp8Bx2wcNKQKujg" "http://localhost:8080/api/users/1/password" -X PUT -d "\"mignet\""
```

Some OAuth2 requests
```
curl -i -H "Accept:application/json" -H "Content-Type: application/json" -H "Authorization:Bearer $TOKEN" "http://localhost:8080/api/auth/authorize?client_id=ng-zero&redirect_uri=http%3A%2F%2Flocalhost%3A4200%2Fcallback&state=DCEeFWf45A53sdfKef424&response_type=code&scope=read_profile" -X POST
Using httpie
http -f POST http://localhost:8080/api/auth/authorize client_id=="ng-zero" redirect_uri=="http://localhost:4200/callback" state=="DCEeFWf45A53sdfKef424" response_type=="code" scope=="read_profile" "Accept:application/json" "Content-Type:application/json" "Authorization:Bearer $TOKEN" user_oauth_approval="true" authorize="Authorize"
```

To do list
- Create the refresh token in the credentials filter and add it to the response.
- In the angular interceptor, if the access token is expired then try to renew it with the refresh token and try to authenticate with it.
- Add a "remember me" angular interceptor to decide if the refresh token must be used if the access token expired.
- Add a refresh token and test it.

