package andrade.rodrigo.walmart.test;

import andrade.rodrigo.walmart.constants.RelationType;
import andrade.rodrigo.walmart.constants.Status;
import andrade.rodrigo.walmart.exceptions.IllegalMapException;
import andrade.rodrigo.walmart.persistence.dao.LocationRepository;
import andrade.rodrigo.walmart.persistence.domain.Location;
import andrade.rodrigo.walmart.ws.LogisticsWS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.neo4j.graphdb.DynamicRelationshipType.withName;

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
        template.delete(location);
    }

    @Test
    @Transactional
    public void persistedLocationShouldBeRetrievableBuProperty() {
        Location location =  template.save(new Location("A", "SP"));
        Location retrieveLocation = locationRepo.findByName(location.getName());
        assertEquals("retrieved location matches persisted one", location, retrieveLocation);
        assertEquals("retrieved movie name matches", "A", retrieveLocation.getName());
        template.delete(location);
    }

    @Test
    @Transactional
    public void persistentMapWithConnections() {
        final String WEIGHT = "weight";
        Location a = template.save(new Location("A", "SP"));
        Location b = template.save(new Location("B", "SP"));
        Location c = template.save(new Location("C", "SP"));
        Location d = template.save(new Location("D", "SP"));

        template.save(a.leadsTo(b, 1));
        template.save(b.leadsTo(c, 2));
        template.save(c.leadsTo(d, 3));

        // check number of saved nodes
        assertEquals(4l, locationRepo.count());

        String expected = "ABCD";
        StringBuffer result =  new StringBuffer();

        // Test traversal by Java API
        Traverser traverser = template.getGraphDatabase()
                .traversalDescription().depthFirst()
                .traverse(template.getGraphDatabaseService().getNodeById(a.getId()));
        ResourceIterable<Node> it = traverser.nodes();
        for (Node node : it) {
            System.out.println("NODE: " + node.getProperty("name"));
            result.append(node.getProperty("name"));
        }
        assertEquals(expected, result.toString());

        // Test Traversal by Spring Data
        result = new StringBuffer();
        TraversalDescription traversal = template.getGraphDatabaseService().traversalDescription().relationships(withName(RelationType.CONNECTS_TO), Direction.BOTH);
        Iterable<Location> iter = locationRepo.findAllByTraversal(locationRepo.findByNameAndMap("A", "SP"), traversal);
        for (Location location : iter) {
            System.out.println("NODE: " + location.getName());
            result.append(location.getName());
        }
        assertEquals(expected, result.toString());
        locationRepo.delete(iter);
    }

    @Test
    @Transactional
    public void testMultiMapsWithSameNamedNodes() {
        final String WEIGHT = "weight";
        Location a = template.save(new Location("A", "SP"));
        Location b = template.save(new Location("B", "SP"));
        Location c = template.save(new Location("C", "SP"));
        Location d = template.save(new Location("D", "SP"));
        template.save(a.leadsTo(b, 1));
        template.save(b.leadsTo(c, 2));
        template.save(c.leadsTo(d, 3));

        Location a2 = template.save(new Location("A", "BA"));
        Location b2 = template.save(new Location("B", "BA"));
        Location c2 = template.save(new Location("C", "BA"));
        Location d2 = template.save(new Location("D", "BA"));

        template.save(a2.leadsTo(d2, 1));
        template.save(b2.leadsTo(a2, 2));
        template.save(c2.leadsTo(b2, 3));
        template.save(d2.leadsTo(c2, 3));

        // Test Traversal by Spring Data
        String expected = "ADCB";
        StringBuffer result = new StringBuffer();
        TraversalDescription traversal = template.getGraphDatabaseService().traversalDescription().relationships(withName(RelationType.CONNECTS_TO), Direction.BOTH);
        Iterable<Location> iter = locationRepo.findAllByTraversal(locationRepo.findByNameAndMap("A", "BA"), traversal);
        for (Location location : iter) {
            System.out.println("NODE: " + location.getName());
            result.append(location.getName());
        }
        assertEquals(expected, result.toString());
        locationRepo.delete(iter);
    }


}
