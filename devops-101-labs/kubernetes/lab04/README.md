# 4: Deployment Management

Estimated time: 45 minutes

## 4.1: Lab Goals

Deployments make running server style workloads convenient. Understanding the different strategies and types helps us know when to use which. We will explore managing a rollout of built-in Deployment strategies and then perform a canary deployment expanding on the relationships between Deployments and Services.

## 4.2: Explore Deployment Characteristics

### Prepare Another `gowebapp` Version

In order to explore deployments, we need a new version of our `gowebapp`. For convenience, we have provided commands to modify the `gowebapp` slightly where the only change is the title of the site will have the word _Improved_ added to it. It will read _Improved Go Web App_ and that version will be tagged `v1.1` so we can cause Kubernetes to know there is a new version.

```sh
cd $K8S_LABS_HOME/gowebapp
# modify the app to say "Improved"
sed -i s/"Go Web App"/"Improved Go Web App"/g gowebapp/code/template/index/anon.tmpl
sed -i s/"Go Web App"/"Improved Go Web App"/g gowebapp/code/template/index/auth.tmpl
sed -i s/"Go Web App"/"Improved Go Web App"/g gowebapp/code/template/base.tmpl
```

On Windows, use WSL (Windows Subsystem for Linux) to run the `sed` command. Or, run a Docker image with a `sed` command (like `alpine`, or `busybox`, or `ubuntu`) and bind mount the `$K8S_LABS_HOME` directory. Alternatively, use your preferred text editor to edit the said files and replace "Go Web App" with "Improved Go Web App".

Run the following `docker` commands to add the new tag and push it to the local registry.

```sh
cd $K8S_LABS_HOME/gowebapp/gowebapp

# build the new version and tag it
docker build -t gowebapp:v1.1 .
```

☝ _Skip the changes below when using Docker Desktop&rsquo;s Kubernetes. Proceed to section 4.3._

Replace `$REGISTRY_HOST` with actual registry host (e.g. registry.example.com/group/project/my-image:v1.1)

```
# Tag and push the updated image to the registry
docker tag gowebapp:v1.1 $REGISTRY_HOST/gowebapp:v1.1
docker push $REGISTRY_HOST/gowebapp:v1.1
```

## 4.3: Controlling a Deployment rollout

### Trigger and pause a deployment

Switch to the `gowebapp` folder:

```sh
cd $K8S_LABS_HOME/gowebapp
```

Let&rsquo;s trigger a deployment by updating the application to the new version. Perform the following update to the `gowebapp-deployment.yaml` file:

- update the `image` field value to be our new `gowebapp:v1.1`

The `gowebapp` containers startup very quickly due to the nature of containers and since we are running native binary application. After updating the file, let&rsquo;s apply the update to Kubernetes and then quickly pause it by executing the commands back to back in one line:

On \*nix,

```sh
kubectl apply -f gowebapp-deployment.yaml && sleep 1 && kubectl rollout pause deployment gowebapp
```

On Windows,

```sh
kubectl apply -f gowebapp-deployment.yaml && timeout 1 && kubectl rollout pause deployment gowebapp
```

C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>kubectl apply -f gowebapp-deployment.yaml && timeout 1 && kubectl rollout pause deployment gowebapp
deployment.apps/gowebapp configured

Waiting for 0 seconds, press a key to continue ...
deployment.apps/gowebapp paused

C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

We should see console output from `kubectl` telling us that deployment was configured and then paused:

_EXAMPLE OUTPUT_
```console
deployment.apps/gowebapp configured
deployment.apps/gowebapp paused
```

### Verify the rollout is paused

How can we verify that the deployment is actually in a paused state?

We have a few options, first let&rsquo;s just look at the pods with `kubectl get pods` and we&rsquo;ll notice that we now have 3 pods, _1 new one_ and the _2 original ones_:

