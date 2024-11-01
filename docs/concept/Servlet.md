===========================================================================
# Spring
* -> là 1 **java framework** của cung cấp **`inversion of control container`** bao gồm các core: **IOC, DI, bean,...**

# Servlet
* -> là 1 công nghệ (code `Java`) triển khai trên phía server để tạo ra web app 
* -> nó cung cấp các API, interface, class (_Ex: `Servlet, GenericServlet, HttpServlet, ServletRequest, ServletResponse, ...`_)
* => nó cho phép server xử lý các HTTP request từ client, giao tiếp với các DBMS, tương tác với JSP, tạo HTML, ...
* => các java web framework thường được xây dựng dựa trên "Servlet" 

# Spring MVC
* -> là 1 **spring framework** để implement web - sử dụng các tính năng của **`Spring`** và implement **`Servlet`**

# Spring boot core
* -> không implement phần web, mà chỉ hỗ trợ các tính năng config - đọc và chạy các khai báo của file config **`spring.factories`**, khởi tạo beans

# Spring boot web (string-boot-starter-web)
* -> cung cấp các thư viện, config sẵn cho web

# JSP
* -> is **`java in HTML`** - need to be translation to java code; 
* -> usually used as **`View`** (_Servlet as Controller_) in MVC approach

===========================================================================
# Process
* Java Core -> Java Web (JavaEE - JSP & Servlet) -> Java Web Framework (VD: Spring, Struts, JSF,...) -> về Spring thì sẽ có Spring MVC, Spring Data, Spring Security, Spring Boot,…