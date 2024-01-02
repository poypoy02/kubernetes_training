## Use the *Default* `bridge` Network

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker network inspect bridge 
[
    {
        "Name": "bridge",
        "Id": "899e6c2dd2df26bd843a8d6bf42f9da6c5138e9eb2d1e61236b316684aed0e07",
        "Created": "2023-12-18T00:07:07.352877505Z",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": null,
            "Config": [
                {
                    "Subnet": "172.17.0.0/16",
                    "Gateway": "172.17.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "63e92278f74e07250de4a714cb0d6821cd36862f9cb452634b0c3aeaadac82e5": {
                "Name": "alpine2",
                "MacAddress": "02:42:ac:11:00:03",
                "IPv6Address": ""
            },
            "ce4cf778203fcf41dedfd888c27fde52aeebc4820bea5a5b38b0acd7f0f75078": {
                "Name": "alpine1",
                "EndpointID": "e9feb725ea45803a4ba1a941ef550941b450ba4e180d6d48053c98a8c13a26ef",
                "MacAddress": "02:42:ac:11:00:02",
                "IPv4Address": "172.17.0.2/16",
                "IPv6Address": ""
            }
        },
        "Options": {
            "com.docker.network.bridge.enable_icc": "true",
            "com.docker.network.bridge.enable_ip_masquerade": "true",
            "com.docker.network.bridge.host_binding_ipv4": "0.0.0.0",
            "com.docker.network.bridge.name": "docker0",
            "com.docker.network.driver.mtu": "1500"
        },
        "Labels": {}
    }
]
PS C:\DevOpsTraining\devops-101-labs\docker\lab03

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker attach alpine1
/ # ping -c 2 172.17.0.3
PING 172.17.0.3 (172.17.0.3): 56 data bytes
64 bytes from 172.17.0.3: seq=0 ttl=64 time=0.100 ms
64 bytes from 172.17.0.3: seq=1 ttl=64 time=0.091 ms

--- 172.17.0.3 ping statistics ---
2 packets transmitted, 2 packets received, 0% packet loss
round-trip min/avg/max = 0.091/0.095/0.100 ms

/ # ping -c 2 alpine2
ping: bad address 'alpine2'
/ # exit

## Use User-Defined `bridge` Networks

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker network create alpine-net
281cb8b235f4fbae4f0421a5360f1e97952c1e0d791662696d6dc62a4376c1c5
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> 

Start two `alpine` containers running `ash` and using the `alpine-net` network.

