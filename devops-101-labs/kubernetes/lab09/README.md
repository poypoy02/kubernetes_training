# 9: Dynamic Application Configuration

Estimated time: 30 minutes

## 9.1: Lab Goals

In this lab, you will be updating your gowebapp container image to accept configuration at runtime. We will create a ConfigMap for configuration and a Secret for the MySQL password. The deployment will be updated to use the new image, configmaps and secrets instead of having them preconfigured in the Docker image.

## 9.2: Build new Docker image for your frontend application

### Step 1: Update Dockerfile for your frontend application

```sh
cd $K8S_LABS_HOME/gowebapp/gowebapp
```

Update the `Dockerfile` in this directory for the frontend Go application. Use your preferred text editor. The template below provides a starting point for updating the contents of this file.

**Note: Replace TODO comments with the appropriate commands.**

`$K8S_LABS_HOME/gowebapp/gowebapp/Dockerfile`

```dockerfile
FROM ubuntu:jammy

# TODO --- add an environment variable declaration for a default DB_PASSWORD  
# of "mydefaultpassword"
# https://docs.docker.com/engine/reference/builder/#env

COPY ./code /opt/gowebapp
# TODO --- remove the following line. We no longer want to include the configuration 
# file in the image.
COPY ./config /opt/gowebapp/config

# TODO --- add a volume declaration for the container configuration path we want to 
# mount at runtime from the host file system: /opt/gowebapp/config
# https://docs.docker.com/engine/reference/builder/#volume

EXPOSE 8080
USER 1000

WORKDIR /opt/gowebapp/
ENTRYPOINT ["/opt/gowebapp/gowebapp"]
```

### Step 2: Build updated gowebapp Docker image locally

Build the gowebapp image locally. Make sure to include &ldquo;.&rdquo; at the end. Notice the new version label.

```sh
docker build -t gowebapp:v2 .
```

## 9.3: Publish New Image

☝ _Skip this step when using Docker Desktop&rsquo;s Kubernetes. Proceed to section 9.4._

Why? 🤔

_When using Docker Desktop's built-in Kubernetes, developers benefit from a fast &ldquo;inner-loop&rdquo; where they can &ldquo;docker build&rdquo; an image and then immediately test it from Kubernetes without having to push and then re-pull, all thanks to Docker&rsquo;s shared image store and [cri-dockerd](https://github.com/Mirantis/cri-dockerd) (formerly dockershim for Kubernetes before 1.24)._

_If you need a target Kubernetes cluster environment to pull the updated image, you'll need to push the image to the registry. Here&rsquo;s how._

1. _Tag the image to target registry. Replace `$REGISTRY_HOST` with actual registry host (e.g. registry.example.com/group/project/my-image:v2)._
    ```sh
    docker tag gowebapp:v2 $REGISTRY_HOST/gowebapp:v2
    ```
2. _Push image to registry. This might require some authentication for private registries (e.g. `docker login`)._
    ```sh
    docker push $REGISTRY_HOST/gowebapp:v2
    ```

<!--
Realize that Kubernetes and Docker are not installed in your installed WSL2 distro. Instead, Docker Desktop for Windows creates its own WSL2 VM called docker-desktop and installs Docker and Kubernetes on that VM. Then Docker Desktop for Windows installs the docker and kubectl CLIs on your WSL2 distro (and also on your Windows machine) and configures them all to point to the Docker and Kubernetes instances it created on the docker-desktop VM. This docker-desktop VM is hosting Docker and Kubernetes and also contains the /run/desktop/mnt/host/c mount point to your Windows C:\ drive and that can be used by your containers to persist data.

You can remote into the docker-desktop VM and see the /run/desktop/mnt/host/c mount point and folder structure by following the instructions (and discussion) at https://stackoverflow.com/a/62117039/11057678:
-->

## 9.4: Create ConfigMap

### Step 1: create ConfigMap for gowebapp&rsquo;s config.json file

```sh
cd $K8S_LABS_HOME/gowebapp/gowebapp/
```

```sh
kubectl create configmap gowebapp --from-file=webapp-config-json=config/config.json
```

```sh
kubectl describe configmap gowebapp
```

Notice that the entire file contents from `config.json` are stored under the key `webapp-config-json`.

```
kubectl create configmap gowebapp --from-file=webapp-config-json=config/config.json
                                              👆
```

### Step 2: update gowebapp deployment to utilize ConfigMap

Update `gowebapp-deployment.yaml`. If you need help, please see https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#add-configmap-data-to-a-specific-path-in-the-volume

```
cd $K8S_LABS_HOME/gowebapp
```

