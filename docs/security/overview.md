===========================================================================
# Reference

https://github.com/dynamind/spring-boot-security-oauth2-minimal
https://www.youtube.com/watch?v=FDP080zzVcc&list=PL4bT56Uw3S4zqmhhzJdsA_8aNhttF3mWa
https://github.com/NotFound403/id-server
https://github.com/andifalk/authorizationserver
https://github.com/dzinot/spring-boot-2-oauth2-authorization-jwt
https://github.com/rwinch/spring-enterprise-authorization-server
https://github.com/wdkeyser02/SpringBootSpringAuthorizationServer/tree/spring_boot_client_01

https://www.koyeb.com/tutorials/using-spring-authorization-server-as-an-auth-solution-on-koyeb
https://topdev.vn/blog/hien-thuc-oauth-resource-server-su-dung-spring-security-oauth2-resource-server/?utm_source=google&utm_medium=cpc&utm_campaign=pmax-branding&utm_content=performance&gad_source=1&gclid=Cj0KCQiAoae5BhCNARIsADVLzZf5WyxctiTESMewMS4aU9rI7d53dAq365QczkbLKBAXhzsJWoh7NKQaAvFpEALw_wcB
https://github.com/dzinot/spring-boot-2-oauth2-authorization-jwt
https://dzone.com/articles/build-an-oauth-20-authorization-server-with-spring
https://github.com/dynamind/spring-boot-security-oauth2-minimal/tree/master/oauth
https://github.com/cavanosa/oauth2-authorization-server
https://github.com/andifalk/authorizationserver
https://github.com/rwinch/spring-enterprise-authorization-server
https://www.baeldung.com/manually-set-user-authentication-spring-security
https://www.baeldung.com/spring-security-oauth-resource-server
https://mainul35.medium.com/oauth2-with-spring-part-2-getting-started-with-authorization-server-13804910cb2a
https://medium.com/@hydro.yura/spring-authorization-server-part-1-oauth-2-1-client-credentials-grant-type-90afe5ba5480
https://huongdanjava.com/implement-oauth-authorization-server-using-spring-authorization-server.html
https://github.com/rwinch/spring-enterprise-authorization-server

===========================================================================

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

===========================================================================
# start project 
* -> ta sẽ dựng project dựa trên **`SPA (Single Page Application) Sample`** của **Authorization Server**: https://github.com/spring-projects/spring-authorization-server/blob/main/samples/README.adoc
* -> đây là kiến trúc **`BFF`**

* -> đầu tiên tạo 1 thư mục mới trên máy của ta, rồi copy từ Repository những samples: backend-for-spa-client, demo-authorizationserver, messages-resource

* -> đầu tiên ta cần chỉnh sửa file : thay **implementation project(":spring-security-oauth2-authorization-server")** bằng **implementation "org.springframework.security:spring-security-oauth2-authorization-server:1.4.0"**

* -> copy qua file **`gradew`** (chạy chương trình), **`gradlew.bat`** (chạy daemon), **`settings.gradle`** (gradle build script), thư mục **`gradle`** (chứa gradle wrapper)
* _lưu ý khi commit có thể làm mất file **gradle-wrapper.jar**, nên cần copy lại khi pull code lần đầu_

* -> giờ để chạy từng project ta sẽ chạy file **.gradle** của chúng
* -> _Ví dụ để chạy file **`samples-backend-for-spa-client.gradle`** của project **backend-for-spa-client**:_
```bash
./gradlew -b .\backend-for-spa-client\samples-backend-for-spa-client.gradle bootRun
```

* -> chạy thằng **spa-client** trên http://127.0.0.1:4200/
* -> chạy **backend-for-spa-client** trên http://127.0.0.1:8080
* -> chạy **demo-authorizationserver** trên http://localhost:9000
* -> chạy **messages-resource** trên http://localhost:8090

