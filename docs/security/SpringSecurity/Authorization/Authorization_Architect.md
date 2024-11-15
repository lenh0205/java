===================================================================
# Authorities

## GrantedAuthority
* -> all **`Authentication`** implementations store a **list of `GrantedAuthority` objects**
* -> these represent **`the authorities that have been granted to the principal`**
* -> the **GrantedAuthority** objects are inserted into the **Authentication** object by the **`AuthenticationManager`** and are later read by **`AccessDecisionManager`** instances when **`making authorization decisions`**

## String presentation of 'GrantedAuthority'
This method is used by an AuthorizationManager instance to obtain a precise String representation of the GrantedAuthority. By returning a representation as a String, a GrantedAuthority can be easily "read" by most AuthorizationManager implementations. If a GrantedAuthority cannot be precisely represented as a String, the GrantedAuthority is considered "complex" and getAuthority() must return null.

## Complex 'GrantedAuthority'
An example of a complex GrantedAuthority would be an implementation that stores a list of operations and authority thresholds that apply to different customer account numbers. Representing this complex GrantedAuthority as a String would be quite difficult. As a result, the getAuthority() method should return null. This indicates to any AuthorizationManager that it needs to support the specific GrantedAuthority implementation to understand its contents.

## SimpleGrantedAuthority
Spring Security includes one concrete GrantedAuthority implementation: SimpleGrantedAuthority. This implementation lets any user-specified String be converted into a GrantedAuthority. All AuthenticationProvider instances included with the security architecture use SimpleGrantedAuthority to populate the Authentication object.

## GrantedAuthorityDefaults
By default, role-based authorization rules include ROLE_ as a prefix. This means that if there is an authorization rule that requires a security context to have a role of "USER", Spring Security will by default look for a GrantedAuthority#getAuthority that returns "ROLE_USER".

You can customize this with GrantedAuthorityDefaults. GrantedAuthorityDefaults exists to allow customizing the prefix to use for role-based authorization rules.

You can configure the authorization rules to use a different prefix by exposing a GrantedAuthorityDefaults bean, like so:
you expose GrantedAuthorityDefaults using a static method to ensure that Spring publishes it before it initializes Spring Security’s method security @Configuration classes
```java - Custom MethodSecurityExpressionHandler
@Bean
static GrantedAuthorityDefaults grantedAuthorityDefaults() {
	return new GrantedAuthorityDefaults("MYPREFIX_");
}
```

===================================================================
# Invocation Handling
* -> Spring Security provides interceptors that control access to secure objects, such as method invocations or web requests. 
* -> a **`pre-invocation`** decision on whether the invocation is allowed to proceed is made by AuthorizationManager instances
* -> also **`post-invocation`** decisions on whether a given value may be returned is made by AuthorizationManager instances

## AuthorizationManager

## Delegate-based AuthorizationManager Implementations

## AuthorityAuthorizationManager

## AuthenticatedAuthorizationManager

## AuthorizationManagers

## Custom Authorization Managers