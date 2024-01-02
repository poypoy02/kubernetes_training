# 3: Kubernetes Troubleshooting

Estimated time: 30 minutes

When deploying to Kubernetes, it&rsquo;s important to understand the relationship between objects and how to troubleshoot when issues arise with your deployments. In this lab, we&rsquo;ll explore some of those relationships as well as various troubleshooting steps you can follow when things go wrong.

## 3.1: Exploring the Event Log

Kubernetes has an event log which captures a vast amount of information regarding changes, warnings, and errors on the cluster. If you encounter problems after a successful `kubectl apply`, the event log will often contain useful troubleshooting information.

Note: By default, the Kubernetes cluster retains one hour of event log history. The retention period can be adjusted by the cluster administrator. Ideally, the event log should be shipped to a centralized logging system.

### Step 01: View the event log

You can quickly see the entire contents of the event log using kubectl get events.

```sh
kubectl get events
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get events
LAST SEEN   TYPE     REASON              OBJECT                           MESSAGE
18m         Normal   Pulled              pod/gowebapp-68f56d48fc-ft24d    Container image "gowebapp:v1" already present on machine
18m         Normal   Created             pod/gowebapp-68f56d48fc-ft24d    Created container gowebapp
18m         Normal   Started             pod/gowebapp-68f56d48fc-ft24d    Started container gowebapp
18m         Normal   Scheduled           pod/gowebapp-68f56d48fc-qlj5q    Successfully assigned default/gowebapp-68f56d48fc-qlj5q to docker-desktop
18m         Normal   Pulled              pod/gowebapp-68f56d48fc-qlj5q    Container image "gowebapp:v1" already present on machine
18m         Normal   Created             pod/gowebapp-68f56d48fc-qlj5q    Created container gowebapp
18m         Normal   Started             pod/gowebapp-68f56d48fc-qlj5q    Started container gowebapp
18m         Normal   SuccessfulCreate    replicaset/gowebapp-68f56d48fc   Created pod: gowebapp-68f56d48fc-ft24d
18m         Normal   SuccessfulCreate    replicaset/gowebapp-68f56d48fc   Created pod: gowebapp-68f56d48fc-qlj5q
18m         Normal   ScalingReplicaSet   deployment/gowebapp              Scaled up replica set gowebapp-68f56d48fc to 2

### Step 02: Watch the event log

You can watch the event log in real time by adding a `-w` to the end of the command. It&rsquo;s helpful to run this in a separate terminal before running any `kubectl apply` commands. This will allow you to watch the events related to the apply process in real time.

```sh
kubectl get events -w

# Use ctrl-c / cmd-c to return to the shell
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get events -w
LAST SEEN   TYPE     REASON              OBJECT                           MESSAGE
19m         Normal   Scheduled           pod/gowebapp-68f56d48fc-ft24d    Successfully assigned default/gowebapp-68f56d48fc-ft24d to docker-desktop
19m         Normal   Pulled              pod/gowebapp-68f56d48fc-ft24d    Container image "gowebapp:v1" already present on machine
19m         Normal   Created             pod/gowebapp-68f56d48fc-ft24d    Created container gowebapp
19m         Normal   Started             pod/gowebapp-68f56d48fc-ft24d    Started container gowebapp
19m         Normal   Scheduled           pod/gowebapp-68f56d48fc-qlj5q    Successfully assigned default/gowebapp-68f56d48fc-qlj5q to docker-desktop
19m         Normal   Pulled              pod/gowebapp-68f56d48fc-qlj5q    Container image "gowebapp:v1" already present on machine
19m         Normal   Created             pod/gowebapp-68f56d48fc-qlj5q    Created container gowebapp
19m         Normal   Started             pod/gowebapp-68f56d48fc-qlj5q    Started container gowebapp
19m         Normal   SuccessfulCreate    replicaset/gowebapp-68f56d48fc   Created pod: gowebapp-68f56d48fc-ft24d
19m         Normal   SuccessfulCreate    replicaset/gowebapp-68f56d48fc   Created pod: gowebapp-68f56d48fc-qlj5q
19m         Normal   ScalingReplicaSet   deployment/gowebapp              Scaled up replica set gowebapp-68f56d48fc to 2
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

