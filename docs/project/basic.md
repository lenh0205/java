
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
