package andrade.rodrigo.walmart.persistence.domain;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 22/02/2015
 * All rights reserved.
 */

@NodeEntity
public class Location  {

    @GraphId
    Long id;

    @Indexed(unique = true)
    String name;

    @Indexed
    String map;

    public Location() {
    }

    public Location(String name, String map) {
        this.name = name;
        this.map = map;
    }

    public Edge leadsTo(Location dest, float weight) {
        Edge edge = new Edge(this, dest, weight);
        return edge;
    }

    public Long getId() {
        return id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

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
        return result;
    }
}