### Step 03: View the event log for a specific object

If you only want to see the event log entries related to a specific object, you can do so by running `kubectl describe <resource_type> <resource_name>`. At the end of the output, you will see the most recent event log entries for that object.

```sh
kubectl describe deployment gowebapp

# The event log entries will be at the very end of the output.
```
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe deployment gowebapp
Name:                   gowebapp
Namespace:              default
CreationTimestamp:      Tue, 19 Dec 2023 13:28:53 +0800
Labels:                 app=gowebapp
                        tier=frontend
Annotations:            deployment.kubernetes.io/revision: 1
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
    Image:      gowebapp:v1
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
OldReplicaSets:  <none>
NewReplicaSet:   gowebapp-68f56d48fc (2/2 replicas created)
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  21m   deployment-controller  Scaled up replica set gowebapp-68f56d48fc to 2
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

## 3.2: Exploring a Deployment

When you created the `gowebapp` and `gowebapp-mysql` deployments earlier, Kubernetes automatically created a series of additional objects eventually resulting in the creation of containers running the application.

### Step 01: List and describe the deployment

Let&rsquo;s start by getting a list of the deployments currently on the Kubernetes cluster.

Note: Since events only stick around for 60 minutes, it&rsquo;s useful to delete and recreate the deployment for this exercise. In a real-world scenario, you&rsquo;d be able to get expired logs from a logging backend.

```sh
kubectl delete deployment gowebapp
kubectl apply -f $K8S_LABS_HOME/gowebapp/gowebapp-deployment.yaml
kubectl get deployments
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl delete deployment gowebapp
deployment.apps "gowebapp" deleted
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl apply -f gowebapp-deployment.yaml
deployment.apps/gowebapp created
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get deployments
NAME             READY   UP-TO-DATE   AVAILABLE   AGE
gowebapp         2/2     2            2           8s
gowebapp-mysql   1/1     1            1           95m

And now let&rsquo;s get more detail about the `gowebapp` frontend deployment.

```sh
kubectl describe deployment gowebapp
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe deployment gowebapp
Name:                   gowebapp
Namespace:              default
CreationTimestamp:      Tue, 19 Dec 2023 13:52:14 +0800
Labels:                 app=gowebapp
                        tier=frontend
Annotations:            deployment.kubernetes.io/revision: 1
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
    Image:      gowebapp:v1
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
OldReplicaSets:  <none>
NewReplicaSet:   gowebapp-68f56d48fc (2/2 replicas created)
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  24s   deployment-controller  Scaled up replica set gowebapp-68f56d48fc to 2
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

The event log at the end of that output is especially helpful. It will tell you the most recent cluster events related to that object. In this case we can see that the deployment controller scaled up a new replica set for our application.

### Step 02: List and describe the replicaset

Deployments create replica sets. Replica sets are used to make it easy to roll from one version of a deployment to another. Each time we update a deployment, a new replica set is created that contains the latest configuration.

Let&rsquo;s take a look at the replicasets currently on the Kubernetes cluster.

```sh
kubectl get replicasets
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get replicasets
NAME                        DESIRED   CURRENT   READY   AGE
gowebapp-68f56d48fc         2         2         2       2m17s
gowebapp-mysql-66486c8fc5   1         1         1       97m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

You can see that for each deployment, there is a corresponding replica set. Now let&rsquo;s get more detail about the `gowebapp` replicaset.

```sh
# You'll need the name of the gowebapp replicaset from the previous command
kubectl describe rs <replica_set_name>
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe rs gowebapp-68f56d48fc
Name:           gowebapp-68f56d48fc
Namespace:      default
Selector:       app=gowebapp,pod-template-hash=68f56d48fc,tier=frontend
Labels:         app=gowebapp
                pod-template-hash=68f56d48fc
                tier=frontend
                deployment.kubernetes.io/max-replicas: 3
                deployment.kubernetes.io/revision: 1
