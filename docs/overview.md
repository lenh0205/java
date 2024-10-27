
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

	@GetMapping
	public List<String> hello()
	{
		return List.of("Hello", "World");
	}
}
```