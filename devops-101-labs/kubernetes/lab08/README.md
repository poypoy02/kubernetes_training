# 8: Storage & Stateful Applications

Estimated time: 30 minutes

## 8.1: Lab Goals

In this lab, you will be converting your MySQL deployment into a StatefulSet. You will deploy it and see how a Persistent Volume is dynamically provisioned for it. You will then demonstrate that the state of your MySQL database successfully persists after deleting and recreating the pod.

## 8.2: Convert MySQL Deployment to a StatefulSet

### Step 1: Remove MySQL Deployment

Before converting the gowebapp-mysql deployment to a StatefulSet, you need to remove the existing deployment from the Kubernetes cluster.

```sh
kubectl delete deployment gowebapp-mysql
```

### Step 2: Convert MySQL from Deployment to StatefulSet

```sh
cd $K8S_LABS_HOME/gowebapp
```

Use your preferred text editor to create a file called `gowebapp-mysql-sts.yaml` and populate it with the lines below. The YAML manifest is based on `gowebapp-mysql-deployment.yaml`, but converted to a StatefulSet. Note the use of `spec.template.spec.containers[0].volumeMounts`, and `spec.volumeClaimTemplates`.

***Note: Replace TODO comments with the appropriate commands***

`gowebapp-mysql-sts.yaml`

```yaml
apiVersion: apps/v1
kind: # TODO: Set kind to StatefulSet
metadata: 
  name: gowebapp-mysql
  labels:
    app: gowebapp-mysql
    tier: backend
spec:
  serviceName: # TODO: Set serviceName to gowebapp-mysql
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
          value: mypassword
        ports:
        - containerPort: 3306
        # In some IDEs, warnings are due to lack of resource limits.
        # Although not indicated in the lab instructions,
        # resource limits are set below.
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
            command: ["mysql", "-uroot", "-pmypassword", "-e", "use gowebapp; select count(*) from user"]
          initialDelaySeconds: 25
          periodSeconds: 10
          timeoutSeconds: 1
        volumeMounts:
        - name: mysql-pv
          mountPath: # TODO: Set mountPath to /var/lib/mysql
  volumeClaimTemplates:
  - metadata:
      name: mysql-pv
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: # TODO: Set storage request to 1Gi
```

For each VolumeClaimTemplate entry defined in a StatefulSet (see `spec.volumeClaimTemplates[0]`), each Pod receives one PersistentVolumeClaim. The PersistentVolumeClaim will provide stable storage using PersistentVolumes provisioned by a PersistentVolume Provisioner.

### Step 3: Create the MySQL StatefulSet

Warning: Ensure that you deleted the gowebapp-mysql-deployment (Step 1) before applying the new StatefulSet.

```
kubectl apply -f gowebapp-mysql-sts.yaml
```

### Step 4: Check that a persistent volume claim was created and bound

To satisfy the persistent volume claim, you need to:

1. Either have a dynamic persistent volume provisioner with a default `StorageClass`,
2. Or statically provision `PersistentVolume`s yourself.

In the Kubernetes cluster (of Docker Desktop), a dynamic persistent volume provisioner is provided (`docker.io/hostpath`) and the default storage class is `hostpath`. This is verified by running `kubectl get storageclass`.

Or, `kubectl get sc` where `sc` is the shortname of `StorageClass`.

_EXAMPLE OUTPUT_
```
NAME                 PROVISIONER          RECLAIMPOLICY   VOLUMEBINDINGMODE   ALLOWVOLUMEEXPANSION   AGE
hostpath (default)   docker.io/hostpath   Delete          Immediate           false                  3d
                     👆
```

The persistent volume claim should bind to a dynamically provisioned persistent volume. 

```
kubectl get pv,pvc
```