Controlled By:  Deployment/gowebapp
Replicas:       2 current / 2 desired
Pods Status:    2 Running / 0 Waiting / 0 Succeeded / 0 Failed
Pod Template:
  Labels:  app=gowebapp
           pod-template-hash=68f56d48fc
           tier=frontend
  Containers:
   gowebapp:
    Image:      gowebapp:v1
    Port:       8080/TCP
    Host Port:  0/TCP
    Environment:
      DB_PASSWORD:  mypassword
    Mounts:         <none>
  Volumes:          <none>
Events:
  Type    Reason            Age    From                   Message
  ----    ------            ----   ----                   -------
  Normal  SuccessfulCreate  3m     replicaset-controller  Created pod: gowebapp-68f56d48fc-dmmkr
  Normal  SuccessfulCreate  2m59s  replicaset-controller  Created pod: gowebapp-68f56d48fc-5qhn8
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe rs gowebapp-mysql-66486c8fc5
Name:           gowebapp-mysql-66486c8fc5
Namespace:      default
Selector:       app=gowebapp-mysql,pod-template-hash=66486c8fc5,tier=backend
Labels:         app=gowebapp-mysql
                pod-template-hash=66486c8fc5
                tier=backend
Annotations:    deployment.kubernetes.io/desired-replicas: 1
                deployment.kubernetes.io/max-replicas: 1
                deployment.kubernetes.io/revision: 1
Controlled By:  Deployment/gowebapp-mysql
Replicas:       1 current / 1 desired
Pods Status:    1 Running / 0 Waiting / 0 Succeeded / 0 Failed
Pod Template:
  Labels:  app=gowebapp-mysql
           pod-template-hash=66486c8fc5
           tier=backend
  Containers:
   gowebapp-mysql:
    Image:      gowebapp-mysql:v1
    Port:       3306/TCP
    Host Port:  0/TCP
    Environment:
      MYSQL_ROOT_PASSWORD:  mypassword
    Mounts:                 <none>
  Volumes:                  <none>
Events:                     <none>
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

