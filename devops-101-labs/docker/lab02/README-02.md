# Build Image from Dockerfile

Estimated time: 30 minutes

## What You Will Learn

In this lab, you'll be building some Docker images and using `ENV` and `EXPOSE` instructions in your Dockerfile.

For more information, see [Dockerfile reference](https://docs.docker.com/engine/reference/builder/).


## Build a Dockerfile (3)

Now, let's try to use some environment variables when running the container.

1. Back in the `j-hello` directory (from previous exercise), modify the Java program (`src/hello/Main.java`) to use an environment variable.

    ```java
    package hello;

    public class Main {
        public static void main(String[] args) {
            String name = System.getenv("HELLO_NAME"); // 👈
            if (name == null || name.trim().isEmpty()) {
                name = "Docker";
            }
            System.out.println("Hello " + name + " from Java!");
        }
    }
    ```

2. Compile the Java program&hellip;

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

    &hellip;build the image, and name it as `training/j-hello`.

3. Run a container from the `training/j-hello` image and set the `HELLO_NAME` environment variable to some non-empty string value (e.g. "O&B").

    _Write down the complete command you used. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

4. Modify the Dockerfile and add an `ENV` instruction to default `HELLO_NAME` to `"John, Paul, George, and Ringo"`.

    ```dockerfile
    FROM eclipse-temurin:17-jre

    WORKDIR /app
    COPY ./target .
    ENV HELLO_NAME="John, Paul, George, and Ringo" # 👈
    ENTRYPOINT ["java", "-cp", ".", "hello.Main"]
    ```

5. Build the image and name it as `training/j-hello`.

    _Write down the complete command you used. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

6. Run a container from the `training/j-hello` image. Do not set the `HELLO_NAME` environment variable. Note that it now _defaults_ to "John, Paul, George, and Ringo" when the `HELLO_NAME` environment variable is not set.

    _Write down the complete command you used. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

7. Run a container from the `training/j-hello` image and set the `HELLO_NAME` environment variable (e.g. "Philippines"). Note how the `HELLO_NAME` environment variable is overridden.

    *Remember the `-e` option?* The `-e` (or `--env`) option is used to set environment variables when running containers. See: [https://docs.docker.com/engine/reference/commandline/run/#env](https://docs.docker.com/engine/reference/commandline/run/#env)

    _Write down the complete command you used. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>


## Build a Dockerfile (4)

1. Go to the `pingservice` directory. Study the contents of the `Dockerfile` and come back to this README to generate the JAR file.

2. To generate the JAR file, use Docker to run Maven. Alternatively, if you have Apache Maven installed, run `mvn package`.

    On \*nix,

    ```
    docker run --rm \
        -w /app \
        -v "$PWD":/app \
        -v "$HOME"/.m2:/root/.m2 \
        maven:3.8-eclipse-temurin-17 \
        mvn package
    ```

    On Windows,

    ```
    docker run --rm ^
        -w /app ^
        -v "%cd%":/app ^
        -v "%USERPROFILE%"\.m2:/root/.m2 ^
        maven:3.8-eclipse-temurin-17 ^
        mvn package
    ```

    If all goes well, there should be a JAR file under the `target` directory (e.g. `pingservice-0.0.1-SNAPSHOT.jar`).

3. Go back to the `Dockerfile`, and modify it as instructed.

    _Copy and paste the contents of your Dockerfile here. You may be asked during the lab exercise debrief._

    <textarea rows="10" cols="80%" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

4. Build the image (`docker build -t ...`) and tag it as `pingservice:0.0.1`. Check if the built image is found.

    _Write down the complete command you used to check if the said image is found. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

5. Run a container from the `pingservice:0.0.1` image with the following options:

    - Publish port `8080` of the container to port `8081` of the host. *Remember the `-p` option?* The `-p` (or `--publish`) option is used to publish container's ports to the host. See: [https://docs.docker.com/engine/reference/commandline/run/#publish](https://docs.docker.com/engine/reference/commandline/run/#publish)
    - Set the `JDBC_URL` environment variable to `jdbc://example:9091/notarealurl`

    _Write down the complete command you used. You may be asked during the lab exercise debrief._

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

6. To verify if the published port works, try sending a request to localhost:8081/ping. What was the response?

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>

7. To verify that the environment variable is set, try sending a request to localhost:8081/connecttodb. What was the response?

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily place your answer here." spellcheck="false"></textarea>



Congratulations! You've completed this lab exercise.
