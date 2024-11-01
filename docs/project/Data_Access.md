==============================================================================
# Connect to SQL Server with Spring Data JPA
* -> by default, Spring Data JPA uses **`Hibernate`**

==============================================================================
# Config

## Libraries

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

## Setting
```bash - application.properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=JavaTesting;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=xxxxxx

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

## Enviroment
* -> ở đây ta sử dụng server host là localhost, ta sẽ cần biết database instance của ta là gì bằng cách sử dụng **`Sql Server Configuration Manager`** (_C:\Windows\SysWOW64\SQLServerManager16.msc_)
* -> ta mở phần **`SQL Server Network Configuration`**, ta sẽ thấy instance mặc định là **MSSQLSERVER** ta sẽ cần **`enable TCP/IP`** đồng thời xem port nó đang chạy (mặc định là **1433**)

* -> giờ ta vào **SQL Server Management Studio 20** để kết nối thử Database Engine của instance đó, tên Database Engine có thể không phải là "MSSQLSERVER" vì lúc setup SQL server ta có thể đã cho nó 1 tên khác
* -> sau khi kết nối, ta sẽ cần tạo tài khoản **sa** để login
* -> ta right-click chọn **`properties`** -> vào phần **security** chọn **`SQL Server and Windows Authentication mode`**
* -> giờ vào **Security/Logins/sa** -> đổi mật khẩu -> rồi vào phần **Status**, chọn **`Grant`** và **`Enable`** Login

## Runing
* -> h ta reload project rồi chạy lại thử xem nó có báo lỗi connect database không là được

==============================================================================
# Entities
* -> thư viện **`jakarta.persistence`** sẽ giúp ta trong trường hợp chuyển từ **Hibernate** sang provider khác mọi thứ vẫn hoạt động bình thường
* -> sau khi cấu hình mapping cho Entities, nếu ta chạy lại dự án nó sẽ tự động tạo table đó trong database

```java - Student.java
@Entity
@Table
public class Student
{
    @Id
    @SequenceGenerator(
        name = "student_sequence",
        sequenceName = "student_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "student_sequence"
    )
    private Long id;
    private String name;
    private String email;
    private LocalDate dob;
    private Integer age;

    public Student() {
    }
    public Student(Long id, String name, String email, LocalDate dob, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.age = age;
    }
    public Student(String name, String email, LocalDate dob, Integer age) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", dob=" + dob +
                ", age=" + age +
                '}';
    }
}
```

==============================================================================
# Seed Data
* -> nếu ta chạy project thì sẽ có 2 record trong database, và nếu request đến endpoint "getStudents" thì nó sẽ trả về JSON gồm list 2 object

```java - StudentConfig.java
@Configuration
public class StudentConfig {
    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository) {
        return args -> {
            Student mariam = new Student("Mariam", "mariam.jamal@gmail.com", LocalDate.of(2000, Month.JANUARY, 5), 21);
            Student alex = new Student("Alex", "alex@gmail.com", LocalDate.of(2004, Month.JANUARY, 5), 21);

            repository.saveAll(List.of(mariam, alex));
        };
    }
}
```

==============================================================================
# Task: ta không muốn lưu trường "Age" trong database mà ta muốn nó tự tính dựa trên "dob"
* -> ta sẽ sử dụng **`@Transient`** annotation
* -> sau khi chạy ta sẽ thấy table của ta sẽ không có cột "age"

```java
@Entity
@Table
public class Student
{
    @Id
    @SequenceGenerator(
            name = "student_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_sequence"
    )
    private Long id;
    private String name;
    private String email;
    private LocalDate dob;

    @Transient // no need to be a column in database
    private Integer age;

    // .....

    public Integer getAge() {
        return Period.between(this.dob, LocalDate.now()).getYears();
    }
}
```
