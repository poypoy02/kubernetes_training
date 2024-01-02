# 5: Pod and Container Configurations

Estimated time: 45 minutes

## 5.1: Lab Goals

In this lab we&rsquo;ll work with resource requests and limits to ensure workloads have sufficient resources to operate. Then, we will work with liveness and readiness probes to proactively monitor the health of our applications.

## 5.2: Resource Requests

Resource _requests_ are a way for us to let Kubernetes know the _minimum required_ resources for a container in a pod to run. Kubernetes will use this to intelligently schedule a pod on the cluster by placing the pod only on a node with sufficient resources to satisfy the request.

Kubernetes keeps track of the allocated resources per node. If you are on cluster with administrator access you may also use the `kubectl describe node` command to view node resource utilization and capacity. In this lab environment we do not have that access but remember this command in the future for environments where you do.

### Step 01: Add resource requests to gowebapp

Modify `gowebapp-deployment.yaml` to add resource _requests_ for

- 256M of memory (256 Megabytes)
- 250m of CPU (250 millicpu)

<details>
  <summary>What units can be used to specify memory?</summary>
  <p></p>
  <p>When specifying memory, you can use fixed-point suffixes:</p>
  <ul>
    <li>E = Exabyte. 1E = 1,000,000,000,000,000,000 bytes</li>
    <li>P = Petabyte. 1P = 1,000,000,000,000,000 bytes</li>
    <li>T = Terabyte. 1T = 1,000,000,000,000 bytes</li>
    <li>G = Gigabyte. 1G = 1,000,000,000 bytes</li>
    <li>M = Megabyte. 1M = 1,000,000 bytes</li>
    <li>K = Kilobyte. 1K = 1,000 bytes</li>
  </ul>
  <p>You can also use the power-of-two equivalents:</p>
  <ul>
    <li>Ei = Exbibyte. 1Ei = 2⁶⁰ = 1,152,921,504,606,846,976 bytes</li>
    <li>Pi = Pebibyte. 1Pi = 2⁵⁰ = 1,125,899,906,842,624 bytes</li>
    <li>Ti = Tebibyte. 1Ti = 2⁴⁰ = 1,099,511,627,776 bytes</li>
    <li>Gi = Gibibyte. 1Gi = 2³⁰ = 1,073,741,824 bytes</li>
    <li>Mi = Mebibyte. 1Mi = 2²⁰ = 1,048,576 bytes</li>
    <li>Ki = Kibibyte. 1Ki = 2¹⁰ = 1,024 bytes</li>
  </ul>
</details>

### Step 02: Re-apply the gowebapp deployment

Use `kubectl` to update the gowebapp deployment.

```sh
cd $K8S_LABS_HOME/gowebapp
kubectl apply -f gowebapp-deployment.yaml
```

### Step 03: Verify the requests have been applied

You can verify that the resource requests were successfully applied by describing the deployment.

