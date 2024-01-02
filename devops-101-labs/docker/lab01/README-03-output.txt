## Run a PostgreSQL Database

2)

PS C:\DevOpsTraining\devops-101-labs> docker run -d --name pg-testdb -e POSTGRES_PASSWORD=password postgres
Unable to find image 'postgres:latest' locally
latest: Pulling from library/postgres
1f7ce2fa46ab: Pull complete
9b1d9816359e: Pull complete
5754b79d0f72: Pull complete
7a111964c309: Pull complete
5d6ba324b247: Pull complete
a1bdf3fbbc15: Pull complete
0c1e2e159ea1: Pull complete
22ba13c99cba: Pull complete
cf860592ab1a: Pull complete
71c094cd207f: Pull complete
9770e571627e: Pull complete
a18c3a6dcc6d: Pull complete
f83c8937f9e2: Pull complete
9402eee5ce24: Pull complete
Digest: sha256:ff37e66b0a03594086c3734d73e750f13480ca9bf64b53fafea18be4d5afb9ad
Status: Downloaded newer image for postgres:latest
8ccfc9f047ac4c1795fd027af6d8294d9c7afa1b8fa749db412d0a36c8ba87a4
PS C:\DevOpsTraining\devops-101-labs>

## Create Tables in the PostgreSQL Database

1-3)

PS C:\DevOpsTraining\devops-101-labs> docker exec -it pg-testdb psql -U postgres -W
Password: 
psql (16.1 (Debian 16.1-1.pgdg120+1))
Type "help" for help.

postgres=# SELECT 1;
 ?column? 
----------
        1
(1 row)

postgres=# \dt
Did not find any relations.
postgres=# \q
PS C:\DevOpsTraining\devops-101-labs> 

PS C:\DevOpsTraining\devops-101-labs> docker exec -it pg-testdb /bin/bash 
root@8ccfc9f047ac:/# 

4-1)

PS C:\DevOpsTraining\devops-101-labs> docker exec -it pg-testdb /bin/bash 
root@8ccfc9f047ac:/# git --version
bash: git: command not found
root@8ccfc9f047ac:/# apt update
Get:1 http://deb.debian.org/debian bookworm InRelease [151 kB]
Get:2 http://deb.debian.org/debian bookworm-updates InRelease [52.1 kB]
Get:3 http://deb.debian.org/debian-security bookworm-security InRelease [48.0 kB]
Get:4 http://deb.debian.org/debian bookworm/main amd64 Packages [8,787 kB]
Get:5 http://apt.postgresql.org/pub/repos/apt bookworm-pgdg InRelease [123 kB]
Get:6 http://apt.postgresql.org/pub/repos/apt bookworm-pgdg/main amd64 Packages [301 kB]
Get:7 http://deb.debian.org/debian bookworm-updates/main amd64 Packages [11.3 kB]
Get:8 http://deb.debian.org/debian-security bookworm-security/main amd64 Packages [128 kB]
Fetched 9,601 kB in 3s (3,095 kB/s)
Reading package lists... Done
Building dependency tree... Done
Reading state information... Done
5 packages can be upgraded. Run 'apt list --upgradable' to see them.
root@8ccfc9f047ac:/# apt install -y git
Reading package lists... Done
Building dependency tree... Done
Reading state information... Done
The following additional packages will be installed:
  ca-certificates git-man less libbrotli1 libcbor0.8 libcurl3-gnutls liberror-perl libexpat1 libfido2-1 libnghttp2-14 libpsl5 librtmp1 libssh2-1 libx11-6 libx11-data libxau6 libxcb1 libxdmcp6 libxext6 libxmuu1
  openssh-client patch publicsuffix xauth
Suggested packages:
  gettext-base git-daemon-run | git-daemon-sysvinit git-doc git-email git-gui gitk gitweb git-cvs git-mediawiki git-svn keychain libpam-ssh monkeysphere ssh-askpass ed diffutils-doc
