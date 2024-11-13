=========================================================================
# Summary

## SecurityContextHolder
* -> contain **`SecurityContext`**

## SecurityContext
* -> contains the **`Authentication`** of the currently authenticated user

## Authentication
* -> can be **`an input`** to **`AuthenticationManager`** to provide the credentials a user has provided to authenticate (_when used in this scenario, **isAuthenticated()** returns **false**_)

* -> **`Authentication`** represent the **`currently authenticated user`**: 
* _i **`principal`** (**UserDetails** instance in user/password authentication), **`credentials`** (often a password), **`authorities`** (**GrantedAuthority** instances)_


## GrantedAuthority
* -> **'GrantedAuthority' instances** are **`high-level permissions`** that the user is granted - 2 examples are **`roles`** and **`scopes`**
* -> the **`Authentication.getAuthorities()`** method provides a **Collection** of **`GrantedAuthority`** objects
* => the **authorities** such as **`roles`** (Ex: ROLE_ADMINISTRATOR, ROLE_HR_SUPERVISOR) are later configured for **`web authorization`**, **`method authorization`**, and **`domain object authorization`**

## AuthenticationManager
* -> is the API that defines how **`Spring Security’s Filters` perform `authentication`**
* -> the **`Authentication`** that is returned is then set on the **SecurityContextHolder** by the **Spring Security’s Filters instances** that invoked the **`AuthenticationManager`**

## ProviderManager
* -> is the **most commonly used implementation** of **`AuthenticationManager`**
* -> ProviderManager delegates to a **List of `AuthenticationProvider` instances**

* -> if **none of the configured `AuthenticationProvider` instances can `authenticate`**, authentication fails with a **`ProviderNotFoundException`**
* -> which is a special **`AuthenticationException`** that indicates that **the `ProviderManager` was not configured to support the `type of Authentication` that was passed into it**

## AuthenticationProvider 
* -> can inject **multiple 'AuthenticationProviders' instances** into **`ProviderManager`** 

* -> each **AuthenticationProvider** has an opportunity to **indicate that authentication should be `successful`, `fail`**
* -> or **indicate it `cannot make a decision` and allow a `downstream 'AuthenticationProvider'` to decide**

* -> each **AuthenticationProvider** performs **`a specific type of authentication`**
* _For example, **DaoAuthenticationProvider** supports `username/password-based authentication`, while **JwtAuthenticationProvider** supports `authenticating a JWT token`_
* => this lets each "AuthenticationProvider" **do a very specific type of authentication** **`while supporting multiple types of authentication`** and **`expose only a single AuthenticationManager bean`**

## AbstractAuthenticationProcessingFilter
* -> is used as a **`base Filter for authenticating a user's credentials`**
* -> the **AuthenticationEntryPoint** will requests the credentials, then the **AbstractAuthenticationProcessingFilter** will **`authenticate any authentication requests that are submitted to it`**

=========================================================================
# Process
* -> before the **credentials can be authenticated**, Spring Security typically requests the credentials by using **`AuthenticationEntryPoint`**

* -> when the user submits their credentials, the **`AbstractAuthenticationProcessingFilter`** creates an **`Authentication`** from the **HttpServletRequest** to be authenticated
* _the **`type of 'Authentication'`** created depends on the **`subclass of 'AbstractAuthenticationProcessingFilter'`**
* _for example: **UsernamePasswordAuthenticationFilter** creates a **UsernamePasswordAuthenticationToken** from **a username and password** that are submitted in the "HttpServletRequest"_

* -> next, the **Authentication** is passed into the **`AuthenticationManager`** to be authenticated

* -> if authentication fails, then **Failure**
* -> the **`SecurityContextHolder`** is cleared out
* -> **`RememberMeServices.loginFail`** is invoked (_if remember me is not configured, this is a no-op_)
* -> **`AuthenticationFailureHandler`** is invoked

