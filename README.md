Building the project
```
mvn clean install
```

The application uses two keystores. The thalasoft.keystore is used to enable https and the user-rest.keystore is used to enable jwt based security.

Generating the ssl keys
```
keytool -delete -alias thalasoft.key -keystore ~/.ssh/thalasoft.keystore
keytool -genkeypair -alias thalasoft.key -keypass my...l -keyalg RSA -keysize 4096 -dname "CN=thalasoft.com,OU=MyProduct,O=Thalasoft,L=Aix,S=PACA,C=FR" -storetype pkcs12 -keystore ~/.ssh/thalasoft.keystore -storepass my...l
keytool -delete -alias user-rest.key -keystore ~/.ssh/user-rest.keystore
keytool -genkeypair -alias user-rest.key -keyalg RSA -keysize 4096 -dname "CN=thalasoft.com,OU=MyProduct,O=Thalasoft,L=Aix,S=PACA,C=FR" -storetype pkcs12 -keystore ~/.ssh/user-rest.keystore -storepass my...l
keytool -list -v -storetype pkcs12 -keystore ~/.ssh/user-rest.keystore
cp ~/.ssh/user-rest.keystore src/main/resources/
cp ~/.ssh/thalasoft.keystore src/main/resources/
keytool -list -v -storetype pkcs12 -keystore src/main/resources/user-rest.keystore
keytool -list -v -storetype pkcs12 -keystore src/main/resources/thalasoft.keystore
```

Building and running the integration tests
```
mvn clean install -Denv="test" -Ddb="h2"
```

The data layer is compatible with MySQL and Oracle
```
-Denv="prod" (an empty env string is considered as prod)
-Denv="test"
-Ddb="mysql"
-Ddb="oracle"
-Ddb="postgresql"
-Ddb="h2"
```

Running the application
```
mvn clean spring-boot:run
```

Running the application with the debugger
```
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

Running the the integration tests with the debugger
```
mvn clean install \
  -Denv="test" \
  -Ddb="h2" \
  -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 -Xnoagent -Djava.compiler=NONE"
```

Some HTTPS request(s)
```
curl -i -H "Accept:application/json" --insecure https://localhost:8443/api/
```

Some example API requests
```
curl -i -H "Accept:application/json" https://dev.thalasoft.com:8443/api/error
curl -i -H "Accept:application/json" -H "Content-Type: application/json" "https://dev.thalasoft.com:8443/api/auth/login" -X POST -d "{ \"email\" : \"mittiprovence@yahoo.se\", \"password\" : \"mignet\" }"
export TOKEN=...
curl -i -H "Accept:application/json" -H "Content-Type: application/json" -H "TokenRefresh: Bearer $TOKEN" -H "ClientId: musicng" "https://dev.thalasoft.com:8443/api/auth/token-refresh" -X POST
curl -i -H "Accept:application/json" -H "Content-Type: application/json" -H "Authorization:Bearer $TOKEN" "https://dev.thalasoft.com:8443/api/users/1/password" -X PUT -d "\"mignet\""
```

Some OAuth2 requests
```
curl -i -H "Accept:application/json" -H "Content-Type: application/json" -H "Authorization:Bearer $TOKEN" "https://dev.thalasoft.com:8443/api/auth/authorize?client_id=musicng&redirect_uri=https%3A%2F%2Fdev.thalasoft.com%3A84%2Fcallback&state=DCEeFWf45A53sdfKef424&response_type=code&scope=read_profile" -X POST
```
Using httpie
```
http -f POST https://dev.thalasoft.com:8443/api/auth/authorize client_id=="musicng" redirect_uri=="https://dev.thalasoft.com:84/callback" state=="DCEeFWf45A53sdfKef424" response_type=="code" scope=="read_profile" "Accept:application/json" "Content-Type:application/json" "Authorization:Bearer $TOKEN" user_oauth_approval="true" authorize="Authorize"
```

To do list
- Add a "remember me" angular interceptor to decide if the refresh token must be used if the access token expired.
