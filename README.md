ehri-indexer-service
====================

Simple indexing webservice for the EHRI Neo4j REST DB. 

It allows to get the EHRI 'entities' from the Neo4j graph database and update (or create) a 'document' in the EHRI Sorl search index. These indexed documents can also be removed. 


Installation
------------

You need the EHRI Neo4j graphdatabase and the Solr instance running and configured for EHRI indexing. 
See the EHRI Github projects for information about that. 

[neo4j-ehri-plugin](https://github.com/mikesname/neo4j-ehri-plugin)

[ehri-indexer](https://github.com/mikesname/ehri-indexer)

Download the ehri-indexer-service sources. 
Change the configuration specified in the 
src/main/resources/config.properties file. 

Build the war using maven by using the following command in the project directory: 

	$ mvn clean install
	
Deploy the war, should be OK using Tomcat6 because that was used during development. 


RESTfull Resources
------------------
Although no data is sent in the request body, indexing is done via a POST request. The data is retrieved from the Neo4j graph DB using the information in the request uri. 

### Index by ID

POST

- index/{id} 

  Index the entity with the specified id. 


DELETE

- index/{id} 

  Remove the index for the entity with the specified id. 
  
### Index by Type
  
POST

- index/{type} 

  Index all the entities of the specified type. 


DELETE

- index/{typ} 

  Remove the index for all the entities of the specified type.   
  

### Index children
  
POST

- index/{type}/{id}
  
  Index all the child entities of the specified (parent) entity. 
  Note that for now the type need to be specified and must be the type of the parent. 


Example usage
-------------

On the commandline using curl, assuming you have deployed it at http://localhost:8080 and you have a user with id 'testuser'. 

    $ curl -X POST http://localhost:8080/ehri-indexer-service/index/testuser
	$ curl -X DELETE http://localhost:8080/ehri-indexer-service/index/testuser


Troubleshooting
---------------

 First you should chek the properties file and test that Neo4j and Solr are running. For example: 
 
EHRI neo4j.
 
 	$ curl -H "Authorization:$USER" localhost:7474/ehri/userProfile/$USER


EHRI Solr

	$ curl http://localhost:8080/solr-ehri/portal/select?q=*:*
	
The service

	$ curl -X GET http://localhost:8080/ehri-indexer-service/application.wadl


Use the commandline tool and not the service, if that works anything wrong is done by the service: 

	$ cd ehri-indexer-service/libs/ehri-indexer/ehri-indexer/1.0-SNAPSHOT

	$ java -jar ehri-indexer-1.0-SNAPSHOT.jar --pretty @$USER
	
Should give JSON output for that user. 


Issues
------
If the Indexer lib/commandline tool is updateted it (https://github.com/mikesname/ehri-indexer) needs to be build from source and copied to the 'in project' repository:  

libs/ehri-indexer/ehri-indexer/1.0-SNAPSHOT/ehri-indexer-1.0-SNAPSHOT.jar



