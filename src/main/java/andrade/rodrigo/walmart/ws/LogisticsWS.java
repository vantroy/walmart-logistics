package andrade.rodrigo.walmart.ws;

import andrade.rodrigo.walmart.constants.Status;
import andrade.rodrigo.walmart.exceptions.IllegalMapException;
import andrade.rodrigo.walmart.exceptions.IllegalNodesException;
import andrade.rodrigo.walmart.persistence.domain.ShortestPathTO;

import javax.jws.WebService;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 21/02/15
 * All rights reserved.
 */

@WebService
public interface LogisticsWS {

    /**
     * @param id: the unique name of the map to which these nodes will belong.
     * @param map: String representation of graph nodes and connections, as per defined
     *           in docs.
     *
     * @return Status.SUCCESS if no exception is thrown.
     *
     * @throws IllegalMapException: when map does not conform to established format (consult docs)
     * @throws IllegalArgumentException: if any parameters are null.
     */
    public Status addMap(String id, String map) throws IllegalMapException, IllegalArgumentException;

    /**
     * Calculates the shortest path on a givem map and start and destibnation nodes.
     * as a bonus, will also calculate total distance and cost of travel.
     *
     * @param mapName: unique map (collection of nodes) in which the search will take place
     * @param start: start node
     * @param destination: end node
     * @param autonomy:vehicular fuel autonomy
     * @param ltPrice: price per litter of fuel
     *
     * @return POJO containing all the information. The path is represented as a List<String>.
     *
     * @throws IllegalNodesException if nodes cannot be found or start and destination are the same.
     * @return
     */
    public ShortestPathTO queryRoute(String mapName, String start, String destination, float autonomy, float ltPrice) throws IllegalNodesException;


    /**
     * Same as queryRoute, but returns a String representing the shortest found path and cost.
     *
     * @param mapName: unique map (collection of nodes) in which the search will take place
     * @param start: start node
     * @param destination: end node
     * @param autonomy:vehicular fuel autonomy
     * @param ltPrice: price per litter of fuel
     *
     * @return String representing the shortest path found and float value indicating
     * the calculated cost of traversing it.
     *
     * @throws IllegalNodesException if nodes cannot be found or start and destination are the same.
     * @return
     */
    public String queryRouteStr(String mapName, String start, String destination, float autonomy, float ltPrice) throws IllegalNodesException;

}
