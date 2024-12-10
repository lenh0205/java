> nên nhớ authorization server primary focus is on **authentication and token management** (vậy nên nó cần **`UserDetailsService`** để retrieve user credentials and details) 
> in OAuth2/OpenID Connect scenarios, việc **User Management** không nên handle bởi Authorization server mà thường implemented as a separate service or feature
> the **spring-security-oauth2-authorization-server** framework **`does not include built-in support for user registration`**
> vậy nên thường thì chức năng user registration sẽ implemented as a separate service or feature
> UserDetailsService của Authorization Server cũng sẽ sử dụng dữ liệu của database của thằng này

# 
* -> ta cần tạo 1 project bao gồm: spring web, srping data JPA, JDBC SQL Server, Thymeleaf với các lớp Controller, Service, Repository và 1 endpoint là /register 
* -> sau khi chạy ổn, ta sẽ thêm **spring-boot-starter-security** lúc này dự án sẽ tự động authen tất cả endpoint (redirect to **/Login** page)
* -> bây giờ thì ta sẽ cần cấu hình SecurityFilterChain để public 1 số endpoint