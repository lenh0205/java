
# Dev Enviroment
* -> install JDK 21.0 LTS 
* -> vào `Enviroment Variable / System variables` của window cấu hình, thêm `C:\Program Files\Java\jdk-21\bin` vào **Path**; đồng thời tạo mới **JAVA_HOME** = ``C:\Program Files\Java\jdk-21`

* -> install intellJ IDE của jetbrain và setup với các option `create desktop shortcut`, `Open Folder as project`, `Add "bin" folder to the PATH`, chọn `.java` 

# Setup 1 project
* -> vào **`spring initializr`** (https://start.spring.io/) để khởi tạo 1 project với option **Maven** project, **Java** language, Spring Boot **3.3.5**, **Jar** packaging, Java **21**
* -> giờ chọn dependencies: **Spring Web**, **Spring Data JPA**, **MS SQL SERVER Driver**

* -> sau đó generate nó ra - tải 1 file zip về và extract nó; mở folder bằng intellJ IDE

# Structure

## Entry point
* _src/main/java/com/example/demo/DemoApplication.java_

```java
package com.example.demo;

// import ....

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
```

## properties
* _src/main/resources/application.properties_ để config application and eviroment specific properties

# RESTful API

```java - endpoint response a json
@SpringBootApplication
@RestController // this one
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping // this one
	public List<Student> hello() {
		return List.of(
				new Student(1L, "Mariam", "mariam.jamal@gmail.com", LocalDate.of(2000, Month.JANUARY, 5), 21)
		);
	}
}
```

## Seperate Layer

```java - API Layer
@RestController
@RequestMapping(path = "api/v1/student")
public class StudentController {
    private final StudentService studentService;

    @Autowired // for DI - saying that "StudentService" should be autowired 
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getStudents() {
        return studentService.getStudents();
    }
}
```

```java - Service Layer (business logic)
public class StudentService {
    public List<Student> getStudents() {
        return List.of(
                new Student(1L, "Mariam", "mariam.jamal@gmail.com", LocalDate.of(2000, Month.JANUARY, 5), 21)
        );
    }
}
```

## Connect to SQL Server with Spring Data JPA
* -> by default, Spring Data JPA uses **`Hibernate`**

```xml - pom.xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

```bash - application.properties
spring.datasource.url=jdbc:sqlserver://VIETINFO123;databaseName=customer
spring.datasource.username=username
spring.datasource.password=password

spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect. org.hibernate.dialect.SQLServer2008Dialect
```