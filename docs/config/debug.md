> https://gustavopeiretti.com/spring-boot-gradle-debug-console/

# Debug "spring-boot application with Gradle" in intelliJ

* -> chạy task ở debug mode (thường nó sẽ tạo 1 **Listening for transport dt_socket at address: 5005**)
```bash
./gradlew bootRun --debug-jvm
``` 

* -> **`Run -> Edit Configuration -> click "+" button -> choose "Remote JVM Debug" -> điền "name" (tự đặt VD: myapp); "Host" và "Port" lấy theo "dt_socket" ta vừa tạo (ở đây là localhost:5005)`**

* -> Run project của ta ở debug mode: **`Run -> chọn Debug 'myapp'`** 

* -> đặt breakpoint rồi truy cập app là được