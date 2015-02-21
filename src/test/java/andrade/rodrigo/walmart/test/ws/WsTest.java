package andrade.rodrigo.walmart.test.ws;

import andrade.rodrigo.walmart.ws.LogisticsWS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 21/02/2015
 * All rights reserved.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test-application-context.xml" })
public class WsTest {

    @Autowired
    ApplicationContext ctx;
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

        boolean res = client.addMap(id, map);
        assertEquals(true, res);
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


}