```console
$ kubectl get pods
NAME                              READY   STATUS    RESTARTS   AGE
gowebapp-5496bb5d8d-xt5b6         1/1     Running   0          9s
gowebapp-869b5bb46f-rrs44         1/1     Running   0          1h
gowebapp-869b5bb46f-zvll4         1/1     Running   0          1h
gowebapp-mysql-5bf5c94cb4-zgljn   1/1     Running   0          3h
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pods
NAME                              READY   STATUS    RESTARTS   AGE
gowebapp-68f56d48fc-5qhn8         1/1     Running   0          81m
gowebapp-68f56d48fc-dmmkr         1/1     Running   0          81m
gowebapp-77846f6d46-c9c2r         1/1     Running   0          11m
gowebapp-mysql-66486c8fc5-dstj2   1/1     Running   0          176m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>  

We can also run `kubectl describe deployment gowebapp` and see that it is in a `DeploymentPaused` progressing state and also, notice that a new ReplicaSet was made but it is scaled to replicas of 1:

_EXAMPLE OUTPUT_
```console
$ kubectl describe deployment gowebapp
Name:                   gowebapp
# omitted output for lab doc readability
Conditions:
  Type           Status   Reason
  ----           ------   ------
  Available      True     MinimumReplicasAvailable
  Progressing    Unknown  DeploymentPaused
OldReplicaSets:  gowebapp-869b5bb46f (2/2 replicas created)
NewReplicaSet:   gowebapp-5496bb5d8d (1/1 replicas created)
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  3h    deployment-controller  Scaled up replica set gowebapp-869b5bb46f to 2
  Normal  ScalingReplicaSet  1m    deployment-controller  Scaled up replica set gowebapp-5496bb5d8d to 1
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe deployment gowebapp
Name:                   gowebapp
Namespace:              default
CreationTimestamp:      Tue, 19 Dec 2023 13:52:14 +0800
Labels:                 app=gowebapp
                        tier=frontend
Annotations:            deployment.kubernetes.io/revision: 4
Selector:               app=gowebapp,tier=frontend
Replicas:               2 desired | 1 updated | 3 total | 3 available | 0 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=gowebapp
           tier=frontend
  Containers:
   gowebapp:
    Image:      gowebapp:v1.1
    Port:       8080/TCP
    Host Port:  0/TCP
    Environment:
      DB_PASSWORD:  mypassword
    Mounts:         <none>
  Volumes:          <none>
Conditions:
  Type           Status   Reason
  ----           ------   ------
  Available      True     MinimumReplicasAvailable
  Progressing    Unknown  DeploymentPaused
OldReplicaSets:  gowebapp-68f56d48fc (2/2 replicas created), gowebapp-575d57df76 (0/0 replicas created)
NewReplicaSet:   gowebapp-77846f6d46 (1/1 replicas created)
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  59m   deployment-controller  Scaled down replica set gowebapp-575d57df76 to 0 from 1
  Normal  ScalingReplicaSet  11m   deployment-controller  Scaled up replica set gowebapp-77846f6d46 to 1
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

### Resume the rollout

Let&rsquo;s resume the deployment and then quickly look at the pods:

On \*nix,

```sh
kubectl rollout resume deployment gowebapp && sleep 1 && kubectl get pods
```

On Windows,

```sh
kubectl rollout resume deployment gowebapp && timeout 1 && kubectl get pods
```

C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>kubectl rollout resume deployment gowebapp && timeout 1 && kubectl get pods
deployment.apps/gowebapp resumed

Waiting for 0 seconds, press a key to continue ...
NAME                              READY   STATUS              RESTARTS   AGE
gowebapp-68f56d48fc-5qhn8         1/1     Running             0          84m
gowebapp-68f56d48fc-dmmkr         0/1     Terminating         0          84m
gowebapp-77846f6d46-5zhtb         0/1     ContainerCreating   0          0s
gowebapp-77846f6d46-c9c2r         1/1     Running             0          14m
gowebapp-mysql-66486c8fc5-dstj2   1/1     Running             0          179m