_EXAMPLE OUTPUT_
```
NAME                                                        CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                               STORAGECLASS   REASON   AGE
persistentvolume/pvc-ca84f581-f07a-4aee-a73d-2cc9e1c4835e   1Gi        RWO            Delete           Bound    default/mysql-pv-gowebapp-mysql-0   hostpath                4m44s

NAME                                              STATUS   VOLUME                                     CAPACITY   ACCESS MODES   STORAGECLASS   AGE
persistentvolumeclaim/mysql-pv-gowebapp-mysql-0   Bound    pvc-ca84f581-f07a-4aee-a73d-2cc9e1c4835e   1Gi        RWO            hostpath       4m44s
```

In the above output, the persistent volume claim is `mysql-pv-gowebapp-mysql-0` and is satisfied by a dynamically provisioned persistent volume `pvc-ca84f581-f07a-4aee-a73d-2cc9e1c4835e`.

## 8.3: Test your application

### Step 1: Access your application

You should be able to access the application through the URL ([http://gowebapp.localdev.me](http://gowebapp.localdev.me)) in your browser.

Sign up for an account, login and use the Notepad. Remember the account (and notes in your notepad). You&rsquo;ll use it verify that the MySQL pod was able to recover properly after simulating a failure.

Warning: Your browser may cache an old instance of the gowebapp application from previous labs. When the webpage loads, look at the top right. If you see &lsquo;Logout&rsquo;, click it. You can then proceed with creating a new test account.

### Step 2: Create a file on the persistent volume

```sh
kubectl exec -it gowebapp-mysql-0 -- touch /var/lib/mysql/Persistence
```

### Step 3: Verify the file on the persistent volume

```sh
kubectl exec -it gowebapp-mysql-0 -- ls /var/lib/mysql/Persistence
```

## 8.4: Illustrate Failure and Recovery of Database State

### Step 1: Monitor StatefulSet

Set up a watch on the stateful set to monitor it. ☝ Open a new tab/terminal and run the following:

```sh
kubectl get sts gowebapp-mysql -w
```

We&rsquo;ll refer to this as the _watch_ tab/terminal.

Note the `-w` (`--watch`, watch for changes) option will keep the `kubectl get` command running and not return. After you&rsquo;ve seen the gowebapp-mysql pod restart through the StatefulSet deployed above, you can `Ctrl-C`/`Cmd-C` to cancel the command and return to the prompt.

### Step 2: Find the name of the MySQL pod

Switch to the _original_ tab/terminal, and run the following:

On \*nix,

```sh
kubectl get pod -l 'app=gowebapp-mysql'
```

On Windows,

```sh
kubectl get pod -l "app=gowebapp-mysql"
```

### Step 3: Kill the pod

```
kubectl delete pod <mysql_pod_name>
```

The StatefulSet will automatically relaunch it.

### Step 4: Monitor StatefulSet

Switch to the _watch_ tab/terminal and view the results.

Monitor the StatefulSet until it&rsquo;s ready again (AVAILABLE turns from 0 to 1).

Use `Ctrl-C`/`Cmd-C` to return to the prompt and close this _watch_ tab/terminal.

## 8.5: Confirm Persistent Volume Kept Application State

### Step 1: Find the name of the new MySQL pod and inspect it

On \*nix,

```sh
kubectl get pod -l 'app=gowebapp-mysql'
```

On Windows,

```sh
kubectl get pod -l "app=gowebapp-mysql"
```

```
kubectl describe pod <mysql_pod_name>
```

You should see that the volume has been re-mounted to the new pod.

### Step 2: Ensure gowebapp can still access the pre-failure data

You should be able to access the application through the URL ([http://gowebapp.localdev.me](http://gowebapp.localdev.me)) in your browser.

Test that you can still login using the username and password you created earlier in this lab. Do you still see your notes?

### Step 3: Check the persistent volume location for the file you created called Persistence

```sh
kubectl exec -it gowebapp-mysql-0 -- ls /var/lib/mysql
```

## 8.6: Conclusion

Congratulations! You have successfully converted your MySQL deployment to a StatefulSet and demonstrated that the data survives pod replacement.
