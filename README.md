# java-isn-2025

## Онлайн магазин за електроника

Spring Boot приложение за онлайн магазин с PostgreSQL база данни.

### Как да пуснем проекта

1. Трябва да имате PostgreSQL локално и да създадете база:
```sql
CREATE DATABASE electronic_store;
```

2. Стартиране:
```bash
mvn spring-boot:run
```

3. Swagger UI е на: http://localhost:8080/swagger-ui.html

---

## Покритие на изискванията

### ManyToMany връзка
Имаме връзка между **Product** и **Category** - един продукт може да е в няколко категории и една категория има много продукти.

Кодът е в `Product.java`:
```java
@ManyToMany
@JoinTable(name = "product_categories", ...)
private Set<Category> categories;
```

### ManyToOne връзки
- Order -> User (много поръчки към един потребител)
- OrderItem -> Order (много артикули в една поръчка)
- OrderItem -> Product
- Review -> User
- Review -> Product

### Таблици (има 7, минимумът беше 5)
1. users
2. categories
3. products
4. product_categories (join таблица за ManyToMany)
5. orders
6. order_items
7. reviews

Миграциите са в `src/main/resources/db/migration/`

### Разпределение на кода
- `controller/` - REST контролери (5 броя)
- `service/` - бизнес логика (5 броя)
- `repository/` - JPA репозиторита (6 броя)
- `entity/` - ентитита
- `dto/` - DTO класове за request/response
- `mapper/` - MapStruct мапъри
- `exception/` - exception handling

### @Query анотация
В `ProductRepository.java` има няколко custom заявки:

```java
@Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
List<Product> findByPriceRange(...);

@Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
List<Product> findByCategoryId(...);

@Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
Double getAverageRating(...);
```

### Тестове
Има unit тестове в `src/test/java/`:
- `ProductServiceTest.java` - тества ProductService
- `OrderServiceTest.java` - тества OrderService и concurrent purchase логиката
- `ProductControllerTest.java` - интеграционни тестове за контролера

За да пуснете тестовете: `mvn test`

### Swagger
Конфигурацията е в `SwaggerConfig.java`. Всички ендпойнти са документирани с анотации `@Operation` и `@Tag`.

### DTO-та и мапъри
- Request DTO-та: `CreateUserRequest`, `CreateProductRequest`, `CreateOrderRequest` и тн
- Response DTO-та: `UserResponse`, `ProductResponse`, `OrderResponse` и тн
- MapStruct мапъри за конвертиране между ентитита и DTO-та

---

## Бизнес логика - конкурентни покупки

Основната бизнес логика е какво става когато двама клиенти се опитват да купят последния продукт едновременно.

Използваме **optimistic locking** с `@Version` анотация в `Product.java`:

```java
@Version
private Long version;
```

Как работи:
1. Клиент А и Клиент Б четат продукт със stock=1, version=0
2. Клиент А запазва първи -> stock=0, version=1 (успешно)
3. Клиент Б се опитва да запази с version=0, но вече е version=1 -> хвърля се `OptimisticLockException`
4. Връщаме грешка 409 Conflict с подходящо съобщение

Кодът е в `OrderService.java`:
```java
try {
    productRepository.save(product);
} catch (ObjectOptimisticLockingFailureException e) {
    throw new ConcurrentPurchaseException("Product was purchased by another customer");
}
```

---

## API ендпойнти

- `GET/POST /api/users` - потребители
- `GET/POST /api/categories` - категории
- `GET/POST/PUT/DELETE /api/products` - продукти
- `GET/POST /api/orders` - поръчки
- `GET/POST /api/reviews` - ревюта

За повече инфо вижте Swagger UI