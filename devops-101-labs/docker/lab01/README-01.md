# Run Docker Containers for a Single Task (and exit)

Estimated time: 15 minutes

## What You Will Learn

This lab is part of a series of labs in which you'll be running some Docker containers:

1. **To run a single task** (and exit) (15 minutes)
2. Interactively (15 minutes)
3. In the background (30 minutes plus an optional 15 more minutes)

In this lab, you'll be running some Docker containers to run a single task. You'll also be removing some exited containers.

The `run` command has the following syntax:

```
docker run [OPTIONS] IMAGE [COMMAND] [ARG...]
```

For more information, see [`docker run` in the Docker Documentation](https://docs.docker.com/engine/reference/commandline/run/).

## Run `hello-world`

1. Open a terminal, and try the following command.

    ```
    docker run hello-world
    ```

2. Notice the output.

    ```
    Unable to find image 'hello-world:latest' locally
    latest: Pulling from library/hello-world
    2db29710123e: Pull complete
    Digest: sha256:80f31da1ac7b312ba29d65080fddf797dd76acfb870e677f390d5acba9741b17
    Status: Downloaded newer image for hello-world:latest

    Hello from Docker!
    ...
    ```

    It indicates that the Docker client looked for the `hello-world` image. But did not find one locally. So, it pulled it from a Docker registry (defaults to Docker Hub). Depending on the network and the size of the image, this may take a few seconds to a few minutes.

    If you have already pulled the `hello-world` image before, don't worry. Docker will simply run the existing image as a container and exit.

3. In a terminal, run the following command.

    ```
    docker ps -a
    ```

    The `ps` command shows running containers. The `-a` option (or `--all`) shows _all_ containers (including exited ones). The default shows just _running_ containers.

    The output will look something like:

    ```
    CONTAINER ID   IMAGE         ... STATUS     ... NAMES
    ...            hello-world   ... Exited...  ... goofy_easley
    ```

    Note the difference between the image and the container.

    _What image was used?_ `hello-world`

    _What is the container?_ `goofy_easley` (your generated name will be different)

    Notice that the container is assigned a unique ID and a generated NAME. If we run `hello-world` again, a new container will be created. And running `docker ps -a` will show the two containers created from the `hello-world` image.

    ```
    CONTAINER ID   IMAGE         ... STATUS     ... NAMES
    ...            hello-world   ... Exited...  ... goofy_easley
    ...            hello-world   ... Exited...  ... pensive_pike
    ```

3. Congratulations! You've successfully run containers from the `hello-world` image. It displayed a message and exited.


## Run some commands in a Container from an `alpine` Image

1. In a terminal, run the following command.

    ```
    docker run alpine cat /etc/issue
    ```

    *What just happened?* Docker created a container from the `alpine` image and started it. Upon starting, it ran the `cat` command (reads file/s and writes them to standard output) with the `/etc/issue` as the file argument. After running the command, the container exited. In short, the `cat /etc/issue` was run inside a `sh` (shell) in a running `alpine` container.

    The [official alpine image](https://hub.docker.com/_/alpine) is a Linux container image that uses [Alpine Linux](https://alpinelinux.org/), a lightweight, minimal Linux distribution.

2. What was the output?

    _Paste the output below._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily copy/paste the output here. You may be asked during the lab exercise debrief." spellcheck="false"></textarea>

<!-- PS C:\DevOpsTraining\devops-101-labs> docker run alpine cat /etc/issue
Welcome to Alpine Linux 3.19
Kernel \r on an \m (\l) -->

3. Run the same `cat /etc/issue` command in a container from the `ubuntu` image. The [official ubuntu image](https://hub.docker.com/_/ubuntu) is a Linux container image that uses [Ubuntu], a Linux distribution based on Debian and composed mostly of free and open-source software.

    ```
    docker run ubuntu cat /etc/issue
    ```

    _What image was used this time?_

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here. You may be asked during the lab exercise debrief." spellcheck="false"></textarea>

<!-- PS C:\DevOpsTraining\devops-101-labs> docker run ubuntu cat /etc/issue
Ubuntu 22.04.3 LTS \n \l -->

4. Run the `ls -l` command in an `alpine` container.

    Write down the command you used to run the shell command(s) in an `alpine` container.

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place the complete command here. You may be asked during the lab exercise debrief." spellcheck="false"></textarea>

5. Run a `sh` (shell) command in an `alpine` container and immediately execute the following command:

    ```
    echo Hello from `hostname` ; echo Today is `date`
    ```

    _Write down the command you used to run the shell command(s) in an `alpine` container. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste the command here." spellcheck="false"></textarea>

## Run `figlet` in an `alpine` Container

Now for some fun! Try the following command:

```
docker run alpine sh -c "apk add figlet ; figlet Hello Docker"
```

It should display something like:

```
 _   _      _ _         ____             _
| | | | ___| | | ___   |  _ \  ___   ___| | _____ _ __
| |_| |/ _ \ | |/ _ \  | | | |/ _ \ / __| |/ / _ \ '__|
|  _  |  __/ | | (_) | | |_| | (_) | (__|   <  __/ |
|_| |_|\___|_|_|\___/  |____/ \___/ \___|_|\_\___|_|

```

## Remove Containers by ID or Name

After running some containers, these containers will remain in your host machine. It is a good idea to list the exited containers and remove them.

1. In a terminal, run the following command to list all containers.

    ```
    docker container ls -a
    ```

    The `-a` option (or `--all`) of the `ls` sub-command shows all containers. This includes running and exited containers. The default shows just running containers.

2. Run the following command which is another way to list all containers.

    ```
    docker ps -a
    ```

    The output will look something like:

    ```
    CONTAINER ID   IMAGE           ...   NAMES
    980181135b9b   hello-world     ...   goofy_easley
    ...            hello-world     ...   ...
    36c6c0d7fd86   alpine          ...   competent_swartz
    ...
    ```

3. Now that we can list exited containers, we can use that to remove them.

4. In a terminal, run the following command to remove a container by ID or NAME. Use a container ID or NAME from the output of `docker ps -a` (or similar) command.

    ```
    docker rm <container-ID-or-name>
    ```

    E.g.

    ```
    docker rm 980181135b9b
    ```
    ```
    docker rm competent_swartz
    ```

    You can also use multiple IDs or NAMES in a single `rm` command.

    E.g.

    ```
    docker rm 870181155c9e happy_tesla
    ```

    The `rm` command has the following syntax:

    ```
    docker rm [OPTIONS] CONTAINER [CONTAINER...]
    ```

    For more information, see [`docker rm` in the Docker Documentation](https://docs.docker.com/engine/reference/commandline/rm/).

5. Keep on repeating until all exited containers have been removed. Alternatively, you can use the `prune` subcommand to remove all stopped containers.

    ```
    docker container prune
    ```

## Extras

If you find yourself wanting to remove the container after a single run, you can use the `--rm` option of the `run` command to automatically remove the container when it exits.

For example,

```
docker run --rm hello-world
```

After a `hello-world` container exits, it will be removed. You can double check by running `docker ps -a`. You should not see the exited container anymore.

Congratulations! You've completed this lab exercise.
