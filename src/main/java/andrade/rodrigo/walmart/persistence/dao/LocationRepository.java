package andrade.rodrigo.walmart.persistence.dao;

import andrade.rodrigo.walmart.persistence.domain.Location;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 22/02/2015
 * All rights reserved.
 */
public interface LocationRepository extends GraphRepository<Location> {

    Location findByName(String name);

}