C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

We should now see that all the old version pods are `Terminating`:

_EXAMPLE OUTPUT_
```console
$ kubectl rollout resume deployment gowebapp && sleep 1 && kubectl get pods
deployment.apps/gowebapp resumed
NAME                              READY   STATUS              RESTARTS   AGE
gowebapp-5496bb5d8d-l7wnt         1/1     Running             0          1m
gowebapp-5496bb5d8d-n8v2l         0/1     ContainerCreating   0          1s
gowebapp-869b5bb46f-5xzj4         1/1     Running             0          1h
gowebapp-869b5bb46f-7l56h         0/1     Terminating         0          1h
gowebapp-mysql-5bf5c94cb4-zgljn   1/1     Running             3          3h
```

### Verify the rollout complete

We can `kubectl describe deployment gowebapp` again and notice that everything is on the new ReplicaSet:

_EXAMPLE OUTPUT_
```console
$ kubectl describe deployment gowebapp
Name:                   gowebapp
# omitted output for lab doc readability
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Available      True    MinimumReplicasAvailable
  Progressing    True    NewReplicaSetAvailable
OldReplicaSets:  <none>
NewReplicaSet:   gowebapp-5496bb5d8d (2/2 replicas created)
Events:
  Type    Reason             Age    From                   Message
  ----    ------             ----   ----                   -------
  Normal  ScalingReplicaSet  6m28s  deployment-controller  Scaled up replica set gowebapp-869b5bb46f to 2
  Normal  ScalingReplicaSet  5m59s  deployment-controller  Scaled up replica set gowebapp-5496bb5d8d to 1
  Normal  ScalingReplicaSet  5m38s  deployment-controller  Scaled down replica set gowebapp-869b5bb46f to 1
  Normal  ScalingReplicaSet  5m38s  deployment-controller  Scaled up replica set gowebapp-5496bb5d8d to 2
  Normal  ScalingReplicaSet  5m36s  deployment-controller  Scaled down replica set gowebapp-869b5bb46f to 0
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe deployment gowebapp
Name:                   gowebapp
Namespace:              default
CreationTimestamp:      Tue, 19 Dec 2023 13:52:14 +0800
Labels:                 app=gowebapp
                        tier=frontend
Annotations:            deployment.kubernetes.io/revision: 4
Selector:               app=gowebapp,tier=frontend
Replicas:               2 desired | 2 updated | 2 total | 2 available | 0 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=gowebapp
           tier=frontend
  Containers:
   gowebapp:
    Image:      gowebapp:v1.1
    Port:       8080/TCP
    Host Port:  0/TCP
    Environment:
      DB_PASSWORD:  mypassword
    Mounts:         <none>
  Volumes:          <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Available      True    MinimumReplicasAvailable
  Progressing    True    NewReplicaSetAvailable
OldReplicaSets:  gowebapp-68f56d48fc (0/0 replicas created), gowebapp-575d57df76 (0/0 replicas created)
NewReplicaSet:   gowebapp-77846f6d46 (2/2 replicas created)
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  15m   deployment-controller  Scaled up replica set gowebapp-77846f6d46 to 1
  Normal  ScalingReplicaSet  58s   deployment-controller  Scaled down replica set gowebapp-68f56d48fc to 1 from 2
  Normal  ScalingReplicaSet  57s   deployment-controller  Scaled up replica set gowebapp-77846f6d46 to 2 from 1
  Normal  ScalingReplicaSet  57s   deployment-controller  Scaled down replica set gowebapp-68f56d48fc to 0 from 1
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

### Verify the change

After running `kubectl get svc`, and noting the port, you should be able to access `gowebapp` through the Service by opening `http://localhost` in a browser with the port that Kubernetes has allocated.

Verify that it now says &ldquo;Improved Go Web App&rdquo;

### Revert back to `v1`

We are going to do a deployment again, so let&rsquo;s revert things back to `v1` so we can try another deployment.

