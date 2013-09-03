package eu.ehri.searchindex;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

// Indexing functionality

 
@Path("/index")
public class Indexer {
    private static Logger logger = LoggerFactory.getLogger(Indexer.class);
	private Configuration config = new Configuration();
	
	/**
	 * Index an entity by its ID (same id as from the Graph database)
	 * 
	 * NOTE for the RESTfullness a PUT makes more sense because we change the search index state
	 * 
	 * @param id
	 * @return
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response indexById(@PathParam("id") String id) {
		 
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity("<message>" + id + "</message>")
				.build();
	}
	
}