## Process
* -> nhấn nút **Login**, "http://127.0.0.1:4200/" gửi GET request "http://127.0.0.1:8080/"
* -> response Status Code **302 Found** với **Location** header là "http://127.0.0.1:8080/oauth2/authorization/messaging-client-oidc" và **Set-Cookie** header là "JSESSIONID=275736A56345841B50599437973C29E5; Path=/; HttpOnly"

* -> Browser redirect tới "http://127.0.0.1:8080/oauth2/authorization/messaging-client-oidc"
* -> response Status Code **302 Found** với **Location** header là:
```bash
http://localhost:9000/oauth2/authorize
    ?response_type=code
    &client_id=messaging-client
    &scope=openid%20profile
    &state=wCGIGIXSTWV6t7se_4TgLL1poUZonP-ub2ajPEdEV2M%3D
    &redirect_uri=http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc
    &nonce=XyR5Co1QrIpuxDukdS9nvPeGXOpQYVs3h0VLw2-CGsc
```

* -> Browser redirect tới "http://localhost:9000/oauth2/authorize?....."
* -> response Status Code **302 Found** với **Location** header "http://localhost:9000/login" và **Set-Cookie** header là "JSESSIONID=8922586F930E313665F2B405CA06A44C; Path=/; HttpOnly"

* -> Browser redirect tới "http://localhost:9000/login", response về trang **`Login`** html

* -> đăng nhập bằng tài khoản định nghĩa trong **UserDetailsService** Bean của "DefaultSecurityConfig.java" của "demo-authorizationserver"
* -> submit from bằng POST request "http://localhost:9000/login" với FormData bao gồm **_csrf**, **username**, **password**
* -> response Status Code **302 Found** với **Set-Cookie** header là JSESSIONID=D1EC073E7957AD440C56D7BD0109C86A; Path=/; HttpOnly
và **Location** header là:
```bash
http://localhost:9000/oauth2/authorize
    ?response_type=code
    &client_id=messaging-client
    &scope=openid%20profile
    &state=NUJ9YysjtRmegPRAwbhnljPgB-lnr5-dODYfFF8t4Rc%3D
    &redirect_uri=http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc
    &nonce=S0AWNxzKsS-trlmLXz48RE6l3k3_VHH3OYXj5XgW0dQ
    &continue
```

* -> Browser redirect tới "http://localhost:9000/oauth2/authorize?....."
* -> response Status Code **302 Found** với **Location** header là 
```bash
http://localhost:9000/oauth2/consent
    ?scope=openid%20profile
    &client_id=messaging-client
    &state=6Po6AwcYdoaTpGdIeRoDZcG5IFnrmIowiSWsIMMDumE%3D"
```

* -> Browser redirect tới "http://localhost:9000/oauth2/consent?...."; response về trang **`Consent`**
* -> user xác nhận uỷ quyền "profile" cho "message-client" và bấm submit
* -> gửi POST request "http://localhost:9000/oauth2/authorize" 
* -> response với Status Code **302 Found** với **Location** Header là:
```bash
http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc?  
    code=jqq9EjyXNQurcaYF1ziAacvWU41DFsc3mzRm0OSHcFTgoOTddJooXfvvX_YL4_tKdoiMMLR9OsOehpenl-UPTxCQq1-jd6KxQduckWsTeG75ywT06I3LdUZht9mr_Cuc
    &state=4e4GAOBpdyKyqUSWXTQXk_5gkXASRbc_ms-t4frc2CA%3D
```

* -> Browser redirect tới "http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc?....."
* -> response với Statue Code là **302 Found** với **Location** header là "http://127.0.0.1:4200" và 3 **Set-Cookie** header:
```bash
set-cookie:  JSESSIONID=FBC5ACAC528ECBEED1403D5E9199A717; Path=/; HttpOnly
set-cookie: XSRF-TOKEN=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:10 GMT; Path=/
set-cookie: XSRF-TOKEN=8e518827-5b96-4903-b5a5-11b62c1460d8; Path=/
```

