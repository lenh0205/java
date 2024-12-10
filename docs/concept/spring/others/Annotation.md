
# '@Import' and '@ComponentScan'
* -> the **`@Import`** annotation is using to group **Configuration** classes

```java
// -> Assume we have 3 configurations for Bird, Cat, Dog

// -> using @Import to group their config
@Configuration
@Import({ DogConfig.class, CatConfig.class })
class MammalConfiguration {
}

// -> apply
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { MammalConfiguration.class, BirdConfig.class })
class ConfigUnitTest {
}
// equivalent to
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { BirdConfig.class, CatConfig.class, DogConfig.class })
class ConfigUnitTest {
}
```