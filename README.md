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


#### Database support
| <span style="font-size: 12px;">Database</span>                   | <span style="font-size: 12px;">Support</span>|
| -------------                                                    | -----                                        |
| <span style="font-size: 10px;">MariaDB</span>                    |  <span style="font-size: 10px;">✔</span>️     |
| <span style="font-size: 10px;">MySQL</span>                      |  <span style="font-size: 10px;">✔</span>️️     |
| <span style="font-size: 10px;">PostgreSQL</span>                 |  <span style="font-size: 10px;">❌</span>️    |
| <span style="font-size: 10px;">MSSQL</span>                      |  <span style="font-size: 10px;">❌</span>️    |

*****

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

- Author - <a href="skype:live:42d31569536d16f1?chat">Nhan Dinh</a>
- GitHub - [Coding and share](https://github.com/codingandshare)

## License

Database backup remote is Open Source Software released under
the [MIT License](https://raw.githubusercontent.com/codingandshare/database-backup-remote/main/LICENSE)
