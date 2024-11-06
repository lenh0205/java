
# Spring Authorization Server 
* -> is **a framework** that provides **`implementations` of the `OAuth 2.1` and `OpenID Connect 1.0` specifications and other related specifications**
* -> built on top of **`Spring Security`** 
* -> for building **`OpenID Connect 1.0 Identity Providers`** and **`OAuth2 Authorization Server products`**

## Features
* _https://docs.spring.io/spring-authorization-server/reference/overview.html_
* _đọc thêm các RFC trong link trên để hiểu thêm_

### Authorization Grant
* -> Authorization Code, User Consent, Client Credentials, Refresh Token, Device Code, User Consent,Token Exchange

### Token Formats
* -> Self-contained (JWT), Reference (Opaque)

### Client Authentication
* -> client_secret_basic, client_secret_post, client_secret_jwt, private_key_jwt, tls_client_auth, self_signed_tls_client_auth, none (public clients)

### Protocol Endpoints
* -> OAuth2 Authorization Endpoint
* -> OAuth2 Device Authorization Endpoint
* -> OAuth2 Device Verification Endpoint
* -> OAuth2 Token Endpoint
* -> OAuth2 Token Introspection Endpoint
* -> OAuth2 Token Revocation Endpoint
* -> OAuth2 Authorization Server Metadata Endpoint
* -> JWK Set Endpoint
* -> OpenID Connect 1.0 Provider Configuration Endpoint
* -> OpenID Connect 1.0 Logout Endpoint
* -> OpenID Connect 1.0 UserInfo Endpoint
* -> OpenID Connect 1.0 Client Registration Endpoint