```sh
kubectl describe deployment gowebapp
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe deployment gowebapp
Name:                   gowebapp
Namespace:              default
CreationTimestamp:      Tue, 19 Dec 2023 13:52:14 +0800
Labels:                 app=gowebapp
                        tier=frontend
Annotations:            deployment.kubernetes.io/revision: 8
Selector:               app=gowebapp,tier=frontend
Replicas:               2 desired | 1 updated | 3 total | 0 available | 3 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=gowebapp
           tier=frontend
  Containers:
   gowebapp:
    Image:      gowebapp:v1
    Port:       8080/TCP
    Host Port:  0/TCP
    Requests:
      cpu:     250m
      memory:  256M
    Environment:
      DB_PASSWORD:  mypassword
    Mounts:         <none>
  Volumes:          <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Available      False   MinimumReplicasUnavailable
  Progressing    True    ReplicaSetUpdated
OldReplicaSets:  gowebapp-68f56d48fc (2/2 replicas created), gowebapp-575d57df76 (0/0 replicas created), gowebapp-77846f6d46 (0/0 replicas created)
NewReplicaSet:   gowebapp-7fdddfbb89 (1/1 replicas created)
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  17s   deployment-controller  Scaled up replica set gowebapp-7fdddfbb89 to 1
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

Scaling a deployment to a higher number of replicas increases resource utilization.

When a cluster&rsquo;s nodes no longer have enough capacity to accommodate additional pods, the scheduler will not be able to accommodate their deployment. The pod will stay in the _Pending_ state until something changes that allows Kubernetes to reach the desired state. Examples could include but are not limited to things like the following:

- New nodes are added to the cluster to allow for more free capacity
- The resource request value are adjusted on the deployment
- Other pods and deployments get deleted or reduced in size
- We reduce the number of desired pods for the `gowebapp` deployment

## 5.3: Resource Limits

Resource _limits_ define the _maximum amount_ of resources a container in a pod should consume. If a container exceeds this limit, it may be terminated. If you are on cluster with administrator access you may also use the `kubectl describe node` command to see how limits impact the allocations on the node.

### Step 01: Add resource limits to gowebapp

Modify `gowebapp-deployment.yaml` to add resource _limits_ for

- 512M of memory
- 500m of CPU (500 millicpu)

### Step 02: Re-apply the gowebapp deployment

Use `kubectl` to update the gowebapp deployment.

```sh
cd $K8S_LABS_HOME/gowebapp
kubectl apply -f gowebapp-deployment.yaml
```

### Step 03: Verify the limits have been applied

You can verify that the resource limits were successfully applied by describing the deployment.

```sh
kubectl describe deployment gowebapp
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe deployment gowebapp 
Name:                   gowebapp
Namespace:              default
CreationTimestamp:      Tue, 19 Dec 2023 13:52:14 +0800
Labels:                 app=gowebapp
                        tier=frontend
Annotations:            deployment.kubernetes.io/revision: 9
Selector:               app=gowebapp,tier=frontend
Replicas:               2 desired | 1 updated | 3 total | 0 available | 3 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=gowebapp
           tier=frontend
  Containers:
   gowebapp:
    Image:      gowebapp:v1
    Port:       8080/TCP
    Host Port:  0/TCP
    Limits:
      cpu:     500m
      memory:  512M
    Requests:
      cpu:     250m
      memory:  256M
    Environment:
      DB_PASSWORD:  mypassword
    Mounts:         <none>
  Volumes:          <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Available      False   MinimumReplicasUnavailable
  Progressing    True    ReplicaSetUpdated
OldReplicaSets:  gowebapp-68f56d48fc (1/1 replicas created), gowebapp-575d57df76 (0/0 replicas created), gowebapp-77846f6d46 (0/0 replicas created), gowebapp-7fdddfbb89 (1/1 replicas created)
NewReplicaSet:   gowebapp-5696d97c79 (1/1 replicas created)
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  117s  deployment-controller  Scaled up replica set gowebapp-7fdddfbb89 to 1
  Normal  ScalingReplicaSet  32s   deployment-controller  Scaled down replica set gowebapp-68f56d48fc to 1 from 2
  Normal  ScalingReplicaSet  32s   deployment-controller  Scaled up replica set gowebapp-5696d97c79 to 1 from 0
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

## 5.4: Probes

