# 6: Kubernetes Networking

Estimated time: 30 minutes

**Important:** The Kubernetes of Docker Desktop binds directly to the ports of your local machine. That means the Ingress controller that will be installed in this lab will be started on port `80` which must be free on your machine.

## 6.1: Overview

In a prior lab, we exposed the gowebapp frontend using a `NodePort` service. In production, users will expect to access a web service on the standard http(s) ports, 80 and 443. With Kubernetes we can accomplish this with either `serviceType: LoadBalancer` (layer 4) or an `kind: Ingress` (layer 7). In this lab, we will modify the application to expose the frontend using an Ingress Controller and Ingress resource, which will allow users to connect to the application on `80`.

## 6.2: Modify gowebapp Service

Since we will be exposing the frontend using an Ingress controller, we no longer need to expose it using a `NodePort` service. We still need a `ClusterIP` service to provide load balancing for the pods. Let&rsquo;s modify the service to change it to `type: ClusterIP`.

### Step 01: Modify gowebapp-service.yaml

```
cd $K8S_LABS_HOME/gowebapp
```

Open `gowebapp-service.yaml` in your editor and change the service type to `ClusterIP`.

### Step 02: Update the service

Use `kubectl` to update the service

```
kubectl apply -f gowebapp-service.yaml
```

### Step 03: Verify the service type has changed

```
kubectl get service -l "app=gowebapp"
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get service -l "app=gowebapp"
NAME       TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
gowebapp   ClusterIP   10.105.139.221   <none>        8080/TCP   20h
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

The service type should now be `ClusterIP`.

## 6.2 Create an Ingress Resource

You must have an Ingress controller to satisfy an Ingress resource. Only creating an Ingress resource has no effect.

You may need to deploy an Ingress controller such as `ingress-nginx`. You can choose from a number of [Ingress controllers](https://kubernetes.io/docs/concepts/services-networking/ingress-controllers).

Ideally, all Ingress controllers should fit the reference specification. In reality, the various Ingress controllers operate slightly differently.

### The Ingress Controller

Your local environment&rsquo;s single-node Kubernetes cluster (Docker Desktop) does not come with an Ingress controller. So, let&rsquo;s deploy one.

Before running `kubectl apply`, take a moment to look 👀 at the [YAML file (https://github.com/kubernetes/ingress-nginx/blob/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml)](https://github.com/kubernetes/ingress-nginx/blob/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml) that will be applied to deploy an Ingress-NGINX controller for Kubernetes.

What objects are being created? Do you see a _Service_ (look for <code>kind:&nbsp;Service</code>)? How about a _Deployment_&nbsp;(<code>kind:&nbsp;Deployment</code>)?

Now, go ahead and apply the YAML manifest to deploy an Ingress-NGINX controller.

```sh
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml
```

A few pods should start in the `ingress-nginx` namespace:

```sh
kubectl get pods --namespace=ingress-nginx
```

_EXAMPLE OUTPUT_
```
NAME                                       READY   STATUS              RESTARTS   AGE
ingress-nginx-admission-create-fnp58       0/1     Completed           0          4m26s
ingress-nginx-admission-patch-2bbh9        0/1     Completed           1          4m26s
ingress-nginx-controller-6bd4d4486-6r6wh   1/1     Running             0          4m26s
                                           👆      👆
```

After a while, they should all be running. Optionally, you can use the following command to wait for the ingress controller pod to be up, running, and ready:

On \*nix,

```sh
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s
```

On Windows,

```sh
kubectl wait --namespace ingress-nginx ^
  --for=condition=ready pod ^
  --selector=app.kubernetes.io/component=controller ^
  --timeout=120s
