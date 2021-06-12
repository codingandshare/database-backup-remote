# Database Backup Remote

<a href="https://github.com/codingandshare/database-backup-remote/actions/workflows/pipeline_build.yml">![image info](https://github.com/codingandshare/database-backup-remote/actions/workflows/pipeline_build.yml/badge.svg)
</a>
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Java: 11](https://img.shields.io/badge/Java-11-red.svg)
![Spring: 5.3.6](https://img.shields.io/badge/Spring-5.3.6-blue.svg)
![Version: 1.0.RELEASE](https://img.shields.io/badge/Version-1.0.RELEASE-green.svg)
![Version: 1.0.RELEASE](https://img.shields.io/badge/Test%20coverage-100%25-green.svg)

- The service help to schedule job backup data remotely for some engine databases.
- This service support some storages for store backup data file.

## Usage
#### Run service manually
- Require OpenJDK 11 installed
- Set environment for service, require some variables:
```sh
export SPRING_PROFILES_ACTIVE=mariadb
export DB_HOST=localhost
export DB_USER=user_backup
export DB_PASS=password
export DB_NAME=database_name
export STORAGE_FOLDER=/tmp/backup
```
- Download file [database-backup-remote-1.0.RELEASE.jar](https://github.com/codingandshare/database-backup-remote/releases/download/releases%2F1.0.RELEASE/database-backup-remote-1.0.RELEASE.jar)
- Execute jar file
```sh
java -Xmx256m -Djava.security.egd=file:/dev/./urandom -jar database-backup-remote.1.0.RELEASE.jar
```
#### Run service with docker
- The link on docker hub: [docker repo](https://hub.docker.com/r/codingandshare/database-backup-remote)
- Pull image to local machine
```sh
docker pull codingandshare/database-backup-remote
```
- Create file `env.list` with content here:
```
SPRING_PROFILES_ACTIVE=mariadb
DB_HOST=IP_address_database
DB_USER=user_backup
DB_PASS=password
DB_NAME=database_name
STORAGE_FOLDER=/tmp/backup
```
- Run docker image latest
```sh
docker run --env-file ./env.list codingandshare/database-backup-remote
```
If you want to use `docker-compose`. Please refer the samples [docker-compose samples](https://github.com/codingandshare/database-backup-remote/tree/main/samples)

#### Environment Variables
- `SPRING_PROFILES_ACTIVE` is required. The value is database type want to backup, on this version only support some values: `mariadb`, `mysql`. When the value is invalid the service will be failed.
- `DB_HOST` is required. The value is ip address of database server need to backup.
- `DB_USER` is required. The value is username can connect to database server.
- `DB_PASS` is required. The value is password connect to database server. By default value is empty.
- `DB_NAME` is required. The database name want to backup data.
- `STORAGE_FOLDER` is required. Is the folder store file backup data.
- `PREFIX_TABLE_META` is optional. Is variable custom prefix meta tables of this servvice. By default is `CAS_BATCH_`.
- `SCHEDULE_BACKUP` is optional. Is variable custom cron expression for schedule backup task. By default is `0 0 23 * * *`, the service will run at 11PM every day.
- `RETENTION_FILE_BACKUP` is optional. Is variable custom retention file store on local. By default is `7` days.
- `GIT_STORAGE` is optional. Is variable enable/disable git storage, help to put the backup file to git repository. By default is `false`
- `GIT_TOKEN` is required when `GIT_STORAGE` is `true`. Is variable config personal token `oauth2`. By default is empty
- `GIT_BRANCH`is required when `GIT_STORAGE` is `true`. Is variable set branch name on git to push file. By default is empty
- `GIT_DIR`is required when `GIT_STORAGE` is `true`. Is variable config folder git cloned.

#### Database support
| <span style="font-size: 12px;">Database</span>                   | <span style="font-size: 12px;">Support</span>|
| -------------                                                    | -----                                        |
| <span style="font-size: 10px;">MariaDB</span>                    |  <span style="font-size: 10px;">✔</span>️     |
| <span style="font-size: 10px;">MySQL</span>                      |  <span style="font-size: 10px;">✔</span>️️     |
| <span style="font-size: 10px;">PostgreSQL</span>                 |  <span style="font-size: 10px;">❌</span>️    |
| <span style="font-size: 10px;">MSSQL</span>                      |  <span style="font-size: 10px;">❌</span>️    |

#### Storage support
| <span style="font-size: 12px;">Storage</span>                   | <span style="font-size: 12px;">Support</span>|
| -------------                                                   | -----                                        |
| <span style="font-size: 10px;">Local storage</span>             |  <span style="font-size: 10px;">✔</span>️     |
| <span style="font-size: 10px;">Git storage</span>               |  <span style="font-size: 10px;">✔</span>️️     |
| <span style="font-size: 10px;">Google Drive</span>              |  <span style="font-size: 10px;">❌</span>️    |
| <span style="font-size: 10px;">AWS</span>                       |  <span style="font-size: 10px;">❌</span>️    |

## Technical in use

- Java for implementation
- OpenJDK 11
- Spring boot
- Spring batch
- Spring JDBC
- Liquibase
- JGit
- Groovy for testing
- Test container

## Stay in touch

- Author - <a href="skype:42d31569536d16f1?chat">Nhan Dinh</a>
- GitHub - [Coding and share](https://github.com/codingandshare)

## License

Database backup remote is Open Source Software released under
the [MIT License](https://raw.githubusercontent.com/codingandshare/database-backup-remote/main/LICENSE)
