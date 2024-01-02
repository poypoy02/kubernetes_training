# Run Docker Containers Interactively

Estimated time: 15 minutes

## What You Will Learn

This lab is part of a series of labs in which you'll be running some Docker containers:

1. To run a single task (and exit) (15 minutes)
2. **Interactively** (15 minutes)
3. In the background (30 minutes plus an optional 15 more minutes)

In this lab, you'll be running some Docker containers _interactively_.

The `run` command has the following syntax:

```
docker run [OPTIONS] IMAGE [COMMAND] [ARG...]
```

For more information, see [`docker run` in the Docker Documentation](https://docs.docker.com/engine/reference/commandline/run/).

## Run an Interactive Shell

1. Use the `--interactive` and `--tty` options of the `run` command. Or, simply use the combined shorthand version, `-it`.

2. In a terminal, run the following command to start a `sh` command in an `alpine` container.

    You may also want to include the `--rm` option to have it removed when the container exits.

    ```
    docker run -it alpine sh
    ```

    *What just happened?* Notice that the prompt changed? You're now inside a shell of a running container.

3. Now let's try to run some commands.

    ```
    cat /etc/issue
    ```

    Note that _after_ running the command, you are still inside the shell. It has not exited (yet). That's interactive!

    Let's continue running some more commands.

    ```
    echo Hello from `hostname`
    ```

    What is the output?

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste the output here." spellcheck="false"></textarea>

    ```
    echo Today is `date`
    ```

    What is the output?

    <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste the output here." spellcheck="false"></textarea>



4. Now, let's try running something (that may need to be installed).

    ```
    figlet Hello Docker
    ```

    Notice that the command is not found. Let's try to install it. Yes, we'll install it on the running container. Run the following command.

    ```
    apk add figlet
    ```

    After a few seconds (or minutes), the `figlet` package should be available for us to run.

    ```
    figlet Hello Docker
    ```

5. You can run some more commands if you like. When you're done, you can exit from the shell (and the running container) by running the following command.

    ```
    exit
    ```


Congratulations! You've completed this lab exercise.
