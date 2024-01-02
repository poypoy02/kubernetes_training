## Compose Sample Application

### React frontend with a Spring backend and a PostgreSQL database

Project structure:
```
.
├── backend
│   ├── Dockerfile
│   ...
├── db
│   └── password.txt
├── compose.yaml
├── frontend
│   ├── ...
│   └── Dockerfile
└── README.md
```

[_compose.yaml_](compose.yaml)
```
services:
  db:
    image: postgres
    ...
  backend:
    build: backend
    ...
  frontend:
    build: frontend
    ports:
    - 3000:80
    ...
```
The compose file defines an application with three services `frontend`, `backend` and `db`.
When deploying the application, docker compose maps port 80 of the frontend service container to port 3000 of the host as specified in the file.  
Make sure port 3000 on the host is not already being in use.


## Deploy with docker compose

```
$ docker compose up -d
Creating network "react-java-db_default" with the default driver
Building backend
Step 1/17 : FROM maven:3.8-eclipse-temurin-17 AS builder
...
Successfully tagged react-java-db_frontend:latest
WARNING: Image for service frontend was built because it did not already exist. To rebuild this image you must use `docker-compose build` or `docker-compose up --build`.
Creating react-java-db_frontend_1 ... done
Creating react-java-db_db_1       ... done
Creating react-java-db_backend_1  ... done
```

## Expected result

Listing containers must show three containers running and the port mapping as below:
```
$ docker ps
ONTAINER ID        IMAGE                       COMMAND                  CREATED             STATUS              PORTS                  NAMES
a63dee74d79e        react-java-db_backend       "java -Djava.securit…"   39 seconds ago      Up 37 seconds                              react-java-db_backend_1
6a7364c0812e        react-java-db_frontend      "docker-entrypoint.s…"   39 seconds ago      Up 33 seconds       0.0.0.0:3000->80/tcp   react-java-db_frontend_1
b176b18fbec4        postgres:14                 "docker-entrypoint.s…"   39 seconds ago      Up 37 seconds       5432/tcp               react-java-db_db_1
```

After the application starts, navigate to `http://localhost:3000` in your web browser to get a colorful message.
![page](./output.jpg)

Stop and remove the containers
```
$ docker compose down
Stopping react-java-db_backend_1  ... done
Stopping react-java-db_frontend_1 ... done
Stopping react-java-db_db_1       ... done
Removing react-java-db_backend_1  ... done
Removing react-java-db_frontend_1 ... done
Removing react-java-db_db_1       ... done
Removing network react-java-db_default
```

This is adapted from [https://github.com/docker/awesome-compose/tree/master/react-java-mysql](https://github.com/docker/awesome-compose/tree/master/react-java-mysql).