```sh
cd $K8S_LABS_HOME/gowebapp
sed -i s/'v1.1'/'v1'/g gowebapp-deployment.yaml
kubectl apply -f gowebapp-deployment.yaml
```

Or, use your preferred text editor to modify `gowebapp-deployment.yaml` and revert things back to `v1`.

## 4.4: Perform a Canary deployment

A canary deployment is not a built-in strategy type for Kubernetes; however, we can still perform one by leveraging multiple Deployments and Services. The goal in this lab will be to setup, test, and perform a canary deployment for our `gowebapp`.

Note: Reminder that the updated `v1.1` image we made at the beginning of this lab only has one difference where the title of the app in the browser has the word _Improved_ added. This will help us be able to detect the different easily.

### Setup the canary deployment

First let&rsquo;s make the second canary Deployment, we have one ready to go that we can use.

Switch to the `gowebapp` folder:

```sh
cd $K8S_LABS_HOME/gowebapp
```

Use your preferred text editor to create a file called `canary-deployment.yaml` and populate it with the contents below.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata: 
  name: canary
  labels:
    app: gowebapp
    tier: frontend
    track: canary
spec:
  replicas: 1
  selector:
    matchLabels:
      app: gowebapp
      tier: frontend
      track: canary
  template:
    metadata:
      labels:
        app: gowebapp
        tier: frontend
        track: canary
    spec:
      containers:
      - name: gowebapp
        image: gowebapp:v1.1
        env:
        - name: DB_PASSWORD
          value: mypassword
        ports:
        - containerPort: 8080
```

☝ _Skip the changes below when using Docker Desktop&rsquo;s Kubernetes. Proceed to `kubectl apply` to create the deployment._

We are using a custom image that we created in a previous lab. Therefore we need to add the registry server to the `image:` line in the YAML so that Kubernetes knows which registry to pull the image from. Otherwise it will try to find the image on the public/default configured registry server (e.g. Docker Hub).

```yaml
apiVersion: apps/v1
kind: Deployment
metadata: 
  name: canary
# ...
spec:
  # ...
  template:
    # ...
    spec:
      containers:
      - name: gowebapp
        image: registry.example.com/group/project/gowebapp:v1.1 # 👈
  #            👆
  # ...
```

Then use `kubectl` to create the Deployment defined above

```sh
kubectl apply -f canary-deployment.yaml
```

Once the new Deployment has been created let&rsquo;s check on the status of our environment by looking at what pods we have now:

```sh
kubectl get pods
```
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pods
NAME                              READY   STATUS    RESTARTS   AGE
canary-54554f9967-qcbtb           1/1     Running   0          17s
gowebapp-68f56d48fc-lzsdq         1/1     Running   0          2m15s
gowebapp-68f56d48fc-qn4b6         1/1     Running   0          2m16s
gowebapp-mysql-66486c8fc5-dstj2   1/1     Running   0          3h7m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

We should see a new pod that now exists which was created from our canary deployment:

_EXAMPLE OUTPUT_
```console
$ kubectl get pods

NAME                              READY   STATUS    RESTARTS   AGE
canary-5889d75f78-mprzk           1/1     Running   0          6s
gowebapp-5496bb5d8d-l7wnt         1/1     Running   3          2h
gowebapp-5496bb5d8d-n8v2l         1/1     Running   3          2h
gowebapp-mysql-5bf5c94cb4-zgljn   1/1     Running   5          2h
```

### Testing of the canary

The canary deployment is up and running and since it uses the same labels as the regular `gowebapp` Deployment, it will be picked up by the existing `gowebapp` Service we have. Let&rsquo;s run a test to verify that we are occasionally seeing the canary get used when we access it via the Service.

First, we will create a curl pod to access the service and run our tests. Use your preferred text editor to create a file called `curl-pod.yaml` and populate it with the contents below.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: curlpod
  labels:
    run: curlpod
spec:
  securityContext:
    runAsUser: 1000
  containers:
  - name: curlpod
    image: curlimages/curl
    command:
    - 'sleep'
    - '30000'
    resources: {}
```

