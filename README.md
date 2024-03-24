# Person API v1

Main features:

* Spring Boot REST API with standard CRUD operations
* Extra [endpoint](http://127.0.0.1:8080/api/v1/list) with pagination, sorting and filtering support
* BasicAuth with BCrypt password encryption
* Validation for DTO input
* Actuator endpoints with [custom one](http://127.0.0.1:8080/actuator/total-persons)
* JPA based in-memory H2 data store
* Exception and validation handlers with custom API response
* OpenAPI v3 documentation
* Docker image build and run


## Build

### Mvn project + docker images

```bash
./buildDockerImage.sh
```

### Run Docker image

```bash
docker-compose -f docker-compose.yml up -d
```


### Open API Documentation

http://127.0.0.1:8080/swagger-ui/index.html

