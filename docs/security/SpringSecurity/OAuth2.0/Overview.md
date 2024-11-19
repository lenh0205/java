
* -> **Spring Security's OAuth 2.0 support** consists of two primary feature sets: **`OAuth2 Resource Server`**, **`OAuth2 Client`**

* -> **`OAuth2 Login`** is a very powerful **OAuth2 Client** feature that deserves its own section in the reference documentation. However, it does not exist as a standalone feature and requires OAuth2 Client in order to function.

* -> **authorization server** role is covered by **`Spring Authorization Server`**, which is a separate project built on Spring Security

# Resource Server
* -> 2 options to protect access to the API using **`OAuth2`** (authorization server provides **`JWT`** or **`opaque access token`**) or **`JWT`** (custom token)


## Opaque token
* -> unlike JWT tokens, do not contain any user information, 
* -> are instead represented by a random, unique string of characters - simply identifiers that are mapped to user information stored on the server
* -> authorization server maintains a database of valid opaque tokens and the user information associated with them

* -> To validate the token send from client and retrieve user information, the server must make a separate call to the authorization server, which issued the opaque token

* -> thằng này có vẻ nhẹ hơn nhưng không tiêu chuẩn như JWT, thành ra là có 1 số ngôn ngữ, framework sẽ không tương thích, có thể gây khó khăn trong scalibility; ngoài ra cần tạo network request mỗi lần get associated user information

# JwtDecoder
* -> JWT support uses a **`JwtDecoder`** bean to validate signatures and decode tokens
* => OAuth2 Resource Server support in Spring Security can be used for any type of Bearer token, including a custom JWT.
* => All that is required to protect an API using JWTs is a JwtDecoder bean, which is used to validate signatures and decode tokens

# 'openid' scope
* -> **the presence of the 'openid' scope** in the above configuration indicates that **`OpenID Connect 1.0 should be used`**
* -> this instructs Spring Security to **`use OIDC-specific components`** (such as **OidcUserService**) during request processing. 
* -> without this scope, Spring Security will **`use OAuth2-specific components`** (such as **DefaultOAuth2UserService**) instead

# ClientRegistration

========================================================================

# Log Users In with OAuth2 - .oauth2Login()
* -> this feature lets an application have users log in to the application by **using their existing account** at an **`OAuth 2.0 Provider (such as GitHub)`** or **`OpenID Connect 1.0 Provider (such as Google)`**
* -> "OAuth 2.0 Login" is implemented by using the **`Authorization Code Grant`**

* -> **`OpenID Connect 1.0`** provides a special token called the **`id_token`** which is designed to provide **`an OAuth2 Client`** with the ability to **perform user identity verification and log users in**
* -> in certain cases, **OAuth2 can be used directly to log users in** - as is the case with **`popular social login providers`** (_that **`do not implement OpenID Connect`**_) such as GitHub and Facebook

* -> ta sẽ cần cấu hình thêm **ClientRegistration** để application support 2 endpoint
* -> **`login endpoint`** (e.g. /oauth2/authorization/my-oidc-client) is used to initiate login and perform a redirect to the third party authorization server
* -> **`redirection endpoint`** (e.g. /login/oauth2/code/my-oidc-client) is used by the authorization server to redirect back to the client application, and will contain a code parameter used to obtain an id_token and/or access_token via the access token request

# Access Protected Resources - .oauth2Client() 
* -> "request to a third party API" that is protected by OAuth2 is a core use case of Spring OAuth2 Client
* _về cơ bản là authorize client và gửi truy cập protected resource bằng a Bearer token in the Authorization header_

* -> vậy nên ta sẽ cần cấu hình app của ta as `an OAuth2 Client`; 
* -> đồng thời app của ta cũng cần require ít nhất 1 **ClientRegistration**

* -> ta cũng sẽ sử dụng **`OAuth2AuthorizedClientManager`** to decide how we will be accessing protected resources và config client sử dụng cách đó
* => Spring Security provides this implementations for **`obtaining access tokens`** that can be **used to access protected resources**

# Access Protected Resources for the Current User - combine 'Log Users In with OAuth2' and 'Access Protected Resources'

```java - Configure OAuth2 Login and OAuth2 Client
// configures the application to act as an OAuth2 Client capable of logging the user in and requesting protected resources from a third party API

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// ...
			.oauth2Login(Customizer.withDefaults())
			.oauth2Client(Customizer.withDefaults());
		return http.build();
	}

}
```

```yml - configure 'InMemoryClientRegistrationRepository' bean
spring:
  security:
    oauth2:
      client:
        registration:
          my-combined-client:
            provider: my-auth-server
            client-id: my-client-id
            client-secret: my-client-secret
            authorization-grant-type: authorization_code
            scope: openid,profile,message.read,message.write
        provider:
          my-auth-server:
            issuer-uri: https://my-auth-server.com
```

========================================================================
# Customize

## OAuth2AuthorizedClientProvider - configuring extension grant type
* -> **any custom `OAuth2AuthorizedClientProvider` bean** will also be picked up and applied to the provided **`OAuth2AuthorizedClientManager`** after **the default grant types**
* _a default OAuth2AuthorizedClientManager will be published automatically by Spring Security when one is not already provided_

## Customize an Existing Grant Type

```java - customize the clock skew of the OAuth2AuthorizedClientProvider for the client_credentials grant
@Configuration
public class SecurityConfig {

	@Bean
	public OAuth2AuthorizedClientProvider clientCredentials() {
		ClientCredentialsOAuth2AuthorizedClientProvider authorizedClientProvider =
				new ClientCredentialsOAuth2AuthorizedClientProvider();
		authorizedClientProvider.setClockSkew(Duration.ofMinutes(5));

		return authorizedClientProvider;
	}

}
```

## Customize the 'RestOperations' used by OAuth2 Client Component
* -> simply **publish a `bean` of type `OAuth2AccessTokenResponseClient<>` with the generic type `OAuth2AuthorizationCodeGrantRequest`** 
* -> and it will be **used by Spring Security to configure OAuth2 Client components**