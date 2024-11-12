=========================================================================
# Overview
* -> **`SecurityContextHolder`** contain **`SecurityContext`**
* -> **SecurityContext**  contains the **`Authentication`** of the currently authenticated user

* -> **`Authentication`** represent the **`currently authenticated user`**: 
* _i **`principal`** (**UserDetails** instance in user/password authentication), **`credentials`** (often a password), **`authorities`** (**GrantedAuthority** instances)_
* -> can be **`an input`** to **`AuthenticationManager`** to provide the credentials a user has provided to authenticate (when used in this scenario, **isAuthenticated()** returns **false**_)

* -> **`GrantedAuthority`** instances are **`high-level permissions`** that the user is granted; 2 examples are **roles** and **scopes**
* -> the **`Authentication.getAuthorities()`** method provides a **Collection** of **`GrantedAuthority`** objects
* => the **authorities** such as **`roles`** (Ex: ROLE_ADMINISTRATOR, ROLE_HR_SUPERVISOR) are later configured for **`web authorization`**, **`method authorization`**, and **`domain object authorization`**

=========================================================================
# SecurityContextHolder

* -> the simplest way to **`indicate a user is authenticated`** is to set the **SecurityContextHolder** directly:
```java
SecurityContext context = SecurityContextHolder.createEmptyContext(); 

Authentication authentication = new TestingAuthenticationToken("username", "password", "ROLE_USER"); 
context.setAuthentication(authentication);
SecurityContextHolder.setContext(context);
```

* -> to obtain **`information about the authenticated principal`**, access the **SecurityContextHolder**
```java - Access Currently Authenticated User
SecurityContext context = SecurityContextHolder.getContext();
Authentication authentication = context.getAuthentication();

String username = authentication.getName();
Object principal = authentication.getPrincipal();
Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
```