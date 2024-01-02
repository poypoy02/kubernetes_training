# Working with the Container Registry

Estimated time: 30 minutes

## What You Will Learn

In this lab, you'll get acquainted with the [Docker Hub](https://hub.docker.com/), and get to use the `login` and `logout` commands to `push` and `pull` images to and from container registries.

For more information, see [`docker login`](https://docs.docker.com/engine/reference/commandline/login/), and [`docker logout`](https://docs.docker.com/engine/reference/commandline/logout/).

## Pull Image from Docker Hub (a container registry)

In previous exercises, you have already pulled several images from the Docker Hub. This is because, by default, `docker pull` pulls images from Docker Hub.

The `pull` command has the following syntax:

```
docker pull [OPTIONS] NAME[:TAG|@DIGEST]
```

1. Use `docker pull` to pull an image that contains the JDK.

    ```
    docker pull eclipse-temurin:17-jdk
    ```

    When pulling, the output would be something like:

    ```
    17-jdk: Pulling from library/eclipse-temurin
    ...
    Digest: sha256:...
    Status: ...
    docker.io/library/eclipse-temurin:17-jdk  👈
    ```

    This is equivalent to running:

    ```
    docker pull docker.io/library/eclipse-temurin:17-jdk
    ```

    Try running the above command. Notice the path in the image name: `docker.io/library/`.

2. Check the version of the JDK in the image by running `java -version`.

    ```
    docker run --rm eclipse-temurin:17-jdk java -version
    ```

    This is equivalent to running:

    ```
    docker run --rm docker.io/library/eclipse-temurin:17-jdk java -version
    ```

## Pull Image from a Different Container Registry

Now, let's try pulling a similar image from a _different_ container registry.

1. Use `docker pull` to pull an image from [Microsoft Artifact Registry](https://mcr.microsoft.com/en-us/catalog?search=Java) that contains the JDK.

    ```
    docker pull mcr.microsoft.com/openjdk/jdk:17-ubuntu
    ```

    By default, `docker pull` pulls images from Docker Hub. It is also possible to manually specify the path of a registry to pull from. Here, the path is `mcr.microsoft.com/openjdk/`.

    When pulling, the output would be something like:

    ```
    17-ubuntu: Pulling from openjdk/jdk
    ...
    Digest: sha256:...
    Status: ...
    mcr.microsoft.com/openjdk/jdk:17-ubuntu  👈
    ```

    _Which container registry was the image pulled from?_

    Compare and contrast the following `pull` commands:

    ```
    docker pull mcr.microsoft.com/openjdk/jdk:17-ubuntu
    ```

    ```
    docker pull docker.io/library/eclipse-temurin:17-jdk
    ```


2. Check the version of the JDK in the image by running `java -version`.

    ```
    docker run --rm mcr.microsoft.com/openjdk/jdk:17-ubuntu java -version
    ```

## Push an Image to Docker Hub

Now, let's copy an image from Docker Hub to your Docker Hub account.

1. If you have not yet created an account in Docker Hub, go to [Docker Hub and create one](https://hub.docker.com/signup). It's free. Remember your username and password. You'll need it to push an image for your account's container registry.

2. Pull the `hello-world` image from Docker Hub.

    *What command did you use to pull the `hello-world` image from Docker Hub?*

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily write down your answer here. You may be asked during the lab exercise debrief." spellcheck="false"></textarea>

3. Tag the image as `<hub-user>/hello-world:latest` in preparation for pushing it to your Docker Hub account container registry.

    ```
    docker tag hello-world:latest <hub-user>/hello-world:latest
    ```

    ☝ Replace `<hub-user>` with your Docker Hub account username.

    Run the following commands to verify that the following images exist.

    ```
    docker image ls hello-world:latest
    docker image ls <hub-user>/hello-world:latest
    ```

4. Push the image to your Docker Hub account container registry.

    ```
    docker push <hub-user>/hello-world:latest
    ```

    _What happened when you ran the above command?_

    ```
    denied: requested access to the resource is denied
    unauthorized: authentication required
    ```

    In order to push an image to your Docker Hub account, authentication is required. This protects your account's repository from unwanted images. Note that, by default, the Docker Hub account repository is _publicly_ accessible. This means that anyone can pull your images. _Private_ repositories are also provided by Docker Hub. In which case, authentication is also required to pull its images.

    To authenticate, use `docker login`.

    ```
    docker login
    ```

    When prompted, use your Docker Hub account for the _username_ and _password_.

    After successfully authenticating, you can now proceed with pushing your copy of the `hello-world` image.

    ```
    docker push <hub-user>/hello-world:latest
    ```

    The format is: `docker push <hub-user>/<repo-name>:<tag>`

    In projects where the created image contains proprietary code, the image is stored in a private container registry. This registry may have their own way of authentication. Please check with the registry's documentation for more information.


5. Remove the tagged image and pull the newly pushed image.

    ```
    docker image remove <hub-user>/hello-world:latest
    ```


    ```
    docker pull docker.io/library/<hub-user>/hello-world:latest
    ```

6. To logout the Docker CLI (command-line interface) from your Docker Hub account, run the following:

    ```
    docker logout
    ```

6. You can visit Docker Hub via browser, and see that your account now has a new repository containing the `hello-world` image.

7. Remove the tagged image.

    ```
    docker image remove <hub-user>/hello-world:latest
    ```

## Use a Local Registry

Now, let's try running a local container registry.

1. Use a command like the following to start the registry container:

    ```
    docker run -d -p 5000:5000 --rm --name registry registry:2
    ```
    
    _This way of running a container registry is only appropriate for testing. A production-ready registry must be protected by TLS and should ideally use an access-control mechanism._

    See [registry - Official Image | Docker Hub](https://hub.docker.com/_/registry) for more information.


2. Test the local container registry by pulling an image from it.

    ```
    docker pull localhost:5000/hello-world
    ```

    _What do you think will happen when running the above command?_

    _Why are we pulling from `localhost:5000`?_


3. Tag the `hello-world` image in preparation for pushing it to the local container registry.

    _What command did you use to tag?_

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily write down your answer here. You may be asked during the lab exercise debrief." spellcheck="false"></textarea>

    Run the following commands to verify that the images exist.

    ```
    docker image ls hello-world:latest
    docker image ls localhost:5000/hello-world:latest
    ```

4. Push the newly tagged `hello-world` image to the local container registry.

    _What command did you use to push?_

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily write down your answer here. You may be asked during the lab exercise debrief." spellcheck="false"></textarea>

    Note that the local container registry was not setup to require authentication. But if it did, you can `login` to a specific container registry using:

    ```
    docker login localhost:5000
    ```


5. Now that the local container registry has a `hello-world` image, let's try pulling the image from it.

    ```
    docker pull localhost:5000/hello-world
    ```


    _What do you think will happen when running the above command?_

    Notice the path in the image name: `localhost:5000/`.

    Compare and contrast the following `pull` commands:

    ```
    docker pull docker.io/library/hello-world:latest
    ```
    ```
    docker pull localhost:5000/hello-world:latest
    ```

6. Remove the tagged image.

    ```
    docker image remove localhost:5000/hello-world
    ```


7. Stop the local container registry.

    ```
    docker stop registry
    ```

In projects where the created image contains proprietary code, the image is stored in a private container registry. This registry may have their own way of authentication. Please check with the registry's documentation for more information.

Congratulations! You've completed this lab exercise.
