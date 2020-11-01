Epsilon_Backend


------------------------------------------------------------------------------------------------------------------------------------
Setup for JDCB connectors:

1. 
Follow the guide to set up the connector pool:

 https://www.hildeberto.com/2010/02/creating-a-connection-pool-to-postgresql-on-glassfish-v3.html

	" We need the PostgreSQL JDBC Driver, since Glassfish and its deployed applications are written in Java. Drivers are available for download at http://jdbc.postgresql.org. For this experiment 		choose the JDBC4 driver.

	Download the driver file postgresql-<version>.jdbc4.jar and copy it to the diretory [glassfish_home]/glassfish/domains/domain1/lib/.

	Restart Glassfish in order to make it load the new database driver. I thought that adopting an OSGI architecture Glassfish would never need restarts again, but I was wrong. At least, the 		restarting process is faster than V2.

	Enter in the administration console and go to Resources/JDBC/Connection Pools.

	Create a new connection pool with the name [database_name]Pool, select the resource type javax.sql.ConnectionPoolDataSource, select the database vendor PostgreSQL and click 	next.

	Select the datasource classname org.postgresql.ds.PGConnectionPoolDataSource and inform the following additional properties:
	DatabaseName=[database-name]
	Password=*******
	PortNumber=5432 (this is the default port but make sure that you are using the 	correct one)
	ServerName=[server-name|ip]
	User=<database-username>.

	Click “Finish” to save the new connection pool.

	Go to the list of connection pools again and select the new one that you just created.

	Click on “Ping” to check if the connection was correctly configured. The message “Ping Succeeded” means that the connection is working fine.

	In order to be able to use this connection pool in JEE applications, we have to create a JNDI name for it:

	Go to Resources/JDBC/JDBC Resources.

	Click on “New” and set the JNDI Name jdbc/[database_name], select the connection pool created above and click “Ok” to finish. This JNDI name will be used by applications to access the 		PostgreSQL database."

2. 
Change the persistance.xml file. Fill in the "@...@". 


<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1" xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
  <persistence-unit name="PU" transaction-type="JTA">
    <jta-data-source>jdbc/epsilonApp</jta-data-source>
        <class>no.no.ntnu.epsilon_backend.tables.Group</class>
        <class>nono.ntnu.epsilon_backend.tables.User</class>
    <properties>
   
      <property name="javax.persistence.schema-generation.database.action" value="create"/>
      <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
      <property name="hibernate.hbm2ddl.auto" value="create"/>
      <property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver"/>
      <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://@ipAdress@:@port@/@databaseName@"/>
      <property name="javax.persistence.jdbc.user" value="@username@"/>
      <property name="javax.persistence.jdbc.password" value="@password@"/>
    
    </properties>
  </persistence-unit>
</persistence>


3. Check the datasourceProducer class and check that the values match the setup in the JDBC pools and the persistence.xml file. 

------------------------------------------------------------------------------------------------------------------------------------