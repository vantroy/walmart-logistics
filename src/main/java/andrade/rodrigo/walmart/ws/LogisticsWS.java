package andrade.rodrigo.walmart.ws;

import andrade.rodrigo.walmart.constants.Status;
import andrade.rodrigo.walmart.exceptions.IllegalMapException;

import javax.jws.WebService;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 21/02/15
 * All rights reserved.
 */

@WebService
public interface LogisticsWS {


    //TODO: define sane response object
    public Status addMap(String id, String map) throws IllegalMapException;

    //TODO: define sane response object
    public String queryRoute(String id, String start, String query, float autonomy, float ltPrice);

}
