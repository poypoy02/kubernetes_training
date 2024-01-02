## Pull Image from Docker Hub (a container registry)

1)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker pull eclipse-temurin:17-jdk
17-jdk: Pulling from library/eclipse-temurin
Digest: sha256:891bc1f583d287ba1f5f429e172c10ac5d692d2a02197dfef9bd6d16b2d9ed15
Status: Image is up to date for eclipse-temurin:17-jdk
docker.io/library/eclipse-temurin:17-jdk

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview eclipse-temurin:17-jdk
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

2)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker run --rm eclipse-temurin:17-jdk java -version
openjdk version "17.0.9" 2023-10-17
OpenJDK Runtime Environment Temurin-17.0.9+9 (build 17.0.9+9)
OpenJDK 64-Bit Server VM Temurin-17.0.9+9 (build 17.0.9+9, mixed mode, sharing)
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

## Pull Image from a Different Container Registry

1)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker pull mcr.microsoft.com/openjdk/jdk:17-ubuntu
17-ubuntu: Pulling from openjdk/jdk
5e8117c0bd28: Pull complete
e21e3c4015c4: Pull complete
e6d9074cec95: Pull complete
Digest: sha256:50b3802001348819cbd3dcfcdf1cdeea21c31ecca767017a510430bdbacd8934
Status: Downloaded newer image for mcr.microsoft.com/openjdk/jdk:17-ubuntu
mcr.microsoft.com/openjdk/jdk:17-ubuntu

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview mcr.microsoft.com/openjdk/jdk:17-ubuntu
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker pull eclipse-temurin:17-jdk
17-jdk: Pulling from library/eclipse-temurin
Digest: sha256:891bc1f583d287ba1f5f429e172c10ac5d692d2a02197dfef9bd6d16b2d9ed15
Status: Image is up to date for eclipse-temurin:17-jdk
docker.io/library/eclipse-temurin:17-jdk

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview eclipse-temurin:17-jdk
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docke^C
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker run --rm eclipse-temurin:17-jdk java -version
openjdk version "17.0.9" 2023-10-17
OpenJDK Runtime Environment Temurin-17.0.9+9 (build 17.0.9+9)
OpenJDK 64-Bit Server VM Temurin-17.0.9+9 (build 17.0.9+9, mixed mode, sharing)
PS C:\DevOpsTraining\devops-101-labs\docker\lab05>

2)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker run --rm mcr.microsoft.com/openjdk/jdk:17-ubuntu java -version
openjdk version "17.0.9" 2023-10-17 LTS
OpenJDK Runtime Environment Microsoft-8552009 (build 17.0.9+8-LTS)
OpenJDK 64-Bit Server VM Microsoft-8552009 (build 17.0.9+8-LTS, mixed mode, sharing)
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

## Push an Image to Docker Hub

1-2)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker pull docker.io/library/hello-world:latest
latest: Pulling from library/hello-world
Digest: sha256:ac69084025c660510933cca701f615283cdbb3aa0963188770b54c31c8962493
Status: Image is up to date for hello-world:latest
docker.io/library/hello-world:latest

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview docker.io/library/hello-world:latest
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

3-4)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker login
Log in with your Docker ID or email address to push and pull images from Docker Hub. If you don't have a Docker ID, head over to https://hub.docker.com/ to create one.
You can log in with your password or a Personal Access Token (PAT). Using a limited-scope PAT grants better security and is required for organizations using SSO. Learn more at https://docs.docker.com/go/access-tokens/

Username: hannipham
Password:
Login Succeeded
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker push hannipham/hello-world:latest
The push refers to repository [docker.io/hannipham/hello-world]
ac28800ec8bb: Mounted from library/hello-world
latest: digest: sha256:d37ada95d47ad12224c205a938129df7a3e52345828b4fa27b03a98825d1e2e7 size: 524
PS C:\DevOpsTraining\devops-101-labs\docker\lab05>

