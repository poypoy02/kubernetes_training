# 11: Security

Estimated time: 30 minutes

## 11.1: Lab Goals

In this lab, you will be scanning the containerized image for vulnerabilities. You'll also be creating a user and binding it to an existing cluster-wide role to demonstrate that the user&rsquo;s access is limited by the given role(s) &mdash; role-based access control.

## 11.2: Scan Image for Common Vulnerabilities and Exposures (CVEs)

Let&rsquo;s run some commands to scan the `gowebapp` images. Docker Scout will require you to login to a Docker Hub account to be able to pull the latest known CVEs. Create one if you do not have one yet. You can login using Docker Desktop GUI or `docker login`.

`docker scout` CLI plugin is available by default on Docker Desktop starting with version 4.17. To install manually, see [https://github.com/docker/scout-cli](https://github.com/docker/scout-cli).

```sh
docker scout quickview gowebapp:v1
```
To get more details,

```sh
docker scout cves gowebapp:v1
```

Given the details of the CVEs, what do you think needs to be done to try to resolve them? Does it have anything to do with the `gowebapp` (app, not the container image) or the base image used to build the `gowebapp` image?

**There is a 3 GB size limit on images analyzed by Docker Scout in Docker Desktop.**

In case you&rsquo;re wondering, the `docker scan` command is deprecated and will no longer be supported after April 13th, 2023.

## 11.3: Create New User and Bind to a ClusterRole

Kubernetes supports several user authentication methods. It also supports combining more than one to authenticate a user. If one of the chained methods fail, the user is not verified. In this lab, we&rsquo;ll use only one authentication method, the [X.509](https://en.wikipedia.org/wiki/X.509) certificate to create a user named "john".

### Step 1: Generate New User Credentials

Instead of generating certificates on the host, we will use Docker containers to create SSL certificates. The Docker Hub Nginx image conveniently comes with OpenSSL built-in.

On \*nix,

```sh
docker run -it --rm -v "$PWD":/work \
    nginx openssl req -out /work/user.csr -subj "/CN=john" \
    -new -newkey rsa:2048 -noenc -keyout /work/user.key
```

On Windows,

```sh
docker run -it --rm -v "%cd%":/work ^
    nginx openssl req -out /work/user.csr -subj "/CN=john" ^
    -new -newkey rsa:2048 -noenc -keyout /work/user.key
```

The current working directory on the host should now have two files:

1. `user.key` - a new private key for the new user
2. `user.csr` - a certificate signing request (CSR) for the new user (with the given private key)

### Step 2: Complete Certificate Signing Request

Create a `CertificateSigningRequest` (Kubernetes object) and have it approved.

Use your preferred text editor to create a file called `csr.yaml` and populate it with the lines below.

**Note: Replace TODO comments with the appropriate commands.**

```yaml
apiVersion: certificates.k8s.io/v1
kind: CertificateSigningRequest
metadata:
  name: john
spec:
  # TODO paste base-64 encoded CSR to .spec.request (single-line)
  request: 
  # Note for AWS users: change .spec.signerName to beta.eks.amazonaws.com/app-serving
  signerName: kubernetes.io/kube-apiserver-client
  expirationSeconds: 864000 # one day
  usages:
    - client auth
```

☝ The _certificate signing request (CSR)_ is in `user.csr`. Convert it to a base-64 encoded string by using containers. The commands below  will display the Base-64 encoded string on the console. Copy it and paste it into `csr.yaml`.

On \*nix,

```sh
docker run -it --rm -v "$PWD":/work \
    nginx bash -c "cat /work/user.csr | base64 | tr -d \"\n\""
```

On Windows, 

```sh
docker run -it --rm -v "%cd%":/work ^
    nginx bash -c "cat /work/user.csr | base64 | tr -d \"\n\""
```

After completing `csr.yaml`, create the `CertificateSigningRequest`.

```console
$ kubectl apply -f csr.yaml
certificatesigningrequest.certificates.k8s.io/john created
```

Verify that a `CertificateSigningRequest` named `john` is `Pending`.

```console
$ kubectl get csr
NAME   AGE   SIGNERNAME                            REQUESTOR            REQUESTEDDURATION   CONDITION
john   3s    kubernetes.io/kube-apiserver-client   docker-for-desktop   10d                 Pending
👆                                                                                           👆
```

NAME   AGE   SIGNERNAME                            REQUESTOR            REQUESTEDDURATION   CONDITION
john   10s   kubernetes.io/kube-apiserver-client   docker-for-desktop   10d                 Pending
PS C:\DevOpsTraining\devops-101-labs\kubernetes\lab11> 

Approve the `CertificateSigningRequest` named `john`.

```console
$ kubectl certificate approve john
certificatesigningrequest.certificates.k8s.io/john approved
```

Verify that a `CertificateSigningRequest` named `john` is `Approved`.

```console
$ kubectl get csr
NAME   AGE   SIGNERNAME                            REQUESTOR            REQUESTEDDURATION   CONDITION
john   52s   kubernetes.io/kube-apiserver-client   docker-for-desktop   10d                 Approved,Issued
👆                                                                                           👆
```
PS C:\DevOpsTraining\devops-101-labs\kubernetes\lab11> kubectl get csr
NAME   AGE   SIGNERNAME                            REQUESTOR            REQUESTEDDURATION   CONDITION
john   30s   kubernetes.io/kube-apiserver-client   docker-for-desktop   10d                 Approved,Issued
PS C:\DevOpsTraining\devops-101-labs\kubernetes\lab11>

See how the `CertificateSigningRequest` has been approved, and it now has the _signed certificate_.

```console
$ kubectl get csr john -o yaml
apiVersion: certificates.k8s.io/v1
kind: CertificateSigningRequest
metadata:
  # ...output ommitted...
  name: john
  # ...output ommitted...
spec:
  expirationSeconds: 864000
  # ...output ommitted...
  request: ... # 👈 Signing request 
  signerName: kubernetes.io/kube-apiserver-client
  usages:
  - client auth
  username: docker-for-desktop
status:
  certificate: ... # 👈 Signed certificate 👀
  conditions:
  - lastTransitionTime: # ...output ommitted...
    lastUpdateTime: # ...output ommitted...
    message: This CSR was approved by kubectl certificate approve.
    reason: KubectlApprove
    status: "True"
    type: Approved
```

PS C:\DevOpsTraining\devops-101-labs\kubernetes\lab11> kubectl get csr john -o yaml
apiVersion: certificates.k8s.io/v1
kind: CertificateSigningRequest
metadata:
  annotations:
    kubectl.kubernetes.io/last-applied-configuration: |
      {"apiVersion":"certificates.k8s.io/v1","kind":"CertificateSigningRequest","metadata":{"annotations":{},"name":"john"},"spec":{"expirationSeconds":864000,"request":"LS0tLS1CRUdJTiBDRVJUSUZJQ0FURSBSRVFVRVNULS0tLS0KTUlJQ1ZEQ0NBVHdDQVFBd0R6RU5NQXNHQTFVRUF3d0VhbTlvYmpDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRApnZ0VQQURDQ0FRb0NnZ0VCQU5CR2huQTlXMStRV3B6c3BmcUc1MkJpTjdtT05SdG03ME43OStaVGZuS2VkMlJKCkN3S0VtRTRiWFFHTE9oWXRjWGNYOEcvTW5wQ09vNGlHVkxoeDBmdkhpSGw0MmRISStPQkNPa0VLTFAwcStFdTMKYTBDemZFZ2t3UUovZUY0dVJ2L0xXMXZZTjBabnQ2M09rbS9WeFhGNFFYUHVsRmtvUS9ySVhkYUZGcHJ4eEFlSApZUWdyV0JoZXJiSEp3SUJIVUZGOGdTRnJTV3JWd3F1ekhWK3R0dXNIS3hYRUFGZmVnbnQ4SzZrckMyY0pzSExnCkZnS3NSVGJkSW1icS82L2pCVGNiQm1IOXA1ZW1TRzVOTmNSaG9OYTdGUkRzWWxYRVB3bDNYNVJwcjFzQVdNN3gKT01xeUhuUzYxSFRzWnc4YzJ5Q1JWWCtsVDRBalpzd3A0bVR6cDRzQ0F3RUFBYUFBTUEwR0NTcUdTSWIzRFFFQgpDd1VBQTRJQkFRQUdielN1VXp5bzF1dktUaDc2bzBNS3J6a2ZjdEVRVVRFZitqQ3lKaXJlUFJwVkRibXFEQUpFCmd4bDB6a0s5ZTVwUTVwdWEwSFhGcVhxVXE5Y0o2M1ZId0JSVXhBSWYzSVJyZnowSGJPYjFrQVBnRUZwbnFrREwKWlBDVUhET1V0dUtjNFJwcEMreUlNWTM0MXlLclFya2xzNDRsUmZBTFBzNzVKVEJxQkRPSHZjYmxwOHRYZTBMYwp2RmlhT25BYVVpVDJsOW0yanJseTJnb3hiZUZ4RnJ6S0U4SlNERUdhei9mbFBtU3l0WUFEaFlEUWpQeWpMY2dNCktGcnlnNmpjNlhMTmRWbFNHblY2TEdaeW9lVGdHZWs3YUFVRzI4NUs0VHUrMlUrSE83dmVCQ1hIblhDaGRoNFQKM2s3YlZqR2lKVzlhZTVnV3lOR1dFMkJ2NldURmViWi8KLS0tLS1FTkQgQ0VSVElGSUNBVEUgUkVRVUVTVC0tLS0tCg==","signerName":"kubernetes.io/kube-apiserver-client","usages":["client auth"]}}
  creationTimestamp: "2023-12-20T06:58:41Z"
  name: john
  resourceVersion: "122034"
  uid: effb351f-bd9f-4f12-9d06-d874f9598f51
spec:
  expirationSeconds: 864000
  groups:
  - system:masters
  - system:authenticated
  request: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURSBSRVFVRVNULS0tLS0KTUlJQ1ZEQ0NBVHdDQVFBd0R6RU5NQXNHQTFVRUF3d0VhbTlvYmpDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRApnZ0VQQURDQ0FRb0NnZ0VCQU5CR2huQTlXMStRV3B6c3BmcUc1MkJpTjdtT05SdG03ME43OStaVGZuS2VkMlJKCkN3S0VtRTRiWFFHTE9oWXRjWGNYOEcvTW5wQ09vNGlHVkxoeDBmdkhpSGw0MmRISStPQkNPa0VLTFAwcStFdTMKYTBDemZFZ2t3UUovZUY0dVJ2L0xXMXZZTjBabnQ2M09rbS9WeFhGNFFYUHVsRmtvUS9ySVhkYUZGcHJ4eEFlSApZUWdyV0JoZXJiSEp3SUJIVUZGOGdTRnJTV3JWd3F1ekhWK3R0dXNIS3hYRUFGZmVnbnQ4SzZrckMyY0pzSExnCkZnS3NSVGJkSW1icS82L2pCVGNiQm1IOXA1ZW1TRzVOTmNSaG9OYTdGUkRzWWxYRVB3bDNYNVJwcjFzQVdNN3gKT01xeUhuUzYxSFRzWnc4YzJ5Q1JWWCtsVDRBalpzd3A0bVR6cDRzQ0F3RUFBYUFBTUEwR0NTcUdTSWIzRFFFQgpDd1VBQTRJQkFRQUdielN1VXp5bzF1dktUaDc2bzBNS3J6a2ZjdEVRVVRFZitqQ3lKaXJlUFJwVkRibXFEQUpFCmd4bDB6a0s5ZTVwUTVwdWEwSFhGcVhxVXE5Y0o2M1ZId0JSVXhBSWYzSVJyZnowSGJPYjFrQVBnRUZwbnFrREwKWlBDVUhET1V0dUtjNFJwcEMreUlNWTM0MXlLclFya2xzNDRsUmZBTFBzNzVKVEJxQkRPSHZjYmxwOHRYZTBMYwp2RmlhT25BYVVpVDJsOW0yanJseTJnb3hiZUZ4RnJ6S0U4SlNERUdhei9mbFBtU3l0WUFEaFlEUWpQeWpMY2dNCktGcnlnNmpjNlhMTmRWbFNHblY2TEdaeW9lVGdHZWs3YUFVRzI4NUs0VHUrMlUrSE83dmVCQ1hIblhDaGRoNFQKM2s3YlZqR2lKVzlhZTVnV3lOR1dFMkJ2NldURmViWi8KLS0tLS1FTkQgQ0VSVElGSUNBVEUgUkVRVUVTVC0tLS0tCg==
  signerName: kubernetes.io/kube-apiserver-client
  usages:
  - client auth
  username: docker-for-desktop
status:
  certificate: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUM5RENDQWR5Z0F3SUJBZ0lRY3NZSUpoaWJ0V1JVdjVlZ0xUSUtMakFOQmdrcWhraUc5dzBCQVFzRkFEQVYKTVJNd0VRWURWUVFERXdwcmRXSmxjbTVsZEdWek1CNFhEVEl6TVRJeU1EQTJOVFF3TmxvWERUSXpNVEl6TURBMgpOVFF3Tmxvd0R6RU5NQXNHQTFVRUF4TUVhbTlvYmpDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDCkFRb0NnZ0VCQU5CR2huQTlXMStRV3B6c3BmcUc1MkJpTjdtT05SdG03ME43OStaVGZuS2VkMlJKQ3dLRW1FNGIKWFFHTE9oWXRjWGNYOEcvTW5wQ09vNGlHVkxoeDBmdkhpSGw0MmRISStPQkNPa0VLTFAwcStFdTNhMEN6ZkVnawp3UUovZUY0dVJ2L0xXMXZZTjBabnQ2M09rbS9WeFhGNFFYUHVsRmtvUS9ySVhkYUZGcHJ4eEFlSFlRZ3JXQmhlCnJiSEp3SUJIVUZGOGdTRnJTV3JWd3F1ekhWK3R0dXNIS3hYRUFGZmVnbnQ4SzZrckMyY0pzSExnRmdLc1JUYmQKSW1icS82L2pCVGNiQm1IOXA1ZW1TRzVOTmNSaG9OYTdGUkRzWWxYRVB3bDNYNVJwcjFzQVdNN3hPTXF5SG5TNgoxSFRzWnc4YzJ5Q1JWWCtsVDRBalpzd3A0bVR6cDRzQ0F3RUFBYU5HTUVRd0V3WURWUjBsQkF3d0NnWUlLd1lCCkJRVUhBd0l3REFZRFZSMFRBUUgvQkFJd0FEQWZCZ05WSFNNRUdEQVdnQlRDTktlZjBORTNBOWdtRmhUSTFEVmEKSFpONGxEQU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFQWXdocFdhdW96YWJSRXBkbDB6WDVaM01xL3RnM0M2agpYcXpZQ05EejVqaWkyRU9hdi9vTFlvb2h5akJna0pTSEJEZncxenFYQnlTbEJLR1BTb0tnRjdZcDRnVmNqa2VxCnlnajJjQ2hTNm9POUpnVEVJUlpZZmpSWUg0eXQ2Z1B0dG0zeWNmT3JQNU1BQTY4SmtSMmdFbHcrdWJVVFpvaHgKT2lqbjZSUTZQcjg0NlBpaVlTdE0zVGNSdEtNbjVwcVRlcVFIdVRmQXJHU2tJa0NaSStHcEdQTWFMalc1UWtHcgpjbUg5RVQ3bmcvcmMyU1Z4cjR5Z1pKRDJzUHR1eFFLS3pla01OT1ZTQ1NZUGppK0xaQnhjMlRsSCtvVlpDZ2Q5Cmp1RTBuQnhHZVg0RTV4dlNMOUdtY05yZWdiU0UzanY1ZDhTNVRiSFBwZmNNVmxkY081cUlidz09Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K
  conditions:
  - lastTransitionTime: "2023-12-20T06:59:06Z"
    lastUpdateTime: "2023-12-20T06:59:06Z"
    message: This CSR was approved by kubectl certificate approve.
    reason: KubectlApprove
    status: "True"
    type: Approved
PS C:\DevOpsTraining\devops-101-labs\kubernetes\lab11> 

Finally, copy the signed certificate to `user.crt` (on local/host machine). This will be added to `kubeconfig`.

On \*nix,

```sh
kubectl get csr john -o jsonpath='{.status.certificate}' | base64 --decode > user.crt
```

On Windows,

```console
$ kubectl get csr john -o jsonpath={.status.certificate} > user.crt-base64
$ docker run -it --rm -v "%cd%":/work ^
    nginx bash -c "cat /work/user.crt-base64 | base64 --decode > /work/user.crt"
$ del user.crt-base64
```

Quick recap 😅: `user.csr`&nbsp;(certificate&nbsp;signing&nbsp;request)&nbsp;&rarr;&nbsp;approve&nbsp;&rarr;&nbsp;`user.crt`&nbsp;(signed&nbsp; certificate)

### Step 3: Add user credentials to `kubeconfig`

Now that we have a `user.crt` (signed certificate), we will make the user credentials available to `kubectl` to use. The commands below adds a user named "john".

On \*nix,

```sh
kubectl config set-credentials john \
    --client-certificate=user.crt \
    --client-key=user.key \
    --embed-certs=true
```

On Windows,

```sh
kubectl config set-credentials john ^
    --client-certificate=user.crt ^
    --client-key=user.key ^
    --embed-certs=true
```

### Step 4: Use newly added user to run `kubectl` commands

Use the `--user=""` global option to use the newly added user to run `kubectl` commands. These are expected to fail because the user has not been given any roles.

```console
$ kubectl --user="john" get pods
Error from server (Forbidden): pods is forbidden: User "john" cannot list resource "pods" in API group "" in the namespace "default"
```

C:\DevOpsTraining\devops-101-labs>kubectl --user="john" get pods
Error from server (Forbidden): pods is forbidden: User "john" cannot list resource "pods" in API group "" in the namespace "default"

C:\DevOpsTraining\devops-101-labs>

## Step 4: Give user a role

We will now give the new user the `view` cluster-wide role. This is an existing ClusterRole as shown in the output of running `kubectl get clusterroles`.

Use your preferred text editor to create a file called `view-binding.yaml` and populate it with the lines below.

***Note: Replace TODO comments with the appropriate commands***

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: # TODO Set kind to ClusterRoleBinding
metadata:
  name: john-view
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: view
subjects:
- apiGroup: rbac.authorization.k8s.io
  kind: User
  name: john
```

Apply the cluster role binding.

```sh
kubectl apply -f view-binding.yaml
```

Running `kubectl get pods` using "john" should now work.

```console
$ kubectl --user="john" get pods
```

But since the `view` cluster-wide role only permits read-only access, user "john" should still not be able to make changes to the cluster. Try running the following `kubectl delete` command, it should fail.

```console
$ kubectl --user="john" delete clusterrolebinding john-view
Error from server (Forbidden): clusterrolebindings.rbac.authorization.k8s.io "john-view" is forbidden: User "john" cannot delete resource "clusterrolebindings" in API group "rbac.authorization.k8s.io" at the cluster scope
```

### Step 5: Clean up

After exploring a bit of RBAC, remove the certificate signing request and cluster role binding from Kubernetes, and remove the user from `kubeconfig`.

```console
$ kubectl delete clusterrolebinding john-view
$ kubectl delete csr john
$ kubectl config delete-user john
```

Also delete the following files:

- `user.crt`
- `user.key`
- `user.csr`

## 11.3: Conclusion

Congratulations! You have successfully tried Docker Scout, added a user and given it a role. And you also demonstrated how RBAC (role-based access control) works.