Note: In the above command, we used the abbreviation `rs` instead of `replicasets`. Many resource types have abbreviated forms. For a complete list, you can run `kubectl api-resources`.

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl api-resources
NAME                              SHORTNAMES   APIVERSION                             NAMESPACED   KIND
bindings                                       v1                                     true         Binding
componentstatuses                 cs           v1                                     false        ComponentStatus
configmaps                        cm           v1                                     true         ConfigMap
endpoints                         ep           v1                                     true         Endpoints
events                            ev           v1                                     true         Event
limitranges                       limits       v1                                     true         LimitRange
namespaces                        ns           v1                                     false        Namespace
nodes                             no           v1                                     false        Node
persistentvolumeclaims            pvc          v1                                     true         PersistentVolumeClaim
persistentvolumes                 pv           v1                                     false        PersistentVolume
pods                              po           v1                                     true         Pod
podtemplates                                   v1                                     true         PodTemplate
replicationcontrollers            rc           v1                                     true         ReplicationController
resourcequotas                    quota        v1                                     true         ResourceQuota
secrets                                        v1                                     true         Secret
serviceaccounts                   sa           v1                                     true         ServiceAccount
services                          svc          v1                                     true         Service
mutatingwebhookconfigurations                  admissionregistration.k8s.io/v1        false        MutatingWebhookConfiguration
validatingwebhookconfigurations                admissionregistration.k8s.io/v1        false        ValidatingWebhookConfiguration
customresourcedefinitions         crd,crds     apiextensions.k8s.io/v1                false        CustomResourceDefinition
apiservices                                    apiregistration.k8s.io/v1              false        APIService
controllerrevisions                            apps/v1                                true         ControllerRevision
daemonsets                        ds           apps/v1                                true         DaemonSet
deployments                       deploy       apps/v1                                true         Deployment
replicasets                       rs           apps/v1                                true         ReplicaSet
statefulsets                      sts          apps/v1                                true         StatefulSet
selfsubjectreviews                             authentication.k8s.io/v1               false        SelfSubjectReview
tokenreviews                                   authentication.k8s.io/v1               false        TokenReview
localsubjectaccessreviews                      authorization.k8s.io/v1                true         LocalSubjectAccessReview
selfsubjectaccessreviews                       authorization.k8s.io/v1                false        SelfSubjectAccessReview
selfsubjectrulesreviews                        authorization.k8s.io/v1                false        SelfSubjectRulesReview
subjectaccessreviews                           authorization.k8s.io/v1                false        SubjectAccessReview
horizontalpodautoscalers          hpa          autoscaling/v2                         true         HorizontalPodAutoscaler
cronjobs                          cj           batch/v1                               true         CronJob
jobs                                           batch/v1                               true         Job
certificatesigningrequests        csr          certificates.k8s.io/v1                 false        CertificateSigningRequest
leases                                         coordination.k8s.io/v1                 true         Lease
endpointslices                                 discovery.k8s.io/v1                    true         EndpointSlice
events                            ev           events.k8s.io/v1                       true         Event
flowschemas                                    flowcontrol.apiserver.k8s.io/v1beta3   false        FlowSchema
prioritylevelconfigurations                    flowcontrol.apiserver.k8s.io/v1beta3   false        PriorityLevelConfiguration
ingressclasses                                 networking.k8s.io/v1                   false        IngressClass
ingresses                         ing          networking.k8s.io/v1                   true         Ingress
networkpolicies                   netpol       networking.k8s.io/v1                   true         NetworkPolicy
runtimeclasses                                 node.k8s.io/v1                         false        RuntimeClass
poddisruptionbudgets              pdb          policy/v1                              true         PodDisruptionBudget
clusterrolebindings                            rbac.authorization.k8s.io/v1           false        ClusterRoleBinding
clusterroles                                   rbac.authorization.k8s.io/v1           false        ClusterRole
rolebindings                                   rbac.authorization.k8s.io/v1           true         RoleBinding
roles                                          rbac.authorization.k8s.io/v1           true         Role
priorityclasses                   pc           scheduling.k8s.io/v1                   false        PriorityClass
csidrivers                                     storage.k8s.io/v1                      false        CSIDriver
csinodes                                       storage.k8s.io/v1                      false        CSINode
csistoragecapacities                           storage.k8s.io/v1                      true         CSIStorageCapacity
storageclasses                    sc           storage.k8s.io/v1                      false        StorageClass
volumeattachments                              storage.k8s.io/v1                      false        VolumeAttachment
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

The event log for the replica set shows that it created two pods.

### Step 03: List and describe the pods

You&rsquo;ve now seen that deployments create replicasets, and replicasets create pods. Now let&rsquo;s look at the pods. Let&rsquo;s start by getting a list of the deployments currently on the Kubernetes cluster.

