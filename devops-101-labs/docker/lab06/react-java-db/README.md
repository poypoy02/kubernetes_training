# Compose Sample Application

Estimated time: 30 minutes (plus 30 minutes of waiting for the downloads/builds to complete)

In this lab exercise, we'll create a Docker Compose file and declare three services: `db`, `backend`, and `frontend`.

## DB (PostgreSQL)

1. Create a `compose.yaml` and add a service named `db`.

    ```yaml
    services:
      db: # 👈
        image: postgres:14
        ports:
          - "5432:5432"
        environment:
          - POSTGRES_DB=example
          - POSTGRES_USER=root
          - POSTGRES_PASSWORD=password
        healthcheck:
          test: ["CMD-SHELL", "pg_isready -U $$POSTGRES_USER -d $$POSTGRES_DB"]
          interval: 10s
          retries: 5
          start_period: 30s
        volumes:
          - db-data:/var/lib/postgresql/data
    volumes:
      db-data: {}
    ```

    The above is similar to <code>docker run &hellip; postgres:14</code> with all the given options to publish ports, environment variables, volumes, and more.

2. Run `docker compose up` to get `db` service up and running. This will be used by the `backend`.

    ```console
    $ docker compose up -d db
    ```

## Backend (Spring/Java)

3. Go to `backend` directory. Try running the `backend` locally and connecting to the `db` service that was started in the previous section.

    On \*nix,

    ```console
    $ cd backend
    $ docker run --rm -it \
        -p 8080:8080 \
        -v "$HOME"/.m2:/root/.m2 \
        -v "$PWD":/opt/app \
        -w /opt/app \
        --network react-java-db_default \
        -e SPRING_DATASOURCE_URL=jdbc:postgresql://db/example \
        -e SPRING_DATASOURCE_USERNAME=root \
        -e SPRING_DATASOURCE_PASSWORD=password \
        maven:3.8-eclipse-temurin-17 \
        mvn spring-boot:run
    ```

    On Windows,

    ```console
    $ cd backend
    $ docker run --rm -it ^
        -p 8080:8080 ^
        -v "%USERPROFILE%"\.m2:/root/.m2 ^
        -v "%cd%":/opt/app ^
        -w /opt/app ^
        --network react-java-db_default ^
        -e SPRING_DATASOURCE_URL=jdbc:postgresql://db/example ^
        -e SPRING_DATASOURCE_USERNAME=root ^
        -e SPRING_DATASOURCE_PASSWORD=password ^
        maven:3.8-eclipse-temurin-17 ^
        mvn spring-boot:run
    ```

    Note how the username/password used in the Java/Spring app matches the ones used to run the `db` (PostgreSQL) service.

    Alternatively, if Apache Maven is installed, run the following directly.

    ```console
    $ cd backend
    $ mvn spring-boot:run -Dspring-boot.run.arguments="-Dspring.datasource.username=root -Dspring.datasource.password=password"
    ```

    It will take a few minutes to download the dependencies. Take a break ☕ 😊

    When the `backend` is running and successfully connects to the `db`, run the following to verify:

    ```console
    $ curl localhost:8080
    {"id":1,"name":"Docker"}
    ```

    It should return a JSON response. If the response contains "Not Found 😕", please check that the `db` started properly and try again.

    _What is the `react-java-db_default` network? Note down your answer. You may be asked during the exercise debrief._

    _Hint: Run `docker network ls` to find out._

4. Stop the `backend` with a `Ctrl+C` (or `Cmd+C` on MacOS). Stop the `db`. To stop the `db`, go to the directory where the `compose.yaml` is found. Then run,

    ```console
    $ docker compose down 
    ```

