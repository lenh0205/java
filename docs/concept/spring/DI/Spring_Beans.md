================================================================================
# Bean
* -> object (dependency) are managed by the **`Spring IoC container`**
* -> để đánh dấu class là 1 "bean" ta sẽ dùng **`@Component`**

* -> **`ApplicationContext`** là nơi chứa các **bean** được **Spring Ioc container** tìm thông qua việc quét toàn bộ các packages
* -> nó là 1 **`BeanFactory`** nhưng tích hợp thêm nhiều tính năng

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

        // lấy ra 1 bean cụ thể
        Car car = context.getBean(Car.class);
    }
}
```

================================================================================
# Bean Scope
* -> **`Singleton`**: chỉ duy nhất một instance của bean sẽ được tạo cho mỗi container; đây là scope mặc định cho spring bean (_khi sử dụng scope này ta cần chắc chắn rằng các bean không có các biến/thuộc tính được share_)
* -> **`Prototype`**: một instance của bean sẽ được tạo cho mỗi lần được yêu cầu(request)
* -> **`Request`**: dùng cho web app, mỗi instance của bean sẽ được tạo cho mỗi HTTP request
* -> **`Session`**: dùng cho web app, mỗi instance của bean sẽ được tạo cho mỗi HTTP Session
* -> **`Global-Session`**: dùng cho web app, được sử dụng để tạo global sesion bean cho các ứng dụng Portlet

```java
@RestController
@Scope("prototype")
public class HomeController {}
```

* -> **`default scope in Spring in singleton`**
* -> _but we can specific it scope like this:_
```java
@Configuration
public class DefaultSecurityConfig {
    @Bean // singleton scope
    public MyService myService() {
        return new MyService();
    }

    @Bean
    @Scope("request")
    public MyScopedService myScopedService() {
        return new MyScopedService();
    }

    @Bean
    @Scope("prototype")
    public MyTransientService myTransientService() {
        return new MyTransientService();
    }
}
```

================================================================================
# Bean Life-Cycle
* -> Spring cho phép ta can thiệp vào vòng đời của "bean" thông qua các **Annotation** đặt lên các method của "bean"

## '@PostConstruct' annotation
* -> method được gọi ngay sau khi bean được tạo và sẵn sàng để inject

```java
@Component
public class InitializationBean {
    @PostConstruct
    public void postConstruct() {
        System.out.println("Bean has been created and is ready to use.");
    }
}
```

## '@PreDestroy' annotation
* -> method được gọi trước khi bean bị hủy hoặc ApplicationContext bị đóng lại

```java
@Component
public class CleanupBean {
    @PreDestroy
    public void preDestroy() {
        System.out.println("Bean is about to be destroyed.");
    }
}
```

## 'InitializingBean' interface
* -> method **`afterPropertiesSet()`** sẽ được gọi sau khi tất cả các phụ thuộc đã được tiêm vào bean

```java
@Component
public class NetworkClient implements InitializingBean {
    public void afterPropertiesSet() {
        // Initialize connection
        System.out.println("Setting up network connections.");
    }
}
```

## 'DisposableBean' interface
* -> method **`destroy()`** sẽ được gọi để làm sạch tài nguyên trước khi bean bị hủy 

```java
@Component
public class ResourceRelease implements DisposableBean {
    public void destroy() {
        // Clean up resources
        System.out.println("Releasing resources.");
    }
}
```
