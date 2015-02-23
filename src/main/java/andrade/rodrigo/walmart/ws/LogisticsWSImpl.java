package andrade.rodrigo.walmart.ws;

import andrade.rodrigo.walmart.GraphService;
import andrade.rodrigo.walmart.constants.Status;
import andrade.rodrigo.walmart.exceptions.IllegalMapException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.feature.Features;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 21/02/15
 * All rights reserved.
 */

@WebService(endpointInterface = "andrade.rodrigo.walmart.ws.LogisticsWS")
@Features(features = "org.apache.cxf.feature.LoggingFeature")
public class LogisticsWSImpl implements LogisticsWS {

    private Log log = LogFactory.getLog(LogisticsWS.class);

    @Autowired
    private GraphService graphService;

    @Override
    public Status addMap(String id, String map) throws IllegalMapException {
        log.info("Received new addMap request.");
        return graphService.addGraph(id, map); //TODO: flesh it out
    }

    @Override
    public String queryRoute(String mapName, String start, String destination, float autonomy, float ltPrice) {
        return "not yet";  //TODO: flesh it out
    }
}
