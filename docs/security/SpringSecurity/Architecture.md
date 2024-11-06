https://docs.spring.io/spring-security/reference/servlet/architecture.html
===========================================================================
# Architecture
* -> **`Spring Security's high-level architecture`** within **Servlet based applications**
* => the **Authentication**, **Authorization**, and **Protection Against Exploits** is built base on this understanding

* -> **Spring Security’s Servlet support** is based on **`Servlet Filters`**

===========================================================================
# A Review of Filters

```r - the typical layering of the handlers for a single HTTP request
Client <-> Filter Chain (Filter1 <-> Filter2 <-> ... <-> FilterN <-> Servlet) 
```