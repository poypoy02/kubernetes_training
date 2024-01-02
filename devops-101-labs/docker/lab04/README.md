# Working with Container Storage

Estimated time: 45 minutes

## What You Will Learn

In this lab, you'll be sharing data (via files) among running containers (and the host machine). You'll get acquainted with the Dockerfile `VOLUME` instruction, and get to use the `-v` (or `--volume`) option (of <code>docker&nbsp;run</code>) to use volumes and/or bind mounts when running containers.

The `-v` or `--volume` option consists of three fields, separated by colon characters (`:`). The fields must be in the correct order, and the meaning of each field is not immediately obvious.

```
-v [host-src:]container-dest[:<options>]
```

- The first field (`host-src`) is an absolute path or a name value on a given host machine. For anonymous volumes, the first field is omitted.
- The second field (`container-dest`) is the path where the file or directory are mounted in the container.
- The third field is optional, and is a comma-separated list of options, such as `ro`.

For more information, go to [VOLUME (shared filesystems)](https://docs.docker.com/engine/reference/run/#volume-shared-filesystems) in [Docker run reference](https://docs.docker.com/engine/reference/run/), and [using volumes](https://docs.docker.com/storage/volumes/).


## Trying out Bind Mounts

In a previous exercise, we used a Docker container to compile a simple Java program. Let's revisit that right now.

1. Do you still remember the commands used to compile the Java program?

    Here's to _help refresh_ your memory. ☝️ No need to run them though. Just refreshing your memory.

    On \*nix,

    <pre>
    docker run --rm \
        <b>-v "$PWD"/src:/src</b> \
        <b>-v "$PWD"/target:/target</b> \
        eclipse-temurin:17-jdk javac -d /target /src/...</pre>

    On Windows,

    <pre>
    docker run --rm ^
        <b>-v "%cd%"\src:/src</b> ^
        <b>-v "%cd%"\target:/target</b> ^
        eclipse-temurin:17-jdk javac -d /target /src/...</pre>

    Note the `-v` option (or `--volume`) bind mounts:
    * the container's `/src` to the host's `src` in the _current directory_,
    * and the container's `/target` to the host's `target` in the _current directory_.

    This means that when the container accesses its `/src` or `/target`, it ends up accessing the host's `src` and `target` directories in the current directory (e.g. `/j-hello`).

2. Go back to that directory (`j-hello`). From there, run the following command to run a container from the `alpine` image and open an interactive shell.

    On \*nix,

    ```
    docker run --rm -it -v "$PWD"/src:/src alpine sh
    ```

    On Windows,

    ```
    docker run --rm -it -v "%cd%"\src:/src alpine sh
    ```

    _Note the `-v` option (or `--volume`) used here. It is bind mounting host's `j-hello/src` to the container's `/src`._

    In the shell, list the files and look at the directories (use `ls -l`).

    Do you see a `/src` folder? Go into that folder and list the files in it.

    In `/src/hello`, there should be a `Main.java` file. Display the contents of the file by running the command:

    ```
    cat Main.java
    ```

    Don't exit just yet. You'll come back to this in a bit.

PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker run --rm -it -v $pwd\src:/src alpine sh
/ # ls -l
total 56
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 bin
drwxr-xr-x    5 root     root           360 Dec 18 06:59 dev
drwxr-xr-x    1 root     root          4096 Dec 18 06:59 etc
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 home
drwxr-xr-x    7 root     root          4096 Dec  7 09:43 lib
drwxr-xr-x    5 root     root          4096 Dec  7 09:43 media
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 mnt
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 opt
dr-xr-xr-x  371 root     root             0 Dec 18 06:59 proc
drwx------    1 root     root          4096 Dec 18 07:00 root
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 run
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 sbin
drwxrwxrwx    1 root     root          4096 Dec 18 06:58 src
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 srv
dr-xr-xr-x   11 root     root             0 Dec 18 06:59 sys
drwxrwxrwt    2 root     root          4096 Dec  7 09:43 tmp
drwxr-xr-x    7 root     root          4096 Dec  7 09:43 usr
drwxr-xr-x   12 root     root          4096 Dec  7 09:43 var
/ # cd /src/hello
/src/hello # ls -l
total 0
-rwxrwxrwx    1 root     root           329 Dec 18 00:59 Main.java
/src/hello # cat Main.java
package hello;

public class Main {
        public static void main(String[] args) {
                System.out.println("Hello Docker from Java!");

                /*
                String name = System.getenv("HELLO_NAME");
                if (name == null || name.trim().isEmpty()) {
                        name = "Docker";
                }
                System.out.println("Hello " + name + " from Java!");
                */
        }
}
/src/hello #

3. Back in the _host_ (not the container), open the `Main.java` (under a `j-hello/src/hello` directory) and modify its contents. Add a comment. Save your changes.

    ```java
    package hello;
    // Johnny was here!

    public class Main {
        public static void main(String[] args) {
            String name = System.getenv("HELLO_NAME");
            if (name == null || name.trim().isEmpty()) {
                name = "Docker";
            }
            System.out.println("Hello " + name + " from Java!");
        }
    }
    ```
/src/hello # cat Main.java
package hello;

public class Main {
        public static void main(String[] args) {
                System.out.println("Hello Docker from Java!");

                /*
                String name = System.getenv("HELLO_NAME");
                if (name == null || name.trim().isEmpty()) {
                        name = "Docker";
                }
                System.out.println("Hello " + name + " from Java!");
                */
        }
}
/src/hello # cat Main.java
package hello;
    // Epoy was here!

public class Main {
        public static void main(String[] args) {
                System.out.println("Hello Docker from Java!");

                /*
                String name = System.getenv("HELLO_NAME");
                if (name == null || name.trim().isEmpty()) {
                        name = "Docker";
                }
                System.out.println("Hello " + name + " from Java!");
                */
        }
}
/src/hello #

4. Back in the shell of the running container (from an `alpine` image), display the contents of the `Main.java` file again. Did you see the same changes you made on the host?

    This is what the `-v` (or `--volume`) option of the `run` command does. It bind mounted the host's `j-hello/src` directory as the container's `/src` directory.

5. Optionally, you can modify the `Main.java` file from the running container.

    You can append some text to it by running the following command.

    ```
    echo "// Paul was here too" >> Main.java
    ```

    Or, you can use `vi` and make changes.

    After saving your changes, you should be able to see the changes from the host too (open it using your favorite text editor).


/src/hello # echo "// I Love You Danielle" >> Main.java
/src/hello # cat Main.java
package hello;
    // Epoy was here!

public class Main {
        public static void main(String[] args) {
                System.out.println("Hello Docker from Java!");

                /*
                String name = System.getenv("HELLO_NAME");
                if (name == null || name.trim().isEmpty()) {
                        name = "Docker";
                }
                System.out.println("Hello " + name + " from Java!");
                */
        }
}
// I Love You Danielle
/src/hello #

6. When ready, exit the shell of the running container.

7. Recall the commands used to compile the Java program (via a JDK container). 

    _Why do you think both `src` and `target` directories were bind mounted? You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here."></textarea>


## Trying out Volumes (or Volume Mounts)

When running a database in a container, does it retain the data after it is stopped and restarted? We'll find out in this exercise.

1. Start a database by running a container (from the `postgres` image) in the background, and naming it as `coffee-db`. Connect to this database using `psql` (a command-line client).

    _Write down the complete command(s) you used. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

    Hint: Two commands are needed here. One to run the container in the background. Another to execute `psql` in the running container.

    You can refer to previous exercises on how to run a `postgres` container.

PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker run -d -e POSTGRES_PASSWORD=password --name coffee-db -it postgres
97009f4a2cf6512aaef2d22320fe8c49fe7452985746d6d784475da7bc188ab6
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker exec -it coffee-db psql -U postgres -W

2. Using `psql`, create a new empty database named `coffee_shop`.

    ```
    CREATE DATABASE coffee_shop;
    \l
    ```

    The `\l` command will list the databases. The newly created database should be listed.

psql (16.1 (Debian 16.1-1.pgdg120+1))
Type "help" for help.

postgres=# CREATE DATABASE coffee_shop;
CREATE DATABASE
postgres=# \l
                                                       List of databases
    Name     |  Owner   | Encoding | Locale Provider |  Collate   |   Ctype    | ICU Locale | ICU Rules |   Access privileges
-------------+----------+----------+-----------------+------------+------------+------------+-----------+-----------------------
 coffee_shop | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           |
 postgres    | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           |
 template0   | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           | =c/postgres          +
             |          |          |                 |            |            |            |           | postgres=CTc/postgres
 template1   | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           | =c/postgres          +
             |          |          |                 |            |            |            |           | postgres=CTc/postgres
(4 rows)

postgres=#

3. Exit from `psql`, and stop 🛑 the database container.

    ```
    docker stop coffee-db
    ```

4. Start 🟢 the database container again.

    ```
    docker start coffee-db
    ```

5. Connect to the database using `psql`. See if the `coffee_shop` database is still there.

    ```
    \l
    ```

PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker start coffee-db
coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker ps
CONTAINER ID   IMAGE      COMMAND                  CREATED              STATUS         PORTS      NAMES
57de44ac5f3b   postgres   "docker-entrypoint.s…"   About a minute ago   Up 2 seconds   5432/tcp   coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker exec -it coffee-db psql -U postgres -W
Password: 
psql (16.1 (Debian 16.1-1.pgdg120+1))
Type "help" for help.

postgres=# \l
                                                       List of databases
    Name     |  Owner   | Encoding | Locale Provider |  Collate   |   Ctype    | ICU Locale | ICU Rules |   Access privileges
-------------+----------+----------+-----------------+------------+------------+------------+-----------+-----------------------
 coffee_shop | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           |
 postgres    | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           |
 template0   | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           | =c/postgres          +
             |          |          |                 |            |            |            |           | postgres=CTc/postgres
 template1   | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           | =c/postgres          +
             |          |          |                 |            |            |            |           | postgres=CTc/postgres
(4 rows)

postgres=#

6. Do you see the `coffee_shop` database? 

    That's because the `postgres` image contains a `VOLUME /var/lib/postgresql/data` instruction (see [here](https://github.com/docker-library/postgres/blob/271cf940d0b8e212d16309271d49a8fdd4f48978/14/alpine/Dockerfile)). This allows it to persist data (in between runs).

    When the container started (for the first time), Docker could not find the `/var/lib/postgresql/data` volume for the container, so it created one (anonymous volume) and mounted it. When the container stopped, the volume remained. When the container restarted, Docker detected that the `/var/lib/postgresql/data` volume for the container already exists, and mounted it. The database read from the _same_ volume, and retained whatever data was persisted before.

    Note that this works because the _same_ container (`coffee-db`) is restarted. If it were another container, then it would persist data on a different anonymous volume.

7. When ready, stop and remove the `coffee-db` container.

    ```
    docker container rm coffee-db
    ```

PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker stop coffee-db
coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker container rm coffee-db
coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> 

8. Since volumes are not removed when the container stops or is removed, it is good housekeeping to remove unused volumes. Volumes are only removed when you explicitly remove them.

    ```
    docker volume ls
    docker volume rm <volume-id>
    ```

PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker volume ls
DRIVER    VOLUME NAME
local     5bf4dd533ca9bf3219db459b3ef961a73d153dcb3642546d78792dd8fcf795fd
local     081044ccd56f3be03320fbaa19c7a88968af60195fb1b03518732e95b6de7bc9
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker volume rm 5bf4dd533ca9bf3219db459b3ef961a73d153dcb3642546d78792dd8fcf795fd
5bf4dd533ca9bf3219db459b3ef961a73d153dcb3642546d78792dd8fcf795fd

9. In the previous steps, a `postgres` container was started and used an _anonymous_ volume. This time, we'll explicitly create a volume and have the `postgres` container use it.

    Create a volume named `coffee-db-data`.

    ```
    docker volume create coffee-db-data
    ```

    Start a database by running a container (from the `postgres` image) in the background, naming it as `coffee-db`, and making it use `coffee-db-data`.

    On \*nix,

    ```
    docker run -d --name coffee-db \
        -e POSTGRES_PASSWORD=secret \
        -v coffee-db-data:/var/lib/postgresql/data \
        postgres
    ```

    On Windows,

    ```
    docker run -d --name coffee-db ^
        -e POSTGRES_PASSWORD=secret ^
        -v coffee-db-data:/var/lib/postgresql/data ^
        postgres
    ```

PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker run -d --name coffee-db -e POSTGRES_PASSWORD=secret -v coffee-db-data:/var/lib/postgresql/data postgres
a872419fb5dfbb885436721d5633baf288bb98b5802fe0558bb7924cd0e334ee
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker ps
CONTAINER ID   IMAGE      COMMAND                  CREATED         STATUS         PORTS      NAMES
a872419fb5df   postgres   "docker-entrypoint.s…"   4 seconds ago   Up 3 seconds   5432/tcp   coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> 

    Connect to this database using `psql` (a command-line client). Repeat steps 2 to 5. They're repeated below for your convenience.

    Using `psql`, create a new empty database named `coffee_shop`.

    ```
    CREATE DATABASE coffee_shop;
    \l
    ```

    The `\l` command will list the databases. The newly created database should be listed.


PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker exec -it coffee-db psql -U postgres -W
Password: 
psql (16.1 (Debian 16.1-1.pgdg120+1))
Type "help" for help.

postgres=# CREATE DATABASE coffee_shop;
CREATE DATABASE
postgres=# \l
                                                       List of databases
    Name     |  Owner   | Encoding | Locale Provider |  Collate   |   Ctype    | ICU Locale | ICU Rules |   Access privileges
-------------+----------+----------+-----------------+------------+------------+------------+-----------+-----------------------
 coffee_shop | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           |
 postgres    | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           |
 template0   | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           | =c/postgres          +
             |          |          |                 |            |            |            |           | postgres=CTc/postgres
 template1   | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           | =c/postgres          +
             |          |          |                 |            |            |            |           | postgres=CTc/postgres
(4 rows)

postgres=#

    Exit from `psql`, and stop 🛑 the database container.

    ```
    docker stop coffee-db
    ```

    Start 🟢 the database container again.

    ```
    docker start coffee-db
    ```

    Connect to the database using `psql`. See if the `coffee_shop` database is still there.

    ```
    \l
    ```

    You should still see the `coffee_shop` database.

PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker ps
CONTAINER ID   IMAGE      COMMAND                  CREATED              STATUS              PORTS      NAMES
a872419fb5df   postgres   "docker-entrypoint.s…"   About a minute ago   Up About a minute   5432/tcp   coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker stop coffee-db
coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker start coffee-db
coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker exec -it coffee-db psql -U postgres -W
Password: 
psql (16.1 (Debian 16.1-1.pgdg120+1))
Type "help" for help.

postgres=# \l
                                                       List of databases
    Name     |  Owner   | Encoding | Locale Provider |  Collate   |   Ctype    | ICU Locale | ICU Rules |   Access privileges
-------------+----------+----------+-----------------+------------+------------+------------+-----------+-----------------------
 coffee_shop | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           |
 postgres    | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           |
 template0   | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           | =c/postgres          +
             |          |          |                 |            |            |            |           | postgres=CTc/postgres
 template1   | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           | =c/postgres          +
             |          |          |                 |            |            |            |           | postgres=CTc/postgres
(4 rows)

postgres=#

10. When ready, stop and remove the `coffee-db` container.

    ```
    docker stop coffee-db
    docker container rm coffee-db
    ```

PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker stop coffee-db
coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker container rm coffee-db
coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> 

11. Since volumes are not removed when the container is removed, let's see if the `coffee_shop` database still exists in the `coffee-db-data` volume.

    Start a database by running a container (from the `postgres` image) in the background, naming it as `coffee-db` again, and making it use `coffee-db-data`.

    _How did you make the container use the `coffee-db-data` volume?_

    Connect to the database using `psql`. See if the `coffee_shop` database is still there.

    ```
    \l
    ```

    You should still see the `coffee_shop` database.

PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker run -d --name coffee-db -e POSTGRES_PASSWORD=secret -v coffee-db-data:/var/lib/postgresql/data postgres
cb0f18e7197f66eedf4cb8b9ff6a933a9712002d8b044233bf2f005d40ad7c4f
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker exec -it coffee-db psql -U postgres -W
Password: 
psql (16.1 (Debian 16.1-1.pgdg120+1))
Type "help" for help.

postgres=# \l
                                                       List of databases
    Name     |  Owner   | Encoding | Locale Provider |  Collate   |   Ctype    | ICU Locale | ICU Rules |   Access privileges
-------------+----------+----------+-----------------+------------+------------+------------+-----------+-----------------------
 coffee_shop | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           |
 postgres    | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           |
 template0   | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           | =c/postgres          +
             |          |          |                 |            |            |            |           | postgres=CTc/postgres
 template1   | postgres | UTF8     | libc            | en_US.utf8 | en_US.utf8 |            |           | =c/postgres          +
             |          |          |                 |            |            |            |           | postgres=CTc/postgres
(4 rows)

postgres=#

12. When ready, stop and remove the `coffee-db` container. Since volumes are not removed when the container stops or is removed, it is good housekeeping to remove unused volumes. Volumes are only removed when you explicitly remove them.

    ```
    docker volume ls
    docker volume rm coffee-db-data
    ```

    Alternatively, to remove all unused volumes and free up space:

    ```
    docker volume prune
    ```

PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker ps  
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker rm coffee-db
coffee-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker volume ls
DRIVER    VOLUME NAME
local     081044ccd56f3be03320fbaa19c7a88968af60195fb1b03518732e95b6de7bc9
local     coffee-db-data
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> docker volume rm coffee-db-data
coffee-db-data
PS C:\DevOpsTraining\devops-101-labs\docker\lab03\j-hello> 

## Database Initialization with Scripts

The `postgres` Docker image supports _additional initialization_ by adding one or more \*.sql scripts under its `/docker-entrypoint-initdb.d` directory. These initialization files will be executed in sorted name order as defined by the current locale, which defaults to `en_US.utf8`. Any \*.sql files will be executed by `POSTGRES_USER`, which defaults to the `postgres` superuser. See [postgres - Official Image](https://hub.docker.com/_/postgres) on [Docker Hub](https://hub.docker.com) for more information.

We'll give it a try in this exercise.

1. In the `sql` directory, there are several \*.sql files that would initialize the database. Run a `postgres` container _and_ provide a `/docker-entrypoint-initdb.d` directory with the \*.sql files in the `sql` directory?

    _Write down the complete command(s) you used. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

    Hint: Use the `-v` option to bind mount the host's `sql` directory to the container's `/docker-entrypoint-initdb.d` directory. 

PS C:\DevOpsTraining\devops-101-labs\docker\lab04> docker run -d --name test-pg -e POSTGRES_PASSWORD=secret -v $pwd/sql:/docker-entrypoint-initdb.d postgres
af053926fbfc2b4cb3c31a4dd00bc3046fa8b6ffe918523766d41fd5af413b7f
PS C:\DevOpsTraining\devops-101-labs\docker\lab04> 

2. To verify that the database is indeed initialized, run `psql` in the container and list all the tables using the `\dt` command.

    _Write down some of the tables you see. You may be asked during the lab exercise debrief._

    <textarea rows="5" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

PS C:\DevOpsTraining\devops-101-labs\docker\lab04> docker exec -it test-pg psql -U postgres -W
Password:
psql (16.1 (Debian 16.1-1.pgdg120+1))
Type "help" for help.

postgres=# \dt
              List of relations
 Schema |      Name       | Type  |  Owner
--------+-----------------+-------+----------
 public | order           | table | postgres
 public | product_catalog | table | postgres
 public | user            | table | postgres
(3 rows)

3. When done, stop the container and clean up.

**Warning:** Scripts in `/docker-entrypoint-initdb.d` are only run if you start the container with a data directory that is empty; any pre-existing database will be left untouched on container startup. One common problem is that if one of your `/docker-entrypoint-initdb.d` scripts fails (which will cause the entrypoint script to exit) and your orchestrator restarts the container with the already initialized data directory, it will not continue on with your scripts.

PS C:\DevOpsTraining\devops-101-labs\docker\lab04> docker stop test-pg
test-pg
PS C:\DevOpsTraining\devops-101-labs\docker\lab04> docker container rm test-pg
test-pg
PS C:\DevOpsTraining\devops-101-labs\docker\lab04> docker ps -a

## Serve Web Pages

In this lab exercise, we'll be running a container (from the `nginx` image) and serve HTML pages from our host machine.

If you have not cloned the Git repository yet, run the following command.

```
git clone git@github.com:dockersamples/linux_tweet_app.git
```

Alternatively, if you have not setup an SSH key to your GitHub account, or you do not have a GitHub account, you can clone the repository with HTTPS.

```
git clone https://github.com/dockersamples/linux_tweet_app.git
```

This will create a `linux_tweet_app` directory.

1. Go into the `linux_tweet_app` directory. Make a subdirectory named `html`. Run a container (from the `nginx` image) in the background and bind mount the container's `/usr/share/nginx/html` to the host's `linux_tweet_app/html`.

    On \*nix,

    ```
    docker run --rm --name web-81 -d -p 8081:80 \
        -v "$PWD"/html:/usr/share/nginx/html ...
    ```

    On Windows,

    ```
    docker run --rm --name web-81 -d -p 8081:80 ^
        -v "%cd%"\html:/usr/share/nginx/html ...
    ```

PS C:\DevOpsTraining\devops-101-labs\docker\lab04\linux_tweet_app> docker run --rm --name web-81 -d -p 8081:80 -v $pwd\html:/usr/share/nginx/html nginx
Unable to find image 'nginx:latest' locally
latest: Pulling from library/nginx
1f7ce2fa46ab: Already exists
9b16c94bb686: Already exists
9a59d19f9c5b: Already exists 
9ea27b074f71: Already exists
c6edf33e2524: Already exists
84b1ff10387b: Already exists
517357831967: Already exists
Digest: sha256:10d1f5b58f74683ad34eb29287e07dab1e90f10af243f151bb50aa5dbb4d62ee
Status: Downloaded newer image for nginx:latest
9b91ac9ccea561e5ba2cde96fd8b34216dd02c2cee0f411373afa5b57024e6a1
PS C:\DevOpsTraining\devops-101-labs\docker\lab04\linux_tweet_app> 

2. Open [http://localhost:8081](http://localhost:8081) in a browser. It is now serving the files found under `/linux_tweet_app/html`. Since there is no `index.html` file in that folder, and `nginx` forbids to show a directory listing, a 403 page is displayed. We'll add some HTML files soon.

3. Copy `index.html` and `linux.png` files to the `html` subdirectory. Then refresh the browser. What do you see?

4. Run another container in the background and bind mounted to the same (host) volume, but exposed on a different port.

    On \*nix,

    ```
    docker run --rm --name web-82 -d -p 8082:80 \
        -v "$PWD"/html:/usr/share/nginx/html ...
    ```

    On Windows,

    ```
    docker run --rm --name web-82 -d -p 8082:80 ^
        -v "%cd%"\html:/usr/share/nginx/html ...
    ```

5. Open [http://localhost:8082](http://localhost:8082) in a browser. What do you see? Is the same page displayed?

6. After seeing a <span style="display: inline-block; padding: 0 0.5em; background-color: blue; color: white"><em>blue</em> page</span>, we'll change the web page. Copy `index-new.html` as `html/index.html` (to overwrite `html/index.html`). Then refresh the browsers ([http://localhost:8081](http://localhost:8081) and [http://localhost:8082](http://localhost:8082)). What do you see?

7. After seeing an <span style="display: inline-block; padding: 0 0.5em; background-color: darkorange; color: white"><em>orange</em> page</span>, we'll change the web page again. Copy `index-original.html` as `html/index.html` (to overwrite `html/index.html`). Then refresh the browsers. What do you see?

    _How come when the contents of `linux_tweet_app/html` folder (on the host) changed, the pages served by both containers also changed? You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here."></textarea>

8. When ready, stop the running containers.

*What just happened?* The two containers (`web-81` and `web-82`) served HTML files from its own `/usr/share/nginx/html`. In this case, the volume in the two containers were bind mounted to the same location on the host machine. Thus, changing the contents of the `linux_tweet_app/html` folder (on the host) changed the pages served by both containers.



Congratulations! You've completed this lab exercise.
