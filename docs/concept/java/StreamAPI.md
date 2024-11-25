> Java 8 là việc hỗ trợ functional programming nhờ có Stream API
> Stream API được xây dựng dựa trên một số khái niệm nền tảng hơn, gồm có Functional interface, Lambda expression,....

===================================================================
# Reactive Streams API & 'Spring WebFlux'

* _**Reactive Stream API** định nghĩa 4 interface: **`Publisher`**, **`Subscriber`**, **`Subscription`**, **`Processor`**_

* **Spring Webflux** là một phiên bản song song với "Spring MVC" và hỗ trợ **`non-blocking reactive streams`**
* -> hỗ trợ **back pressure** và sử dụng **`Server Netty`** để run hệ thống reactive

===================================================================
# Functional interface / SAM type (Single abstract method) 
* -> là **`interface`** có duy nhất **`1 abstract method`** và các default/static method nếu cần
* -> mục đích là giúp các method trong java class trở nên linh hoạt như functional programming (_VD: truyền function vào function khác như javascript_)

```java
// Định nghĩa khuôn mẫu cho method truyền vô
@FunctionalInterface
interface Calculable {
    double calculate();
}
...
// Có thể gọi được method trong khuôn mẫu
public void printResult(Calculable func) {
    System.out.println("Result: " + func.calculate());
}
...
// Method thực sự truyền vào, được wrap vô trong khuôn mẫu
printResult(new Calculable() {
    @Override
    public double calculate() {
        return 3.14;
    }
})

```