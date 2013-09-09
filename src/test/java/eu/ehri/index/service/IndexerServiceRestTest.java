package eu.ehri.index.service;

import static org.junit.Assert.*;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.test.framework.JerseyTest;

/* Note that testing the REST interface of the service 
 feels more like an integration test of this service
 
 Could setup an integration environment with a neo4j and solr setup for testing. 
 Then do real REST calls using some scripting tool; 
 
 The business logic and libraries used should have there own tests, 
 so we are left with only a little logic inside the Service to test. 
*/
public class IndexerServiceRestTest extends JerseyTest {

    public IndexerServiceRestTest() throws Exception {
        super("eu.ehri.index.service");
    }

    // the big question is how to mock the IndexerService and it' internals 
    // so it doesn't do real stuff, like retrieving from Neo4j and sending to Solr 
    	
	@Test
	public void testWrongId() 
	{
		WebResource resource = resource().path("index/NONEXISTINGID");
		ClientResponse response = resource.post(ClientResponse.class);
		// this should fail, but now it fails because Solr isn't running!
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
	}
	
    /**
     * Test if a WADL document is available at the relative path
     * "application.wadl".
     * 
     * This should work if the rest api is deployed correctly 
     */
    @Test
    public void testApplicationWadl() {
        String serviceWadl = resource().path("application.wadl").
                accept(MediaTypes.WADL).get(String.class);
                
        assertTrue(serviceWadl.length() > 0);
    }
}
