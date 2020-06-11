# This is a java project for the SG/IAM interview process

This project can be launched using docker-compose 

```
docker build . -t carbon/sgiam_kata #to build the docker image for the api
docker-compose up -d #to launch the stack
docker-compose down #to shutdown
```

The swagger-ui is available at : https://localhost:8443/v1/swagger-ui.html