Then use `kubectl` to create the pod defined above

```sh
kubectl apply -f curl-pod.yaml
```

Shell in to this pod to run our tests.

```sh
kubectl exec -it curlpod -- sh
```

From inside the curl pod, try accessing the gowebapp Service several times, this will help ensure we get a good sampling of access. To make things easy we placed this work into the following command which will issue a `curl` command to retrieve the HTML and look for lines that distinguish the two different versions of the application.

Run the test from inside the shell.

```sh
n=0; while [[ $n -lt 10 ]]; do curl -s gowebapp:8080 | grep 'title'; n=$((n+1)); done
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl exec -it curlpod -- sh
/home/curl_user $ n=0; while [[ $n -lt 10 ]]; do curl -s gowebapp:8080 | grep 'title'; n=$((n+1)); done
        <title>Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
/home/curl_user $

After performing the above, we should see some output like the following where occasionally the pods behind the canary deployment respond:

_EXAMPLE OUTPUT_
```console
$ n=0; while [[ $n -lt 10 ]]; do curl -s gowebapp:8080 | grep 'title'; n=$((n+1)); done
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Go Web App</title>
```

Note: If you try to perform this test with the web browser on your laptop it may not work as the browser will be performing `keep-alive` connections to the server and therefore you will keep accessing the same pod each time.

Exit the pod we used for testing.

```sh
exit
```

We have validated that the canary deployment is out there and receiving traffic, but if we want to test against the canary specifically we need a second Service that is just using the `track: canary` label so it only sends traffic to those pods.

Use your preferred text editor to create a file called `canary-service.yaml`. Follow instructions below to populate `canary-service.yaml`.

***Note: Replace TODO comments with the appropriate commands***

```yaml
apiVersion: v1
kind: Service
metadata: 
  name: canary
  labels:
    app: gowebapp
    tier: frontend
spec:
  type: NodePort
  ports:
  - port: 8080
  selector:
    app: gowebapp
    tier: frontend
    #TODO: add a label: track: canary
```

Once the label has been added use `kubectl` to create the Service defined above

```sh
kubectl apply -f canary-service.yaml
```

We can verify the service was created by looking for it in the list of services:

```sh
kubectl get services
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get services
NAME             TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
canary           NodePort    10.99.29.33      <none>        8080:30973/TCP   7s
gowebapp         NodePort    10.105.139.221   <none>        8080:32211/TCP   129m
gowebapp-mysql   ClusterIP   10.96.12.157     <none>        3306/TCP         3h36m
kubernetes       ClusterIP   10.96.0.1        <none>        443/TCP          42h
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

We now should see three services related to our application: `gowebapp`, `gowebapp-mysql`, and the new canary:

_EXAMPLE OUTPUT_
```console
$ kubectl get services
NAME             TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)        AGE
canary           NodePort    10.110.249.13    <none>        8080:30083/TCP 1m
gowebapp         NodePort    10.100.217.172   <none>        8080:30940/TCP 2h
gowebapp-mysql   ClusterIP   10.102.206.239   <none>        3306/TCP       2h
kubernetes       ClusterIP   10.96.0.1        <none>        443/TCP        4h
```

Now that we have the new Service created, repeat our test from earlier and access the application 10 times:

```sh
kubectl exec -it curlpod -- sh
n=0; while [[ $n -lt 10 ]]; do curl -s canary:8080 | grep 'title'; n=$((n+1)); done
```

We should see each request to this new service is now being serviced by our canary:

_EXAMPLE OUTPUT_
```console
$ n=0; while [[ $n -lt 10 ]]; do curl -s canary:8080 | grep 'title'; n=$((n+1)); done
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
```
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl exec -it curlpod -- sh
/home/curl_user $ n=0; while [[ $n -lt 10 ]]; do curl -s canary:8080 | grep 'title'; n=$((n+1)); done
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Improved Go Web App</title>
        <title>Improved Go Web App</title>
