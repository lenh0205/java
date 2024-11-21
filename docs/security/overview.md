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
