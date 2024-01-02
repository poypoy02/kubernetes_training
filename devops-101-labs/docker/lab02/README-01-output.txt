## Build Image from Existing Dockerfile

1)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02> git clone https://github.com/dockersamples/linux_tweet_app.git
remote: Enumerating objects: 14, done.
remote: Counting objects: 100% (8/8), done.
remote: Compressing objects: 100% (4/4), done.
Receiving objects:  28% (4/14)used 4 (delta 4), pack-reused 6
Receiving objects: 100% (14/14), 10.79 KiB | 160.00 KiB/s, done.
Resolving deltas: 100% (5/5), done.
PS C:\DevOpsTraining\devops-101-labs\docker\lab02>

2)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02> cd .\linux_tweet_app\
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> ls 


    Directory: C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app


Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
-a----        12/18/2023  11:37 AM            155 Dockerfile
-a----        12/18/2023  11:37 AM           1718 index-new.html
-a----        12/18/2023  11:37 AM           1585 index-original.html
-a----        12/18/2023  11:37 AM           1641 index.html
-a----        12/18/2023  11:37 AM          22365 linux.png
-a----        12/18/2023  11:37 AM            297 README.md


PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> cat .\Dockerfile
FROM nginx:latest

COPY index.html /usr/share/nginx/html
COPY linux.png /usr/share/nginx/html

EXPOSE 80 443

CMD ["nginx", "-g", "daemon off;"]
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> 

3)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> docker build -t training/linux_tweet_app .
[+] Building 11.8s (8/8) FINISHED                                                                                                                                                                         docker:default
 => [internal] load .dockerignore                                                                                                                                                                                   0.1s
 => => transferring context: 2B                                                                                                                                                                                     0.0s 
 => [internal] load build definition from Dockerfile                                                                                                                                                                0.1s 
 => => transferring dockerfile: 194B                                                                                                                                                                                0.0s 
 => [internal] load metadata for docker.io/library/nginx:latest                                                                                                                                                     3.7s
 => [1/3] FROM docker.io/library/nginx:latest@sha256:10d1f5b58f74683ad34eb29287e07dab1e90f10af243f151bb50aa5dbb4d62ee                                                                                               7.4s
 => => resolve docker.io/library/nginx:latest@sha256:10d1f5b58f74683ad34eb29287e07dab1e90f10af243f151bb50aa5dbb4d62ee                                                                                               0.1s 
 => => sha256:10d1f5b58f74683ad34eb29287e07dab1e90f10af243f151bb50aa5dbb4d62ee 1.86kB / 1.86kB                                                                                                                      0.0s 
 => => sha256:3c4c1f42a89e343c7b050c5e5d6f670a0e0b82e70e0e7d023f10092a04bbb5a7 1.78kB / 1.78kB                                                                                                                      0.0s 
 => => sha256:a6bd71f48f6839d9faae1f29d3babef831e76bc213107682c5cc80f0cbb30866 8.15kB / 8.15kB                                                                                                                      0.0s
 => => sha256:9b16c94bb68628753a94b89ddf26abc0974cd35a96f785895ab011d9b5042ee5 41.38MB / 41.38MB                                                                                                                    4.2s 
 => => sha256:9a59d19f9c5bb1ebdfef2255496b1bb5d658fdccc300c4c1f0d18c73f1bb14b5 625B / 625B                                                                                                                          0.3s 
 => => sha256:9ea27b074f71d5766a59cdbfaa15f4cd3d17bffb83fed066373eb287326abbd3 959B / 959B                                                                                                                          5.2s 
 => => sha256:c6edf33e2524b241a0b191d0a0d2ca3d8d4ae7470333b059dd97ba30e663a1a3 371B / 371B                                                                                                                          0.8s 
 => => sha256:84b1ff10387b26e2952f006c0a4fe4c6f3c0743cb08ee448bb7157220ad2fc8f 1.21kB / 1.21kB                                                                                                                      1.1s 
 => => sha256:51735783196785d1f604dc7711ea70fb3fab3cd9d99eaeff991c5afbfa0f20e8 1.40kB / 1.40kB                                                                                                                      6.8s
 => => extracting sha256:9b16c94bb68628753a94b89ddf26abc0974cd35a96f785895ab011d9b5042ee5                                                                                                                           0.7s
 => => extracting sha256:9a59d19f9c5bb1ebdfef2255496b1bb5d658fdccc300c4c1f0d18c73f1bb14b5                                                                                                                           0.0s
 => => extracting sha256:9ea27b074f71d5766a59cdbfaa15f4cd3d17bffb83fed066373eb287326abbd3                                                                                                                           0.0s
 => => extracting sha256:c6edf33e2524b241a0b191d0a0d2ca3d8d4ae7470333b059dd97ba30e663a1a3                                                                                                                           0.0s
 => => extracting sha256:84b1ff10387b26e2952f006c0a4fe4c6f3c0743cb08ee448bb7157220ad2fc8f                                                                                                                           0.0s
 => => extracting sha256:51735783196785d1f604dc7711ea70fb3fab3cd9d99eaeff991c5afbfa0f20e8                                                                                                                           0.0s
 => [internal] load build context                                                                                                                                                                                   0.6s 
 => => transferring context: 24.09kB                                                                                                                                                                                0.5s
 => [2/3] COPY index.html /usr/share/nginx/html                                                                                                                                                                     0.1s 
 => [3/3] COPY linux.png /usr/share/nginx/html                                                                                                                                                                      0.1s 
 => exporting to image                                                                                                                                                                                              0.2s 
 => => exporting layers                                                                                                                                                                                             0.1s 
 => => writing image sha256:dd71a897fe59fdfebe9a8859b2f54054660d63f18e1207e1c28aae29a1f1e011                                                                                                                        0.0s 
 => => naming to docker.io/training/linux_tweet_app                                                                                                                                                                 0.0s 

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> 