```
docker run --rm --name alpine1 --network alpine-net -d -it alpine ash
docker run --rm --name alpine2 --network alpine-net -d -it alpine ash
```

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker run --rm --name alpine1 --network alpine-net -d -it alpine ash
d201ab0a5c6b9a63674163bf9b62f975cb86c96d5768f97890cbd9677b5f4a7b
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker run --rm --name alpine2 --network alpine-net -d -it alpine ash
8b54a165fffdd99ce5f087b9f628f6ddca278c54c4b06ce5fdf09c14c6d4f19d
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> 

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker network inspect alpine-net
[
    {
        "Name": "alpine-net",
        "Id": "281cb8b235f4fbae4f0421a5360f1e97952c1e0d791662696d6dc62a4376c1c5",
        "Created": "2023-12-18T05:51:56.714629345Z",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "172.18.0.0/16",
                    "Gateway": "172.18.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "8b54a165fffdd99ce5f087b9f628f6ddca278c54c4b06ce5fdf09c14c6d4f19d": {
                "Name": "alpine2",
                "EndpointID": "b8e85b11b1ca46c21a0dcbe5af3df34311058897fa5ed9e816843fab77bdb8ef",
                "MacAddress": "02:42:ac:12:00:03",
                "IPv4Address": "172.18.0.3/16",
                "IPv6Address": ""
            },
            "d201ab0a5c6b9a63674163bf9b62f975cb86c96d5768f97890cbd9677b5f4a7b": {
                "Name": "alpine1",
                "EndpointID": "2952221b2c9c0df64853295e53fce07611bba74f36a564d3ebdcf01a5e8bccc8",
                "MacAddress": "02:42:ac:12:00:02",
                "IPv4Address": "172.18.0.2/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> 

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker attach alpine1
/ # ping -c 2 172.18.0.3
PING 172.18.0.3 (172.18.0.3): 56 data bytes
64 bytes from 172.18.0.3: seq=0 ttl=64 time=0.144 ms
64 bytes from 172.18.0.3: seq=1 ttl=64 time=0.093 ms

--- 172.18.0.3 ping statistics ---
2 packets transmitted, 2 packets received, 0% packet loss
round-trip min/avg/max = 0.093/0.118/0.144 ms
/ #

/ # ping -c 2 alpine2
PING alpine2 (172.18.0.3): 56 data bytes
64 bytes from 172.18.0.3: seq=0 ttl=64 time=0.067 ms
64 bytes from 172.18.0.3: seq=1 ttl=64 time=0.121 ms

--- alpine2 ping statistics ---
2 packets transmitted, 2 packets received, 0% packet loss
round-trip min/avg/max = 0.067/0.094/0.121 ms
/ #

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker network ls
NETWORK ID     NAME         DRIVER    SCOPE
281cb8b235f4   alpine-net   bridge    local
899e6c2dd2df   bridge       bridge    local
d16ee14a8fdb   host         host      local
874a8d3e4b42   none         null      local
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker network rm alpine-net
alpine-net
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker network ls
NETWORK ID     NAME      DRIVER    SCOPE
899e6c2dd2df   bridge    bridge    local
d16ee14a8fdb   host      host      local
874a8d3e4b42   none      null      local
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> 

## Run `postgres` and `pgAdmin4` Containers

1)

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker network ls
NETWORK ID     NAME      DRIVER    SCOPE
899e6c2dd2df   bridge    bridge    local
d16ee14a8fdb   host      host      local
874a8d3e4b42   none      null      local
c2f4e2d3f58d   pg-net    bridge    local
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> 

2-3)

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker run --rm -d -e POSTGRES_PASSWORD=password --name pg-db --network pg-net postgres
a85aacd9258531840bfc4988e52c42f538d57357aec88f52899e55dc0b7882f7
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker ps    
CONTAINER ID   IMAGE      COMMAND                  CREATED          STATUS          PORTS      NAMES
a85aacd92585   postgres   "docker-entrypoint.s…"   18 seconds ago   Up 17 seconds   5432/tcp   pg-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> 

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker ps
CONTAINER ID   IMAGE            COMMAND                  CREATED         STATUS         PORTS                           NAMES
85b5a86771da   dpage/pgadmin4   "/entrypoint.sh"         5 seconds ago   Up 2 seconds   443/tcp, 0.0.0.0:5050->80/tcp   affectionate_elion
a85aacd92585   postgres         "docker-entrypoint.s…"   2 minutes ago   Up 2 minutes   5432/tcp                        pg-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> 

4-5)

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker ps
CONTAINER ID   IMAGE            COMMAND                  CREATED          STATUS          PORTS                           NAMES
85b5a86771da   dpage/pgadmin4   "/entrypoint.sh"         9 minutes ago    Up 9 minutes    443/tcp, 0.0.0.0:5050->80/tcp   affectionate_elion
a85aacd92585   postgres         "docker-entrypoint.s…"   12 minutes ago   Up 12 minutes   5432/tcp                        pg-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker container stop affectionate_elion
affectionate_elion
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker container stop pg-db
pg-db
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker ps 
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
PS C:\DevOpsTraining\devops-101-labs\docker\lab03>

PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker network rm pg-net
pg-net
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> docker network ls
NETWORK ID     NAME      DRIVER    SCOPE
899e6c2dd2df   bridge    bridge    local
d16ee14a8fdb   host      host      local
874a8d3e4b42   none      null      local
PS C:\DevOpsTraining\devops-101-labs\docker\lab03> 
