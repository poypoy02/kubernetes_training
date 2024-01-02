# Networking

Estimated time: 30 minutes

## What You Will Learn

In this lab, you'll be running containers that can communicate with each other over a network. You'll get to use the `-p` (or `--publish`) option (of `docker run`) to publish a container's port(s) to the host.


## Use the *Default* `bridge` Network

Start two `alpine` containers running `ash`, which is Alpine's default shell rather than `bash`.

```
docker run --rm --name alpine1 -d -it alpine ash
docker run --rm --name alpine2 -d -it alpine ash
```

Inspect the *default* bridge network to see what containers are connected to it.

```
docker network inspect bridge

```

Near the top, information about the bridge network is listed, including the IP address of the gateway between the Docker host and the bridge network (`172.17.0.1`). Under the `Containers` key, each connected container is listed, along with information about its IP address.

Try to ping the second container (`alpine2`) from the first container (`alpine1`). Ping it by its IP address (which is displayed by `docker network inspect bridge` command):

```
docker attach alpine1
# / ping -c 2 172.17.0.3
PING 172.18.0.3 (172.17.0.3): 56 data bytes
64 bytes from 172.17.0.3: seq=0 ttl=64 time=0.119 ms
64 bytes from 172.17.0.3: seq=1 ttl=64 time=0.114 ms
...
```

Try to ping the second container by its name, `alpine2`:

```
# / ping -c 2 alpine2

ping: bad address 'alpine2'
```

This will fail. 😯 No worries. You'll find out why in the next section.

Clean up. Stop the running containers.


## Use User-Defined `bridge` Networks

On user-defined (non-default) bridge networks, containers can not only communicate by IP address, but can also resolve a container name to an IP address. This capability is called _automatic service discovery_.

Create a network named `alpine-net`.

```
docker network create alpine-net
```

Inspect the `alpine-net` network to see what containers are connected to it.

```
docker network inspect alpine-net
```

Try to ping the second container from the first container. Ping it by its IP address (which is displayed by `docker network inspect alpine-net` command):

```
docker attach alpine1
# / ping -c 2 172.18.0.3
PING 172.18.0.3 (172.18.0.3): 56 data bytes
64 bytes from 172.18.0.3: seq=0 ttl=64 time=0.118 ms
64 bytes from 172.18.0.3: seq=1 ttl=64 time=0.115 ms
...

```

Try to ping the second container by its name, `alpine2`:

```
# / ping -c 2 alpine2
...
```

This time, it succeeds! On user-defined networks like `alpine-net`, containers can not only communicate by IP address, but can also resolve a container _name_ to an IP address. This capability is called _automatic service discovery_.

_When pinging, why were the containers reached via `alpine1` and `alpine2`? You may be asked during the lab exercise debrief._

<textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here."></textarea>

Clean up. Stop the running containers and remove `alpine-net` network.

## Run `postgres` and `pgAdmin4` Containers

This time we'll run `postgres` (database server) and `pgAdmin4` (web-based database admin/client) containers. The database admin/client should be able to connect to the database server.

1. Create a Docker network named `pg-net`. We'll use it when running `postgres` and `pgAdmin4` containers.

    _Write down the complete command you used to create `pg-net` network. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here."></textarea>


2. Start a `postgres` container and use the `pg-net` network.

    On \*nix,

    ```
    docker run --rm -d \
        -e POSTGRES_PASSWORD=password \
        --name pg-db \
        --network pg-net \
        postgres
    ```

    On Windows,

    ```
    docker run --rm -d ^
        -e POSTGRES_PASSWORD=password ^
        --name pg-db ^
        --network pg-net ^
        postgres
    ```

3. Start a `dpage/pgadmin4` container and publish port 80 of the container to port 5050 on the Docker host.

    On \*nix,

    ```
    docker run --rm -d \
        -e PGADMIN_DEFAULT_EMAIL=test@here.com \
        -e PGADMIN_DEFAULT_PASSWORD=secret \
        --network pg-net \
        -p 5050:80 \
        dpage/pgadmin4
    ```

    On Windows,

    ```
    docker run --rm -d ^
        -e PGADMIN_DEFAULT_EMAIL=test@here.com ^
        -e PGADMIN_DEFAULT_PASSWORD=secret ^
        --network pg-net ^
        -p 5050:80 ^
        dpage/pgadmin4
    ```

4. Open [http://localhost:5050](http://localhost:5050) in a browser. Log in to pgAdmin4 with the given email and password.

    Connect to the database server.

    _What is the host name of the database server? You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here."></textarea>

    We published port 80 of the pgAdmin container to port 5050 on the Docker host. This allowed us to access it via a browser running on the host.

    _But why did we not publish port 5432 of the PostgreSQL database server container? You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here."></textarea>

5. Clean up. Stop running containers and remove `pg-net` network.

Congratulations! You've completed this lab exercise.
