# Multiple Mongo Spring Boot Starter

Spring Boot application integrates multiple `MongoDB` quickly.

## Quickstart

- Import dependencies

```xml
    <dependency>
        <groupId>com.yookue.springstarter</groupId>
        <artifactId>multiple-mongo-spring-boot-starter</artifactId>
        <version>LATEST</version>
    </dependency>
```

> By default, this starter will auto take effect, you can turn it off by `spring.multiple-mongo.enabled = false`

- Configure Spring Boot `application.yml` with prefix `spring.multiple-mongo`

```yml
spring:
    multiple-mongo:
        primary:
            host: '192.168.0.1'
            database: 'foo1'
        secondary:
            host: '192.168.0.2'
            database: 'foo2'
        tertiary:
            host: '192.168.0.3'
            database: 'foo3'
```

> This starter supports 3 `MongoClientFactory` at most. (Three strikes and you're out)

- **Optional feature**: If you want to use mongodb repositories, locate your repositories under the following packages (take `primary` as an example)

    - Repositories: `**.repository.primary.mongo`

- Configure your beans with the following beans by `@Autowired`/`@Resource` annotation, combined with `@Qualifier` annotation (take `primary` as an example)

| Bean Type               | Qualifier                                          |
|-------------------------|----------------------------------------------------|
| MongoClientFactory      | PrimaryMongoAutoConfiguration.MONGO_CLIENT_FACTORY |
| MongoClient             | PrimaryMongoAutoConfiguration.MONGO_CLIENT         |
| MongoTransactionManager | PrimaryMongoAutoConfiguration.TRANSACTION_MANAGER  |
| MongoTemplate           | PrimaryMongoAutoConfiguration.MONGO_TEMPLATE       |
| GridFsTemplate          | PrimaryMongoAutoConfiguration.GRID_FS_TEMPLATE     |
| ReactiveMongoTemplate   | PrimaryMongoReactiveConfiguration.MONGO_TEMPLATE   |
| ReactiveGridFsTemplate  | PrimaryMongoReactiveConfiguration.GRID_FS_TEMPLATE |

## Document

- Github: https://github.com/yookue/multiple-mongo-spring-boot-starter
- Mongodb: https://mongodb.com

## Requirement

- jdk 1.8+

## License

This project is under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

See the `NOTICE.txt` file for required notices and attributions.

## Donation

You like this package? Then [donate to Yookue](https://yookue.com/public/donate) to support the development.

## Website

- Yookue: https://yookue.com
