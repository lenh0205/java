=============================================================================
# Overview
* -> _a common practice in Spring Boot is using **`an external configuration`** to **define our properties**_
* -> we can use **`properties files`**, **`YAML files`**, **`environment variables`** and **`command-line arguments`**

* -> should not include both the standard **application.properties** and the **application.yml** files in the same project, as it could lead to unexpected results
* ->  the **`'application.properties' are loaded later`** on and will override the values

=============================================================================
# Usage 

## '@Value' annotation

```java - inject value of property "key.something" via field injection into one of our objects
@Value("${key.something}")
private String injectedProperty;
```

## Environment Abstraction

```java
@Autowired
private Environment env;

public String getSomeKey(){
    return env.getProperty("key.something");
}
```

## '@ConfigurationProperties' annotation
* -> to bind properties to **`a type-safe structured objects`**

```java
@ConfigurationProperties(prefix = "mail")
public class ConfigProperties {
    String name;
    String description;
    // ...
}
```

=============================================================================
## Properties Configuration
* -> by default, Spring Boot can access configurations set in an **`application.properties`** file, which uses **a key-value format**
* -> **each line** is a single configuration, so we need to **`express hierarchical data by using the same prefixes for our keys`** (_in the example, every key belongs to "spring.datasource"_)

```bash
spring.datasource.url=jdbc:h2:dev
spring.datasource.username=SA
spring.datasource.password=password

# use "${}" syntax for an placeholder to the contents of other keys, system properties, or environment variables
app.name=MyApp
app.description=${app.name} is a Spring Boot application

# "List Structure" with array indices to have the same kind of properties with different values
application.servers[0].ip=127.0.0.1
application.servers[0].path=/path1
application.servers[1].ip=127.0.0.2
application.servers[1].path=/path2
application.servers[2].ip=127.0.0.3
application.servers[2].path=/path3

# Multiple Profiles - create multi-document properties file
# -> define multiple document as multiple profile in the same file
# -> a common set of properties at the root level (the "logging.file.name" property will be the same in all profiles)
logging.file.name=myapplication.log
bael.property=defaultValue
#---
spring.config.activate.on-profile=dev
spring.datasource.password=password
spring.datasource.url=jdbc:h2:dev
spring.datasource.username=SA
bael.property=devValue
#---
spring.config.activate.on-profile=prod
spring.datasource.password=password
spring.datasource.url=jdbc:h2:prod
spring.datasource.username=prodUser
bael.property=prodValue

# Profiles Across Multiple Files
# -> As an alternative to having different profiles in the same file, we can store multiple profiles across different files. Prior to version 2.4.0, this was the only method available for properties files.
# -> We achieve this by putting the name of the profile in the file name — for example, application-dev.yml or application-dev.properties.
```

## YAML Configuration
* -> YAML is **`a convenient format for specifying hierarchical configuration data`**

```yml
spring:
    datasource:
        password: password
        url: jdbc:h2:dev
        username: SA

# List Structure
application:
    servers:
    -   ip: '127.0.0.1'
        path: '/path1'
    -   ip: '127.0.0.2'
        path: '/path2'
    -   ip: '127.0.0.3'
        path: '/path3'

#  Multiple Profiles
logging:
  file:
    name: myapplication.log
---
spring:
  config:
    activate:
      on-profile: staging
  datasource:
    password: 'password'
    url: jdbc:h2:staging
    username: SA
bael:
  property: stagingValue
```