====================================================================
# Serialization 
* -> is the process of **`converting an object into a byte stream`**
* _**de-serialization** is the opposite of it_

## 'transient' keyword
* -> is primarily meant for **`ignoring fields during Java object serialization`**
* -> but it also prevents these fields from **being persisted when using a JPA framework**

====================================================================
# '@Transient' annotation in JPA
* -> to **`ignore certain fields when persisting Java objects into database records`** using **an ORM framework that compliant with JPA**
* _it does not affect Java object serialization_