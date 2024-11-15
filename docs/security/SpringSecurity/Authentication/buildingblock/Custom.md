
===============================================================
# Publish an 'AuthenticationManager' bean
* -> **`a fairly common requirement`** is **publishing an "AuthenticationManager' bean** to allow for **`custom authentication`**
* -> such as in a **`@Service`** or **`Spring MVC @Controller`**

* -> it is our responsibility to **save the authenticated user** in the **`SecurityContextRepository`** if needed
* _for example, if using the **`HttpSession`** to **persist the 'SecurityContext' between requests**, we can use **`HttpSessionSecurityContextRepository`**_

```java - For example: authenticate users via a "REST API" instead of using "Form Login"
//publish AuthenticationManager bean for Custom Authentication

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/login").permitAll()
				.anyRequest().authenticated()
			);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder
    ) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails userDetails = User.withDefaultPasswordEncoder()
			.username("user")
			.password("password")
			.roles("USER")
			.build();

		return new InMemoryUserDetailsManager(userDetails);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
```

```java - Usage by controller
@RestController
public class LoginController {

	private final AuthenticationManager authenticationManager;

	public LoginController(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/login")
	public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
		Authentication authenticationRequest =
			UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
		Authentication authenticationResponse =
			this.authenticationManager.authenticate(authenticationRequest);
		// ...

	}

	public record LoginRequest(String username, String password) {
	}

}
```

===============================================================
# Customize the 'AuthenticationManager'
* -> normally, "Spring Security" **builds an `AuthenticationManager` internally** composed of a **AuthenticationProvider**
* -> in certain cases, it may still be **desired to customize the instance of 'AuthenticationManager' used by Spring Security**

* -> we can take advantage of the fact that the **`AuthenticationManagerBuilder`** used to **build Spring Security's `global` 'AuthenticationManager'** is published as a **`bean`**
```java - Configure "global" 'AuthenticationManagerBuilder'
// ---------> disable credential erasure for cached users

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// ...
		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		// Return a UserDetailsService that caches users
		// ...
	}

	@Autowired
	public void configure(AuthenticationManagerBuilder builder) {
		builder.eraseCredentials(false); // this
	}
}
```

* -> alternatively, we may **configure a `local` 'AuthenticationManager' to override the `global` one**
```java - Configure "local" 'AuthenticationManager' for Spring Security
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.anyRequest().authenticated()
			)
			.httpBasic(Customizer.withDefaults())
			.formLogin(Customizer.withDefaults())
			.authenticationManager(authenticationManager());

		return http.build();
	}

	private AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());

		ProviderManager providerManager = new ProviderManager(authenticationProvider);
		providerManager.setEraseCredentialsAfterAuthentication(false); // this

		return providerManager;
	}

	private UserDetailsService userDetailsService() {
		UserDetails userDetails = User.withDefaultPasswordEncoder()
			.username("user")
			.password("password")
			.roles("USER")
			.build();

		return new InMemoryUserDetailsManager(userDetails);
	}

	private PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
```

===============================================================
