# Run Docker Containers in the Background

Estimated time: 30 minutes (plus 15 minutes for the MySQL part)

## What You Will Learn

This lab is part of a series of labs in which you'll be running some Docker containers:

1. To run a single task (and exit) (15 minutes)
2. Interactively (15 minutes)
3. **In the background** (30 minutes plus an optional 15 more minutes)

In this lab, you'll be running some Docker containers in the background, and then interacting with them (by running commands in them).

The `run` command has the following syntax:

```
docker run [OPTIONS] IMAGE [COMMAND] [ARG...]
```

For more information, see [`docker run` in the Docker Documentation](https://docs.docker.com/engine/reference/commandline/run/).

The `exec` command has the following syntax:

```
docker exec [OPTIONS] CONTAINER COMMAND [ARG...]
```

For more information, see [`docker exec` in the Docker Documentation](https://docs.docker.com/engine/reference/commandline/exec/).

## Run a PostgreSQL Database

1. In previous labs, when running containers, a randomly generated name was assigned. It is sometimes useful to give it a pre-determined name. This given name can be used when working with a container that is running in the background.

    To run a container with a given name, use the `--name` option.

    ```
    docker run --name <name> [OTHER_OPTIONS] [IMAGE] [COMMAND] [ARG...]
    ```

2. Run a `postgres` container *in the background* and give it a name `pg-testdb`.

    Use the `--detach` option (or `-d` for short) of the `run` command to run a container in detached mode (or in the background).

    ```
    docker run -d --name pg-testdb -e POSTGRES_PASSWORD=password postgres
    ```

    *What just happened?* After running a `postgres` container, you were able to go back to your terminal prompt. This is because the container is now running in the background. This is what the `-d` option does.

    *What is the `-e` option?* The `-e` option is used to set environment variables when running containers. For now, we'll simply use it because the image requires it to start properly. Environment variables will be explained further in subsequent sections.

    Remember the password. It will be used when connecting via command-line client.

## Create Tables in the PostgreSQL Database

1. Now let's run something _inside_ the `postgres` container that is running in the background.

    Use the `exec` command to run a command in a _running_ container.

    In a terminal, run the following command.

    ```
    docker exec -it pg-testdb psql -U postgres -W
    ```

    Enter the password when prompted.

    Remember the `-it` option? It is to make it _interactive_.

    Where did `pg-testdb` come from?

    The `exec` command expects a container (identified by ID or NAME). In a previous step, we started a `postgres` container and gave it a name. Now, we use that name to identify the container to be used with the `exec` command.

    `psql` is the command (and arguments include `-U postgres -W`) that is executed in the running container (with name `pg-testdb`).

    *Where did `psql` come from?*

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily write down your answer here. You may be asked during the lab exercise debrief." spellcheck="false"></textarea>


2. In the `psql` prompt (`postgres=#`), run a SQL command to test if it is working. In the `psql` prompt, enter the following SQL command.

    ```
    SELECT 1;
    ```

    The above command should succeed. Let's list the tables (if any).

    ```
    \dt
    ```

    Looks like we'll need to create some more tables. After exiting `psql`, we will come back through a shell and install SQL scripts that will create some tables.

    Exit `psql` now.

    ```
    \q
    ```

3. Verify that you're back in the host terminal.

4. Let's create a sample database. We'll use the [Sakila sample database](https://dev.mysql.com/doc/sakila/en/sakila-introduction.html).

    Run a [`bash` (shell)](https://en.wikipedia.org/wiki/Bash_(Unix_shell)) inside the `pg-testdb` container (_still running in the background_).

    _What command did you use to run bash inside pg-testdb?_

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily write the command you used here. You may be asked during the lab exercise debrief." spellcheck="false"></textarea>

    From a `bash` (shell) inside the `pg-testdb` container, run the following commands to:

    1. Install `git`

        ```
        git --version
        # ok, if not found
        apt update
        apt install -y git
        git --version
        ```

    2. Clone a repository containing DDL scripts

        ```
        cd /usr
        git clone https://github.com/jOOQ/sakila.git
        cd sakila/postgres-sakila-db
        ```

    3. Run DDL scripts via `psql`

        ```
        psql -U postgres
        ```

        Inside the `psql` prompt, enter the password, and run the following commands:

        ```
        CREATE DATABASE sakila;
        \c sakila;

        _What other SQL queries did you try?_

        <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Write down other SQL queries here. You may be asked during the lab exercise debrief."></textarea>

            \i postgres-sakila-insert-data.sql
        ```

        Creating the data may take a few minutes.

    4. Run some SQL queries on newly created tables

        SELECT first_name, last_name FROM actor;

        ```

    5. Exit `psql` when ready.

        ```
        \q
        ```

5. Exit `bash` when ready.

    ```
    exit
    ```

6. Verify that `pg-testdb` is still running.

    ```
    docker ps
    ```

7. When ready, stop the `pg-testdb` container.

    ```
    docker stop pg-testdb
    ```

Congratulations! You've completed this lab exercise.


---

## Run a MySQL Database *(Optional)*

This is basically a _repeat_ of the same steps, but applied to a `mysql` database. This time around, the needed commands will not be provided. You'll have to figure out what commands are needed.

1. Run a `mysql` container *in the background* and give it a name `mysql-testdb`.

    Go to [Docker Hub](https://hub.docker.com) and search for the `mysql` image. See what environment variables may be needed to start/run it. Use the `-e` option.

## Create Tables in the MySQL Database

1. Run `mysql` (command-line client) inside the running `mysql-testdb` container.

2. Create a sample database. Use the Sakila sample database.

    Run a `bash` (shell) inside the `mysql-testdb` container (still running in the background).

    From a `bash` (shell) inside the `mysql-testdb` container, run the following commands to:

    1. Install `git`
    2. Clone a repository containing DDL scripts
        - https://github.com/jOOQ/sakila.git
        - Relevant subfolder is `mysql-sakila-db`
    3. Run DDL scripts via `mysql` (command-line client)

        Inside the `mysql` prompt, run the following commands:

        ```
        CREATE DATABASE sakila;
        USE sakila;
        source mysql-sakila-schema.sql
        source mysql-sakila-insert-data.sql
        ```

        Creating the data may take a few minutes.

    4. Run some SQL queries on newly created tables

        ```
        SELECT first_name, last_name FROM actor;
        SELECT title, release_year FROM film;
        ```

    5. Exit `mysql` (command-line client) when ready.

        ```
        \q
        ```

    6. Exit `bash` when ready.

5. Verify that `mysql-testdb` is still running.

6. When ready, stop the `mysql-testdb` container.


