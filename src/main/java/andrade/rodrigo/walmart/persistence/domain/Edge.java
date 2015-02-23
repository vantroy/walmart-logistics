package andrade.rodrigo.walmart.persistence.domain;

import andrade.rodrigo.walmart.constants.RelationType;
import org.springframework.data.neo4j.annotation.*;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 22/02/2015
 * All rights reserved.
 */

@RelationshipEntity(type = RelationType.CONNECTS_TO)
public class Edge {

    @GraphId Long id;
    @StartNode @Fetch Location start;
    @EndNode @Fetch Location destination;
    float weight;

    public Edge() {
    }

    public Edge(Location start, Location destination, float weight) {
        this.start = start;
        this.destination = destination;
        this.weight = weight;
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", start=" + start.getName() +
                ", destination=" + destination.getName() +
                ", weight=" + weight +
                '}';
    }
}