**Note: Replace TODO comments with the appropriate commands**

`gowebapp-deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata: 
  name: gowebapp
  labels:
    app: gowebapp
    tier: frontend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: gowebapp
      tier: frontend
  template:
    metadata:
      labels:
        app: gowebapp
        tier: frontend
    spec:
      containers:
      - name: gowebapp
        # TODO: change image to v2
        image: gowebapp:v1       
        env:
        - name: DB_PASSWORD
          value: mypassword
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          tcpSocket:
            port: 8080
          periodSeconds: 5
          timeoutSeconds: 1
        readinessProbe:
          httpGet:
            path: /
            port: 8080
          periodSeconds: 5
          timeoutSeconds: 1
        volumeMounts:
        - #TODO: Set volume name to 'config-volume'
          #TODO: Set mountPath to '/opt/gowebapp/config'
      volumes:
      - #TODO: define volume name: config-volume
        configMap:
          #TODO: Set ConfigMap name to 'gowebapp'
          items:
          - key: webapp-config-json
            path: config.json
```

☝ _Skip this step when using Docker Desktop&rsquo;s Kubernetes. Proceed to section 9.5._

We are using a custom image that we created in a previous lab. Therefore we need to add the registry server to the `image:` line in the YAML so that Kubernetes knows which registry to pull the image from. Otherwise it will try to find the image on the public/default configured registry server (e.g. Docker Hub).

```yaml
apiVersion: apps/v1
kind: Deployment
# ...
spec:
  # ...
  template:
    # ...
    spec:
      containers:
      - name: gowebapp
        image: registry.example.com/group/project/gowebapp:v2 # 👈
  #            👆
  # ...
```

## 9.5: Perform rolling upgrade

Start the rolling upgrade

```sh
kubectl apply -f gowebapp-deployment.yaml
```

Verify that rollout was successful. The two gowebapp pods&rsquo; status should be &lsquo;Running&rsquo;

On \*nix,

```sh
kubectl get pods -l 'app=gowebapp'
```

On Windows,

```sh
kubectl get pods -l "app=gowebapp"
```

## 9.6: Create a Secret

### Step 1: create secret for the MySQL password

It&rsquo;s not a good idea to hard-code sensitive information in configurations. Let&rsquo;s create a secret for the MySQL database password, so that we can reference it from configurations. Additionally, let&rsquo;s override the default password we placed and used inside the configuration file and Docker image.

```sh
kubectl create secret generic mysql --from-literal=password=mypassword
```

What did the `kubectl` command create? 🤔

Running `kubectl create secret --help` shows the following:

```
Create a secret using specified subcommand.

Available Commands:
  docker-registry   Create a secret for use with a Docker registry
  generic           Create a secret from a local file, directory, or literal value
  tls               Create a TLS secret

Usage:
  kubectl create secret [flags] [options]
```

And running `kubectl create secret generic --help` shows the following:

```
Create a secret based on a file, directory, or specified literal value.

 A single secret may package one or more key/value pairs.

# output truncated

Examples:
  # output truncated

  # Create a new secret named my-secret with key1=supersecret and key2=topsecret
  kubectl create secret generic my-secret --from-literal=key1=supersecret --from-literal=key2=topsecret

  # output truncated
```

So, the `kubectl create secret` command above created a _generic_ secret named `mysql`. You can get the details of the secret that was just created by running:

```sh
kubectl describe secret mysql
```

### Step 2: update MySQL StatefulSet to incorporate the secret

Update `gowebapp-mysql-sts.yaml`. If you need help, please see https://kubernetes.io/docs/concepts/configuration/secret/#using-secrets-as-environment-variable

```sh
cd $K8S_LABS_HOME/gowebapp
```

**Note: Replace TODO comments with the appropriate commands**

`gowebapp-mysql-sts.yaml`

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata: 
  name: gowebapp-mysql
  labels:
    app: gowebapp-mysql
    tier: backend
