# 7: Resource Organization

Estimated time: 30 minutes

## 7.1: Exploring Namespaces

Namespaces enable you to isolate objects within the same Kubernetes cluster. The isolation comes in the form of:

- Object & DNS Name Scoping
- Object Access Control
- Resource Quotas

### Step 1: Current and secondary namespaces

In this lab you will be working with _two_ ✌ namespaces. You can see your namespaces in the output of the following command.

```sh
kubectl get namespaces
```

Let&rsquo;s create the second namespace.

```sh
kubectl create namespace my-namespace
```

This new namespace has no running pods at the moment.

Alternatively, a Namespace can also be created using a YAML manifest file and applied using `kubectl apply`.

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: my-namespace
```

Do you see your second namespace in the output of `kubectl get namespace` command?

_EXAMPLE OUTPUT_
```
NAME              STATUS   AGE
default           Active   33d
ingress-nginx     Active   58m
kube-node-lease   Active   33d
kube-public       Active   33d
kube-system       Active   33d
my-namespace      Active   5m
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get namespaces
NAME              STATUS   AGE
default           Active   2d13h
ingress-nginx     Active   55m
kube-node-lease   Active   2d13h
kube-public       Active   2d13h
kube-system       Active   2d13h
my-namespace      Active   15s
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

### Step 2: List resources in Namespaces

Many objects in Kubernetes are created within a Namespace. By default, if no namespace is specified when creating a namespaced object, it will be placed in a namespace associated with your current configuration context.

Let&rsquo;s verify this by listing the pods in a few namespaces.

There should be no pods in this namespace since we just created it.

```
kubectl get pods --namespace my-namespace
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pods --namespace my-namespace
No resources found in my-namespace namespace.
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

All of the gowebapp pods we created are in the "default" namespace.

```
kubectl get pods --namespace default
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pods --namespace default
NAME                              READY   STATUS    RESTARTS       AGE
gowebapp-5696d97c79-g2kf2         1/1     Running   25 (35m ago)   134m
gowebapp-5696d97c79-v5kh5         1/1     Running   0              30m
gowebapp-mysql-66486c8fc5-d2977   1/1     Running   0              31m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

Since the "default" is our _current_ namespace, the output of this command should be the same as the last.

```
kubectl get pods
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pods
NAME                              READY   STATUS    RESTARTS       AGE
gowebapp-5696d97c79-g2kf2         1/1     Running   25 (36m ago)   134m
gowebapp-5696d97c79-v5kh5         1/1     Running   0              31m
gowebapp-mysql-66486c8fc5-d2977   1/1     Running   0              32m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

## 7.2: DNS Namespacing

Namespaces also impact DNS resolution within the cluster. Pods can connect to services in their own namespace using the service&rsquo;s short name. If a pod needs to connect to a service in a different Namespace, it must use the service&rsquo;s fully-qualified name.

```
kubectl get services
```

_EXAMPLE OUTPUT_
```
NAME             TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
gowebapp         ClusterIP   10.104.159.241   <none>        8080/TCP   8m11s
gowebapp-mysql   ClusterIP   10.100.107.166   <none>        3306/TCP   8m22s
```

Let&rsquo;s test connecting to the `gowebapp` service using `curl` from within a pod three different ways:

1. using the short name `gowebapp` from the same namespace that the gowebapp service is deployed to (the default namespace)
2. using the short name `gowebapp` from the a namespace that is different from the one that the gowebapp service is deployed to (this should fail)
3. using the fully-qualified name `gowebapp.default.svc.cluster.local`

The easiest way to &ldquo;access a terminal&rdquo; within a namespace is to launch a pod with an interactive terminal inside the desired namespace.

On \*nix,

```
kubectl run curl --namespace default --image=curlimages/curl -i --tty --rm \
    --overrides='{"spec": { "securityContext": { "runAsUser": 1000 }}}' \
    -- sh
```

On Windows,

```
kubectl run curl --namespace default --image=curlimages/curl -i --tty --rm ^
    --overrides="{\"spec\": { \"securityContext\": { \"runAsUser\": 1000 }}}" ^
    -- sh
```

Use variations of the above command to launch pods in the desired namespaces. Then try curling the short and fully-qualified URLs above. Be sure to append the port number that the service listening on, which is `8080`.

If successful you should see a response like the following

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">


    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="">

    <title>Go Web App</title>
<!-- OUTPUT TRUNCATED -->
```

Attempting to access a service in a different namespace without using its fully-qualified name should result in an error.

Place your notes below.

1. From a Pod inside the _default_ namespace:

    - Did `curl gowebapp:8080` succeed?

        <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

    - Did `curl gowebapp.default.svc.cluster.local:8080` succeed?

        <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

2. From a Pod inside the _my-namespace_ namespace:

    - Did `curl gowebapp:8080` succeed?

        <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

    - Did `curl gowebapp.default.svc.cluster.local:8080` succeed?

        <textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

