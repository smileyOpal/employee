# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Swagger](http://localhost:8080/swagger-ui.html)

## Compile project

    ./mvnw clean compile
    
## Testing

    ./mvnw test
    
### Packaging as jar

    ./mvnw package
    
### docker build

    docker build . --tag assignment
    
### docker run

    docker run --publish 8080:8080 assignment
    
