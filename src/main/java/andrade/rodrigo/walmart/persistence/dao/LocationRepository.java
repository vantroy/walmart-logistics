package andrade.rodrigo.walmart.persistence.dao;

import andrade.rodrigo.walmart.persistence.domain.Location;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Map;
import java.util.Set;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 22/02/2015
 * All rights reserved.
 */
public interface LocationRepository extends GraphRepository<Location> {

    public Location findByName(String name);

    public Location findByNameAndMap(String name, String map);

    public Set<Location> findByMap(String map);

    @Query("START start=node({0}), end=node({1})\n" +
            "MATCH p=(start)-[:CONNECTS_TO*]->(end)\n" +
            "RETURN p as shortestPath,\n" +
            "REDUCE(weight=0, r in relationships(p) | weight+r.weight) AS totalWeight\n" +
            "ORDER BY totalWeight ASC\n" +
            "LIMIT 1")
    public Iterable<Map<String, Object>> findShortestPath(Long startId, Long destinationId);

}