```sh
kubectl get pods
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pods
NAME                              READY   STATUS    RESTARTS   AGE
gowebapp-68f56d48fc-5qhn8         1/1     Running   0          4m8s
gowebapp-68f56d48fc-dmmkr         1/1     Running   0          4m8s
gowebapp-mysql-66486c8fc5-dstj2   1/1     Running   0          99m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

You can see that there are three pods. Two pods for the `gowebapp` frontend, and one for the MySQL backend. This corresponds to the replicas field we set in each deployment.

Now let&rsquo;s get more details about one of the `gowebapp` frontend pods.

```sh
# You'll need the name of one of the gowebapp pods from the previous command
kubectl describe pod <pod_name>
```
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe pod gowebapp-68f56d48fc-5qhn8
Name:             gowebapp-68f56d48fc-5qhn8
Namespace:        default
Priority:         0
Service Account:  default
Node:             docker-desktop/192.168.65.3
Start Time:       Tue, 19 Dec 2023 13:52:15 +0800
Labels:           app=gowebapp
                  pod-template-hash=68f56d48fc
                  tier=frontend
Annotations:      <none>
Status:           Running
IP:               10.1.0.17
IPs:
  IP:           10.1.0.17
Controlled By:  ReplicaSet/gowebapp-68f56d48fc
Containers:
  gowebapp:
    Container ID:   docker://fa56d8491aeb0c197568a0764caa60a9e002b591033452b820018a46936fb6df
    Image:          gowebapp:v1
    Image ID:       docker://sha256:0e74afa941df7b4d7222ef844996bc854207e6b760f4c5438931a893f218caf3
    Port:           8080/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Tue, 19 Dec 2023 13:52:15 +0800
    Ready:          True
    Restart Count:  0
    Environment:
      DB_PASSWORD:  mypassword
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-rwcdn (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             True
  ContainersReady   True
  PodScheduled      True
Volumes:
  kube-api-access-rwcdn:
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
  Type    Reason     Age    From               Message
  ----    ------     ----   ----               -------
  Normal  Scheduled  5m50s  default-scheduler  Successfully assigned default/gowebapp-68f56d48fc-5qhn8 to docker-desktop
  Normal  Pulled     5m50s  kubelet            Container image "gowebapp:v1" already present on machine
  Normal  Created    5m50s  kubelet            Created container gowebapp
  Normal  Started    5m50s  kubelet            Started container gowebapp
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

The event log for the pod shows that the pod was scheduled to a node, and that the node created and started a container.

### Step 04: Get pod logs

Now that we know how to get a list of pods, we can use a pod name to get its logs. This is the equivalent of running docker logs `<container_id>`. If your application streams logs to `stdout` and `stderr`, they will be accessible via this command.

```sh
# If your pod has only one container
kubectl logs <pod_name>

# If your pod has more than one container
kubectl logs <pod_name> -c <container_name>
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl logs gowebapp-68f56d48fc-5qhn8
2023-12-19 05:52:15 AM Running HTTP :8080
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

## 3.3: Exploring a Service

Just like with a deployment, a service has relationships with other objects that are important to understand. Let&rsquo;s explore this now.

### Step 01: List and describe the service

Let&rsquo;s start by getting a list of the services currently on the Kubernetes cluster.

```sh
kubectl get services
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get services
NAME             TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
gowebapp         NodePort    10.105.139.221   <none>        8080:32211/TCP   38m
gowebapp-mysql   ClusterIP   10.96.12.157     <none>        3306/TCP         126m
kubernetes       ClusterIP   10.96.0.1        <none>        443/TCP          41h
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

And now let&rsquo;s get more detail about the `gowebapp` frontend service.

```sh
kubectl describe service gowebapp
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe service gowebapp
Name:                     gowebapp
Namespace:                default
Labels:                   app=gowebapp
                          tier=frontend
Annotations:              <none>
Selector:                 app=gowebapp,tier=frontend
Type:                     NodePort
IP Family Policy:         SingleStack
IP Families:              IPv4
IP:                       10.105.139.221
IPs:                      10.105.139.221
LoadBalancer Ingress:     localhost
Port:                     <unset>  8080/TCP
TargetPort:               8080/TCP
NodePort:                 <unset>  32211/TCP
Endpoints:                10.1.0.17:8080,10.1.0.18:8080
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

One of the notable fields is Endpoints. Each endpoint is the IP address of a pod that is backing that service. In this case, we should see two endpoints, one for each of the two `gowebapp` frontend pods created by the `gowebapp` deployment.

We can also see a list of endpoints for each service by running the following command.

