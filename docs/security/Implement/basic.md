> create a client-server application that will fetch data from a REST API (resource server)
> both with require an OAuth authentication (`authenticating the users` and `issuing access tokens`) from a an OAuth authorization server (using `Spring Security OAuth Authorization Server`)

=========================================================================
# Configuration
* -> _xem phần `~\security\SpringAuthorizationServer\basic_setup.md` để setup 1 cách cơ bản_

## application.yml
* _add an entry **127.0.0.1:auth-server** in our **`/etc/hosts`** file_
* -> to allow to run the client and the auth server **on our local machine** and **avoids problems with session cookie overwrites between the two**

```yml
server:
  port: 9000

spring:
  security:
    oauth2:
      authorizationserver:
        # each authorization server needs to have a unique issuer URL
        # set up with a "localhost" alias of "http://auth-server" on port 9000
        issuer: http://auth-server:9000

        # we'll have a single client named "articles-client"
        client:
          articles-client:
            registration:
              client-id: articles-client
              client-secret: "{noop}secret"
              client-name: Articles Client
              client-authentication-methods:
                - client_secret_basic
              authorization-grant-types:
                - authorization_code
                - refresh_token
              redirect-uris:
                - http://127.0.0.1:8080/login/oauth2/code/articles-client-oidc
                - http://127.0.0.1:8080/authorized
              scopes:
                # we'll have the required "OidcScopes.OPENID" and our custom one "articles. read"
                - openid
                - articles.read
```

## Spring Beans configuration

```java
@Configuration
@EnableWebSecurity
public class DefaultSecurityConfig {
    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
        .oidc(withDefaults()); // Enable OpenID Connect 1.0

        // generate a default form login page:
        return http.formLogin(withDefaults()).build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest()
            .authenticated())
        .formLogin(withDefaults());
        return http.build();
    }

    // define a set of example users - use for testing
    // create a repository with just a single admin user
    @Bean
    UserDetailsService users() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails user = User.builder()
        .username("admin")
        .password("password")
        .passwordEncoder(encoder::encode)
        .roles("USER")
        .build();
        return new InMemoryUserDetailsManager(user);
    }
}
```

=========================================================================
# Resource Server
* -> create **a resource server** that will return a list of articles from **a GET endpoint**
* -> the endpoints should **`allow only requests that are authenticated against our OAuth server`**

## Denpendencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.2.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>3.2.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    <version>3.2.2</version>
</dependency>
```

## Configuration

### application.yml 

```yml
server:
  port: 8090 # server port

# security configuration
spring:
  security:
    oauth2:
      resourceserver:
      # required set up the proper URL for our authentication server
      # with the "host" and the "port" we've configured in the ProviderSettings bean
        jwt:
          issuer-uri: http://auth-server:9000
```

## Web security configuration

```java
@Configuration
@EnableWebSecurity
public class ResourceServerConfig {
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/articles/**")
          // explicitly state that every request to article resources should be authorized 
          // have the proper "articles.read" authority
          .authorizeHttpRequests(authorize -> authorize.anyRequest()
            .hasAuthority("SCOPE_articles.read"))
        
          // configure the OAuth server connection based on the application.yml configuration
          .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
```

## Controller

```java - Articles Controller
// create a REST controller that will return a list of articles under the GET /articles endpoint:
@RestController
public class ArticlesController {

    @GetMapping("/articles")
    public String[] getArticles() {
        return new String[] { "Article 1", "Article 2", "Article 3" };
    }
}
```

=========================================================================
# API Client
* -> create **`a REST API client`** that will **fetch the list of articles from the resource server**

## Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.2.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
    <version>3.2.2</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
    <version>3.2.2</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webflux</artifactId>
    <version>6.1.3</version>
</dependency>
<dependency>
    <groupId>io.projectreactor.netty</groupId>
    <artifactId>reactor-netty</artifactId>
    <version>1.1.15</version>
</dependency>
```

## Configuration

```yml - application.yml
server:
  port: 8080

spring:
  security:
    oauth2:
      client:
        registration:
          articles-client-oidc:
            provider: spring
            client-id: articles-client
            client-secret: secret
            authorization-grant-type: authorization_code
            redirect-uri: "http://127.0.0.1:8080/login/oauth2/code/{registrationId}"
            scope: openid
            client-name: articles-client-oidc
          articles-client-authorization-code:
            provider: spring
            client-id: articles-client
            client-secret: secret
            authorization-grant-type: authorization_code
            redirect-uri: "http://127.0.0.1:8080/authorized"
            scope: articles.read
            client-name: articles-client-authorization-code
        provider:
          spring:
            issuer-uri: http://auth-server:9000
```

## Spring Beans configuration

```java
// create a WebClient instance to perform HTTP requests to our resource server
// use the standard implementation with just one addition of the OAuth authorization filter
@Bean
WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
    ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
      new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
    return WebClient.builder()
      .apply(oauth2Client.oauth2Configuration())
      .build();
}

// the WebClient requires an OAuth2AuthorizedClientManager as a dependency
@Bean
OAuth2AuthorizedClientManager authorizedClientManager(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientRepository authorizedClientRepository) {

    OAuth2AuthorizedClientProvider authorizedClientProvider =
      OAuth2AuthorizedClientProviderBuilder.builder()
        .authorizationCode()
        .refreshToken()
        .build();
    DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
      clientRegistrationRepository, authorizedClientRepository);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
}
```

## web security config

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .authorizeHttpRequests(authorizeRequests ->
            // need every request to be authenticated
            authorizeRequests.anyRequest().authenticated()
          )
          .oauth2Login(oauth2Login ->
            // configure the login page URL (defined in .yml config) 
            oauth2Login.loginPage("/oauth2/authorization/articles-client-oidc"))
          // the OAuth client
          .oauth2Client(withDefaults());
        return http.build();
    }
}
```

## Articles Client Controller
* -> create the **data access controller**
* -> We'll use the **``previously configured WebClient`** to **send an HTTP request to our resource server**

```java
@RestController
public class ArticlesController {

    private WebClient webClient;

    @GetMapping(value = "/articles")
    public String[] getArticles(
      // taking the OAuth authorization token from the request in a form of OAuth2AuthorizedClient class
      // automatically bound by Spring using the @RegisterdOAuth2AuthorizedClient annotation with proper identification
      // in our case, it's pulled from identification that we configured previously in the .yml file
      @RegisteredOAuth2AuthorizedClient("articles-client-authorization-code") OAuth2AuthorizedClient authorizedClient) 
    {
        // this authorization token is further passed to the HTTP request
        return this.webClient
          .get()
          .uri("http://127.0.0.1:8090/articles")
          .attributes(oauth2AuthorizedClient(authorizedClient))
          .retrieve()
          .bodyToMono(String[].class)
          .block();
    }
}
```

=========================================================================
# Run
* -> first, go into the browser and try to access the `http://127.0.0.1:8080/articles` page
* -> we'll be automatically redirected to **the OAuth server login page** under `http://auth-server:9000/login` URL
* -> after providing the proper username-password, the authorization server will redirect us back to the requested URL - the list of articles
* -> further requests to the articles endpoint won’t require logging in, as the access token will be stored in a cookie.
