
# Logging in Code
```java
private static final Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);
logger.info("----------> password: {}", user.getPassword());
```

# Enable Logging for package

```yml - application.yml
logging:
  level:
    root: INFO
    org.springframework.web: INFO
    org.springframework.security: DEBUG
    org.springframework.security.oauth2: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql: TRACE
    org.springframework.jdbc.core: DEBUG
    org.springframework.jdbc.core.JdbcTemplate: DEBUG
    org.springframework.jdbc.datasource.DataSourceTransactionManager: DEBUG
    com.zaxxer.hikari: DEBUG
```