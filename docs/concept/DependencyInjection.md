================================================================================
# Dependency Injection
* -> spring hỗ trợ 3 kiểu Dependency Injection: **Field-based Injection**, **Constructor-based Injection**, **Setter-based Injection**
* -> ta sẽ sử dụng annotation **`@Autowired`** để báo cho Spring biết tự động tìm và inject bean phù hợp vào vị trí đặt annotation

* -> để đánh dấu một class là 1 "bean" ta dùng **`@Component`**, **`@Service`**, **`@Repository`**, **`@Controller`** (_nói chung là mấy thằng này như nhau chẳng qua là làm rõ hơn về ý nghĩa_) 
* -> hoặc tạo 1 lớp có annotation **`@Configuration`** và định nghĩa các method trả về đối tượng bean rồi đánh dấu bằng **`@Bean`**

```java
@Configuration
public class AppConfig {
    @Bean
    public BookService bookService() {
        return new BookService();
    }
}
```

## Field-based Injection
* -> @Autowired trên field không được khuyến khích, do nó sử dụng Java reflection để inject

```java
public interface IEngine {
    void run();
}

@Component
public class ChinaEngine implements Engine {
    @Override
    public void run() {}
}

@Component
public class Car {

    // "Autowired" báo cho Spring tìm bean nào phù hợp với IEngine interface
    // và có một bean phù hợp là ChinaEngine; nó tương đương với = new ChinaEngine()
    @Autowired
    private final Engine engine;
}
```

## Constructor-based Injection
* -> giúp rõ ràng các phụ thuộc cần phải có 

```java - Car.java
@Component
public class Car {
    private final Engine engine;
    
    // Các bản Spring Boot mới thì không cần @Autowired trên constructor
    @Autowired
    public Car(Engine engine) {
        this.engine = engine;
    }
}
```

## Setter-based Injection
* -> thường ta sẽ sử dụng cách này trong **`phụ thuộc vòng`** (VD: A phụ thuộc B và B phụ thuộc A)
* _nếu cả 2 phụ thuộc đều sử dụng **constructor based injection** thì Spring Boot sẽ không biết nên tạo bean nào trước_
* _giải pháp là 1 bean sẽ dùng constructor, 1 bean sẽ dùng setter_

```java
@Component
public class Car {
    private final Engine engine;
    
    // Thêm @Required để setter luôn được gọi để inject
    // ta cũng có thể dùng @Autowired
    @Required
    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
```

================================================================================
# Sử dụng file config for DI

```java
public class Address {
  private String country;
  private String province;
  private String district;
  public Address() {
  }
  public Address(String country, String province, String district) {
    this.country = country;
    this.province = province;
    this.district = district;
  }
  // ....
}

public class Person {
  private String name;
  private int age;
  private Address address;
  public Person() {
  }
  public Person(String name, int age, Address address) {
    this.name = name;
    this.age = age;
    this.address = address;
  }
}
```

```xml - applicationContext.xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
  <!-- Inject by setter -->
  <bean id="person" class="stackjava.com.springdiobject.demo.Person">
    <property name="name" value="stackjava.com"></property>
    <property name="age" value="25"></property>
    <property name="address" ref="address"></property>
  </bean>
  <!-- Inject by constructor -->
  <bean id="person2" class="stackjava.com.springdiobject.demo.Person">
    <constructor-arg name="name" type="String" value="spring"></constructor-arg>
    <constructor-arg name="age" type="int" value="30"></constructor-arg>
    <constructor-arg name="address" ref="address"></constructor-arg>
  </bean>
  <bean id="address" class="stackjava.com.springdiobject.demo.Address">
    <property name="country" value="Viet Nam"></property>
    <property name="province" value="Ha Noi"></property>
    <property name="district" value="Thanh Xuan"></property>
  </bean>
</beans>
```

```java - entry point
public class MainApp {
  public static void main(String[] args) {
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    Person person= (Person) context.getBean("person");
    person.print();
    Person person1 = (Person) context.getBean("person2");
    person1.print();
  }
}
```

================================================================================
## Case: mutiple bean with same interface
* -> khi đó Spring boot sẽ báo lỗi, ta sẽ cần đánh dấu bean nào cần được ưu tiên bằng **`@Primary`** hoặc chỉ rõ tên bean cần inject bằng **`@Qualifier`**

```java - use "@Primary"
@Component
public class VNEngine implements Engine {
    @Override
    public void run() {}
}
```

```java - use "@Qualifier"
@Component
public class Car {
    @Autowired
    @Qualifier("VNEngine")  // Phải khớp hoa thường luôn nhe
    private final Engine engine;
}
```

# Case: Spring DI với Collections

```java
public class Person {
  private String name;
  private int age;
  private List<Address> addresses;
  private List<String> emails;
  public Person() {
  }
  public Person(String name, int age, List<Address> addresses, List<String> emails) {
    this.name = name;
    this.age = age;
    this.addresses = addresses;
    this.emails = emails;
  }
}
```

```xml - applicationContext.xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
  <!-- Inject by constructor -->
  <bean id="person2" class="stackjava.com.springdicollections.demo.Person">
    <constructor-arg name="name" type="String" value="spring"></constructor-arg>
    <constructor-arg name="age" type="int" value="30"></constructor-arg>
    <constructor-arg name="addresses">
      <list>
        <ref bean="address1" />
        <ref bean="address2" />
      </list>
    </constructor-arg>
    <constructor-arg name="emails">
      <list>
        <value>abc@gmail.com</value>
        <value>def@yahoo.com</value>
      </list>
    </constructor-arg>
  </bean>
  <!-- Inject by setter -->
  <bean id="person" class="stackjava.com.springdicollections.demo.Person">
    <property name="name" value="stackjava.com"></property>
    <property name="age" value="25"></property>
    <property name="addresses">
      <list>
        <ref bean="address2" />
      </list>
    </property>
    <property name="emails">
      <list>
        <value>ghi@hotmail.com</value>
        <value>klm@zzz.com</value>
      </list>
    </property>
  </bean>
  <bean id="address1" class="stackjava.com.springdicollections.demo.Address">
    <property name="country" value="Viet Nam"></property>
    <property name="province" value="Ha Noi"></property>
    <property name="district" value="Thanh Xuan"></property>
  </bean>
  <bean id="address2" class="stackjava.com.springdicollections.demo.Address">
    <property name="country" value="Viet Nam"></property>
    <property name="province" value="Ha Noi"></property>
    <property name="district" value="Ha Dong"></property>
  </bean>
</beans>
```

```java - entry point
public static void main(String[] args) {
  ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
  Person person = (Person) context.getBean("person");
  person.print();
  Person person1 = (Person) context.getBean("person2");
  person1.print();
}
```
