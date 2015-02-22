package andrade.rodrigo.walmart.test;

import andrade.rodrigo.walmart.domain.Location;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 22/02/2015
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/test-application-context.xml"})
public class PersistanceTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    Neo4jTemplate template;


    @Test
    @Transactional
    public void persistedMovieShouldBeRetrievableFromGraphDb() {
        Location location = new Location();
        location.setName("A");
        location.setMap("SP");
        template.save(location);
        Location retrieveLocation = template.findOne(location.getId(), Location.class);
        assertEquals("retrieved location matches persisted one", location, retrieveLocation);
        assertEquals("retrieved movie name matches", "A", retrieveLocation.getName());
    }


}
