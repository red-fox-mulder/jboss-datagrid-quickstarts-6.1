rest-endpoint: Use JDG remotely through REST
============================================
Author: Tristan Tarrant, Martin Gencur
Level: Intermediate
Technologies: Infinispan, REST
Summary: Demonstrates how to use Infinispan remotely using the REST protocol.
Target Product: JDG

What is it?
-----------

JBoss Data Grid uses a RESTful service, eliminating the need for tightly coupled client libraries and bindings. The REST API requires a REST client or custom code to understand and create REST calls. JBoss Data Grid's sole requirement is a HTTP client library. For Java, the Apache HTTP Commons Client is recommended. Alternatively, the java.net API can be used.

This quickstart demonstrates how to connect remotely to JBoss Data Grid (JDG) to store, retrieve, and remove data from cache using the HTTP Commons Client RESTful client APIs. This simple Football Manager console application allows you to add and remove teams, add players to or remove players from teams, or print a list of the current teams and players using the REST interface based connector.


System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Data Grid 6.0 

 
Configure Maven
---------------

If you have not yet done so, you must configure Maven before testing the quickstarts.


Configure JDG
-------------

1. Obtain JDG server distribution on Red Hat's Customer Portal at https://access.redhat.com/jbossnetwork/restricted/listSoftware.html

2. Install a JDBC driver into JDG (since JDG includes H2 by default, this step may be skipped for the scope of this example). More information can be found at https://access.redhat.com/knowledge/docs/en-US/JBoss_Enterprise_Application_Platform/6/html/Administration_and_Configuration_Guide/chap-Datasource_Management.html . _NOTE: JDG does not support deploying applications so one cannot install it as a deployment._

3. This Quickstart uses JDBC to store the cache. To permit this, it's necessary to alter JDG configuration file (`JDG_HOME/standalone/configuration/standalone.xml`) to contain the following definitions:
   
* Datasource subsystem definition:

    
        <subsystem xmlns="urn:jboss:domain:datasources:1.0">
            <!-- Define this Datasource with jndi name  java:jboss/datasources/ExampleDS -->
            <datasources>
                <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
                    <!-- The connection URL uses H2 Database Engine with in-memory database called test -->
                    <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1</connection-url>
                    <!-- JDBC driver name -->
                    <driver>h2</driver>
                    <!-- Credentials -->
                    <security>
                        <user-name>sa</user-name>
                        <password>sa</password>
                    </security>
                </datasource>
                <!-- Define the JDBC driver called 'h2' -->
                <drivers>
                    <driver name="h2" module="com.h2database.h2">
                        <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
                    </driver>
                </drivers>
            </datasources>
        </subsystem>

* Infinispan subsystem definition:

        <subsystem xmlns="urn:infinispan:server:core:5.2" default-cache-container="local">
            <cache-container name="local" default-cache="default">
                <local-cache name="default" start="EAGER">
                    <locking isolation="NONE" acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                    <transaction mode="NONE"/>
                </local-cache>
                <local-cache name="memcachedCache" start="EAGER">
                    <locking isolation="NONE" acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                    <transaction mode="NONE"/>
                </local-cache>
                <local-cache name="namedCache" start="EAGER"/>
                
                <!-- ADD a local cache called 'teams' -->
               
                <local-cache 
                    name="teams"
                    start="EAGER"
                    batching="false"
                    indexing="NONE">
                    
                    <!-- Define the locking isolation of this cache -->
                    <locking
                        isolation="REPEATABLE_READ"
                        acquire-timeout="20000"
                        concurrency-level="500"
                        striping="false" />
                        
                    <!-- Disable transactions for this cache -->
                    <transaction mode="NONE" />
                    
                    <!-- Define the JdbcBinaryCacheStores to point to the ExampleDS previously defined -->
                    <string-keyed-jdbc-store datasource="java:jboss/datasources/ExampleDS" passivation="false" preload="false" purge="false">
                        <!-- Define the database dialect -->
                        <property name="databaseType">H2</property>
                        
                        <!-- specifies information about database table/column names and data types -->
                        <string-keyed-table prefix="JDG">
                            <id-column name="id" type="VARCHAR"/>
                            <data-column name="datum" type="BINARY"/>
                            <timestamp-column name="version" type="BIGINT"/>
                        </string-keyed-table>
                    </string-keyed-jdbc-store>
                </local-cache>
                <!-- End of local cache called 'teams' definition -->

            </cache-container>
        </subsystem>

4. Disable REST endpoint security: by default the standalone.xml configuration protects the REST endpoint with BASIC authentication. Since this quickstart cannot perform authentication, the REST connector should be configured without it:

        <rest-connector virtual-server="default-host" cache-container="local" />

Start JBoss Data Grid 6
-----------------------

1. Open a command line and navigate to the root of the JBoss Data Grid directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   JDG_HOME/bin/standalone.sh
        For Windows: JDG_HOME\bin\standalone.bat


Build and Run the Quickstart
-------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package 
                
4. This will create a file at `target/rest-endpoint-quickstart.jar` 

5. Run the example application in its directory:

        mvn exec:java
 

Using the application
---------------------
Basic usage scenarios can look like this (keyboard shortcuts will be shown to you upon start):

        at  -  add a team
        ap  -  add a player to a team
        rt  -  remove a team
        rp  -  remove a player from a team
        p   -  print all teams and players
        q   -  quit
        
Type `q` one more time to exit the application.    


Debug the Application
------------------------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

    mvn dependency:sources
    mvn dependency:resolve -Dclassifier=javadoc





