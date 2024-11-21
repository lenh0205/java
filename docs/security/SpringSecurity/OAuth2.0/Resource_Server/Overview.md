
# OAuth 2.0 Resource Server JWT
* _to have a working `resource server` that supports `JWT-encoded Bearer Tokens` require 2 package:_
* -> most **Resource Server** support is collected into **`spring-security-oauth2-resource-server`**
* -> the support for **decoding and verifying JWTs** is in **`spring-security-oauth2-jose`**

## Minimal Configuration for JWTs
* -> first, include the **`needed dependencies`**
* -> second, indicate the **`location of the authorization server`** (_using **application.yml** file_)




