======================================================================
# Username/Password Authentication

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
			.httpBasic(Customizer.withDefaults())
			.formLogin(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
        // 'User.withDefaultPasswordEncoder' to ensure that the password stored in memory is protected
        // however, it does not protect against obtaining the password by decompiling the source code
        // so this should only be used for "getting started" and is not intended for "production"
		UserDetails userDetails = User.withDefaultPasswordEncoder()
			.username("user")
			.password("password")
			.roles("USER")
			.build();

		return new InMemoryUserDetailsManager(userDetails);
	}

}
```

## InMemoryUserDetailsManager
* -> implements **`UserDetailsManager`** interface to provides **management of `UserDetails`** (_changePassword, CreateUser, deleteUser, updateUser, userExists, loadUserByUsername_)

* -> the **UserDetailsManager** implements **`UserDetailsService`** interface to provide support for **`username/password based authentication`** that is **stored in memory**

## UserDetailsService
* -> is used by **`DaoAuthenticationProvider`** when **`authenticating with a username and password`**
* -> for **retrieving a username, a password, and other attributes** 
* -> Spring Security provides **`in-memory`**, **`JDBC`**, and **`caching`** implementations of **UserDetailsService**

* -> we can **`define custom authentication`** by **exposing a custom `UserDetailsService` as a `bean`** 
* (_This is only used if the AuthenticationManagerBuilder has not been populated and no AuthenticationProviderBean is defined_)

```java
@Bean
CustomUserDetailsService customUserDetailsService() {
	return new CustomUserDetailsService();
}
```

## DaoAuthenticationProvider
* -> is an **`AuthenticationProvider`** implementation
* -> uses a **`UserDetailsService`** and **`PasswordEncoder`** to **authenticate a username and password**

## Process
* -> first, the **`authencation Filter`** passes a **UsernamePasswordAuthenticationToken** to the **`AuthenticationManager`** (ProviderManager)
* -> the **ProviderManager** is configured to use an **AuthenticationProvider** of type **`DaoAuthenticationProvider`**
* -> **DaoAuthenticationProvider** looks up the **`UserDetails`** from the **`UserDetailsService`**
* -> **DaoAuthenticationProvider** uses the **`PasswordEncoder`** to validate the password on the returned UserDetails 

* -> when **authentication is successful**, the **`Authentication`** that is returned is of type **`UsernamePasswordAuthenticationToken`** 
* -> and has a **`principal`** that is the **`UserDetails`** returned by the configured **UserDetailsService**
* -> finally, the returned **UsernamePasswordAuthenticationToken** is set on the **`SecurityContextHolder`** by the **`authentication Filter`**

## 'UserDetails' based authentication 
* -> is used by Spring Security when it is configured to **`accept a username and password for authentication`**

## DAO - Data Access Object
* -> a **design pattern** that **`provides an interface`** to **`encapsulate all the logic related to accessing and manipulating data in a data storage system`**
* => making it **easier to change or replace** the underlying data storage system **without affecting the rest of the application**

======================================================================
> use "/Login" page

# Form Login
* -> Spring Security provides **support for username and password** through **`an HTML form`**

## Process
* -> _xem phần `Process` trong `~\security\SpringSecurity\Authentication_Architecture.md`_

* -> the **`ExceptionTranslationFilter`** initiates Start Authentication use the configured **AuthenticationEntryPoint** - will be **`LoginUrlAuthenticationEntryPoint`**
* -> to **`sends a redirect to the login page`** (_sử dụng default hoặc config thêm để custom /login page_)

* -> the **AbstractAuthenticationProcessingFilter** will be the **`UsernamePasswordAuthenticationFilter`**
* -> the Filter will extract the **username and password** from the **'HttpServletRequest' instance** to creates a **`UsernamePasswordAuthenticationToken`** (_implement **`Authentication`** interface_) 

* -> this 'Authentication' will be passed into **`AuthenticationManager`** instance to be authenticated (_the details of what 'AuthenticationManager' looks like depend on **`how the user information is stored`**_)

## Configuration
* -> by default, Spring Security form login is enabled; however, as soon as any **servlet-based configuration** is provided, **`form based login must be explicitly provided`**

```java - a minimal, explicit Java configuration: (using default login page renders)
public SecurityFilterChain filterChain(HttpSecurity http) {
	http
		.formLogin(withDefaults());
	// ...
}
```

```java - to provide a custom login form
public SecurityFilterChain filterChain(HttpSecurity http) {
	http
		.formLogin(form -> form
			.loginPage("/login")
			.permitAll()
		);
	// ...
}
```
```html - src/main/resources/templates/login.html
<!--  use 'Thymeleaf' template produces an HTML login form that complies with a login page of /login -->
<!-- the form needs to include a "CSRF Token", which is automatically included by 'Thymeleaf' -->
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="https://www.thymeleaf.org">
	<head>
		<title>Please Log In</title>
	</head>
	<body>
		<h1>Please Log In</h1>
		<div th:if="${param.error}">
			Invalid username and password.</div>
		<div th:if="${param.logout}">
			You have been logged out.</div>

        <!-- the form should perform a 'post' to /login. -->
		<form th:action="@{/login}" method="post">
            <!-- the form should specify the username in a parameter named username. -->
            <!-- the form should specify the password in a parameter named password -->
			<div><input type="text" name="username" placeholder="Username"/></div>
			<div><input type="password" name="password" placeholder="Password"/></div>
			<input type="submit" value="Log in" />
		</form>
	</body>
</html>
```
```java - Spring MVC controller maps "GET /login" to the login template
@Controller
class LoginController {
	@GetMapping("/login")
	String login() {
		return "login";
	}
}
```

======================================================================
> use "WWW-Authenticate" header
>  "Basic HTTP Authentication" for servlet-based applications

# Basic Authentication

## Process
* -> _xem phần `Process` trong `~\security\SpringSecurity\Authentication_Architecture.md`_
* -> when a user makes an unauthenticated request to the resource /private for which it is not authorized

* -> the **`ExceptionTranslationFilter`** initiates Start Authentication use the configured **AuthenticationEntryPoint** - will be **`BasicAuthenticationEntryPoint`**
* -> to **`sends a WWW-Authenticate header`** 
* ->  the **RequestCache** is typically a **`NullRequestCache`** that does not save the request since the client is capable of replaying the requests it originally requested

* -> When **a client receives the "WWW-Authenticate header**, it knows it should **`retry with a username and password`**

* ->  When the **user submits their username and password**, 
* -> the **`BasicAuthenticationFilter`** creates a **`UsernamePasswordAuthenticationToken`** (_i **`Authentication`** implementation_) by extracting the username and password from the HttpServletRequest
* -> next, the **`UsernamePasswordAuthenticationToken`** is passed into the **`AuthenticationManager`** to be authenticated

## Configuration
* -> By default, Spring Security’s HTTP Basic Authentication support is enabled; However, as soon as any **servlet based configuration** is provided, **`HTTP Basic must be explicitly provided`**

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
	http
		// ...
		.httpBasic(withDefaults());
	return http.build();
}	
```
