> "imperative" và "reactive" application là cái gì ?

# Spring Security
* -> is **a framework**
* -> with **first class support for securing** (_provides **`authentication`**, **`authorization`**, and **`protection`** against common attacks_) 
* -> for both **`imperative` and `reactive` applications**
* -> it is the de-facto standard for securing **`Spring-based applications`**

========================================================================
## Setup project with 'Gradle'
* -> as most open source projects, **Spring Security deploys its dependencies** as **`Maven artifacts`** - which allows for first-class **`Gradle`** support

## Spring Boot with Gradle 
* -> **`add Spring Security to our application's classpath`**

```bash - build.gradle
# manually add the "spring-boot-starter-security" starter
# to aggregates Spring Security related dependencies
dependencies {
	implementation "org.springframework.boot:spring-boot-starter-security"
}

# "spring-boot" provides a "Maven BOM" to manage dependency versions
# if necessary, we can override the "Spring Security" version
ext['spring-security.version']='6.3.4'

# sometime, we also need to update the version of Spring Framework
ext['spring.version']='6.1.14'
```

## Gradle Repository

```bash - gradle
# all GA releases (that is, versions ending in .RELEASE) are deployed to Maven Central
repositories {
	mavenCentral()
}

# if we use a SNAPSHOT version, we need to ensure that we have the Spring Snapshot repository defined:
repositories {
	maven { url 'https://repo.spring.io/snapshot' }
}

# if we use a milestone or release candidate version, we need to ensure that we have the Spring Milestone repository defined:
repositories {
	maven { url 'https://repo.spring.io/milestone' }
}
```

## Run
* -> run **`./gradlew :bootRun`**  to use the Spring Boot plugin to **launch the application directly from the source code** (_without recompiling and repackaging the code to create a separate JAR or WAR file_)
* -> or we can run **`./gradlew build`** to **build entire project** (_creation of a JAR or WAR file containing the compiled code and any necessary dependencies_)

========================================================================
# Add Spring boot Web for illustrating
* _ta sẽ add thêm 2 dependencies **spring-boot-starter-thymeleaf** làm **`template engine`**; và **spring-boot-starter-web** đề **`build web, including RESTful, applications using Spring MVC`**_

```bash
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter'
	implementation 'org.springframework.boot:spring-boot-starter-web' # required
	implementation "org.springframework.boot:spring-boot-starter-security" # required

	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf' 
	implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

```java - src/main/java/com/example/demo/DemoApplication.java
@Controller
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
```

## Run
* -> nếu giờ ta thử run project với **./gradlew :bootRun**; 
* -> rồi truy cập tới 1 endpoint bất kỳ (_VD: http://localhost:8080/some/path_), **Spring Security** will denies access with **`a 401 Unauthorized`** or **`redirect to a default login page`**

* -> trong **Output Console** ta sẽ thấy nó gen cho ta 1 credential mặc định để development
```bash
Using generated security password: e8f5c614-af19-4b3f-9ea2-2a69ef1f7a7e
This generated password is for development use only. Your security configuration must be updated before running your application in production
```
* -> ta có thể đăng nhập trang **/login** hoặc querying endpoint bằng credentials này
```bash
$ curl -i -u user:e8f5c614-af19-4b3f-9ea2-2a69ef1f7a7e http://localhost:8080/some/path
HTTP/1.1 404
```

* _nói chung tại runtime, `Spring Security` sẽ:_
* => requires **`an authenticated user for any endpoint`** (including Boot’s /error endpoint)
* => **`registers a default user`** with a generated password at startup
* => protects **`password storage with BCrypt`** as well as others
* => provides **`form-based login and logout flows`**
* => **authenticates** **`form-based login`** as well as **`HTTP Basic`**
* => provides content negotiation; for **web requests**, **`redirects to the login page`**; for **service requests**, returns **`a 401 Unauthorized`**

* => mitigates **`CSRF attacks`**
* => mitigates **`Session Fixation attacks`**
* => writes **`Strict-Transport-Security`** to ensure HTTPS
* => writes **`X-Content-Type-Options`** to mitigate sniffing attacks
* => writes **`Cache Control headers`** that protect authenticated resources
* => writes **`X-Frame-Options`** to mitigate Clickjacking
* => integrates with **`HttpServletRequest's authentication methods`**
* => publishes **`authentication success and failure events`**

========================================================================
# Spring Boot Security Auto Configuration
* -> _illustrate how **Spring Boot** is coordinating with **Spring Security** to provide those features above_

```java
@EnableWebSecurity // publishes 'Spring Security’s default Filter chain' as a @Bean
// -> this will be automatically registered for every request
// -> because Spring Boot adds any Filter published as a @Bean to the application’s filter chain
@Configuration
public class DefaultSecurityConfig {
    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    // publishes a UserDetailsService @Bean with a username of user and a randomly generated password that is logged to the console
    InMemoryUserDetailsManager inMemoryUserDetailsManager() { 
        String generatedPassword = // ...;
        return new InMemoryUserDetailsManager(User.withUsername("user")
                .password(generatedPassword).roles("USER").build());
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEventPublisher.class)
    // publishes an AuthenticationEventPublisher @Bean for publishing authentication events
    DefaultAuthenticationEventPublisher defaultAuthenticationEventPublisher(ApplicationEventPublisher delegate) { 
        return new DefaultAuthenticationEventPublisher(delegate);
    }
}
```
