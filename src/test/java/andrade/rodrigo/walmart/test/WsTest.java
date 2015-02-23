package andrade.rodrigo.walmart.test;

import andrade.rodrigo.walmart.constants.RelationType;
import andrade.rodrigo.walmart.constants.Status;
import andrade.rodrigo.walmart.exceptions.IllegalMapException;
import andrade.rodrigo.walmart.persistence.dao.LocationRepository;
import andrade.rodrigo.walmart.persistence.domain.Location;
import andrade.rodrigo.walmart.ws.LogisticsWS;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.core.EntityPath;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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

    private static final Logger log = LoggerFactory.getLogger(WsTest.class);

    @Autowired
    ApplicationContext ctx;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    LocationRepository locationRepo;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    Neo4jTemplate template;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    Neo4jOperations operations;


    private LogisticsWS client;
    private String walMap =  "A B 10\n" +
            "B D 15\n" +
            "A C 20\n" +
            "C D 30\n" +
            "B E 50\n" +
            "D E 30";

    @Before
    public void setUpClient() {
        client = (LogisticsWS) ctx.getBean("client");
        locationRepo.deleteAll();
    }

    @Test
    @Transactional
    public void testAddMap() {
        String id = "SP";
        Status res = null;
        try {
            res = client.addMap(id, walMap);

            // Do it again to test update / keys
            res = client.addMap(id, walMap);
        } catch (IllegalMapException e) {
            e.printStackTrace();
        }
        assertEquals(Status.SUCCESS, res);
        Result<Location> nodes = locationRepo.findAll();
        int i = 0;
        for (Location location : nodes) {
            //Visual Confirmarion that we got it right
            System.out.println(location);
            i++;
        }
        assertEquals(5, i);
    }

    @Test
    @Transactional
    public void findShortestPathRepositoryMethod() throws IllegalMapException {
        String mapId = "TEST_SHORTEST_PATH";
        Float expectedWeight = 25f;
        float weight = 0;
        float fuelPrice = 2.5f;
        String expectedPath = "A B D";


        client.addMap(mapId, walMap);
        Location A = locationRepo.findByNameAndMap("A", mapId);
        Location D = locationRepo.findByNameAndMap("D", mapId);
        Iterable<Map<String, Object>> rawPath = locationRepo.findShortestPath(A.getId(), D.getId());
        EntityPath<Location, Location> path = null;

        // It returns an Iterable type. Haven't found a way around it, even if its 1 element.
        for (Map<String, Object> map : rawPath) {
            weight = template.convert(map.get("totalWeight"), Float.class);
            path = template.convert(map.get("shortestPath"), EntityPath.class);
        }

        StringBuffer showPath = new StringBuffer();
        for (PropertyContainer node : path) {
            try {
                showPath.append(node.getProperty("name") + " ");
            } catch (NotFoundException e) {
                log.debug("Safely ignoring NotFoundException while traversing generic EntityPath");
            }
        }

        assertEquals(expectedWeight, weight, 0);
        assertEquals(expectedPath, showPath.toString().trim());
        System.out.println("Found path: " + showPath.toString().trim() + " " + weight * (fuelPrice/10));
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
                "B D 15" + // <-- Missing line break == wrong number elements/line
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
    @Ignore
    public void testQueryRoute() {
        String id = "SP";
        String start = "A";
        String dest = "D";
        float autonomy = 10f;
        float ltPrice = 2.5f;

        String res = client.queryRoute(id, start, dest, autonomy, ltPrice);
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
    public void persistedLocationShouldBeRetrievableByProperty() {
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
    }


}
