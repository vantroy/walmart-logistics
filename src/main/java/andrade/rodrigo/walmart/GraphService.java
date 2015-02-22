package andrade.rodrigo.walmart;

import andrade.rodrigo.walmart.constants.Status;
import andrade.rodrigo.walmart.exceptions.IllegalMapException;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Scanner;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 21/02/2015
 * All rights reserved.
 */
public class GraphService {


    public Status addGraph(String id, String map) throws IllegalMapException {

        // TODO: check persistence if already exists
        DirectedGraph<String, DefaultWeightedEdge> graph = parseMaptoGraph(map);
        return Status.SERVER_ERROR;
    }

    private DirectedGraph<String, DefaultWeightedEdge> parseMaptoGraph(String map) throws IllegalMapException {
        DirectedGraph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
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

            // TODO: continue creating graph



        }
        scanner.close();
        return graph;
    }

    // Very Rudimentary input checking
    private void validateLine(String[] pieces) throws IllegalMapException {
        if(pieces.length != 3) {
            throw new IllegalMapException("Map does not conform to speciefied format");
        }
    }


}