5. Enhance `compose.yaml` with a `backend` service.

    - (1) Add a `backend` name to the `services` top-level element. Underneath it,
        - Add `build` which specifies the build configuration for creating container image from source. Set it to the directory that will be used as the build context (and where the `Dockerfile` can be found).
        - Add `ports`, `environment`, and `healthcheck` (see below)
        - Note how `SPRING_DATASOURCE_URL` (which is translated to `spring.datasource.url`) is set to `"jdbc:postgresql://db/example"`. The host name of the database server is `db`. This is the name of the database service that was declared in the previous section.
    - (2) Under a `networks` (top-level element), add `spring-postgresql`.
        - This will be used by `backend` and `db` to communicate with each other.
        - Comment out the `ports` declaration of the `db` service. Instead of making `db` accessible from the host machine, we'll just let `backend` access it.

    Your Docker Compose file will end up looking something like this (below):

    ```yaml
    services:
      db:
        image: postgres:14
        # ports:
        #   - "5432:5432" # 👈 (2)
        environment:
          - POSTGRES_DB=example
          - POSTGRES_USER=root
          - POSTGRES_PASSWORD=password
        healthcheck:
          # ... (omitted for brevity)
        volumes:
          - db-data:/var/lib/postgresql
        networks:
          - spring-postgresql # 👈 (2)
      backend: # 👈 (1)
        build: backend
        ports:
          - "8080:8080"
        environment:
          SPRING_DATASOURCE_URL: "jdbc:postgresql://db/example"
          SPRING_DATASOURCE_USERNAME: root
          SPRING_DATASOURCE_PASSWORD: password
        healthcheck:
          test: "curl --fail --silent localhost:8080/actuator/health | grep UP || exit 1"
          interval: 10s
          retries: 5
          start_period: 30s
        networks:
          - spring-postgresql # 👈 (2)
        depends_on:
          db:
            condition: service_healthy
    volumes:
      db-data: {}
    networks:
      spring-postgresql: {} # 👈 (2)
    ```

    The `backend` service declaration is similar to <code>docker run &hellip; eclipse-temurin:17-jre java -jar &hellip;</code> with all the given options to publish ports, environment variables, and more.

6. Run `docker compose up` to get `db` and `backend` services up and running. This will be used by the `frontend`.

    ```console
    $ docker compose up -d backend db
    ```

    Alternatively, you can run `backend` (a single service). Because the `depends_on` element of the `backend` service is defined, Docker Compose knows that it needs to start the `db` service too.

    ```console
    $ docker compose up -d backend 
    ```

    It will take a few minutes to build the `backend` image. Take a break ☕ 😊

## Frontend (React.js)

