===================================================================
# Thymeleaf
* -> là một **`Java template engine`** dùng để xử lý và tạo **HTML, XML, Javascript, CSS và text**

## Setup
* -> cài 2 thư viện
```bash
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
```

* -> khi tạo các trang thymeleaf html phải được đặt trong **`resources/templates/`** và thêm dòng này
```html
<html xmlns:th="http://www.thymeleaf.org">
```

* -> có thể tạo file **`index.html`**, nó sẽ được tự động lấy làm trang mặc định khi start trang web ở "/" nếu chưa định nghĩa các action 

* -> để tạo các trang khác, tạo 1 lớp Controller ngay bên dưới **`java/com/example/demo`** (_để chổ khác sẽ lỗi_)
* -> lý do là vì những package cũng như các class để chạy được (DI) phải đặt dưới cùng 1 **namespace** của class chứa entry point để chạy project

```java
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentController {
    @GetMapping("/new")
    public String getStudents() {
        return "text";
    }

    @GetMapping("/person")
    public String getPerson(Model model) {
        model.addAttribute("something", "this is person");
        return "people";
    }
}
```
```html - people.html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <title>Welcome</title>
</head>
<body>
    <h1 th:text="${something}"/>
</body>
</html>
```

* -> chạy vào "http://localhost:8080/person" để xem kết quả

===================================================================
# 'spring-webflux' & 'reactor-netty'
* -> **`spring-webflux`**: chứa các thành phần cho reactive của spring
* -> **`reactor-netty`** - vì spring không thực sự cung cấp cơ chế xử lý reactive nên chúng ta sẽ cần bổ sung thư viện này

* _https://techmaster.vn/posts/37979/tim-hieu-ve-spring-core-bai-9-spring-webflux_