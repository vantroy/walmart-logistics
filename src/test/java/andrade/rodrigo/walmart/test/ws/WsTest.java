package andrade.rodrigo.walmart.test.ws;

import andrade.rodrigo.walmart.constants.Status;
import andrade.rodrigo.walmart.exceptions.IllegalMapException;
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
@ContextConfiguration(locations = {"classpath:/test-application-context.xml"})
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


}