7. Go to `frontend` directory. Try running the `frontend` locally and consuming the `backend` service that was started in the previous section.

    On \*nix,

    ```console
    $ cd frontend
    $ docker run --rm -it \
        -v "$PWD":/usr/src/app \
        -w /usr/src/app \
        node:lts \
        npm install
    $ docker run --rm -it \
        -v "$PWD":/usr/src/app \
        -w /usr/src/app \
        --network react-java-db_spring-postgresql \
        -p 3000:3000 \
        node:lts \
        npm start
    ```

    On Windows,

    ```console
    $ cd frontend
    $ docker run --rm -it ^
        -v "%cd%":/opt/app ^
        -w /opt/app ^
        node:lts ^
        npm install
    $ docker run --rm -it ^
        -v "%cd%":/opt/app ^
        -w /opt/app ^
        --network react-java-db_spring-postgresql ^
        -p 3000:3000 ^
        node:lts ^
        npm start
    ```

    Alternatively, if `npm` (Node Package Manager) is installed, run the following directly. 

    ```console
    $ cd frontend
    $ npm install
    $ npm start 
    ```

    _If running `npm` locally_, you'll need to change `src/setupProxy.js` to point to `localhost` instead of `backend`.

    It will take a few minutes to download the dependencies and start the development server (from `react-scripts`). Take a break ☕ 😊

    When it is up and running, a browser will start and open [http://localhost:3000](http://localhost:3000). It should display "Hello from Docker".

    ![](output.jpg)

    The "Docker" value comes from the `backend`. And the `backend` gets it from the `db`.

    If some other value is displayed (e.g. "Not Found 😕"), please review the steps taken to start `db` and `backend` services.

8. Stop the `frontend` with a `Ctrl+C` (or `Cmd+C` on MacOS). Stop the `backend` and `db` services. To stop `db` and `backend`, go to the directory where the `compose.yaml` is found. Then run,

    ```console
    $ docker compose down 
    ```

9. Enhance `compose.yaml` with a `frontend` service.

    Your Docker Compose file will end up looking something like this (below):

    ```yaml
    services:
      db:
        image: postgres:14
        # ... (no changes)
      backend:
        build: backend
        # ports:
        #  - "8080:8080" # 👈 comment out
        environment:
          # ... (no changes)
        healthcheck:
          # ... (no changes)
        networks:
          - spring-postgresql
          - react-spring # 👈
        secrets:
          # ... (no changes)
        depends_on:
          # ... (no changes)
      frontend: # 👈
        build: frontend
        ports:
          - "3000:80"
        networks:
          - react-spring
        depends_on:
          - backend
    volumes:
      # ... (no changes)
    secrets:
      # ... (no changes)
    networks:
      spring-postgresql: {}
      react-spring: {} # 👈
    ```

## All Together Now (1..., 2..., 1-2-3-4)

10. Run `docker compose up` to get `frontend`, `backend`, and `db` services up and running.

    ```console
    $ docker compose up -d
    ```

    It will take a few minutes to build the `frontend` image. Take a break ☕ 😊

    When the image is built, and the services are up and running, open a browser and go to [http://localhost:3000](http://localhost:3000). It should display "Hello from Docker". The `frontend` gets the "Docker" value from the `backend`. And the `backend` gets it from the `db`.

 
11. When done, stop the services. Go to the directory where the `compose.yaml` is found. Then run,

    ```console
    $ docker compose down 
    ```

## Let's Recap

Here's a recap of what we just did.

1. Declared a service named `db` that is based on a `postgres` image and set the ports, environment variables, and others.
2. Added a service declaration named `backend` that builds an image from `backend/Dockerfile` and set the ports, environment variables, and others.
3. Added a service declaration named `frontend` that builds an image from `frontend/Dockerfile` and set the ports, and others.
4. Started the `db`, `backend`, and `frontend` services via `docker compose up`.

_Was it easier to use Docker Compose to run the services in a single command? (as opposed to running Docker containers individually and trying to remember all those options/settings)_

Congratulations! You've created a Docker Compose file and used it to run several Docker containers. You've completed this lab exercise. 

---

## Use Docker Secret (Bonus)

The `db` and `backend` services share a common value (environment variable) -- a password. It is handy to use a single element to contain this value. In this _optional_ section, a Docker Secret will be used to contain the password. And this secret will be used by `db` and `backend` services.

1. Make sure that `db`, `backend`, and `frontend` services have been stopped.

    Since the `db-data` volume is retained across runs of `postgres` containers, the _old_ password may still be used. This may cause incorrect password errors.

    Remove the volume via `docker volume rm ...db-data` (see `docker volume ls` to know which volume to remove), or do a `docker volume prune` when the services are stopped.

2. Declare a secret named `db-password` (3) that points to `db/password.txt`.

    Your Docker Compose file will end up looking something like this (below):

    ```yaml
    services:
      db:
        image: postgres:14
        # ... (no changes)
        environment:
          - POSTGRES_DB=example
          - POSTGRES_USER=root
          #- POSTGRES_PASSWORD=password # 👈 (3)
          - POSTGRES_PASSWORD_FILE=/run/secrets/db-password # 👈 (3)
        healthcheck:
          # ... (no changes)
        volumes:
          # ... (no changes)
        networks:
          # ... (no changes)
        secrets:
          - db-password # 👈 (3)
      backend:
        build: backend
        ports:
          - "8080:8080"
        environment:
          SPRING_DATASOURCE_URL: "jdbc:postgresql://db/example"
          SPRING_DATASOURCE_USERNAME: root
          # SPRING_DATASOURCE_PASSWORD: password # 👈 (3)
          SPRING_CONFIG_IMPORT: "optional:configtree:/run/secrets/" # 👈 (3)
        healthcheck:
          # ... (no changes)
        networks:
          # ... (no changes)
        secrets:
          # Configtree converts the subdirectory path to a dot-separated configuration property name,
          # so the secret value is readable from configuration property name `spring.datasource.password`.
          - source: db-password
            target: spring/datasource/password # 👈 (3)
        depends_on:
          db:
            condition: service_healthy
      frontend:
        build: frontend
        # ... (no changes)
    volumes:
      # ... (no changes)
    networks:
      # ... (no changes)
    secrets:
      db-password:
        file: db/password.txt # 👈 (3)
    ```

3. After making the changes to the Docker Compose file, create and start the containers again.

    ```console
    $ docker compose up -d
    ```

4. Check that everything is working as before.

5. When done, stop the containers and clean up.

    ```console
    $ docker compose down
    ```