[Liveness and readiness probes](https://kubernetes.io/docs/concepts/workloads/pods/pod-lifecycle/#container-probes) are health checks that Kubernetes can perform that proactively monitor the health of pods and the containers within.

- _Liveness_ probes check if a container in a pod is still &ldquo;alive&rdquo;. If the probe fails, the container will be restarted.
- _Readiness_ probes check if a container in a pod is ready to serve requests from a service. If the probe fails, the pod will be removed as an endpoint for any matching services.

In this section we will configure both liveness and readiness probes.

### Step 01: Liveness Probe Exercise

Many applications running for long periods of time eventually transition to broken states, and cannot recover except by being restarted. Kubernetes provides _liveness probes_ to detect and remedy such situations. In this exercise, you create a pod that runs a container based on `busybox` image. Use your preferred text editor to create a file called `exec-liveness.yaml` and populate it with the contents below.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: exec-liveness
  labels:
    test: liveness
spec:
  securityContext:
    runAsUser: 1000
  containers:
  - name: liveness
    image: busybox
    args:
    - /bin/sh
    - -c
    - touch /tmp/healthy; sleep infinity
    livenessProbe:
      exec:
        command:
        - cat
        - /tmp/healthy
      initialDelaySeconds: 5
      periodSeconds: 5
```

In the configuration file, you can see that the pod has a single container. Under `.spec.containers[0].livenessProbe`:

- The `periodSeconds` field specifies that the kubelet should perform a liveness probe every 5 seconds.
- The `initialDelaySeconds` field tells the kubelet that it should wait 5 second before performing the first probe.
- To perform a probe, the kubelet executes the command `cat /tmp/healthy` in the container (see `exec.command` field).
    - If the command succeeds, it returns `0`, and the kubelet considers the container to be alive and healthy.
    - If the command returns a _non-zero_ value, the kubelet kills the container and restarts it.

When the container starts, it executes this command: `/bin/sh -c touch /tmp/healthy`. Deploy the pod:

```sh
kubectl apply -f exec-liveness.yaml
```

View events for the pod, and verify that no liveness probes have failed.

```sh
kubectl describe pod exec-liveness
```

_SAMPLE OUTPUT_
```
Type    Reason     Age   From               Message
----    ------     ----  ----               -------
Normal  Scheduled  3s    default-scheduler  Successfully assigned default/exec-liveness to master
Normal  Pulling    2s    kubelet, master    Pulling image "busybox"
Normal  Pulled     0s    kubelet, master    Successfully pulled image "busybox"
Normal  Created    0s    kubelet, master    Created container liveness
Normal  Started    0s    kubelet, master    Started container liveness
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe pod exec-liveness
Name:             exec-liveness
Namespace:        default
Priority:         0
Service Account:  default
Node:             docker-desktop/192.168.65.3
Start Time:       Wed, 20 Dec 2023 08:49:26 +0800
Labels:           test=liveness
Annotations:      <none>
Status:           Running
IP:               10.1.0.37
IPs:
  IP:  10.1.0.37
Containers:
  liveness:
    Container ID:  docker://e5954c6fc5d56d1fb8b671f1da76b1e0409a8c6682e67773cd3fd5c8974a47d9
    Image:         busybox
    Image ID:      docker-pullable://busybox@sha256:5c63a9b46e7139d2d5841462859edcbbf57f238af891b6096578e5894cfe5ae2
    Port:          <none>
    Host Port:     <none>
    Args:
      /bin/sh
      -c
      touch /tmp/healthy; sleep infinity
    State:          Running
      Started:      Wed, 20 Dec 2023 08:49:33 +0800
    Ready:          True
    Restart Count:  0
    Liveness:       exec [cat /tmp/healthy] delay=5s timeout=1s period=5s #success=1 #failure=3
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-8ppp8 (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             True
  ContainersReady   True
  PodScheduled      True
Volumes:
  kube-api-access-8ppp8:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  12s   default-scheduler  Successfully assigned default/exec-liveness to docker-desktop
  Normal  Pulling    11s   kubelet            Pulling image "busybox"
  Normal  Pulled     5s    kubelet            Successfully pulled image "busybox" in 6.644s (6.644s including waiting)
  Normal  Created    5s    kubelet            Created container liveness
  Normal  Started    5s    kubelet            Started container liveness
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

Trigger a probe failure by manually deleting `/tmp/healthy`.

```sh
kubectl exec exec-liveness -- rm /tmp/healthy
```

View events for the pod _after 5 seconds_, and verify that liveness probes have failed.

```
kubectl describe pod exec-liveness
```

_SAMPLE OUTPUT_
```
Type     Reason     Age              From               Message
----     ------     ----             ----               -------
Normal   Scheduled  26s              default-scheduler  Successfully assigned default/exec-liveness to master
Normal   Pulling    25s              kubelet, master    Pulling image "busybox"
Normal   Pulled     23s              kubelet, master    Successfully pulled image "busybox"
Normal   Created    23s              kubelet, master    Created container liveness
Normal   Started    23s              kubelet, master    Started container liveness
Warning  Unhealthy  1s (x2 over 6s)  kubelet, master    Liveness probe failed: cat: can't open '/tmp/healthy': No such file or directory
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe pod exec-liveness
Name:             exec-liveness
Namespace:        default
Priority:         0
Service Account:  default
Node:             docker-desktop/192.168.65.3
Start Time:       Wed, 20 Dec 2023 08:49:26 +0800
Labels:           test=liveness
Annotations:      <none>
Status:           Running
IP:               10.1.0.37
IPs:
  IP:  10.1.0.37
Containers:
  liveness:
    Container ID:  docker://e5954c6fc5d56d1fb8b671f1da76b1e0409a8c6682e67773cd3fd5c8974a47d9
    Image:         busybox
    Image ID:      docker-pullable://busybox@sha256:5c63a9b46e7139d2d5841462859edcbbf57f238af891b6096578e5894cfe5ae2
    Port:          <none>
    Host Port:     <none>
    Args:
      /bin/sh
      -c
      touch /tmp/healthy; sleep infinity
    State:          Running
      Started:      Wed, 20 Dec 2023 08:49:33 +0800
    Ready:          True
    Restart Count:  0
    Liveness:       exec [cat /tmp/healthy] delay=5s timeout=1s period=5s #success=1 #failure=3
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-8ppp8 (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             True
  ContainersReady   True
  PodScheduled      True
Volumes:
  kube-api-access-8ppp8:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type     Reason     Age                From               Message
  ----     ------     ----               ----               -------
  Normal   Scheduled  2m3s               default-scheduler  Successfully assigned default/exec-liveness to docker-desktop
  Normal   Pulling    2m2s               kubelet            Pulling image "busybox"
  Normal   Pulled     116s               kubelet            Successfully pulled image "busybox" in 6.644s (6.644s including waiting)
  Normal   Created    116s               kubelet            Created container liveness
  Normal   Started    116s               kubelet            Started container liveness
  Warning  Unhealthy  13s (x3 over 22s)  kubelet            Liveness probe failed: cat: can't open '/tmp/healthy': No such file or directory
  Normal   Killing    12s                kubelet            Container liveness failed liveness probe, will be restarted
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

At the bottom of the output, there are messages indicating that the liveness probes have failed, and the containers have been killed and recreated.

Since the probe failure triggers a pod restart, the containers will restart and run `touch /tmp/healthy`. Verify that the pod is running again:

```sh
kubectl get pods exec-liveness
```

See that the number of restarts has incremented to `1`.

After experiencing the _liveness_ probe, let's try the _readiness_ probe.

### Step 02: Readiness Probe Exercise

Applications running for long periods of time eventually transition to broken states, and should be taken out of service so that traffic no longer routes to them. Kubernetes provides _readiness probes_ to detect and handle such situations. In this exercise, you create a deployment that runs 2 pods based on `busybox` image. We will force a failure on one of the pods and watch it be taken out of service.

Use your preferred text editor to create a file called `exec-readiness.yaml` and populate it with the contents below.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: exec-readiness
  labels:
    test: readiness
spec:
  securityContext:
    runAsUser: 1000
  containers:
  - name: readiness
    image: busybox
    args:
    - /bin/sh
    - -c
    - touch /tmp/healthy; sleep infinity
    readinessProbe:
      exec:
        command:
        - cat
        - /tmp/healthy
      initialDelaySeconds: 5
      periodSeconds: 5
```

This configuration is pretty much the same as our liveness example, except we are using a readiness probe instead of liveness. To perform a probe, the kubelet executes the command `cat /tmp/healthy` in the container. If the command succeeds, it returns `0`, and the kubelet considers the container to be ready. If the command returns a _non-zero_ value, it will be taken out of service.

When the container starts, it executes this command: `/bin/sh -c touch /tmp/healthy`.

Create the pod.

```sh
kubectl apply -f exec-readiness.yaml
```

Create a service to front the pod:

```sh
kubectl expose pod exec-readiness --port=80
```

View events for the pod, and verify that no readiness probes have failed.

```sh
kubectl get pods exec-readiness -o wide
```
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pods exec-readiness -o wide
NAME             READY   STATUS    RESTARTS   AGE   IP          NODE             NOMINATED NODE   READINESS GATES
exec-readiness   1/1     Running   0          30s   10.1.0.38   docker-desktop   <none>           <none>
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

Take note of the IP address for the pod.

```sh
kubectl describe pod exec-readiness
```

_SAMPLE OUTPUT_
```
Type    Reason     Age   From               Message
----    ------     ----  ----               -------
Normal  Scheduled  23s   default-scheduler  Successfully assigned default/exec-readiness to master
Normal  Pulling    22s   kubelet, master    Pulling image "busybox"
Normal  Pulled     21s   kubelet, master    Successfully pulled image "busybox"
Normal  Created    20s   kubelet, master    Created container readiness
Normal  Started    20s   kubelet, master    Started container readiness
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe pod exec-readiness
Name:             exec-readiness
Namespace:        default
Priority:         0
Service Account:  default
Node:             docker-desktop/192.168.65.3
Start Time:       Wed, 20 Dec 2023 08:58:51 +0800
Labels:           test=readiness
Annotations:      <none>
Status:           Running
IP:               10.1.0.38
IPs:
  IP:  10.1.0.38
Containers:
  readiness:
    Container ID:  docker://039cb4f89c17f2f3d5be59b2d29fcd8657c36dc38593b4626d89c48c604506cd
    Image:         busybox
    Image ID:      docker-pullable://busybox@sha256:5c63a9b46e7139d2d5841462859edcbbf57f238af891b6096578e5894cfe5ae2
    Port:          <none>
    Host Port:     <none>
    Args:
      /bin/sh
      -c
      touch /tmp/healthy; sleep infinity
    State:          Running
      Started:      Wed, 20 Dec 2023 08:58:56 +0800
    Ready:          True
    Restart Count:  0
    Readiness:      exec [cat /tmp/healthy] delay=5s timeout=1s period=5s #success=1 #failure=3
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-688hn (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             True
  ContainersReady   True
  PodScheduled      True
Volumes:
  kube-api-access-688hn:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  58s   default-scheduler  Successfully assigned default/exec-readiness to docker-desktop
  Normal  Pulling    57s   kubelet            Pulling image "busybox"
  Normal  Pulled     53s   kubelet            Successfully pulled image "busybox" in 3.68s (3.68s including waiting)
  Normal  Created    53s   kubelet            Created container readiness
  Normal  Started    53s   kubelet            Started container readiness
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

View the service and verify that it lists IP address of both pods in the endpoints section:

```sh
kubectl get ep exec-readiness
```

_SAMPLE OUTPUT_
```
NAME             ENDPOINTS           AGE
exec-readiness   192.168.219.90:80   63s
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get ep exec-readiness
NAME             ENDPOINTS      AGE
exec-readiness   10.1.0.38:80   66s
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

Trigger a probe failure by manually deleting `/tmp/healthy`.

```sh
kubectl exec exec-readiness -- rm /tmp/healthy
```

View events for the pod _after 5 seconds_, and verify that readiness probes have failed.

```sh
kubectl describe pod exec-readiness
```

_SAMPLE OUTPUT_
```
Type     Reason     Age              From               Message
----     ------     ----             ----               -------
Normal   Scheduled  2m9s             default-scheduler  Successfully assigned default/exec-readiness to master
Normal   Pulling    2m8s             kubelet, master    Pulling image "busybox"
Normal   Pulled     2m7s             kubelet, master    Successfully pulled image "busybox"
Normal   Created    2m6s             kubelet, master    Created container readiness
Normal   Started    2m6s             kubelet, master    Started container readiness
Warning  Unhealthy  4s (x2 over 9s)  kubelet, master    Readiness probe failed: cat: can't open '/tmp/healthy': No such file or directory
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe pod exec-readiness
Name:             exec-readiness
Namespace:        default
Priority:         0
Service Account:  default
Node:             docker-desktop/192.168.65.3
Start Time:       Wed, 20 Dec 2023 08:58:51 +0800
Labels:           test=readiness
Annotations:      <none>
Status:           Running
IP:               10.1.0.38
IPs:
  IP:  10.1.0.38
Containers:
  readiness:
    Container ID:  docker://039cb4f89c17f2f3d5be59b2d29fcd8657c36dc38593b4626d89c48c604506cd
    Image:         busybox
    Image ID:      docker-pullable://busybox@sha256:5c63a9b46e7139d2d5841462859edcbbf57f238af891b6096578e5894cfe5ae2
    Port:          <none>
    Host Port:     <none>
    Args:
      /bin/sh
      -c
      touch /tmp/healthy; sleep infinity
    State:          Running
      Started:      Wed, 20 Dec 2023 08:58:56 +0800
    Ready:          False
    Restart Count:  0
    Readiness:      exec [cat /tmp/healthy] delay=5s timeout=1s period=5s #success=1 #failure=3
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-688hn (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             False
  ContainersReady   False
  PodScheduled      True
Volumes:
  kube-api-access-688hn:
    Type:                    Projected (a volume that contains injected data from multiple sources)
    TokenExpirationSeconds:  3607
    ConfigMapName:           kube-root-ca.crt
    ConfigMapOptional:       <nil>
    DownwardAPI:             true
QoS Class:                   BestEffort
Node-Selectors:              <none>
Tolerations:                 node.kubernetes.io/not-ready:NoExecute op=Exists for 300s
                             node.kubernetes.io/unreachable:NoExecute op=Exists for 300s
Events:
  Type     Reason     Age               From               Message
  ----     ------     ----              ----               -------
  Normal   Scheduled  4m50s             default-scheduler  Successfully assigned default/exec-readiness to docker-desktop
  Normal   Pulling    4m50s             kubelet            Pulling image "busybox"
  Normal   Pulled     4m46s             kubelet            Successfully pulled image "busybox" in 3.68s (3.68s including waiting)
  Normal   Created    4m46s             kubelet            Created container readiness
  Normal   Started    4m46s             kubelet            Started container readiness
  Warning  Unhealthy  0s (x5 over 15s)  kubelet            Readiness probe failed: cat: can't open '/tmp/healthy': No such file or directory
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

At the bottom of the output, there are messages indicating that the readiness probes have failed, and pod gets marked not ready.

Since the readiness probe failure triggers pod to be taken out of service, the pod will no longer be listed as an endpoint for the service.

```sh
kubectl get ep exec-readiness
```

_SAMPLE OUTPUT_
```
NAME             ENDPOINTS   AGE
exec-readiness               2m39s
```
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get ep exec-readiness
NAME             ENDPOINTS   AGE
exec-readiness               5m18s
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

Unlike the liveness probe, this problem will require manual intervention before the pod is put back in service. To restore the pod, run the following:

```sh
kubectl exec exec-readiness -- touch /tmp/healthy
```

Wait _10 seconds_, and verify that pod is now back in service.

```sh
kubectl get ep exec-readiness
```
_SAMPLE OUTPUT_
```
NAME             ENDPOINTS           AGE
exec-readiness   192.168.219.90:80   3m20s
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get ep exec-readiness
NAME             ENDPOINTS      AGE
exec-readiness   10.1.0.38:80   5m55s
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

ENDPOINTS should show IP address for the pod again.

Cleanup the pods and services we created so we only have the `gowebapp`:

```sh
kubectl delete service exec-readiness
kubectl delete pod exec-liveness
kubectl delete pod exec-readiness
```

### Step 03: Add Probes to the `gowebapp-mysql` Pod

#### Design the probes and update the Deployment

Now that we&rsquo;ve learned about the different kinds of probes and experimented with pre-made examples above, spend a few moments and figure out what probes makes sense to add to our `gowebapp-mysql` pod.

🤔

<textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

Got your list written down?

<details>
  <summary>Compare it to what we have listed below and what we will perform in the lab.</summary>
  <p><ol>
    <li>Make sure port <code>3306</code> is open and listening for a connection</li>
    <li>Make sure the <code>gowebapp</code> database exists and we can execute a query from one of the tables</li>
  </ol></p>
  <p>Hopefully you came up with something similiar.</p>
</details>

Now let&rsquo;s layer on the Kubernetes specific part of this. What probe type (liveness/readiness) should be used for each and what handler should be used (TCP, HTTP, EXEC)?

🤔

<textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

Figure out which ones to use?

<details>
  <summary>Compare it to what we have listed below and what we will perform in the lab.</summary>
  <p><ol>
    <li>Port Check - Liveness using TCP handler</li>
    <li>DB Query - Readiness using an EXEC handler executing a SQL query</li>
  </ol></p>
  <p>Great!</p>
</details>

Now modify the `gowebapp-mysql-deployment.yaml` file to add both of the aforementioned probes to it. Below are some values and hints to help you with adding the probes:

- use the following DB query value for the `exec.command` field of the `readiness` probe:
    - `["mysql", "-uroot", "-pmypassword", "-e", "use gowebapp; select count(*) from user"]`
- override the default options for each probe accordingly:
    - Liveness Probe: TCP check (port 3306)
        - `initialDelaySeconds: 20`
        - `periodSeconds: 5`
        - `timeoutSeconds: 1`
    - Readiness Probe: EXEC
        - `initialDelaySeconds: 25`
        - `periodSeconds: 10`
        - `timeoutSeconds: 1`

After you&rsquo;ve made the updates to the YAML file apply the updated configuration:

```sh
kubectl apply -f gowebapp-mysql-deployment.yaml
```

Wait a bit and then verify the pod started up OK and that the probes show up when describing the pod:

On \*nix,

```sh
kubectl get pods
kubectl describe pod -l 'tier=backend' | grep 'Liveness\|Readiness'
```

On Windows,

```sh
kubectl get pods
kubectl describe pod -l "tier=backend" | findstr "Liveness Readiness"
```

#### Validate the liveness probe

When a liveness probe fails, Kubernetes will try to restart the pod. In our database pod, its difficult to try to turn off the port at runtime, so instead, let&rsquo;s check it by updating the probe to check port `3307` instead of `3306`. Update the `gowebapp-mysql-deployment.yaml` accordingly.

We will then apply it and monitor the pods. Since we delay the probe 20 seconds, it fires every 5 seconds, and it requires 3 consequtive failures to fail the probe we should see the container start resetting about 35 seconds after it initial starts and then every 15 seconds after that. We will apply the new configuration and then immediately watch the pods.

```sh
kubectl apply -f gowebapp-mysql-deployment.yaml
kubectl get pods -w
```

Note the second `kubectl` command will stay running and not return. After you&rsquo;ve waited about a minute and seen the behavior described above you can `Ctrl-C`/`Cmd-C` to cancel the command and return to a prompt.

Now we can return everything back to normal (liveness probe back to checking port `3306`).

```sh
kubectl apply -f gowebapp-mysql-deployment.yaml
sleep 10
kubectl get pods
```

On Windows, use `timeout` instead of `sleep`.

#### Validate the readiness probe

When a readiness probe fails, Kubernetes will remove the pod from receiving traffic behind a Kubernetes Service. This failure is easier to simulate at runtime. We will rename one of the database tables so that the query the probe is executing will fail. In this scenario the database is still alive, but it is not ready.

On \*nix,

```sh
MYSQL_POD=$(kubectl get pod -l "tier=backend" -o jsonpath='{.items[0].metadata.name}')
kubectl exec $MYSQL_POD -- mysql -uroot -pmypassword -e "USE gowebapp; RENAME TABLE user TO user2"
```

On Windows,

```sh
kubectl get pod -l "tier=backend" -o jsonpath="{.items[0].metadata.name}" > tmp
set /p MYSQL_POD= < tmp
del tmp
kubectl exec %MYSQL_POD% -- mysql -uroot -pmypassword -e "USE gowebapp; RENAME TABLE user TO user2"
```

We can validate the pod is no longer &ldquo;Ready&rdquo; a few ways:

When listing the pods we will see `0/1` under `READY` instead of the normal `1/1`

```sh
sleep 30
kubectl get pods
```

_EXAMPLE OUTPUT_
```console
$ kubectl get pods
NAME                              READY   STATUS    RESTARTS   AGE
gowebapp-6bdcf89bbf-4vwrk         1/1     Running   3          3h21m
gowebapp-6bdcf89bbf-f589d         1/1     Running   3          3h21m
gowebapp-mysql-546867b499-z2xx2   0/1     Running   0          33m
                                  👆
```

We can look at the events for the pod and notice the readiness probe fail and the pod go Unhealthy:

On \*nix,

```sh
kubectl describe pod $MYSQL_POD
```

On Windows,

```sh
kubectl describe pod %MYSQL_POD%
```

_EXAMPLE OUTPUT_
```console
$ kubectl get pods
Events:
   Type     Reason     Age                  From               Message
   ----     ------     ----                 ----               -------
   Normal   Scheduled  <unknown>            default-scheduler  Successfully assigned default/gowebapp-mysql-546867b499-z2xx2 to master
   Normal   Pulled     36m                  kubelet, master    Container image "localhost:5000/gowebapp-mysql:v1" already present on machine
   Normal   Created    36m                  kubelet, master    Created container gowebapp-mysql
   Normal   Started    36m                  kubelet, master    Started container gowebapp-mysql
   Warning  Unhealthy  100s (x49 over 29m)  kubelet, master    Readiness probe failed: Warning: Using a password on the command line interface can be insecure.
ERROR 1146 (42S02) at line 1: Table 'gowebapp.user' doesn't exist
```

We can also look at the Endpoints for the Service and notice that no pods are listed:

```sh
kubectl describe service gowebapp-mysql
```

_EXAMPLE OUTPUT_
```console
$ kubectl describe service gowebapp-mysql
Name:              gowebapp-mysql
Namespace:         default
Labels:            app=gowebapp-mysql
                  tier=backend
Annotations:       kubectl.kubernetes.io/last-applied-configuration:
                    {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{},"labels":{"app":"gowebapp-mysql","tier":"backend"},"name":"gowebapp-mysql...
Selector:          app=gowebapp-mysql,tier=backend
Type:              ClusterIP
IP:                10.109.57.182
Port:              <unset>  3306/TCP
TargetPort:        3306/TCP
Endpoints:
Session Affinity:  None
Events:            <none>
```

Now that we&rsquo;re done validating, we can restore the missing database table and watch things return to normal.

On \*nix,

```sh
kubectl exec $MYSQL_POD -- mysql -uroot -pmypassword -e "USE gowebapp; RENAME TABLE user2 TO user"
sleep 10
kubectl get pods
```

On Windows,

```sh
kubectl exec %MYSQL_POD% -- mysql -uroot -pmypassword -e "USE gowebapp; RENAME TABLE user2 TO user"
timeout 10
kubectl get pods
```

### Step 04: Add Probes to the gowebapp Pods

#### Design the probes and update the Deployment

Spend a few moments and figure out what probes makes sense to add to our `gowebapp` webserver pod.

🤔

<textarea rows="5" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>

Got your list written down?

<details>
  <summary>Compare it to what we have listed below and what we will perform in the lab.</summary>
  <p><ol>
    <li>Make sure port <code>8080</code> is open and listening for a connection</li>
    <li>Make sure the website content renders correctly</li>
    <li>Make sure that a web call that actually causes database <strong>read</strong> activity works</li>
    <li>Make sure that a web call that actually causes database <strong>write</strong> activity works</li>
  </ol></p>
  <p>Hopefully you came up with the same or similar ones. The first two are easier and more straight forward and we will implement those in this lab. Unfortunately the last two are difficult because of the design of the <code>gowebapp</code> code. Why?</p>
  <p>The last two are difficult because they are not backed by a proper API. The form post to <code>/login</code> actually still returns a successful <code>200</code> response code with HTML. We would have to write a script and place it inside the <code>gowebapp</code> Docker image that could do all that logic and then we would call that script with an <code>EXEC</code> handler probe. This is a good example of updating your application to have a specific URLs to call and wire up your probes against is a good idea. ☝ We will not be doing this in this lab because it is out-of-scope for this training. We will only focus on the first two items. Nonetheless, this is an opportunity to improve collaboration between development/developers and operations/operators (DevOps&nbsp;🤔).</p>
  <p>Now, let&rsquo;s focus on the first two items.</p>
</details>

Modify the `gowebapp-deployment.yaml` file to add the first two aforementioned probes to it. Below are some values and hints to help you with adding the probes:

- use a _Liveness_ probe to do a TCP check on port `8080`
- use a _Readiness_ probe to do a HTTP GET check on the root path of the site `/`
- specifically define the following options for both probes:
    - `periodSeconds: 5`
    - `timeoutSeconds: 1`

After you&rsquo;ve made the updates to the YAML file apply the updated configuration:

```sh
kubectl apply -f gowebapp-deployment.yaml
```

Wait a bit and then verify the pod started up OK and that the probes show up when describing the pod:

On \*nix,

```sh
kubectl get pods
kubectl describe pod -l 'tier=frontend' | grep 'Liveness\|Readiness'
```

On Windows,

```sh
kubectl get pods
kubectl describe pod -l "tier=frontend" | findstr "Liveness Readiness"
```

We&rsquo;ve already seen a few times how readiness probes behave and it should be the same here with the gowebapp webserver pods. Feel free to simulate failures if you want.

## 5.5: Conclusion

Congratulations! You have successfully added resource requests/limits and probes to your application. These features are essential to ensuring that your application has the resources it requires and that Kubernetes can effectively monitor and address issues that may arise with it. Additionally, the probe exercises and samples show how to leverage built-in Kubernetes tooling in your own applications.
