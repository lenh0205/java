
# UserDetailService
* -> mỗi lần login nó sẽ chạy qua method **loadUserByUsername** của **UserDetailService**
* -> tại đây kiểm tra ta sẽ check được xem có thực sự lấy được thông tin User không để trả về **`UserDetails`** 

# DataSource
* -> kiểm tra xem kết nối của project với database có đúng không
```java
@Autowired
private DataSource dataSource;

try (Connection connection = dataSource.getConnection()) { 
    DatabaseMetaData metaData = connection.getMetaData(); 
    logger.info("Connected to: {}", metaData.getDatabaseProductName()); 
    logger.info("Database URL: {}", metaData.getURL()); 
    logger.info("Database User: {}", metaData.getUserName()); 
    logger.info("Database Driver Name: {}", metaData.getDriverName()); 
    logger.info("Database Driver Version: {}", metaData.getDriverVersion()); 
    logger.info("Database Major Version: {}", metaData.getDatabaseMajorVersion()); 
    logger.info("Database Minor Version: {}", metaData.getDatabaseMinorVersion()); 
    logger.info("Database Max Connections: {}", metaData.getMaxConnections()); 
} catch (SQLException e) { 
    logger.error("Failed to get DataSource connection", e);
}
```

# passwordEncoder
* -> passwordEncoder sẽ được sử dụng cả lúc so sánh password lúc ta đăng nhập và cả lúc so sánh client-secret

* -> cần cấu hình passwordEncoder nếu không nó sẽ báo lỗi:
java.lang.IllegalArgumentException: You have entered a password with no PasswordEncoder. If that is your intent, it should be prefixed with `{noop}`.
        at org.springframework.security.crypto.password.DelegatingPasswordEncoder$UnmappedIdPasswordEncoder.matches(DelegatingPasswordEncoder.java:296) ~[spring-security-crypto-6.3.0.jar:6.3.0]

* -> lý do là vì khi ta Login, **`UsernamePasswordAuthenticationFilter`** extracts the **username** and **password** from the request để tạo **`Authentication`** object
* -> rồi pass **Authentication** cho **AuthenticationManager** - thằng này sẽ delegate task cho **DaoAuthenticationProvider** - thằng này call **`UserDetailsService.loadUserByUsername`** để load UserDetails
* -> thằng UserDetailsService sẽ lấy login username của ta để tìm record trong bảng User của database rồi cấu trúc nên **UserDetails** bao gồm password,....
* -> sau đó **UsernamePasswordAuthenticationFilter** lại lấy login password của ta so sánh với password ta trả về từ **UserDetails**: **`PasswordEncoder.matches(rawPassword, encodedPassword)`**

# "scope" claim of Access Token
* -> defines the permissions the user has granted to the client

* -> ở Authorization server, trong quá trình **client registration**, đối với 1 client cụ thể ta sẽ chỉ định những scopes nó có thể authorize thông qua trường **`scopes`** của **`client`** entity
* -> client gửi authorization request **`/oauth2/authorize`** để authen + yêu cầu những scope nó cần; nhưng những **`requested scopes`** này phải nằm trong **`scopes`** của **`Client`** entity
* -> tiếp theo nó sẽ check xem những **`requested scopes`** có nằm trong trường **`authorities`** của **`AuthorizationConsent`** entity không; nếu chưa có thì sẽ redirect tới trang **consent**
* -> consent xong thì nó sẽ lưu những **`scopes`** mới này vào **AuthorizationConsent**

* -> user sẽ authorize cho registered client ở consent bao gồm standard scope (Ex: openid, profile) và custom scope (Ex: message.read, user.write)
* -> nó là những permission nhưng không phải cái cái nào cũng đến từ **roles** hoặc **authorities** của user

# "authorities" claim of Access Token
* -> typically reflects the user's roles/permissions within the system
* -> this is generally set by your UserDetailsService implementation (**`.roles()`** and **`.authorities()`**) and is based on the user's roles and authorities within the system

