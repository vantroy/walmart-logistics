package andrade.rodrigo.walmart.ws;

import javax.jws.WebService;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 21/02/15
 * All rights reserved.
 */

@WebService(endpointInterface = "andrade.rodrigo.walmart.ws.LogisticsWS")
public class LogisticsWSImpl implements LogisticsWS {

    @Override
    public boolean addMap(String map) {
        return false; //TODO: flesh it out
    }

    @Override
    public String queryRouteStr(String query) {
        return null;  //TODO: flesh it out
    }
}
