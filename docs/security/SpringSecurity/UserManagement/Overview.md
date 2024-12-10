> nên nhớ authorization server primary focus is on **authentication and token management** (vậy nên nó cần **`UserDetailsService`** để retrieve user credentials and details) 
> in OAuth2/OpenID Connect scenarios, việc **User Management** không nên handle bởi Authorization server mà thường implemented as a separate service or feature
> the **spring-security-oauth2-authorization-server** framework **`does not include built-in support for user registration`**
> vậy nên thường thì chức năng user registration sẽ implemented as a separate service or feature
> UserDetailsService của Authorization Server cũng sẽ sử dụng dữ liệu của database của thằng này