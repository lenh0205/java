> https://www.codejava.net/frameworks/spring/understand-spring-data-jpa-with-simple-example

# 'pom.xml" file - Configure Dependencies in Maven
* -> specify the **`required dependencies`** inside the <dependencies> section
* -> save the file and **`Maven will automatically download all the required JAR files`**

```xml - core of Spring framework with support for Spring Data JPA
<!-- spring 5 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.1.4.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>5.1.4.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-jpa</artifactId>
    <version>2.1.4.RELEASE</version>
</dependency>

<!-- Hibernate framework core ORM -->
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.1.Final</version>
</dependency>

<!-- JDBC driver for MySQL -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.14</version>
</dependency>
```

# Configure Database Connection Properties in persistence.xml
* -> since **Hibernate** is used as **the provider of JPA (Java Persistence API)**, we need to specify the **`database connection properties`** in the **`persistence.xml`** file 
* -> which is created under the **META-INF** directory which is under the **src/main/resources** directory

```xml - persistence.xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
          http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd"
    version="2.1">
     
    <persistence-unit name="TestDB"> <!-- need to used later -->
        <properties>
            <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/testdb" />
            <property name="javax.persistence.jdbc.user" value="root" />
            <property name="javax.persistence.jdbc.password" value="password" />
            <property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver" />
            <property name="hibernate.show_sql" value="true" />
            <property name="hibernate.format_sql" value="true" />
        </properties>
    </persistence-unit>
     
</persistence>
```

# Configure 'EntityManagerFactory' and 'TransactionManager'
* -> we will use **`Java-based configuration with annotations`** for a simple Spring application
* -> create the **`AppConfig`** class with the following code:

```java
package net.codejava.spring;
 
import javax.persistence.EntityManagerFactory;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
 
@Configuration // tells Spring to process this class as the source of configuration
@EnableJpaRepositories(basePackages = {"net.codejava.spring"}) // enable Spring Data JPA in a Spring application
// -> tells Spring to scan for repository classes under the package net.codejava.spring
// -> when a repository class is found, Spring will generate an appropriate proxy class at runtime to provide implementation details
public class AppConfig {
    @Bean
    public LocalEntityManagerFactoryBean entityManagerFactory() {
        // to work with the persistence unit named "TestDB"
        LocalEntityManagerFactoryBean factoryBean = new LocalEntityManagerFactoryBean();
        factoryBean.setPersistenceUnitName("TestDB");
         
        return factoryBean;
    }
     
    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        // sets up a transaction manager for the configured 'EntityManagerFactory'
        // to add transaction capababilities for respositories
        // (can also use @EnableTransactionManagement)
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
         
        return transactionManager;
    }  
}
```

# Code Model Class
```java
package net.codejava.spring;
 
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
 
@Entity // domain model class is mapped to the table (with the same name) in the database
public class Customer {
    @Id // map the field id to the primary key column 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // map the field id to the primary key column 
    private Long id;
    private String firstName;
    private String lastName;
 
    protected Customer() {
    }
 
    @Override
    public String toString() {
        return "Customer [firstName=" + firstName + ", lastName=" + lastName + "]";
    }  
 
    // getters and setters are not shown for brevity
} 
```

# Code Repository Interface
* -> **a repository interface** **`leverages the power of Spring Data JPA`**
* _instead of **writing boilerplate code for a generic DAO class** (as we would normally do with Hibernate/JPA `without Spring Data JPA`), the interface **`extends the CrudRepository`**_

```java
package net.codejava.spring;
 
import java.util.List;
import org.springframework.data.repository.CrudRepository;
 
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    // this will find all "customers" whose "lastname" matches the specified lastName in the method's argument
    List<Customer> findByLastName(String lastName);
}
```

## CrudRepository
* -> **CrudRepository** - which is **`a special interface defined by Spring Data JPA`**
* -> the **type parameter <Customer, Long>** specifies **`the type of the domain model class`** is "Customer" and **`the type of the primary key`** is "Long
* -> defines **`common CRUD operations`** like: count(), delete(T entity), deleteAll(), deleteAll(Iterable<? extends T> entities), deleteById(ID id), existsById(ID id), findAll(), findAllById(Iterable<ID> ids), findById(ID id), save(S entity), saveAll(Iterable<S> entities)

* => _we **don't have to code any implementations** for our "CustomerRepository" interface; **`at runtime, Spring Data JPA generates the implementation class`** that takes care all the details_
* => _in the "CustomerRepository" interface, we can declare findByXXX() methods (XXX is the name of a field in the domain model class), and **`Spring Data JPA will generate the appropriate code`**_

## JpaRepository
* -> **Spring Data JPA** also provides the **`JpaRepository` interface** which **extends the `CrudRepository` interface**  
* -> JpaRepository **`defines methods that are specific to JPA`**

# Code Service Class

```cs - make use of the "CustomerRepository"
package net.codejava.spring;
 
import java.util.List;
import java.util.Optional;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
@Service("customerService") //  Spring framework will create an instance of this class as a managed bean in the application context
public class CustomerService {

    @Autowired
    // this field tell Spring Data JPA to automatically inject an instance of CustomerRepository into this service class
    private CustomerRepository repository;
     
    public void test() {
        // Save a new customer
        Customer newCustomer = new Customer();
        newCustomer.setFirstName("John");
        newCustomer.setLastName("Smith");
         
        repository.save(newCustomer);
         
        // Find a customer by ID
        Optional<Customer> result = repository.findById(1L);
        result.ifPresent(customer -> System.out.println(customer));
         
        // Find customers by last name
        List<Customer> customers = repository.findByLastName("Smith");
        customers.forEach(customer -> System.out.println(customer));
         
        // List all customers
        Iterable<Customer> iterator = repository.findAll();
        iterator.forEach(customer -> System.out.println(customer));
         
        // Count number of customer
        long count = repository.count();
        System.out.println("Number of customers: " + count);
    }
} 
```

# Code Test Program for Spring Data JPA
* -> the program below bootstraps **Spring framework** to **`scan classes in the 'net.codejava.spring' package`**
* _then it gets the CustomerService **bean** and invoke its test() method_

```cs
package net.codejava.spring;
 
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
 
public class CustomerTest {
 
    public static void main(String[] args) {
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        appContext.scan("net.codejava.spring");
        appContext.refresh();
 
        CustomerService customerService = (CustomerService) appContext.getBean("customerService");
        customerService.test();
 
        appContext.close();
    }
 
}
```