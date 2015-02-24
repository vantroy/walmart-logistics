package andrade.rodrigo.walmart.persistence.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 23/02/2015
 * All rights reserved.
 */
public class ShortestPathTO {

    float autonomy;
    float ltPrice;
    List<String> path = new ArrayList();
    float distance; // A.K.A. weight

    public float getAutonomy() {
        return autonomy;
    }

    public float calculateTripCost() {
        return distance * (ltPrice/autonomy);
    }

    public void setAutonomy(float autonomy) {
        this.autonomy = autonomy;
    }

    public float getLtPrice() {
        return ltPrice;
    }

    public void setLtPrice(float ltPrice) {
        this.ltPrice = ltPrice;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        for (String nodeName : path) {
            out.append(nodeName + " ");
        }
        out.append(calculateTripCost());
        return out.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShortestPathTO)) return false;

        ShortestPathTO that = (ShortestPathTO) o;

        if (Float.compare(that.autonomy, autonomy) != 0) return false;
        if (Float.compare(that.distance, distance) != 0) return false;
        if (Float.compare(that.ltPrice, ltPrice) != 0) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (autonomy != +0.0f ? Float.floatToIntBits(autonomy) : 0);
        result = 31 * result + (ltPrice != +0.0f ? Float.floatToIntBits(ltPrice) : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (distance != +0.0f ? Float.floatToIntBits(distance) : 0);
        return result;
    }
}

