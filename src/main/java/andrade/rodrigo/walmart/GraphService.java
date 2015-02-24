package andrade.rodrigo.walmart;

import andrade.rodrigo.walmart.constants.Status;
import andrade.rodrigo.walmart.exceptions.IllegalMapException;
import andrade.rodrigo.walmart.exceptions.IllegalNodesException;
import andrade.rodrigo.walmart.persistence.dao.LocationRepository;
import andrade.rodrigo.walmart.persistence.domain.Location;
import andrade.rodrigo.walmart.persistence.domain.ShortestPathTO;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.PropertyContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.EntityPath;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 21/02/2015
 * All rights reserved.
 */
@Transactional
public class GraphService {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    LocationRepository locationRepo;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    Neo4jTemplate template;

    private static final Logger log = LoggerFactory.getLogger(GraphService.class);

    /**
     * @param id: the unique name of the map to which these nodes will belong.
     * @param map: String representation of graph nodes and connections, as per defined
     *           in docs.
     *
     * @return Status.SUCCESS if no exception is thrown.
     * @throws IllegalMapException
     */
    public Status addGraph(String id, String map) throws IllegalMapException {
        parseMaptoGraph(id, map);
        return Status.SUCCESS; //NoException Thrown
    }

    /**
     * Calculates the shortest path on a givem map and start and destibnation nodes.
     * as a bonus, will also calculate total distance and cost of travel.
     *
     * @param mapName: unique map (collection of nodes) in which the search will take place
     * @param start: start node
     * @param destination: end node
     * @param autonomy:vehicular fuel autonomy
     * @param ltPrice: price per litter of fuel
     * @return
     */
    public ShortestPathTO findShortestPath(String mapName, String start, String destination, float autonomy, float ltPrice) throws IllegalNodesException {
        Location startEntity = locationRepo.findByNameAndMap(start, mapName);
        Location destEntity = locationRepo.findByNameAndMap(destination, mapName);

        // Crude error checking
        if(startEntity == null | destEntity == null | start.equals(destination)) {
            throw new IllegalNodesException("Invalid parameters, either nodes were not found or they are the same");
        }

        ShortestPathTO shortestPath = new ShortestPathTO();
        Iterable<Map<String, Object>> res = locationRepo.findShortestPath(startEntity.getId(), destEntity.getId());

        // It returns an Iterable type. Haven't found a way around it, even if its only 1 element.
        Map<String, Object> map = res.iterator().next();
        shortestPath.setDistance(template.convert(map.get("totalWeight"), Float.class));
        shortestPath.setPath(extractPath(template.convert(map.get("shortestPath"), EntityPath.class)));
        shortestPath.setAutonomy(autonomy);
        shortestPath.setLtPrice(ltPrice);
        return shortestPath;
    }

    private void parseMaptoGraph(String mapName, String map) throws IllegalMapException {
        Scanner scanner = new Scanner(map);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] pieces = line.trim().split(("\\s+"));
            validateLine(pieces);
            float weight = 0f;
            try {
                weight = Float.parseFloat(pieces[2]);
            } catch (Exception e) {
                // NullPointer or NumberFormat exceptions possible here, wrap both.
                throw new IllegalMapException("Caught " + e.getClass().getName() + ": " + e.getMessage());
            }

            Location start = save(new Location(pieces[0], mapName));
            Location dest = save(new Location(pieces[1], mapName));
            save(start.leadsTo(dest, weight));
        }
        scanner.close();
    }

    // Very Rudimentary input checking
    private void validateLine(String[] pieces) throws IllegalMapException {
        if (pieces.length != 3) {
            throw new IllegalMapException("Map does not conform to speciefied format");
        }
    }

    /**
     * Unpacks the info from neo4j arcane ogjects.
     * Should be deprecated when we find a way to return a
     * list of Entities from the query.
     */
    private List<String> extractPath(EntityPath<Location, Location> path) {
        List<String> nodeNames = new ArrayList<>();
        for (PropertyContainer node : path) {
            try {
                nodeNames.add((String) node.getProperty("name"));
            } catch (NotFoundException e) {
                log.debug("Safely ignoring NotFoundException while traversing generic EntityPath. The Edges have no names");
            }
        }
        return nodeNames;
    }


    /**
     * Convenience method for SAVE or UPDATE entities, necessary to control the
     * map and name "composite" keys, since Neo4J does not support it.
     *
     * @param location
     * @return an updated version of the entity with it's internal db @GraphID anotaded if
     * its a new Entity, or the same instance if its an update operation.
     * field written
     */
    public Location save(Location location) {
        if (location.getId() != null) {
            return locationRepo.save(location);
        } else {
            Location original = locationRepo.findByNameAndMap(location.getName(), location.getMap());
            if (original != null) {
                return original;
            }
            return locationRepo.save(location);
        }
    }


}
