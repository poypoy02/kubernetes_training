# Build Image from Dockerfile

Estimated time: 30 minutes

## What You Will Learn

In this lab, you'll be building some Docker images.

The `build` command has the following syntax:

```
docker build [OPTIONS] PATH | URL | -
```

For more information, see [`docker build` in the Docker Documentation](https://docs.docker.com/engine/reference/commandline/build/).

## Build Image from Existing Dockerfile

1. Clone the Git repository that contains the code and a Dockerfile that we will use to build an image. Run the following command.

    ```console
    git clone git@github.com:dockersamples/linux_tweet_app.git
    ```

    Alternatively, if you have not setup an SSH key to your GitHub account, or you do not have a GitHub account, you can clone the repository with HTTPS.

    ```console
    git clone https://github.com/dockersamples/linux_tweet_app.git
    ```

    This will create a `linux_tweet_app` directory.

<----->

2. Go into the `linux_tweet_app` directory and study the Dockerfile. Open it using your favorite text editor.

    It should contain something like:

    ```dockerfile
    FROM nginx:latest

    COPY index.html /usr/share/nginx/html
    COPY linux.png /usr/share/nginx/html

    EXPOSE 80 443

    CMD ["nginx", "-g", "daemon off;"]
    ```

    Don't worry if these look alien to you. You'll get to create your own Dockerfile in the next lab exercises.



3. Build a Docker image using the given Dockerfile and name it as `training/linux_tweet_app`. Run the following command.

    ```shell
    # Make sure the command is run inside the
    # linux_tweet_app directory where the Dockerfile can be found
    docker build -t training/linux_tweet_app .
    # Don't forget the dot at the end
    ```

    The `--tag` option (or `-t` for short) of the `build` command gives the image a _name_ and optionally a tag in the 'name:tag' format. In the above command, no tag was used, and only a name was provided.

    The `.` is the path of the context in which the Docker image will be built.


4. After a few seconds or minutes, Docker will complete building a new image named `training/linux_tweet_app`.

    To see what images you have, run the following command.

    ```
    docker images
    ```

    To filter images by name,

    ```
    docker images training/*
    ```

    You should get an output that is similar to this.

    ```
    REPOSITORY                        TAG       IMAGE ID       CREATED              SIZE
    training/linux_tweet_app          latest    e66556b85f14   About a minute ago   142MB
    ```

    Hey, the default tag is `latest`.

5. Run a container from the `training/linux_tweet_app` image.

    ```
    docker run --rm -d -p 8080:80 training/linux_tweet_app
    ```

    Remember the `--rm` and `-d` options?

    What is the `-p` option? It will be discussed in the next few sections. For now, use the above command and options to run the container.

6. Open [http://localhost:8080](http://localhost:8080) in a browser. What do you see?

    Note the difference between the image and the container.

    _What is the image?_ `training/linux_tweet_app`

    _What is the container?_ _Run `docker ps` to find out_

7. When ready, stop the container.

Congratulations! You've run a container from an image that you just built.


## Build a Dockerfile (1)

In this exercise, we'll create our own Dockerfile and use some simple instructions.

1. Create a new directory named `alpine-hello`. In it, create a new file named `Dockerfile`.

2. Open the `Dockerfile` and add the following:

    ```dockerfile
    FROM alpine:latest

    ENTRYPOINT ["echo", "Hello Docker!"]
    ```

    Note the use of the `FROM` and `ENTRYPOINT` instructions.

    _What base image is used here? You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

    Save the changes to the `Dockerfile`.

3. In a terminal, run a command to build the image and name it as `training/alpine-hello`. Don't forget to be in the correct directory (this will be the context of the build).

    ```
    docker build -t training/alpine-hello .
    ```

4. Run a container from the `training/alpine-hello` image. What do you see?

    ```
    docker run --rm training/alpine-hello
    ```


## Build a Dockerfile (2)

Now let's build a more ellaborate Dockerfile. We'll use Docker containers to compile a simple Java program and run it. Don't worry if you don't know Java. The code will be provided. Don't worry if you do not have a JDK installed. We'll be using Docker.

1. Create a new directory named `j-hello`. In it, create sub/directories to have a `src/hello` path. In `src/hello`, create a new file named `Main.java` and add the following lines:

    ```java
    package hello;

    public class Main {
        public static void main(String[] args) {
            System.out.println("Hello Docker from Java!");
        }
    }
    ```

    Rejoice 🙌 if the needed Java file is already there. 😊

2. Compile the Java file by running `javac` from a Docker container. Go to the root of `j-hello` directory and run the following command.

    On \*nix,

    ```
    docker run --rm \
        -v "$PWD"/src:/src \
        -v "$PWD"/target:/target \
        eclipse-temurin:17-jdk javac -d /target /src/hello/Main.java
    ```

    On Windows,

    ```
    docker run --rm ^
        -v "%cd%"\src:/src ^
        -v "%cd%"\target:/target ^
        eclipse-temurin:17-jdk javac -d /target /src/hello/Main.java
    ```

    _What image is used here? You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here."></textarea>

    What is the `-v` option? It will be discussed in the next few sections. For now, use the above command and options to run the container to compile the Java file.

    Did it compile?

    If there are errors, review the `Main.java` file. After making corrections to the file, repeat the step to compile the Java file.

    If it succeeded, a `.class` file would be created under the `target` sub/directory (i.e. `hello/Main.class`).

3. Before creating our `Dockerfile`, let's run the compiled Java class and see if it works.

    On \*nix,

    ```
    docker run --rm -v "$PWD"/target:/target \
        eclipse-temurin:17-jre java -cp /target hello.Main
    ```

    On Windows,

    ```
    docker run --rm -v "%cd%"\target:/target ^
        eclipse-temurin:17-jre java -cp /target hello.Main
    ```

    Did it work?

    _What image is used here? You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here."></textarea>

4. Now, let's create our `Dockerfile` and add the following:

    ```dockerfile
    FROM eclipse-temurin:17-jre

    WORKDIR /app
    COPY ./target .
    ENTRYPOINT ["java", "-cp", ".", "hello.Main"]
    ```

    Note the `FROM`, `WORKDIR`, `COPY`, and `ENTRYPOINT` instructions.

5. Build an image and name it as `training/j-hello`. Do not forget to provide the current directory as the path (which will be used as the context).

    _Write down the complete command you used. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here."></textarea>

6. Run a container from the `training/j-hello` image.

    _Write down the complete command you used. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here. You may be asked during the lab exercise debrief."></textarea>

## (_Optional_) Understanding How Images are Built

Let's try using `docker history` to see how a Docker image is built. Note that it's much better to see the Dockerfile that was used to build it. But when the Dockerfile is not available, the `docker history` command can come in handy.

```
docker history [OPTIONS] IMAGE
```

1. Try running the `history` command on an image to get a glimpse of how it was built.

    ```
    docker history alpine
    ```

    The output would be something like:

    ```
    IMAGE          CREATED        CREATED BY                                      SIZE      COMMENT
    9c6f07244728   . months ago   /bin/sh -c #(nop)  CMD ["/bin/sh"]              0B
    <missing>      . months ago   /bin/sh -c #(nop) ADD file:2a949686d9886ac7c…   5.54MB
    ```

    Notice the `CREATED BY` and `SIZE` columns. Note that _some_ Dockerfile instructions add size to the image, and some do not.

2. Let's try seeing how the `training/j-hello` (from previous exercise) was built.

    _Write down the complete command you used. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here."></textarea> 

A third-party tool that can help with image exploration is [Dive](https://github.com/wagoodman/dive) by Alex Goodman. You can try running Dive using its Docker image.

```
docker pull wagoodman/dive:latest
```

On \*nix,

```
docker run --rm -it \
    -v /var/run/docker.sock:/var/run/docker.sock \
    wagoodman/dive:latest <dive arguments>
```

On Windows,

```
docker run --rm -it ^
    -v /var/run/docker.sock:/var/run/docker.sock ^
    wagoodman/dive:latest <dive arguments>
```

Congratulations! You've completed this lab exercise.
