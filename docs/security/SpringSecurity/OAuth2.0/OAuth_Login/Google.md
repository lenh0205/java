* https://docs.spring.io/spring-security/reference/6.3/servlet/oauth2/login/core.html
* https://developers.google.com/identity/openid-connect/openid-connect
* https://developers.google.com/oauthplayground/
* https://developers.google.com/identity/gsi/web/guides/overview
* https://developers.google.com/identity/protocols/oauth2
* https://openid.net/developers/how-connect-works/
* https://openid.net/certification/

========================================================================
# Using OAuth 2.0 to Access Google APIs

## Google API Console
* -> Before your application can use Google's OAuth 2.0 authentication system for user login, we must set up a project in the Google API Console 

## Setup
* -> ta sẽ cần khởi tạo 1 project trong Google Cloud
* -> ta sẽ cần tạo 1 OAuth 2.0 credentials, nhưng khi tạo nó sẽ required ta **`customize user consent screen`** trước
* -> sau đó tạo 1 credentials dạng **`OAuth client ID`**, nhớ thêm **`Authorized redirect URIs`** 

## Concept
* -> **`OAuth 2.0 credentials`**: a **client ID** and **client secret**, to authenticate users and gain access to Google's APIs

* -> **`a consent screen`** that describes **the information that the user is releasing and the terms that apply**
* _For example, when the user logs in, they might be asked to give your app access to their email address and basic account information_
* -> we request access to this information using the **`scope`** parameter, which our app includes in its **authentication request** (ví dụ ta sẽ cấu hình cho phép truy cập scope: email, drive, user profile, ...)

========================================================================
# Authenticating the user
* -> using **`server flow`** and **`implicit flow`**

