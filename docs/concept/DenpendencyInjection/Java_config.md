>  Spring provides **``Java config`** approach for **dependency injection**, which can eliminate the drawbacks of **`XML`** and **`annotations`**
> ta sẽ không cần phải viết từa lưa annotation
> cũng như có thể navigate thoải mái giữa các Bean (không như XML)


# Java Configuration
* ->  write **`a separate Java configuration class`** to **configure the dependency injection** by **`instantiating the objects directly`**

```java
@Configuration // this class will be processed by 'Spring IoC container' to generate bean instances based on '@Bean' methods
public class AppConfig1 {
     
    @Bean("client1") // this method will returns a bean to be managed by the application context
    // -> the bean is an instance of the 'Client' interface and has name "client1"
    public Client getClient1(Service service1) {
        // the "ClientImpl" constructor will need the beans from "Service" class 
        return new ClientImpl(service1);
    }
     
    @Bean // use method name as bean name
    public Service client2() {
        return new ServiceImpl1();
    }

    @Bean({"service", "srv", "SRV"}) // a bean can have multiple names
    public Service service3() {
        return new ServiceImpl3();
    }
}


// define
public interface Client {
    void doSomething();
}

public class ClientImpl implements Client {
 
    private Service service;
     
    public ClientImpl(Service b) {
        this.service = b;
    }
 
    @Override
    public void doSomething() {
        String info = service.getInfo();
        System.out.println(info);
    }
}
```

## Test DI of Java config

```java
public class SpringDependencyInjectionExample {
 
    public static void main(String[] args) {
        ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig1.class, AppConfig2.class);
         
        Client client1 = (Client) appContext.getBean("client1");
        client1.doSomething();
         
        Client client2 = (Client) appContext.getBean("client2");
        client2.doSomething();
    }
}
```