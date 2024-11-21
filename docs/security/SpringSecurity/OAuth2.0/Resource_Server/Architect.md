
# OAuth2.0 Bearer Token within Spring Security

## Reading Bearer Token Process (using 'Basic Authentication') 
* -> first, **a user makes an unauthenticated request** to the "/private" resource for which the user is not authorized
* -> Spring Security's **`AuthorizationFilter`** indicates that the unauthenticated request is Denied by **throwing an 'AccessDeniedException'**

* -> since **the user is not authenticated**, **`ExceptionTranslationFilter`** initiates Start Authentication
* -> the configured **AuthenticationEntryPoint** is an instance of **`BearerTokenAuthenticationEntryPoint`**, which **sends a WWW-Authenticate header**
* -> the "RequestCache" is typically a **`NullRequestCache`** that does not save the request, since **the client is capable of replaying the requests it originally requested**

* -> when **a client receives the WWW-Authenticate: `Bearer header`**, it knows it should retry with a "bearer token"
* -> when the **user submits their bearer token**, the **`BearerTokenAuthenticationFilter`** creates a **`BearerTokenAuthenticationToken`**
* (_which is a type of **`Authentication`** by extracting the token from the **HttpServletRequest**_)

* -> next, the **HttpServletRequest** is passed to the **`AuthenticationManagerResolver`**, which selects the **`AuthenticationManager`**
* -> the **`BearerTokenAuthenticationToken`** is **passed into the AuthenticationManager to be authenticated**. 
* (_the details of what **AuthenticationManager** looks like depends on whether we're configured for **JWT** or **opaque token**_)

* -> if **`authentication fails`**, then Failure
* -> the **SecurityContextHolder** is cleared out.
* -> the **AuthenticationEntryPoint** is invoked to trigger the **WWW-Authenticate** header to be sent again

* -> if **`authentication is successful`**, then Success
* -> the **Authentication** is set on the **SecurityContextHolder**
* -> the **BearerTokenAuthenticationFilter** invokes **FilterChain.doFilter(request,response)** to continue with the rest of the application logic


## architectural components that Spring Security uses to support JWT Authentication in servlet-based applications
* -> the **authentication Filter** passes a **`BearerTokenAuthenticationToken`** to the **AuthenticationManager** which is implemented by **`ProviderManager`**
* -> the "ProviderManager" is configured to use an **AuthenticationProvider** of type **`JwtAuthenticationProvider`**

* -> "JwtAuthenticationProvider" **decodes, verifies, and validates the Jwt** using a **`JwtDecoder`**
* -> "JwtAuthenticationProvider" then uses the **`JwtAuthenticationConverter`** to **convert the Jwt into a Collection of granted authorities**

* -> when authentication is successful, the **Authentication** that is returned is of type **`JwtAuthenticationToken`** and has **`a principal`** that is the Jwt returned by the configured JwtDecoder
* -> ultimately, the returned JwtAuthenticationToken will be set on the **SecurityContextHolder** by the **authentication Filter**