/home/curl_user $

Exit the pod when the test is complete.

```sh
exit
```

We can assume testing went well and we now want to move forward with the new version of the application in production. We perform this by doing two tasks:

- Update the image of the production deployment (`image` field) to be the new version
- Change the `replicas` field on the canary to `0`

After modifying those files, let&rsquo;s apply them both. For convenience, let `kubectl` apply all YAML files in the `gowebapp` directory, wait a bit, and then see what the deployments look like:

```sh
kubectl apply -f $K8S_LABS_HOME/gowebapp
sleep 20
kubectl get deployments -o wide
```

On Windows, use `timeout` instead of `sleep`.

We should see something like the following where the `canary` and `gowebapp` deployments are using the same image version and the `canary` is now at `0` pods:

_EXAMPLE OUTPUT_
```console
$ kubectl get deployments -o wide
 NAME             READY   UP-TO-DATE   AVAILABLE   AGE    CONTAINERS       IMAGES                             SELECTOR
 canary           0/0     0            0           10m    gowebapp         gowebapp:v1.1                      app=gowebapp,tier=frontend,track=canary
 gowebapp         2/2     2            2           6d1h   gowebapp         gowebapp:v1.1                      app=gowebapp,tier=frontend
 gowebapp-mysql   1/1     1            1           6d1h   gowebapp-mysql   gowebapp-mysql:v1                  app=gowebapp-mysql,tier=backend
```
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get deployments -o wide
NAME             READY   UP-TO-DATE   AVAILABLE   AGE     CONTAINERS       IMAGES                SELECTOR
canary           0/0     0            0           11m     gowebapp         gowebapp:v1.1         app=gowebapp,tier=frontend,track=canary
gowebapp         2/2     1            2           103m    gowebapp         gowebapp:v1.1         app=gowebapp,tier=frontend
gowebapp-mysql   0/1     1            0           3h18m   gowebapp-mysql   gowebapp-mysql:v1.1   app=gowebapp-mysql,tier=backend
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

### Cleanup

Let&rsquo;s delete the canary Deployment and Service we created since we won&rsquo;t be using those in the future&hellip;

```sh
kubectl delete service canary
kubectl delete deployment canary
```

&hellip;and revert back to the `v1` version.

```sh
cd $K8S_LABS_HOME/gowebapp
sed -i s/'v1.1'/'v1'/g gowebapp-deployment.yaml
kubectl apply -f gowebapp-deployment.yaml
sleep 20
kubectl get deployments -o wide
#shell in to the curlpod
kubectl exec -it curlpod -- sh
n=0; while [[ $n -lt 10 ]]; do curl -s gowebapp:8080 | grep 'title'; n=$((n+1)); done
exit
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get deployments -o wide
NAME             READY   UP-TO-DATE   AVAILABLE   AGE     CONTAINERS       IMAGES                SELECTOR
gowebapp         2/2     2            2           105m    gowebapp         gowebapp:v1           app=gowebapp,tier=frontend
gowebapp-mysql   0/1     1            0           3h20m   gowebapp-mysql   gowebapp-mysql:v1.1   app=gowebapp-mysql,tier=backend
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl exec -it curlpod -- sh
/home/curl_user $ n=0; while [[ $n -lt 10 ]]; do curl -s gowebapp:8080 | grep 'title'; n=$((n+1)); done
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
        <title>Go Web App</title>
/home/curl_user $

After verifying that it is back to `v1` (no _Improved_ added), let&rsquo;s delete the curl pod.

```sh
kubectl delete pod curlpod
```
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl delete pod curlpod
pod "curlpod" deleted
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

## 4.5: Conclusion

We spent some time understanding Deployments more and getting hands on managing and manipulating them. For larger scale applications you now know how to pause and control an active rollout. Additionally, we configured, setup, and tested a canary deployment.