* -> if authentication is successful, then **Success**
* -> **`SessionAuthenticationStrategy`** is notified of a new login
* -> the **`Authentication`** is set on the **`SecurityContextHolder`**
* _later, if we need to save the **`SecurityContext`** so that it can be **automatically set on future requests**, **`SecurityContextRepository#saveContext must be explicitly invoked`**_
* -> **`RememberMeServices.loginSuccess`** is invoked (_if remember me is not configured, this is a no-op_)
* -> **ApplicationEventPublisher** publishes an **`InteractiveAuthenticationSuccessEvent`**
* -> **`AuthenticationSuccessHandler`** is invoked

=========================================================================
# 'SecurityContextHolder' Example

* -> the simplest way to **`indicate a user is authenticated`** is to set the **SecurityContextHolder** directly:
```java
// creating an empty 'SecurityContext'
// because using SecurityContextHolder.getContext().setAuthentication(authentication) to get 'SecurityContext' may cause race conditions across multiple threads
SecurityContext context = SecurityContextHolder.createEmptyContext(); 

// create a new 'Authentication' object
// Spring Security does not care what type of Authentication implementation is set on the SecurityContext
// here, we use TestingAuthenticationToken, because it is very simple
// a more common production scenario is UsernamePasswordAuthenticationToken(userDetails, password, authorities)
Authentication authentication = new TestingAuthenticationToken("username", "password", "ROLE_USER"); 

// set the SecurityContext on the SecurityContextHolder
// spring Security uses this information for authorization
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

# 'TheadLocal' in 'SecurityContextHolder'
By default, SecurityContextHolder uses a ThreadLocal to store these details, which means that the SecurityContext is always available to methods in the same thread, even if the SecurityContext is not explicitly passed around as an argument to those methods. Using a ThreadLocal in this way is quite safe if you take care to clear the thread after the present principal’s request is processed. Spring Security’s FilterChainProxy ensures that the SecurityContext is always cleared.

Some applications are not entirely suitable for using a ThreadLocal, because of the specific way they work with threads. For example, a Swing client might want all threads in a Java Virtual Machine to use the same security context. You can configure SecurityContextHolder with a strategy on startup to specify how you would like the context to be stored. For a standalone application, you would use the SecurityContextHolder.MODE_GLOBAL strategy. Other applications might want to have threads spawned by the secure thread also assume the same security identity. You can achieve this by using SecurityContextHolder.MODE_INHERITABLETHREADLOCAL. You can change the mode from the default SecurityContextHolder.MODE_THREADLOCAL in two ways. The first is to set a system property. The second is to call a static method on SecurityContextHolder. Most applications need not change from the default. However, if you do, take a look at the JavaDoc for SecurityContextHolder to learn more.

=========================================================================
# config parent 'AuthenticationManager' for 'ProviderManager'
ProviderManager also allows configuring an optional parent AuthenticationManager, which is consulted in the event that no AuthenticationProvider can perform authentication. The parent can be any type of AuthenticationManager, but it is often an instance of ProviderManager.

In fact, multiple ProviderManager instances might share the same parent AuthenticationManager. This is somewhat common in scenarios where there are multiple SecurityFilterChain instances that have some authentication in common (the shared parent AuthenticationManager), but also different authentication mechanisms (the different ProviderManager instances).

# Cache issue with 'ProviderManager' default behavior 
By default, ProviderManager tries to clear any sensitive credentials information from the Authentication object that is returned by a successful authentication request. This prevents information, such as passwords, being retained longer than necessary in the HttpSession.

This may cause issues when you use a cache of user objects, for example, to improve performance in a stateless application. If the Authentication contains a reference to an object in the cache (such as a UserDetails instance) and this has its credentials removed, it is no longer possible to authenticate against the cached value. You need to take this into account if you use a cache. An obvious solution is to first make a copy of the object, either in the cache implementation or in the AuthenticationProvider that creates the returned Authentication object. Alternatively, you can disable the eraseCredentialsAfterAuthentication property on ProviderManager. See the Javadoc for the ProviderManager class.