## 7.3: Changing Default Namespace

Right now, if we run any `kubectl` commands without specifying a namespace, the namespace _default_ will be used. If desired, we can set a different namespace to be our default.

### Step 01: View the `kubectl` configuration file

The `kubectl` configuration file, also called _kubeconfig_ can be viewed in two ways.

```
kubectl config view
```

Or

On \*nix,
```
cat ~/.kube/config
```

On Windows,
```
type "%USERPROFILE%"\.kube\config
```

```yaml
# EXAMPLE OUTPUT
apiVersion: v1
kind: Config
current-context: docker-desktop
preferences: {}
clusters:
- name: docker-desktop
  cluster:
    certificate-authority: #REDACTED
    server: #REDACTED
contexts:
- name: docker-desktop
  context:
    cluster: docker-desktop
    user: docker-desktop
users:
- name: docker-desktop
  user:
    token: #REDACTED
```

You&rsquo;ll notice that there are three sections:

1. A cluster contains endpoint data for a Kubernetes cluster.
2. A context defines a named cluster, user, namespace tuple which is used to send requests to the specified cluster using the provided authentication info and namespace.
3. A user defines client credentials for authenticating to a Kubernetes cluster.

### Step 02: create a new context

In addition to mapping users to clusters, contexts are also where we can set a default namespace. Let&rsquo;s create a new context that uses the existing user and cluster, but defaults to the `my-namespace` namespace.

```sh
kubectl config set-context my-context --user docker-desktop --cluster docker-desktop --namespace my-namespace
```

### Step 03: view the changes to kubeconfig

```sh
kubectl config view
```

You should see the new `my-context` context which includes `my-namespace` as its default namespace.

### Step 04: use the new context

Next, we need to tell `kubectl` to use our new context.

```sh
kubectl config use-context my-context
```

At any time, you can see which context is currently active by running

```sh
kubectl config current-context
```

Or see a list of available contexts by running

```sh
kubectl config get-contexts
```

### Step 05: deploy to my-namespace

Let&rsquo;s deploy a test deployment to `my-namespace`. Since it is now our default namespace, we shouldn&rsquo;t need to specify it manually.

```
kubectl create deployment nginx --image=nginx
kubectl scale --replicas=3 deployment nginx
```

### Step 06: list the deployment and its pods

```sh
kubectl get deployments
```

```sh
kubectl get pods
```

### Step 07: list the gowebapp deployment and its pods

We can still list the gowebapp deployment and pods, but we&rsquo;ll need to tell `kubectl` to target the _default_ namespace.

```sh
kubectl get deployments --namespace default
```

`-n` is short for `--namespace`

```sh
kubectl get pods -n default
```

### Step 08: cleanup

Before continuing we&rsquo;ll need to clean up a few things.

Delete the nginx deployment

```sh
kubectl delete deployment nginx
```

Switch back to the default context

```sh
kubectl config use-context docker-desktop
```

## 7.4: More `kubectl`

### Controlling Output

Let&rsquo;s create a quick deployment for testing output and interaction:

```sh
kubectl create deployment nginx --image=nginx
kubectl scale --replicas=3 deployment nginx
```

Explore the various output of resources by trying the different output options. For example:

```sh
kubectl get deployment nginx -o [json|yaml|wide]
```

A useful output format is to filter the raw json output through jsonpath. This is handy for piping the output of a `kubectl get` command directly to other tools like `sed`, `awk`, `grep`, etc.

For example, let&rsquo;s figure out the command to see how we can list each namespace name by itself newline separated. Use this reference guide for help: https://kubernetes.io/docs/reference/kubectl/jsonpath

<textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

### Pod Interaction

Now that we&rsquo;ve played around a bit with `kubectl` and its output, let&rsquo;s explore some of the commands that interact with pods and containers. Determine the `kubectl` command in which the goal is the following:

Forward the nginx port 80 from one of the pods onto `localhost:8000` of your client machine.

<textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

Start a tail with follow of the logs on the same pod used above

<textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

Run a remote command inside the container to for example get the current date and time

<textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

### Filtering with Labels

We can also filter queries with `kubectl` using labels.

For instance, we can list pods with the label `tier=frontend`.

```sh
kubectl get pods -l tier=frontend
```

In our small cluster with only a few pods and deployments, filtering using labels won&rsquo;t provide much benefit. In large production environments however, this feature is crucial for locating specific resources amongst hundreds or even thousands of pods and deployments.

### Cleaning Up

Let&rsquo;s clean up the nginx deployment. Since we did not put nginx in its own namespace this time, we cannot simply delete the entire namespace to clean all. We must specifically clean up just the nginx deployment.

```sh
kubectl delete deployment nginx
```

## 7.5: Conclusion

Congratulations! You have successfully explored namespaces, managed your _kubeconfig_, and queried using labels.
