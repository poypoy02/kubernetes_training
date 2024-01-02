READNE-01

## Run some commands in a Container from an `alpine` Image

4) Run the `ls -l` command in an `alpine` container.

PS C:\DevOpsTraining\devops-101-labs> docker run alpine ls -l
total 56
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 bin
drwxr-xr-x    5 root     root           340 Dec 18 01:38 dev
drwxr-xr-x    1 root     root          4096 Dec 18 01:38 etc
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 home
drwxr-xr-x    7 root     root          4096 Dec  7 09:43 lib
drwxr-xr-x    5 root     root          4096 Dec  7 09:43 media
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 mnt
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 opt
dr-xr-xr-x  390 root     root             0 Dec 18 01:38 proc
drwx------    2 root     root          4096 Dec  7 09:43 root
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 run
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 sbin
drwxr-xr-x    2 root     root          4096 Dec  7 09:43 srv
dr-xr-xr-x   11 root     root             0 Dec 18 01:38 sys
drwxrwxrwt    2 root     root          4096 Dec  7 09:43 tmp
drwxr-xr-x    7 root     root          4096 Dec  7 09:43 usr
drwxr-xr-x   12 root     root          4096 Dec  7 09:43 var

5) Run a `sh` (shell) command in an `alpine` container and immediately execute the following command:

PS C:\DevOpsTraining\devops-101-labs> docker run alpine sh -c 'echo Hello from `hostname` ; echo Today is `date`'  
Hello from 86d156c3bbd4
Today is Mon Dec 18 01:49:51 UTC 2023
PS C:\DevOpsTraining\devops-101-labs> 

## Run `figlet` in an `alpine` Container

docker run alpine sh -c "apk add figlet ; figlet Hello Docker"

PS C:\DevOpsTraining\devops-101-labs> docker run alpine sh -c 'apk add figlet ; figlet Hello Docker'
fetch https://dl-cdn.alpinelinux.org/alpine/v3.19/main/x86_64/APKINDEX.tar.gz
fetch https://dl-cdn.alpinelinux.org/alpine/v3.19/community/x86_64/APKINDEX.tar.gz
(1/1) Installing figlet (2.2.5-r3)
Executing busybox-1.36.1-r15.trigger
OK: 8 MiB in 16 packages
 _   _      _ _         ____             _
| | | | ___| | | ___   |  _ \  ___   ___| | _____ _ __
| |_| |/ _ \ | |/ _ \  | | | |/ _ \ / __| |/ / _ \ '__|
|  _  |  __/ | | (_) | | |_| | (_) | (__|   <  __/ |
|_| |_|\___|_|_|\___/  |____/ \___/ \___|_|\_\___|_|

PS C:\DevOpsTraining\devops-101-labs> 