* -> Browser redirect tới "http://127.0.0.1:4200", reponse về trang html
* -> trang này sẽ gửi AJAX request tới "http://127.0.0.1:8080/userinfo" để lấy về "profile" data của user và hiền thị lên màn hình

* -> Khi ta click vào "Message" để lấy resource nó gửi request "http://127.0.0.1:8080/oauth2/authorization/messaging-client-authorization-code"
* -> response về Status Code **302 Found** với **Location** header là 
```bash
http://localhost:9000/oauth2/authorize
    ?response_type=code
    &client_id=messaging-client
    &scope=message.read%20message.write
    &state=3C9T5GpVqm4AtJ0RDH_6RsEVYrQcjC7jT3k_akZ9iB8%3D
    &redirect_uri=http://127.0.0.1:8080/authorized
```

* -> Browser sẽ redirect tới "http://localhost:9000/oauth2/authorize?....."
* -> response về Status Code **302 Found** với **Location** header là:
```bash
http://localhost:9000/oauth2/consent
    ?scope=message.read%20message.write
    &client_id=messaging-client
    &state=nfUPUPbHj6ydwhjSM3yNBoJuLTMBZCOjt6n10zc_txY%3D
```

* -> Browser redirect tới "http://localhost:9000/oauth2/consent?...", response trả về trang **`consent`** html
* -> user xác nhận uỷ quyền "message.read" và "message.write" rồi submit form
* -> gửi POST request tới "http://localhost:9000/oauth2/authorize"
* -> response với Statue Code là **302 Found** với **Location** header là:
```bash
http://127.0.0.1:8080/authorized?
    code=W0tldlgjogdJuYxto55UrqZPD1ZnbOY1gXrMRdgqyxtsmrmg4FXA9iNgo1cPlV9GLsmFYEZpldQVtoX-gGmeRMKe2EZtFxu3G2RKW0HW3PVmPu5VTBAfPWuYM3vv-EuZ
    &state=3C9T5GpVqm4AtJ0RDH_6RsEVYrQcjC7jT3k_akZ9iB8%3D
```

* -> Browser redirect tới "http://127.0.0.1:8080/authorized?..."
* -> response với Statue Code là **302 Found** với **Location** header là "http://127.0.0.1:8080/?continue"

* -> Browser redirect tới "http://127.0.0.1:8080/?continue"
* -> response với Statue Code là **302 Found** với **Location** header là "http://127.0.0.1:4200"

* -> Browser redirect tới "http://127.0.0.1:4200/"
* -> trang này gửi 1 AJAX request tới "http://127.0.0.1:8080/messages" để lầy về resource và hiển thị ra màn hình


===========================================================================
# Access 

## Authorization server
-> **application.yml** hiện tại có cấu hình **`spring#security#oauth2#client`** cho "Google" và "Github" và **`spring#ssl#bundle#jks`** 

-> custom Implement for authentication
extends OAuth2ClientAuthenticationToken -> dùng cho "AuthenticationProvider"
implements AuthenticationProvider -> dùng cho authorization server bean config

-> custom Implement for federation:
implements AuthenticationSuccessHandler -> dùng cho security bean config
implements OAuth2TokenCustomizer<JwtEncodingContext> -> dùng cho authorization server bean config
implements Consumer<OAuth2User> -> chưa xài ở đâu

-> custom Implement for web authentication:
implements AuthenticationConverter -> dùng cho authorization server bean config