The following NEW packages will be installed:
  ca-certificates git git-man less libbrotli1 libcbor0.8 libcurl3-gnutls liberror-perl libexpat1 libfido2-1 libnghttp2-14 libpsl5 librtmp1 libssh2-1 libx11-6 libx11-data libxau6 libxcb1 libxdmcp6 libxext6 libxmuu1    
  openssh-client patch publicsuffix xauth
0 upgraded, 25 newly installed, 0 to remove and 5 not upgraded.
Need to get 13.4 MB of archives.
After this operation, 62.1 MB of additional disk space will be used.
Get:1 http://deb.debian.org/debian bookworm/main amd64 less amd64 590-2 [131 kB]
Get:2 http://deb.debian.org/debian bookworm/main amd64 ca-certificates all 20230311 [153 kB]
Get:3 http://deb.debian.org/debian bookworm/main amd64 libcbor0.8 amd64 0.8.0-2+b1 [27.4 kB]
Get:4 http://deb.debian.org/debian bookworm/main amd64 libfido2-1 amd64 1.12.0-2+b1 [77.2 kB]
Updating certificates in /etc/ssl/certs...
140 added, 0 removed; done.
Setting up libx11-data (2:1.8.4-2+deb12u2) ...
Setting up librtmp1:amd64 (2.4+20151223.gitfa8646d.1-2+b2) ...
Setting up patch (2.7.6-7) ...
Setting up git-man (1:2.39.2-1.1) ...
Setting up libx11-6:amd64 (2:1.8.4-2+deb12u2) ...
Setting up libssh2-1:amd64 (1.10.0-3+b1) ...
Setting up libfido2-1:amd64 (1.12.0-2+b1) ...
Setting up publicsuffix (20230209.2326-1) ...
Setting up libxmuu1:amd64 (2:1.1.3-3) ...
Setting up openssh-client (1:9.2p1-2+deb12u1) ...
Setting up libxext6:amd64 (2:1.3.4-1+b1) ...
Setting up libcurl3-gnutls:amd64 (7.88.1-10+deb12u4) ...
Setting up git (1:2.39.2-1.1) ...
Setting up xauth (1:1.1.2-1) ...
Processing triggers for libc-bin (2.36-9+deb12u3) ...
Processing triggers for ca-certificates (20230311) ...
Updating certificates in /etc/ssl/certs...
0 added, 0 removed; done.
Running hooks in /etc/ca-certificates/update.d...
done.
root@8ccfc9f047ac:/# git --version
git version 2.39.2   
root@8ccfc9f047ac:/# 

4-2)

root@8ccfc9f047ac:/# cd /usr
root@8ccfc9f047ac:/usr# git clone https://github.com/jOOQ/sakila.git
Cloning into 'sakila'...
remote: Enumerating objects: 131, done.
remote: Counting objects: 100% (131/131), done.
remote: Compressing objects: 100% (73/73), done.
remote: Total 131 (delta 84), reused 100 (delta 57), pack-reused 0
Receiving objects: 100% (131/131), 3.87 MiB | 714.00 KiB/s, done.
Resolving deltas: 100% (84/84), done.
root@8ccfc9f047ac:/usr# cd sakila/postgres-sakila-db
root@8ccfc9f047ac:/usr/sakila/postgres-sakila-db#

4-3)

postgres=#         CREATE DATABASE sakila;
akila;CREATE DATABASE
postgres=#         \c sakila;
You are now connected to database "sakila" as user "postgres".

        \i postgres-sakila-schema.sql

