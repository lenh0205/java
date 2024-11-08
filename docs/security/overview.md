https://github.com/dynamind/spring-boot-security-oauth2-minimal
https://www.youtube.com/watch?v=FDP080zzVcc&list=PL4bT56Uw3S4zqmhhzJdsA_8aNhttF3mWa
https://github.com/NotFound403/id-server
https://github.com/andifalk/authorizationserver
https://github.com/dzinot/spring-boot-2-oauth2-authorization-jwt
https://github.com/rwinch/spring-enterprise-authorization-server
https://github.com/wdkeyser02/SpringBootSpringAuthorizationServer/tree/spring_boot_client_01

# Công việc cần làm
* -> dựng các subject cơ bản: 1 Client, 1 Web Application để làm Authorization Server, 1 WebAPI để làm Resource Server
* -> mỗi thằng trong tụi nó sẽ cần cài packages để hỗ trợ security với OAuth2 và OpenID Connect
* -> sau đó mỗi projecct sẽ có các static configuration để các security package này đọc và cho phép các subject này tương tác với nhau

## static Configuration

### Authorization server
* -> port và host mà nó sẽ chạy - đây có thể là giá trị của **`issuer`**
* -> cấu hình cho từng client: client-id, client-secret, authentication-method, authorization grant type, redirect URI, Scopes (openid, ...)

### Resource server
* -> cấu hình **`issuer URI`**

### Client
* -> cấu hình **`issuer URI`**
* -> cấu hình client-oidc: client-id, client-secret, authorization grant type, redirect-uri, scope (openid), client-name
* -> cấu hình client-authorization: client-id, client-secret, authorization-grant-type, redirect-uri, scope, client-name