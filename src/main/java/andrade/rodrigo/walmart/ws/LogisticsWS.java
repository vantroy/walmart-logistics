package andrade.rodrigo.walmart.ws;

import javax.jws.WebService;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 21/02/15
 * All rights reserved.
 */

@WebService
public interface LogisticsWS {


    //TODO: define sane response object
    public boolean addMap(String map);

    //TODO: define sane response object
    public String queryRouteStr(String query);

}