sakila=# \i postgres-sakila-schema.sql
SET
SET
SET
SET
SET
COMMENT
CREATE EXTENSION
ALTER LANGUAGE
SET
CREATE SEQUENCE
ALTER TABLE
SET
SET
CREATE TABLE
ALTER TABLE
CREATE TYPE
ALTER TYPE
CREATE DOMAIN
ALTER DOMAIN
CREATE FUNCTION
ALTER FUNCTION
CREATE AGGREGATE
ALTER AGGREGATE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE VIEW
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE VIEW
ALTER TABLE
CREATE VIEW
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE VIEW
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE VIEW
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE SEQUENCE
ALTER TABLE
CREATE TABLE
ALTER TABLE
CREATE VIEW
ALTER TABLE
CREATE VIEW
ALTER TABLE
CREATE FUNCTION
ALTER FUNCTION
CREATE FUNCTION
ALTER FUNCTION
CREATE FUNCTION
ALTER FUNCTION
CREATE FUNCTION
ALTER FUNCTION
CREATE FUNCTION
ALTER FUNCTION
CREATE FUNCTION
ALTER FUNCTION
CREATE FUNCTION
ALTER FUNCTION
CREATE FUNCTION
ALTER FUNCTION
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE INDEX
CREATE RULE
CREATE RULE
CREATE RULE
CREATE RULE
CREATE RULE
CREATE RULE
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
CREATE TRIGGER
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
ALTER TABLE
REVOKE
GRANT
GRANT
sakila=#

4-4-5)

        SELECT first_name, last_name FROM actor;

     first_name  |  last_name
-------------+--------------
 PENELOPE    | GUINESS
 NICK        | WAHLBERG
 ED          | CHASE
 JENNIFER    | DAVIS
 JOHNNY      | LOLLOBRIGIDA
 BETTE       | NICHOLSON
 GRACE       | MOSTEL
 MATTHEW     | JOHANSSON
 JOE         | SWANK
 CHRISTIAN   | GABLE
 ZERO        | CAGE
 KARL        | BERRY
 UMA         | WOOD
 VIVIEN      | BERGEN
 CUBA        | OLIVIER
 FRED        | COSTNER
 HELEN       | VOIGHT
 DAN         | TORN
 BOB         | FAWCETT
 LUCILLE     | TRACY
 KIRSTEN     | PALTROW
 ELVIS       | MARX

        SELECT title, release_year FROM film;
        ```
            title            | release_year
-----------------------------+--------------
 ACADEMY DINOSAUR            |         2006
 ACE GOLDFINGER              |         2006
 ADAPTATION HOLES            |         2006
 AFFAIR PREJUDICE            |         2006
 AFRICAN EGG                 |         2006
 AGENT TRUMAN                |         2006
 AIRPLANE SIERRA             |         2006
 AIRPORT POLLOCK             |         2006
 ALABAMA DEVIL               |         2006
 ALADDIN CALENDAR            |         2006
 ALAMO VIDEOTAPE             |         2006
 ALASKA PHANTOM              |         2006
 ALI FOREVER                 |         2006
 ALICE FANTASIA              |         2006
 ALIEN CENTER                |         2006
 ALLEY EVOLUTION             |         2006
 ALONE TRIP                  |         2006
 ALTER VICTORY               |         2006
 AMADEUS HOLY                |         2006
 AMELIE HELLFIGHTERS         |         2006
 AMERICAN CIRCUS             |         2006
 AMISTAD MIDSUMMER           |         2006

sakila=#  SELECT first_name, last_name FROM actor;
sakila=# SELECT title, release_year FROM film;
sakila=# 
sakila=# \q
root@8ccfc9f047ac:/usr/sakila/postgres-sakila-db# 

4-5-7)

root@8ccfc9f047ac:/usr/sakila/postgres-sakila-db# exit
exit
PS C:\DevOpsTraining\devops-101-labs> docker ps
CONTAINER ID   IMAGE      COMMAND                  CREATED          STATUS          PORTS      NAMES
8ccfc9f047ac   postgres   "docker-entrypoint.s…"   18 minutes ago   Up 17 minutes   5432/tcp   pg-testdb
PS C:\DevOpsTraining\devops-101-labs> 

C:\DevOpsTraining\devops-101-labs> docker stop pg-testdb
pg-testdb
PS C:\DevOpsTraining\devops-101-labs> docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
PS C:\DevOpsTraining\devops-101-labs> 