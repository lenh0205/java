> Persisting Authentication
> username-password, OAuth, Session

===================================================================
# Illustration
* _a summarized HTTP exchange for an unauthenticated user requesting a protected resource might look like this:_

```bash
# Unauthenticated User Requests Protected Resource
GET / HTTP/1.1
Host: example.com
Cookie: SESSION=91470ce0-3f3c-455b-b7ad-079b02290f7b

HTTP/1.1 302 Found
Location: /login

# Username and Password Submitted
POST /login HTTP/1.1
Host: example.com
Cookie: SESSION=91470ce0-3f3c-455b-b7ad-079b02290f7b

username=user&password=password&_csrf=35942e65-a172-4cd4-a1d4-d16a51147b3e

# Authenticated User is Associated to New Session
# upon authenticating the user, the user is associated to a new session id to prevent session fixation attacks
HTTP/1.1 302 Found
Location: /
Set-Cookie: SESSION=4c66e474-3f5a-43ed-8e48-cc1d8cb1d1c8; Path=/; HttpOnly; SameSite=Lax

# Authenticated Session Provided as Credentials
GET / HTTP/1.1
Host: example.com
Cookie: SESSION=4c66e474-3f5a-43ed-8e48-cc1d8cb1d1c8
```

===================================================================
# SecurityContextRepository
* -> Spring Security the **associate of the `user` to `future requests`** is made using **`SecurityContextRepository`**

* -> the **`default implementation`** of **SecurityContextRepository** is **`DelegatingSecurityContextRepository`**

* -> which delegates to the: **`HttpSessionSecurityContextRepository`**, **`RequestAttributeSecurityContextRepository`**

===================================================================
# DelegatingSecurityContextRepository
* -> the "DelegatingSecurityContextRepository" **saves the `SecurityContext`** to **multiple `SecurityContextRepository` delegates**
* -> and allows **retrieval from any of the delegates in a specified order**

* _the `most useful arrangement` for this is configured with the below example,_
* -> this is the **`default configuration`** in **Spring Security 6**
* -> which allows the use of both **RequestAttributeSecurityContextRepository** and **HttpSessionSecurityContextRepository** **`simultaneously`**_

```java - Configure "DelegatingSecurityContextRepository"
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	http
		// ...
		.securityContext((securityContext) -> securityContext
			.securityContextRepository(new DelegatingSecurityContextRepository(
				new RequestAttributeSecurityContextRepository(),
				new HttpSessionSecurityContextRepository()
			))
		);
	return http.build();
}
```

===================================================================
# HttpSessionSecurityContextRepository
* -> the "HttpSessionSecurityContextRepository" **associates the `SecurityContext` to the `HttpSession`**

* -> if we wish to **associate the user with subsequent requests** in **`another way`** or not at all (__)
* -> users can replace **HttpSessionSecurityContextRepository** with **another implementation of `SecurityContextRepository`** 

# NullSecurityContextRepository
* -> if it is **`not desirable`** to **associate the SecurityContext to an HttpSession** (_i.e. when authenticating with **`OAuth`**)
* -> the **NullSecurityContextRepository** is an **implementation of `SecurityContextRepository` that does nothing**

===================================================================
# RequestAttributeSecurityContextRepository
* -> the "RequestAttributeSecurityContextRepository" saves the **`SecurityContext`** as **`a request attribute`**
* -> to **`make sure the SecurityContext is available for a single request`** that occurs across **dispatch types that may `clear out the SecurityContext`**

## Illustration
* -> For example, assume that a client makes a request, is authenticated, and then an error occurs. Depending on the servlet container implementation, the error means that any SecurityContext that was established is cleared out and then the error dispatch is made. When the error dispatch is made, there is no SecurityContext established. This means that the error page cannot use the SecurityContext for authorization or displaying the current user unless the SecurityContext is persisted somehow.

## Example: 

```java - use "RequestAttributeSecurityContextRepository"
public SecurityFilterChain filterChain(HttpSecurity http) {
	http
		// ...
		.securityContext((securityContext) -> securityContext
			.securityContextRepository(new RequestAttributeSecurityContextRepository())
		);
	return http.build();
}
```

===================================================================
# SecurityContextPersistenceFilter


===================================================================
# SecurityContextHolderFilter