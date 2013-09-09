package eu.ehri.index.service;

import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

import eu.ehri.project.indexer.Indexer;
import eu.ehri.project.indexer.converter.impl.JsonConverter;
import eu.ehri.project.indexer.index.Index;
import eu.ehri.project.indexer.index.impl.SolrIndex;
import eu.ehri.project.indexer.sink.impl.IndexJsonSink;
import eu.ehri.project.indexer.source.impl.WebJsonSource;

// Indexing functionality

@Path("/index")
public class IndexerService {
    private static Logger LOG = LoggerFactory.getLogger(IndexerService.class);
	private Configuration config = new Configuration();
	private Index index = new SolrIndex(getConfig().getSolrEhriUrl());

	public Configuration getConfig() {
		return config;
	}

	public Index getIndex() {
		return index;
	}

	/**
	 * Index an entity by its ID (same id as from the Graph database)
	 * 
	 * NOTE for the RESTfullness a PUT makes more sense because we change the search index state
	 * 
	 * @param id
	 * @return
	 */
	@POST
	@Path("/{id}")
	public Response indexById(@PathParam("id") String id) {
		Indexer.Builder<JsonNode> builder = getNeo4jToSolrIndexerBuilder();

		String specs = "@" + id; // Note that we could construct it without the urlsFromSpecs
        for (URI uri : Indexer.urlsFromSpecs(getConfig().getNeo4jEhriUrl(), specs)) {
            builder.addSource(new WebJsonSource(uri));
        }

        
        try {
        	builder.build().iterate();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
    				.build();
        }

	    // OK, we could return the json...
        return Response.status(Response.Status.ACCEPTED)
				.build();
	}
	
	/**
	 * Delete/remove the index for the entity with the given ID
	 * 
	 * @param id
	 * @return
	 */
	@DELETE
	@Path("/{id}")
	public Response deleteById(@PathParam("id") String id) {
		// Note: could filter id, to prevent query injection, 
		// but its an internal API so not needed
	
        try {
        	String[] ids = {id};
        	getIndex().deleteItems(Lists.newArrayList(ids), true);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
    				.build();
        }
      
        return Response.status(Response.Status.ACCEPTED)
				.build();
	}

	/**
	 * Index all entities that are of the given type
	 * 
	 * @param type
	 * @return
	 */
	@POST
	@Path("/type/{type}")
	public Response indexByType(@PathParam("type") String type) {
		Indexer.Builder<JsonNode> builder = getNeo4jToSolrIndexerBuilder();
	    
	    String specs = type; // Note that we could construct it without the urlsFromSpecs
	    // Also note that we retrieve all entities, 
	    // but the ehri-indexer takes care of handling memmory consumption
        for (URI uri : Indexer.urlsFromSpecs(getConfig().getNeo4jEhriUrl(), specs)) {
            builder.addSource(new WebJsonSource(uri));
        }
        
        try {
        	builder.build().iterate();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
    				.build();
        }
		
        return Response.status(Response.Status.ACCEPTED)
				.build();
	}
	
	/**
	 * Delete/remove the index for the entities that are of the given type
	 * 
	 * @param id
	 * @return
	 */
	@DELETE
	@Path("/type/{type}")
	public Response deleteByType(@PathParam("type") String type) {
    	try {
    		// no splitting, assume one type
    		String[] types = {type};
    		getIndex().deleteTypes(Lists.newArrayList(types), true);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
    				.build();
        }

		return Response.status(Response.Status.ACCEPTED)
				.build();
	}
	
	/**
	 * Index all 'children' of a given entity
	 * We need to specify the type because of the Indexer. 
	 * Otherwise I would prefer /{id}/list
	 * 
	 * @param type
	 * @return
	 */
	@POST
	@Path("/type/{type}/{id}")
	public Response indexChidren(@PathParam("id") String id, 
			@PathParam("type") String type) {
		Indexer.Builder<JsonNode> builder = getNeo4jToSolrIndexerBuilder();

		// The Indexer needs a pipe delimited string like "{type}|{id}", 
		// where the {type} only serves as an indicator for getting the children. 
	    String specs = type + "|" + id; 
        for (URI uri : Indexer.urlsFromSpecs(getConfig().getNeo4jEhriUrl(), specs)) {
            builder.addSource(new WebJsonSource(uri));
        }
        
        try {
        	builder.build().iterate();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
    				.build();
        }

		return Response.status(Response.Status.ACCEPTED)
				.build();
	}
	
	protected Indexer.Builder<JsonNode> getNeo4jToSolrIndexerBuilder()
	{
	    Indexer.Builder<JsonNode> builder = new Indexer.Builder<JsonNode>();
	    //if (LOG.isDebugEnabled()) {
	    //	builder.addSink(new OutputStreamJsonSink(System.out, true));
	    //}
	    builder.addSink(new IndexJsonSink(index));
        builder.addConverter(new JsonConverter());

        return builder;
	}
}
