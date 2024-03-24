# Person API v1

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


## CRUD endpoins

### List all persons
```bash
    curl -u admin:admin -X GET http://localhost:8080/api/v1/list -H "Accept: application/json"
    
```