```sh
kubectl get endpoints
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get endpoints
NAME             ENDPOINTS                       AGE
gowebapp         10.1.0.17:8080,10.1.0.18:8080   39m
gowebapp-mysql   10.1.0.14:3306                  126m
kubernetes       192.168.65.3:6443               41h
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

If you see fewer endpoints than you expect. It&rsquo;s a good indicator that there&rsquo;s an issue with either your pods or service.

## 3.4: Troubleshooting a Deployment

Now that you&rsquo;ve seen some of the commands used for exploring objects and their relationships in Kubernetes, let&rsquo;s simulate a failure and see how to troubleshoot it.

### Step 01: Modify the gowebapp deployment

Let&rsquo;s start by modifying the gowebapp deployment to deliberately introduce a failure.

```sh
cd $K8S_LABS_HOME/gowebapp
```

Use your preferred text editor to edit `gowebapp-deployment.yaml`. Change the `image:` field from using tag `v1` to `v99`.

The image `gowebapp:v99` doesn&rsquo;t exist, so this should cause a number of failures.

### Step 02: Apply the updated deployment

Use `kubectl` to apply the updated deployment.

```sh
kubectl apply -f gowebapp-deployment.yaml
```

### Step 03: Explore the results

Run the following commands to see what information Kubernetes provides when there&rsquo;s a problem with a deployment.

```sh
# Note that gowebapp deployment no longer shows 2 pods "UP-TO-DATE"
kubectl get deployments

# Note that there's a second, new replica set for gowebapp, but it shows '0' pods ready
kubectl get rs

# Note that you still have 2 endpoints available for the gowebapp service. Kubernetes won't
# destroy the old pods until the new ones are healthy. This has prevented the application from
# failing despite the configuration error.
kubectl get ep

# Note that there's one new pod for gowebapp that is failing to start. The status is
# ``ImagePullBackOff`` which indicates that Docker is unable to pull the image defined in the
# deployment.
kubectl get pods

# Finally, if you describe the pod and review its event log, you'll see an error message that
# states ``Failed to pull image "gowebapp:v99"``.
kubectl describe pod <name_of_failed_pod>
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get deployments
NAME             READY   UP-TO-DATE   AVAILABLE   AGE
gowebapp         2/2     1            2           8m44s
gowebapp-mysql   1/1     1            1           103m

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get rs
NAME                        DESIRED   CURRENT   READY   AGE
gowebapp-575d57df76         1         1         0       23s
gowebapp-68f56d48fc         2         2         2       8m49s
gowebapp-mysql-66486c8fc5   1         1         1       103m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get ep
NAME             ENDPOINTS                       AGE
gowebapp         10.1.0.17:8080,10.1.0.18:8080   52m
gowebapp-mysql   10.1.0.14:3306                  139m
kubernetes       192.168.65.3:6443               41h
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pods
NAME                              READY   STATUS             RESTARTS   AGE
gowebapp-575d57df76-g2xr9         0/1     ImagePullBackOff   0          12m
gowebapp-68f56d48fc-5qhn8         1/1     Running            0          21m
gowebapp-68f56d48fc-dmmkr         1/1     Running            0          21m
gowebapp-mysql-66486c8fc5-dstj2   1/1     Running            0          116m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe pod gowebapp-575d57df76-g2xr9 
Name:             gowebapp-575d57df76-g2xr9
Namespace:        default
Priority:         0
Service Account:  default
Node:             docker-desktop/192.168.65.3
Start Time:       Tue, 19 Dec 2023 14:00:40 +0800
Labels:           app=gowebapp
                  pod-template-hash=575d57df76
                  tier=frontend
Annotations:      <none>
Status:           Pending
IP:               10.1.0.19
IPs:
  IP:           10.1.0.19
