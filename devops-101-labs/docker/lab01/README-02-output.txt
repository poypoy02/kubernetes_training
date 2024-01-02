## Run an Interactive Shell

3)

/ # uname -a
Linux c6301694bbfb 5.15.133.1-microsoft-standard-WSL2 #1 SMP Thu Oct 5 21:02:42 UTC 2023 x86_64 Linux
/ # pwd
/
/ # cd /etc/
/etc # pwd
/etc
/etc # cat issue
Welcome to Alpine Linux 3.19
Kernel \r on an \m (\l)

/etc # echo Hello from 'hostname'
Hello from hostname
/etc # echo Hello from `hostname`
Hello from c6301694bbfb
/etc # echo Today is `date`
Today is Mon Dec 18 02:05:10 UTC 2023
/etc #