```

### Test Ingress Controller

Check that the Ingress controller is deployed.

```sh
kubectl get services --namespace ingress-nginx
```
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get services --namespace ingress-nginx
NAME                                 TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)                      AGE
ingress-nginx-controller             LoadBalancer   10.98.133.50   localhost     80:32763/TCP,443:30379/TCP   2m35s
ingress-nginx-controller-admission   ClusterIP      10.109.2.54    <none>        443/TCP                      2m35s
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

_EXAMPLE OUTPUT_
```
NAME                                 TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
ingress-nginx-controller             LoadBalancer   10.97.110.135   localhost     80:31927/TCP,443:30602/TCP   1h
                                                                    👆
```

Test if the Ingress controller can be accessed. Alternatively, you can also open your browser to [http://localhost:80](http://localhost:80).

```sh
curl localhost:80
```

You should get an HTML 404 response from NGINX. Something like,

```html
<html>
<head><title>404 Not Found</title></head>
<body>
<center><h1>404 Not Found</h1></center>
<hr><center>nginx</center>
</body>
</html>
```

C:\DevOpsTraining\devops-101-labs>curl localhost:80
<html>
<head><title>404 Not Found</title></head>
<body>
<center><h1>404 Not Found</h1></center>
<hr><center>nginx</center>
</body>
</html>

This is expected, since there are no Ingress resources (i.e. no routing rules) defined yet. That&rsquo;s what we&rsquo;ll do next.

On most systems, if you don't have any other service of type `LoadBalancer` bound to port `80`, the Ingress controller will be assigned the `EXTERNAL-IP` of `localhost`, which means that it will be reachable on `localhost:80`. If that doesn&rsquo;t work, you might have to fall back to the `kubectl port-forward` method described later.

### Step 01: Define the Ingress Resource

```
cd $K8S_LABS_HOME/gowebapp
```

Use your preferred text editor to create a file called `gowebapp-ingress.yaml` and populate it with the contents below.

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress # 👈
metadata:
  name: gowebapp-ingress
spec:
  ingressClassName: nginx
  rules:
  - host: gowebapp.localdev.me # 👈
    http:
      paths:
      - pathType: Prefix # or ImplementationSpecific or Exact
        path: "/"
        backend:
          service:
            name: gowebapp
            port:
              number: 8080
```

### Step 02: Apply the Ingress Resource

Then create an ingress resource. The above routes `gowebapp.localdev.me` (host) to the `gowebapp` service.

```sh
kubectl apply -f gowebapp-ingress.yaml
```

### Step 03: Test access to gowebapp

Run the following command to verify the Ingress resource has been created:

```
kubectl get ingress
```

_EXAMPLE OUTPUT_
```
NAME               CLASS    HOSTS                  ADDRESS     PORTS   AGE
gowebapp-ingress   nginx    gowebapp.localdev.me   localhost   80      3m
                            👆
```

Test access to the application by accessing the ingress URL on your browser ([http://gowebapp.localdev.me](http://gowebapp.localdev.me)).

Now, when you access the URL, the gowebapp application should appear.

Alternatively, you can access the gowebapp through `curl`.

```sh
curl http://gowebapp.localdev.me
```

If that doesn&rsquo;t work ☝, you might have to fall back to the `kubectl port-forward` method to forward a local port (e.g. `8080`) to the Ingress controller. Note the `kubectl port-forward` command will stay running and not return. After you&rsquo;ve accessed the gowebapp through the Ingress resource deployed above, you can `Ctrl-C`/`Cmd-C` to cancel the command and return to the prompt.

```sh
kubectl port-forward --namespace=ingress-nginx service/ingress-nginx-controller 8080:80
```

At this point, with port forwarding, you should be able to access your deployment at `http://gowebapp.localdev.me:8080`.

Notice that the gowebapp _cannot_ be reached via `http://localhost:80` (or another port when using `kubectl port-forward`).

```sh
curl http://localhost:80
```

This is because, the Ingress controller (NGINX) is given a rule that only routes `gowebapp.localdev.me` requests to the gowebapp service.

Review the ingress specification and make sure you understand it. If you need help, please consult the reference documentation at https://kubernetes.io/docs/concepts/services-networking/ingress/

## 6.3: Conclusion

Congratulations! You have successfully exposed your application to the internet using an Ingress controller.
