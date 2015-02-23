package andrade.rodrigo.walmart.persistence.domain;

import andrade.rodrigo.walmart.constants.RelationType;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 22/02/2015
 * All rights reserved.
 */

@RelationshipEntity(type = RelationType.CONNECTS_TO)
public class Edge {

    @GraphId Long id;
    @StartNode Location start;
    @EndNode Location end;
    float weight;

    public Edge() {
    }

    public Edge(Location start, Location end, float weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }


}
