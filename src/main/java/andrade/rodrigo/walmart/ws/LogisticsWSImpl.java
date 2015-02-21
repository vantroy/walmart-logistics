package andrade.rodrigo.walmart.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.feature.Features;

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

    @Override
    public boolean addMap(String id, String map) {
        log.info("Received new addMap request.");
        return false; //TODO: flesh it out
    }

    @Override
    public String queryRouteStr(String id, String start, String query, float autonomy, float ltPrice) {
        return "not yet";  //TODO: flesh it out
    }
}