Controlled By:  ReplicaSet/gowebapp-575d57df76
Containers:
  gowebapp:
    Container ID:
    Image:          gowebapp:v99
    Image ID:
    Port:           8080/TCP
    Host Port:      0/TCP
    State:          Waiting
      Reason:       ImagePullBackOff
    Ready:          False
    Restart Count:  0
    Environment:
      DB_PASSWORD:  mypassword
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-4kh2n (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             False
  ContainersReady   False
  PodScheduled      True
Volumes:
  kube-api-access-4kh2n:
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
  Type     Reason     Age                  From               Message
  ----     ------     ----                 ----               -------
  Normal   Scheduled  13m                  default-scheduler  Successfully assigned default/gowebapp-575d57df76-g2xr9 to docker-desktop
  Normal   Pulling    11m (x4 over 13m)    kubelet            Pulling image "gowebapp:v99"
  Warning  Failed     11m (x4 over 13m)    kubelet            Failed to pull image "gowebapp:v99": Error response from daemon: pull access denied for gowebapp, repository does not exist or may require 'docker login': denied: requested access to the resource is denied
  Warning  Failed     11m (x4 over 13m)    kubelet            Error: ErrImagePull
  Warning  Failed     10m (x6 over 13m)    kubelet            Error: ImagePullBackOff
  Normal   BackOff    3m2s (x40 over 13m)  kubelet            Back-off pulling image "gowebapp:v99"
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

### Step 04: Fix the Problem

```sh
cd $K8S_LABS_HOME/gowebapp
```

Use your preferred text editor to edit `gowebapp-deployment.yaml`. Change the image: field back to `gowebapp:v1`. Then re-apply `gowebapp-deployment.yaml`.

```
kubectl apply -f gowebapp-deployment.yaml
```

### Step 05: Verify the Fix

Re-run the commands from step 03 and verify that everything has returned to normal.

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get deployments
NAME             READY   UP-TO-DATE   AVAILABLE   AGE
gowebapp         2/2     2            2           24m
gowebapp-mysql   1/1     1            1           119m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get rs
NAME                        DESIRED   CURRENT   READY   AGE
gowebapp-575d57df76         0         0         0       16m
gowebapp-68f56d48fc         2         2         2       24m
gowebapp-mysql-66486c8fc5   1         1         1       119m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get ep
NAME             ENDPOINTS                       AGE
gowebapp         10.1.0.17:8080,10.1.0.18:8080   56m
gowebapp-mysql   10.1.0.14:3306                  144m
kubernetes       192.168.65.3:6443               41h
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pods 
NAME                              READY   STATUS    RESTARTS   AGE
gowebapp-68f56d48fc-5qhn8         1/1     Running   0          32m
gowebapp-68f56d48fc-dmmkr         1/1     Running   0          32m
gowebapp-mysql-66486c8fc5-dstj2   1/1     Running   0          127m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl describe pod gowebapp-68f56d48fc-5qhn8
Name:             gowebapp-68f56d48fc-5qhn8
Namespace:        default
Priority:         0
Service Account:  default
Node:             docker-desktop/192.168.65.3
Start Time:       Tue, 19 Dec 2023 13:52:15 +0800
Labels:           app=gowebapp
                  pod-template-hash=68f56d48fc
                  tier=frontend
Annotations:      <none>
Status:           Running
IP:               10.1.0.17
IPs:
  IP:           10.1.0.17
Controlled By:  ReplicaSet/gowebapp-68f56d48fc
Containers:
  gowebapp:
    Container ID:   docker://fa56d8491aeb0c197568a0764caa60a9e002b591033452b820018a46936fb6df
    Image:          gowebapp:v1
    Image ID:       docker://sha256:0e74afa941df7b4d7222ef844996bc854207e6b760f4c5438931a893f218caf3
    Port:           8080/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Tue, 19 Dec 2023 13:52:15 +0800
    Ready:          True
    Restart Count:  0
    Environment:
      DB_PASSWORD:  mypassword
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from kube-api-access-rwcdn (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             True
  ContainersReady   True
  PodScheduled      True
Volumes:
  kube-api-access-rwcdn:
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
  Normal  Scheduled  33m   default-scheduler  Successfully assigned default/gowebapp-68f56d48fc-5qhn8 to docker-desktop
  Normal  Pulled     33m   kubelet            Container image "gowebapp:v1" already present on machine
  Normal  Created    33m   kubelet            Created container gowebapp
  Normal  Started    33m   kubelet            Started container gowebapp
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> 

## 3.5: Troubleshooting a Service

Now let&rsquo;s look at how we can troubleshoot issues related to services. A typical issue you will encounter is a mismatch between a service&rsquo;s selectors and the target pods&rsquo; labels.

### Step 01: Modify the gowebapp service

Let&rsquo;s start by modifying the `gowebapp` service to deliberately introduce a failure.

```
cd $K8S_LABS_HOME/gowebapp
```

Use your preferred text editor to edit `gowebapp-service.yaml`. Change the selector <code>app:&nbsp;gowebapp</code> to <code>app:&nbsp;pythonwebapp</code>.

_Warning: Ensure you modify <code>app:&nbsp;gowebapp</code> in the `selector:` section, not the `labels:` section._

### Step 02: Apply the updated service

Use `kubectl` to apply the updated service.

```sh
kubectl apply -f gowebapp-service.yaml
```

### Step 03: Explore the results

The first, most obvious result of introducing a failure here is that `gowebapp` will no longer be accessible from a browser. Go ahead and confirm this.

Next, run the following commands to see what information Kubernetes provides when there&rsquo;s a mismatch between a service&rsquo;s selectors and the target pods&rsquo; labels.

```sh
# Note that just "getting" gowebapp gives no indications of a problem.
kubectl get service

# Note that the endpoints field is blank. This means the service can't find any pods to send
# requests to.
kubectl describe service gowebapp

# You can also list the endpoints by running the following command. The ``ENDPOINTS`` field here
# should also be blank.
kubectl get ep gowebapp

# Note the list of selectors
kubectl describe service gowebapp

# Get a list of pods
kubectl get pods

# Note the labels applied to the pod don't match the selectors from the service
kubectl get pod <name_of_a_gowebapp_pod> --show-labels
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get service
NAME             TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
gowebapp         NodePort    10.105.139.221   <none>        8080:32211/TCP   67m
gowebapp-mysql   ClusterIP   10.96.12.157     <none>        3306/TCP         154m
kubernetes       ClusterIP   10.96.0.1        <none>        443/TCP          41h
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get ep gowebapp
NAME       ENDPOINTS   AGE
gowebapp   <none>      69m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pods
NAME                              READY   STATUS    RESTARTS   AGE
gowebapp-68f56d48fc-5qhn8         1/1     Running   0          39m
gowebapp-68f56d48fc-dmmkr         1/1     Running   0          39m
gowebapp-mysql-66486c8fc5-dstj2   1/1     Running   0          134m
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp> kubectl get pod gowebapp-68f56d48fc-5qhn8 --show-labels
NAME                        READY   STATUS    RESTARTS   AGE   LABELS
gowebapp-68f56d48fc-5qhn8   1/1     Running   0          39m   app=gowebapp,pod-template-hash=68f56d48fc,tier=frontend
PS C:\DevOpsTraining\devops-101-labs\kubernetes\gowebapp>

### Step 04: Fix the Problem

```sh
cd $K8S_LABS_HOME/gowebapp
```

Use your preferred text editor to edit `gowebapp-service.yaml`. Change the selector back to `app: gowebapp`. Then re-apply `gowebapp-service.yaml`.

```sh
kubectl apply -f gowebapp-service.yaml
```

### Step 05: Verify the Fix

Re-run the commands from step 03 and verify that everything has returned to normal.

## 3.6: Keep Exploring

You can use `kubectl get` and `kubectl describe` on any resource type in Kubernetes. You can run `kubectl api-resources` to get a list of all the available resource types. As we introduce more resource types throughout the remainder of the course, run these commands to see what information each of them provides.

## 3.7: Conclusion

In this lab we explored the relationships between several Kubernetes resource types. We then explored various commands that can be used to explore and troubleshoot problems with those resources.

Group together the resource types. Which resource types are grouped under Service? Which resource types grouped under Deployment?

1. Service
2. Deployment
3. Endpoint
4. Pod
5. ReplicaSet

<p>
<em>Write down your answers here. You may be asked during the lab exercise debrief.</em>
<textarea rows="3" style="width: 100%; max-width: 60em" placeholder="Temporarily paste your answers here." spellcheck="false"></textarea>
</p>

If you need to clean up, you can use `kubectl delete` and the YAML manifests.

```sh
cd $K8S_LABS_HOME/gowebapp
kubectl delete -f gowebapp-service.yaml,gowebapp-deployment.yaml
kubectl delete -f gowebapp-mysql-service.yaml,gowebapp-mysql-deployment.yaml
```