-> **@Configuration** bean for authorization server:
- SecurityFilterChain: .securityMatcher, .with, .authorizeHttpRequests, .exceptionHandling
- JdbcRegisteredClientRepository: tạo ra các **RegisteredClient** sau đó dùng **`JdbcRegisteredClientRepository`** để save tất cả chúng
- JdbcOAuth2AuthorizationService
- JdbcOAuth2AuthorizationConsentService
- OAuth2TokenCustomizer<JwtEncodingContext> : tạo 1 **`FederatedIdentityIdTokenCustomizer`** instance
- JWKSource<SecurityContext>: trả về callback với return là **JWK sử dụng RSA**
- JwtDecoder
- AuthorizationServerSettings
- EmbeddedDatabase:  tạo **`EmbeddedDatabaseBuilder`** instace

-> **@Configuration + @EnableWebSecurity** for security bean:
- SecurityFilterChain: .authorizeHttpRequests, .formLogin, .oauth2Login
- UserDetailsService: tạo 1 **`InMemoryUserDetailsManager`** instance
- SessionRegistry: tạo 1 **`SessionRegistryImpl`** instance
- HttpSessionEventPublisher: tạo 1 **`HttpSessionEventPublisher`** instance

-> **@Configuration** for Tomcat web server bean:
WebServerFactoryCustomizer<TomcatServletWebServerFactory>

-> **@Controller** AuthorizationConsentController đang sử dụng các beans: 
RegisteredClientRepository, 
OAuth2AuthorizationConsentService

## Resource server

* -> **application.yml** cấu hình **`spring#oauth2#resourceserver#jwt`** và **`spring#ssl#bundle#jks`**

* -> **@Configuration + @EnableWebSecurity** for security beans:
- SecurityFilterChain: .securityMatcher, .authorizeHttpRequests, .oauth2ResourceServer

* -> **@Configuration** for Tomcat web server bean:
WebServerFactoryCustomizer<TomcatServletWebServerFactory>

## backend client

* -> **application.yml** cấu hình **`spring#security#oauth2#client#registration`** và **`spring#cloud#gateway#mvc#routes và app#base-uri`**

* -> **resources/META-INF/spring.factories** cấu hình với interface và class ta mới định nghĩa **`GatewayFilterFunctions`** và **`FilterSupplier`** 

* -> **@Configuration + @EnableWebSecurity** cấu hình security:
- SecurityFilterChain: .csrf, .cors, .exceptionHandling với AuthenticationEntryPoint, .oauth2Login, .logout, .oauth2Client

===========================================================================
# Summary

## 'TomCat' web server config
* -> 2 thằng **Authorization server** và **Resource server** đều cấu hình @Configuration beans cho Tomcat server như nhau
* -> mục đích là thêm 1 HTTP connector vào Tomcat server 
* -> _VD: với thằng authorization server thì **connector** này sẽ listen on port 9000 and **`redirects any incoming requests to port 9443`** if they are not already secure (using HTTPS)_
```java
@Configuration(proxyBeanMethods = false)
public class TomcatServerConfig {

	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> connectorCustomizer() {
		return (tomcat) -> tomcat.addAdditionalTomcatConnectors(createHttpConnector());
	}

	private Connector createHttpConnector() {
        //sets the protocol for the connector to HTTP:
		Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		connector.setScheme("http");
		connector.setPort(9000);
		connector.setSecure(false); // the connector will not use SSL/TLS encryption for incoming requests
		connector.setRedirectPort(9443); //  redirect any incoming requests that are not already secure (i.e., using HTTPS) to port 9443.
		return connector;
	}
}
```

