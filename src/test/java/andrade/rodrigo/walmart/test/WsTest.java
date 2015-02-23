package andrade.rodrigo.walmart.test;

import andrade.rodrigo.walmart.constants.Status;
import andrade.rodrigo.walmart.exceptions.IllegalMapException;
import andrade.rodrigo.walmart.persistence.dao.LocationRepository;
import andrade.rodrigo.walmart.persistence.domain.Location;
import andrade.rodrigo.walmart.ws.LogisticsWS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Traverser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 21/02/2015
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/test-application-context.xml"})
public class WsTest {

    @Autowired
    ApplicationContext ctx;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    LocationRepository locationRepo;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    Neo4jTemplate template;

    private LogisticsWS client;

    @Before
    public void setUpClient() {
        client = (LogisticsWS) ctx.getBean("client");
    }

    @Test
    public void testAddMap() {
        String id = "SP";
        String map = "A B 10\n" +
                "B D 15\n" +
                "A C 20\n" +
                "C D 30\n" +
                "B E 50\n" +
                "D E 30";

        Status res = null;
        try {
            res = client.addMap(id, map);
        } catch (IllegalMapException e) {
            e.printStackTrace();
        }
        assertEquals(Status.SUCCESS, res);
    }

    @Test(expected=IllegalMapException.class)
    public void testAddMapExceptionWrongNumber() throws IllegalMapException {
        String id = "TEST_WRONG_ARGS_1";
        String map = "A B 10\n" +
                "B D 15\n" +
                "D E A"; // <-- Last item in this line should be a number

        client.addMap(id, map);
    }

    @Test(expected=IllegalMapException.class)
    public void testAddMapExceptionFaultyArguments() throws IllegalMapException {
        String id = "TEST_WRONG_ARGS_2";
        String map = "A B 10\n" +
                "B D 15" + // <-- Missing line break == wrong number of items
                "D E 3";

        client.addMap(id, map);
    }

    @Test
    public void testAddMapExceptionWrapped() {
        // Asserts if the right type of "wrapped" exception is properly signaled
        String id = "TEST_WRONG_ARGS_3";
        String map = "A B 10\n" +
                "B D 15\n" +
                "D E A"; // <-- Last item in this line should be a number
        Exception e = null;
        try {
            client.addMap(id, map);
        } catch (IllegalMapException e1) {
            e = e1;
            e1.printStackTrace();
        }
        assertEquals("Caught java.lang.NumberFormatException: For input string: \"A\"", e.getMessage());
    }

    @Test
    public void testQueryRoute() {
        String id = "SP";
        String start = "A";
        String dest = "D";
        float autonomy = 10f;
        float ltPrice = 2.5f;

        String res = client.queryRouteStr(id, start, dest, autonomy, ltPrice);
        assertEquals("A B D, 6.25", res);
    }

    //-- Neo4J specific tests
    @Test
    @Transactional
    public void persistedLocationShouldBeRetrievableFromGraphDb() {
        Location location = template.save(new Location("A", "SP"));
        Location retrieveLocation = template.findOne(location.getId(), Location.class);
        assertEquals("retrieved location matches persisted one", location, retrieveLocation);
        assertEquals("retrieved movie name matches", "A", retrieveLocation.getName());
    }

    @Test
    @Transactional
    public void persistedLocationShouldBeRetrievableBuProperty() {
        Location location =  template.save(new Location("A", "SP"));
        Location retrieveLocation = locationRepo.findByName(location.getName());
        assertEquals("retrieved location matches persisted one", location, retrieveLocation);
        assertEquals("retrieved movie name matches", "A", retrieveLocation.getName());
    }

    @Test
    @Transactional
    public void persistentMapWithConnections() {
        final String WEIGHT = "weight";
        Location a = new Location("A", "SP");
        Location b = new Location("B", "SP");
        Location c = new Location("C", "SP");
        Location d = new Location("D", "SP");

        template.save(a.leadsTo(b, 1));
        template.save(b.leadsTo(c, 2));
        template.save(c.leadsTo(d, 3));

        template.traversalDescription().traverse((Node) a);
        assertEquals("4", locationRepo.count());
        Traverser traverser = template.getGraphDatabase()
                .traversalDescription()
                .traverse(template.getGraphDatabaseService().getNodeById(a.getId()));

    }

}