4)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> docker images
REPOSITORY                                                TAG                                                                          IMAGE ID       CREATED          SIZE
training/linux_tweet_app                                  latest                                                                       dd71a897fe59   36 seconds ago   187MB
eclipse-temurin                                           17-jdk                                                                       b97bd7dd6cbd   41 hours ago     407MB
ubuntu                                                    latest                                                                       174c8c134b2a   5 days ago       77.9MB
postgres                                                  latest                                                                       d2d312b19332   6 days ago       432MB
alpine                                                    latest                                                                       f8c20f8bbcb6   10 days ago      7.38MB
api/faq                                                   latest                                                                       8979fa93483a   8 weeks ago      459MB
hubproxy.docker.internal:5555/docker/desktop-kubernetes   kubernetes-v1.28.2-cni-v1.3.0-critools-v1.28.0-cri-dockerd-v0.3.4-1-debian   1d7e8203bdb9   2 months ago     430MB
registry.k8s.io/kube-apiserver                            v1.28.2                                                                      cdcab12b2dd1   3 months ago     126MB
registry.k8s.io/kube-scheduler                            v1.28.2                                                                      7a5d9d67a13f   3 months ago     60.1MB
registry.k8s.io/kube-proxy                                v1.28.2                                                                      c120fed2beb8   3 months ago     73.1MB
registry.k8s.io/kube-controller-manager                   v1.28.2                                                                      55f13c92defb   3 months ago     122MB
registry.k8s.io/etcd                                      3.5.9-0                                                                      73deb9a3f702   7 months ago     294MB
docker/desktop-vpnkit-controller                          dc331cb22850be0cdd97c84a9cfecaf44a1afb6e                                     556098075b3d   7 months ago     36.2MB
hello-world                                               latest                                                                       d2c94e258dcb   7 months ago     13.3kB
registry.k8s.io/coredns/coredns                           v1.10.1                                                                      ead0a4a53df8   10 months ago    53.6MB
registry.k8s.io/etcd                                      3.5.7-0                                                                      86b6af7dd652   10 months ago    296MB
registry.k8s.io/pause                                     3.9                                                                          e6f181688397   14 months ago    744kB
docker/desktop-storage-provisioner                        v2.0                                                                         99f89471f470   2 years ago      41.9MB
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> docker images training/*
REPOSITORY                 TAG       IMAGE ID       CREATED          SIZE
training/linux_tweet_app   latest    dd71a897fe59   42 seconds ago   187MB
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> 

5)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> docker run --rm -d -p 6969:80 training/linux_tweet_app
2c24801d553f0f15862f9fd6640f2a22f5aee3ff52fcb2b55b9a092cd3a993ed
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> docker ps
CONTAINER ID   IMAGE                      COMMAND                  CREATED         STATUS         PORTS                           NAMES
2c24801d553f   training/linux_tweet_app   "/docker-entrypoint.…"   4 seconds ago   Up 4 seconds   443/tcp, 0.0.0.0:6969->80/tcp   condescending_shannon
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> 

7)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> docker stop condescending_shannon   
condescending_shannon
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\linux_tweet_app> 

## Build a Dockerfile (1)

1-4)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\alpine-hello> docker build -t training/alpine-hello .
2023/12/18 11:56:37 http2: server: error reading preface from client //./pipe/docker_engine: file has already been closed
[+] Building 0.3s (5/5) FINISHED                                                                                                                                                                          docker:default 
 => [internal] load build definition from Dockerfile                                                                                                                                                                0.1s 
 => => transferring dockerfile: 95B                                                                                                                                                                                 0.0s 
 => [internal] load .dockerignore                                                                                                                                                                                   0.1s 
 => => transferring context: 2B                                                                                                                                                                                     0.0s 
 => [internal] load metadata for docker.io/library/alpine:latest                                                                                                                                                    0.0s 
 => [1/1] FROM docker.io/library/alpine:latest                                                                                                                                                                      0.0s 
 => exporting to image                                                                                                                                                                                              0.0s 
 => => exporting layers                                                                                                                                                                                             0.0s 
 => => writing image sha256:04b42150552faf70b02bf2caba3019d391b686b62444b2d928b4733957b3e3a7                                                                                                                        0.0s 
 => => naming to docker.io/training/alpine-hello                                                                                                                                                                    0.0s 

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\alpine-hello>

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\alpine-hello> docker run --rm training/alpine-hello
Hello Docker!
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\alpine-hello> 

## Build a Dockerfile (2)

1-2)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\j-hello> docker run --rm -v $pwd\src:/src -v $pwd\target:/target eclipse-temurin:17-jdk javac -d /target /src/hello/Main.java

3)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\j-hello> docker run --rm -v $pwd\target:/target eclipse-temurin:17-jre java -cp /target hello.Main
Unable to find image 'eclipse-temurin:17-jre' locally
17-jre: Pulling from library/eclipse-temurin
3dd181f9be59: Already exists
0f838805bddf: Already exists
e7eee5bc80e6: Pull complete
51526e7965d8: Pull complete
ffcdc7c6c160: Pull complete
Digest: sha256:8e688ce1110516649923796288fe9e4f4dc116a6efcf7f7ac97109e19ec8969e
Status: Downloaded newer image for eclipse-temurin:17-jre
Hello Docker from Java!
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\j-hello> 

5)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\j-hello> docker build -t training/j-hello .
[+] Building 1.0s (8/8) FINISHED                                                                                                                                                                          docker:default
 => [internal] load .dockerignore                                                                                                                                                                                   0.1s
 => => transferring context: 2B                                                                                                                                                                                     0.0s 
 => [internal] load build definition from Dockerfile                                                                                                                                                                0.1s 
 => => transferring dockerfile: 142B                                                                                                                                                                                0.0s
 => [internal] load metadata for docker.io/library/eclipse-temurin:17-jre                                                                                                                                           0.0s 
 => [1/3] FROM docker.io/library/eclipse-temurin:17-jre                                                                                                                                                             0.3s 
 => [internal] load build context                                                                                                                                                                                   0.2s 
 => => transferring context: 545B                                                                                                                                                                                   0.0s 
 => [2/3] WORKDIR /app                                                                                                                                                                                              0.1s
 => [3/3] COPY ./target .                                                                                                                                                                                           0.1s
 => exporting to image                                                                                                                                                                                              0.2s
 => => exporting layers                                                                                                                                                                                             0.2s 
 => => writing image sha256:bde87644d0b5ffdda64b2623c50b1d808b5cc03ee7685d6d776113afdb08d634                                                                                                                        0.0s
 => => naming to docker.io/training/j-hello                                                                                                                                                                         0.0s 

What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\j-hello> 

6)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\j-hello> docker run training/j-hello     
Hello Docker from Java!
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\j-hello>

## (_Optional_) Understanding How Images are Built

1)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\j-hello> docker history alpine
IMAGE          CREATED       CREATED BY                                      SIZE      COMMENT
f8c20f8bbcb6   10 days ago   /bin/sh -c #(nop)  CMD ["/bin/sh"]              0B
<missing>      10 days ago   /bin/sh -c #(nop) ADD file:1f4eb46669b5b6275…   7.38MB
PS C:\DevOpsTraining\devops-101-labs\docker\lab02\j-hello> 

2)

PS C:\DevOpsTraining\devops-101-labs\docker\lab02\j-hello> docker history training/j-hello
IMAGE          CREATED         CREATED BY                                      SIZE      COMMENT
bde87644d0b5   4 minutes ago   ENTRYPOINT ["java" "-cp" "." "hello.Main"]      0B        buildkit.dockerfile.v0
<missing>      4 minutes ago   COPY ./target . # buildkit                      431B      buildkit.dockerfile.v0
<missing>      4 minutes ago   WORKDIR /app                                    0B        buildkit.dockerfile.v0
<missing>      42 hours ago    /bin/sh -c #(nop)  ENTRYPOINT ["/__cacert_en…   0B
<missing>      42 hours ago    /bin/sh -c #(nop) COPY file:8b8864b3e02a33a5…   1.18kB
<missing>      42 hours ago    /bin/sh -c set -eux;     echo "Verifying ins…   0B
<missing>      42 hours ago    /bin/sh -c set -eux;     ARCH="$(dpkg --prin…   141MB
<missing>      42 hours ago    /bin/sh -c #(nop)  ENV JAVA_VERSION=jdk-17.0…   0B
<missing>      42 hours ago    /bin/sh -c set -eux;     apt-get update;    …   50MB
<missing>      42 hours ago    /bin/sh -c #(nop)  ENV LANG=en_US.UTF-8 LANG…   0B
<missing>      42 hours ago    /bin/sh -c #(nop)  ENV PATH=/opt/java/openjd…   0B
<missing>      42 hours ago    /bin/sh -c #(nop)  ENV JAVA_HOME=/opt/java/o…   0B
<missing>      5 days ago      /bin/sh -c #(nop)  CMD ["/bin/bash"]            0B
<missing>      5 days ago      /bin/sh -c #(nop) ADD file:2b3b5254f38a790d4…   77.9MB
<missing>      5 days ago      /bin/sh -c #(nop)  LABEL org.opencontainers.…   0B
<missing>      5 days ago      /bin/sh -c #(nop)  LABEL org.opencontainers.…   0B
<missing>      5 days ago      /bin/sh -c #(nop)  ARG LAUNCHPAD_BUILD_ARCH     0B
<missing>      5 days ago      /bin/sh -c #(nop)  ARG RELEASE     