5)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker image remove hannipham/hello-world:latest
Untagged: hannipham/hello-world:latest
Untagged: hannipham/hello-world@sha256:d37ada95d47ad12224c205a938129df7a3e52345828b4fa27b03a98825d1e2e7
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

    Compare and contrast the following `pull` commands:

    ```
    docker pull docker.io/library/hello-world:latest
    ```

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker pull docker.io/library/hello-world:latest
latest: Pulling from library/hello-world
Digest: sha256:ac69084025c660510933cca701f615283cdbb3aa0963188770b54c31c8962493
Status: Image is up to date for hello-world:latest
docker.io/library/hello-world:latest

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview docker.io/library/hello-world:latest
PS C:\DevOpsTraining\devops-101-labs\docker\lab05>

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker pull hannipham/hello-world:latest
latest: Pulling from hannipham/hello-world
Digest: sha256:d37ada95d47ad12224c205a938129df7a3e52345828b4fa27b03a98825d1e2e7
Status: Downloaded newer image for hannipham/hello-world:latest
docker.io/hannipham/hello-world:latest

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview hannipham/hello-world:latest
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

6-7)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker logout
Removing login credentials for https://index.docker.io/v1/
PS C:\DevOpsTraining\devops-101-labs\docker\lab05>

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker image remove hannipham/hello-world:latest
Untagged: hannipham/hello-world:latest
Untagged: hannipham/hello-world@sha256:d37ada95d47ad12224c205a938129df7a3e52345828b4fa27b03a98825d1e2e7
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

## Use a Local Registry

1)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker run -d -p 5000:5000 --rm --name registry registry:2
Unable to find image 'registry:2' locally
2: Pulling from library/registry
c926b61bad3b: Pull complete
5501dced60f8: Pull complete
e875fe5e6b9c: Pull complete
21f4bf2f86f9: Pull complete
98513cca25bb: Pull complete
Digest: sha256:0a182cb82c93939407967d6d71d6caf11dcef0e5689c6afe2d60518e3b34ab86
Status: Downloaded newer image for registry:2
c840ea28804a049f91cd4d310d4d6912650ecb69600959d513b60aac5cf9bd7b
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

2)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker pull localhost:5000/hello-world
Using default tag: latest
Error response from daemon: manifest for localhost:5000/hello-world:latest not found: manifest unknown: manifest unknown
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

3)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker image ls hello-world:latest
REPOSITORY    TAG       IMAGE ID       CREATED        SIZE
hello-world   latest    d2c94e258dcb   7 months ago   13.3kB
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker image ls localhost:5000/hello-world:latest
REPOSITORY   TAG       IMAGE ID   CREATED   SIZE
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

4)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker tag hello-world localhost:5000/hello-world:latest

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker push localhost:5000/hello-world
Using default tag: latest
The push refers to repository [localhost:5000/hello-world]
ac28800ec8bb: Pushed
latest: digest: sha256:d37ada95d47ad12224c205a938129df7a3e52345828b4fa27b03a98825d1e2e7 size: 524
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

5)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker pull localhost:5000/hello-world
Using default tag: latest
latest: Pulling from hello-world
Digest: sha256:d37ada95d47ad12224c205a938129df7a3e52345828b4fa27b03a98825d1e2e7
Status: Image is up to date for localhost:5000/hello-world:latest
localhost:5000/hello-world:latest

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview localhost:5000/hello-world
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 


PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker pull docker.io/library/hello-world:latest
latest: Pulling from library/hello-world
Digest: sha256:ac69084025c660510933cca701f615283cdbb3aa0963188770b54c31c8962493
Status: Image is up to date for hello-world:latest
docker.io/library/hello-world:latest

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview docker.io/library/hello-world:latest
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

6)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker image remove localhost:5000/hello-world
Untagged: localhost:5000/hello-world:latest
Untagged: localhost:5000/hello-world@sha256:d37ada95d47ad12224c205a938129df7a3e52345828b4fa27b03a98825d1e2e7
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 

7)

PS C:\DevOpsTraining\devops-101-labs\docker\lab05> docker stop registry
registry
PS C:\DevOpsTraining\devops-101-labs\docker\lab05> 