spec:
  serviceName: gowebapp-mysql
  replicas: 1
  selector:
    matchLabels:
      app: gowebapp-mysql
      tier: backend
  template:
    metadata:
      labels:
        app: gowebapp-mysql
        tier: backend
    spec:
      containers:
      - name: gowebapp-mysql
        image: gowebapp-mysql:v1
        env:
        - name: MYSQL_ROOT_PASSWORD
          valueFrom:
            secretKeyRef:
              #TODO: Set secret name to 'mysql'
              #TODO: Set key to 'password'
        ports:
        - containerPort: 3306
        resources:
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          tcpSocket:
            port: 3306
          initialDelaySeconds: 20
          periodSeconds: 5
          timeoutSeconds: 1
        readinessProbe:
          exec:
            #TODO: replace "mypassword" in command line below to be ${MYSQL_ROOT_PASSWORD} and
            #      also update the line to this new format. Unfortunately we need to move to
            #      a different format due to https://github.com/kubernetes/kubernetes/issues/40846
            command: ["bash", "-c", "mysql -uroot -pmypassword -e 'use gowebapp; select count(*) from user'"]
          initialDelaySeconds: 25
          periodSeconds: 10
          timeoutSeconds: 1
        volumeMounts:
        - name: mysql-pv
          mountPath: /var/lib/mysql
  volumeClaimTemplates:
  - metadata:
      name: mysql-pv
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 1Gi
```

### Step 3: update gowebapp deployment to incorporate the secret

Update `gowebapp-deployment.yaml`. If you need help, please see https://kubernetes.io/docs/concepts/configuration/secret/#using-secrets-as-environment-variable

```sh
cd $K8S_LABS_HOME/gowebapp
```

**Note: Replace TODO comments with the appropriate commands**

`gowebapp-deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata: 
  name: gowebapp
  # ...
spec:
  # ...
  template:
    # ...
    spec:
      containers:
      - name: gowebapp
        # ...
        env:
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              #TODO: Set secret name to 'mysql'
              #TODO: Set key name to 'password'
    # ...
```

☝ _Skip this step when using Docker Desktop&rsquo;s Kubernetes. Proceed to section 9.7._

We are using a custom image that we created in a previous lab. Therefore we need to add the registry server to the `image:` line in the YAML so that Kubernetes knows which registry to pull the image from. Otherwise it will try to find the image on the public/default configured registry server (e.g. Docker Hub).

```yaml
apiVersion: apps/v1
kind: Deployment
# ...
spec:
  # ...
  template:
    # ...
    spec:
      containers:
      - name: gowebapp
        image: registry.example.com/group/project/gowebapp:v2 # 👈
  #            👆
  # ...
```

```yaml
apiVersion: apps/v1
kind: StatefulSet
# ...
spec:
  # ...
  template:
    # ...
    spec:
      containers:
      - name: gowebapp-mysql
        image: registry.example.com/group/project/gowebapp-mysql:v1 # 👈
  #            👆
  # ...
```

## 9.7: Perform upgrade

```sh
kubectl apply -f gowebapp-mysql-sts.yaml
kubectl apply -f gowebapp-deployment.yaml
```

Verify that rollout was successful. The gowebapp and gowebapp-mysql pods&rsquo; status should be &lsquo;Running&rsquo;

```sh
kubectl get pods -l 'app=gowebapp-mysql'
kubectl get pods -l 'app=gowebapp'
```

## 9.8: Access your application

You should be able to access the application by running the following command in the terminal, or opening a browser to [http://gowebapp.localdev.me](http://gowebapp.localdev.me).

```sh
curl http://gowebapp.localdev.me
```

You can now test the application. Since we added persistent storage in the last lab, your username, password, and notes should still be valid and present.

## 9.9: Conclusion

Congratulations! You have successfully updated your application to use dynamic configuration.

<fieldset style="margin-block-end: 1em;">
  <legend>Which one(s) got to use a Secret?</legend>
  <span style="margin-inline-start: 1.25em;">
    <label style="margin-inline-end: 1.25em;"><input type="radio" name="used-a-secret" value="gowebapp" /><span style="margin-inline-start: 0.5em">gowebapp</span></label>
    <label style="margin-inline-end: 1.25em;"><input type="radio" name="used-a-secret" value="gowebapp-mysql" /><span style="margin-inline-start: 0.5em">gowebapp-mysql</span></label>
    <label style="margin-inline-end: 1.25em;"><input type="radio" name="used-a-secret" value="both" /><span style="margin-inline-start: 0.5em">both</span></label>
  </span>
</fieldset>

<fieldset style="margin-block-end: 1em;">
  <legend>Which one(s) got to use a ConfigMap?</legend>
  <span style="margin-inline-start: 1.25em;">
    <label style="margin-inline-end: 1.25em;"><input type="radio" name="used-a-configmap" value="gowebapp" /><span style="margin-inline-start: 0.5em">gowebapp</span></label>
    <label style="margin-inline-end: 1.25em;"><input type="radio" name="used-a-configmap" value="gowebapp-mysql" /><span style="margin-inline-start: 0.5em">gowebapp-mysql</span></label>
    <label style="margin-inline-end: 1.25em;"><input type="radio" name="used-a-configmap" value="both" /><span style="margin-inline-start: 0.5em">both</span></label>
  </span>
</fieldset>
