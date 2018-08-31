## db-xls-dump
A simple RESTful api to execute a SQL query on MariaDB and dump the resultset to xls file

### Requires JDK 8 as minimum version and Maven 3.x.x for compile and build

### Clean the project using maven
``` shell
# in this case, the project root directory is "db-xls-dump"
cd $project_root_dir
mvn clean
```

### Build the project using maven
``` shell
# in this case, the project root directory is "db-xls-dump"
cd $project_root_dir
mvn package -DskipTests
```
Note: The build will be available in <project_root_dir>/target/db-xls-dump-x.y.z.jar. In this case, the project root directory is "db-xls-dump".

### Execute the Jar and run the API
``` shell
# in this case, the project root directory is "db-xls-dump"
cd $project_root_dir
./mvnw spring-boot:run
```
     
#### OR

``` shell
# in this case, the project root directory is "db-xls-dump"
cd $project_root_dir
java -jar target/db-xls-dump-0.1.0.jar
```
     
NOTE: Java 8 is required as minimum JAVA/JRE version.

The API will be accessible at http://localhost:8080/dbdump.  There's a heartbeat end-point accessible at http://localhost:8080/dbdump/heartbeat.

Download the postman collection json file at: https://pradyroy.in/db-xls-dump/DB-XLS-DUMP.postman_collection