## 'spa-client' và 'backend-client'
* -> thằng **spa-client** hiện tại sẽ có lấy resource 2 endpoint của **backend-client** (http://127.0.0.1:8080/) là "/messages" và "/userinfo"
* -> ta sẽ cấu hình những endpoint này với **`Spring Cloud Gateway`** trong file **application.yml** của **backend-client** 

```yml - application.yml
spring:
  cloud:
    gateway:
      mvc:
        routes:
          - id: userinfo
            uri: http://localhost:9000
            predicates:
              - Path=/userinfo
            filters:
              - TokenRelay=
          - id: messages
            uri: http://localhost:8090
            predicates:
              - Path=/messages
            filters:
              - RelayTokenIfExists=messaging-client-authorization-code
``` 

# Compare to default

## Authorization Server
* _với **application.yml**_
* -> không có cấu hình **`spring#security#oauth2#authorizationserver#client`**
* -> mà sẽ có cấu hình của **Spring Security OAuth2 Client**: **`spring#security#oauth2#client#registration`**

* _với "authorizationServerSecurityFilterChain"_
* -> **OAuth2AuthorizationServerConfigurer** có thêm **`.deviceAuthorizationEndpoint()`**, **`.deviceVerificationEndpoint()`**, **`.clientAuthentication`**, **`.authorizationEndpoint()`**
* -> **HttpSecurity** có thêm **`authorizeHttpRequests`**

* _với "defaultSecurityFilterChain"_
* -> **HttpSecurity** có thêm **`.oauth2Login()`**

* -> không cấu hình **RegisteredClientRepository** nhưng cấu hình **`JdbcRegisteredClientRepository`**

* -> **`JWKSource<SecurityContext>`** được implement khác đi

* -> có thêm cấu hình **`EmbeddedDatabase`**
* -> có thêm cấu hình **`JdbcOAuth2AuthorizationService`**
* -> có thêm cấu hình  **`JdbcOAuth2AuthorizationConsentService`**
* -> có thêm cấu hình  **`OAuth2TokenCustomizer<JwtEncodingContext>`**
* -> có thêm cấu hình **`SessionRegistry`**
* -> có thêm cấu hình **`HttpSessionEventPublisher`**

* -> mặc dù thằng này sử dụng **OAuth2 Client** nhưng lại không gọi **`.oauth2Client()`**

## Resource server
* _với **application.yml**_
* -> sẽ có cấu hình **`spring#security#oauth2#resourceserver#jwt`** nhưng là cấu hình cho **`jwk-set-uri`** chứ không phải **`issuer-uri`**
* => **`issuer-uri`** là **base URL of the authorization server** 
* => resource server sẽ dùng nó để **validate the `iss` claim in the token**
* => đồng thời sẽ dùng URI này để retrieve **the OpenID Connect (OIDC) discovery document** (**`<issuer-uri>/.well-known/openid-configuration`**)
* => **discory document** sẽ cung cấp cho ta various endpoints including the **`jwks_uri`** - points to the **JWK set** (**`public keys`** used to **`verify the JWT's signature`**)
* => nếu cấu hình **`jwk-set-uri`** -  a direct URL pointing to the JWK endpoint
* => thường cấu hình dạng này nếu **authorization server does not provide an OIDC discovery document** hoặc muốn **explicitly specify the JWK set location**
* => khi lấy được **`collection of public keys`** từ **jwk-set-uri** và tìm key phù hợp dựa vào **`kid`** claim của JWT'header
* => lý do 1 list public keys là do **authorization server** often **`rotate keys`** to enhance security
* => việc **validate token** sẽ còn bao gồm kiểm tra **`exp`** claim (not expired), **`iss`** claim (token issue by trusted Authorization Server), **`aud`** claim (intended for this Resource Server)
* => sau khi **validate token** thì nhiệm vụ còn lại của resource server chỉ còn là **Authention** (VD: use **`sub`** claim to identifies authenticated user), **Authorization** (VD: use **`scope`** claim to enforce access control policies.)

* _với cấu hình **securityFilterChain**_
* -> thay vì authorize any request thì nó sẽ có thêm **`.securityMatcher()`**

## Backend client
* _với **application.yml**_
* -> vẫn vậy vẫn có cấu hình **`spring#security#oath2#client#registration`** và **`spring#security#oath2#client#provider`**

* _với **securityFilterChain**_ thì có thêm **`.authorizeHttpRequests()`**, **`.csrf()`**, **`.cors()`**, **`.exceptionHandling()`**, **`.logout`**

# Token

* -> **Resource server** sẽ chỉ deal với **`Access Token`** (not Id Token or Refresh Token)

* -> **Client** sẽ tương tác với cả 3 loại token; sau khi gửi credential tới authorization server thì sẽ nhận được 3 loại token
* -> send **`Access Token`** to **resource server** để access protected resource
* -> client sẽ detect **exp** claim để send **`Refresh Token`** to **authorization server** để obtain new **Access Tokens** or **ID Tokens** when the current ones expire
* -> **`Id Token`** is a part of the **OpenID Connect flow** được sinh ra để cho **client** biết về user infomation
* -> **`OIDC`** chính là thằng cung cấp a **standardized way** to authenticate users and manage their identity bao gồm **`standard token`** và **`standard user info endpoint`**
* -> (_nếu ta chỉ sử dụng **OAuth2.0** để authentication bằng cách thêm 1 số claim vào access token đồng thêm tạo thêm custom API để lấy user info_)
* => **Id token** cũng là 1 JWT dành riêng cho **`client`** bao gồm các claim: sub, email, iss, aud, exp
* => client trước tiên sẽ validate Signature của nó bằng public key lấy từ **`jwks_uri`** endpoint; rồi validate các claim như **iss**, **aud**, **exp**, **nonce** rồi mới sử dụng 
* => nó sẽ chứa basic **`user information`** mà client cần (VD: display user profile); nếu cần thêm những user detail khác thì nó có thể gọi **`standard user info endpoint`** với **Access Token** được đính kèm
* => thay vì client phải gửi thêm 1 request để fetch user information hoặc guess based on the Access Token (nhưng điều này là không nên vì access token nên được dùng cho resource server)

* => nếu **refresh token** expired thì sẽ cần **`Reauthentication`**, client sẽ cần redirect the user to the Authorization Server **`/authorize`** endpoint với suitable param để login
* => nếu **authorization server** sử dụng cơ chế **`Refresh Token Rotation`** (_for mitigates the risk of token replay attacks_) thì everytime client gửi refresh token để get token mới (_`id_token` hoặc `access_token`_) thì nó cũng sẽ trả cả 3 token **refresh token, access token, id token** mới hoàn toàn (tức là expire time của 3 thằng này cũng sẽ được reset)

* => **PKCE (Proof Key for Code Exchange)** will be **`automatically enabled by default`** in Spring **Security's OAuth 2.0 client** implementation when the **authorization_code grant type** is configured (_starting from Spring Security 5.5_) 
* (_The `OAuth2AuthorizedClientManager` and `OAuth2LoginAuthenticationFilter` automatically add PKCE support when performing the Authorization Code flow_)
* => dùng để chống **authorization code interception attack**
* => với **`PKCE`** thì client sẽ có thêm nhiệm vụ là generate 1 cặp **`Code_Challenge và Code_Verifier`** thay vì lưu trữ trực tiếp **Client Secret** (embedded secret can be extract in SPA, native app)
* => khi **`Code_Challenge`** sẽ được gửi kèm khi user được redirect tới trang **/Login** của authorization server và được authorization server lưu lại
* => sau đó để client lấy được **Access Token** nó sẽ gửi **`Code_Verifier`**, **Client ID**, **Authorization Code** cho Authorization Server
* => Authorization Server sử dụng Code_Challenge và Code_Verifier để validate sau đó cấp Access Token

* => mặc dù ban đầu **PKCE** là thiết kế cho **public client** (_where secrets can't be securely stored_) nhưng sau đó được dùng cho cả confidential clients (such as **`server-side web applications`** - có thể lưu **client secret**) như là việc thêm 1 lớp bảo vệ bao gồm:
* (`Authorization Code Interception`: An attacker cannot use an intercepted authorization code without the code_verifier)
* (`Man-in-the-Middle Attacks`: PKCE ensures the client exchanging the code is the same one that initiated the authorization request_)