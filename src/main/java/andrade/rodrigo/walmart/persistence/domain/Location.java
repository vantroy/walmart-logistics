package andrade.rodrigo.walmart.persistence.domain;

import andrade.rodrigo.walmart.constants.RelationType;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.support.index.IndexType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 22/02/2015
 * All rights reserved.
 */

@NodeEntity
public class Location  {

    @GraphId
    Long id;

    String name;

    @Indexed (indexType = IndexType.LABEL)
    String map;

    @RelatedToVia(type = RelationType.CONNECTS_TO, direction = Direction.OUTGOING)
    @Fetch
    Set<Edge> connections = new HashSet<Edge>();

    public Location() {
    }

    public Location(String name, String map) {
        this.name = name;
        this.map = map;
    }

    public Location leadsTo(Location dest, float weight) {
        Edge edge = new Edge(this, dest, weight);
        connections.add(edge);
        return this;
    }

    public Long getId() {
        return id;
    }

    @Indexed (unique = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public Set<Edge> getConnections() {
        return connections;
    }

    public void setConnections(Set<Edge> connections) {
        this.connections = connections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        if (connections != null ? !connections.equals(location.connections) : location.connections != null)
            return false;
        if (id != null ? !id.equals(location.id) : location.id != null) return false;
        if (map != null ? !map.equals(location.map) : location.map != null) return false;
        if (name != null ? !name.equals(location.name) : location.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (map != null ? map.hashCode() : 0);
        result = 31 * result + (connections != null ? connections.hashCode() : 0);
        return result;
    }
}
