package andrade.rodrigo.walmart;

import andrade.rodrigo.walmart.constants.Status;
import andrade.rodrigo.walmart.exceptions.IllegalMapException;
import andrade.rodrigo.walmart.persistence.dao.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Scanner;

/**
 * Created by Rodrigo Del Cistia Andrade <vantroy@gmail.com>
 * Date: 21/02/2015
 * All rights reserved.
 */
@Service
@Transactional
public class GraphServiceImpl implements GraphService {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    LocationRepository locationRepo;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    Neo4jTemplate template;

    public Status addGraph(String id, String map) throws IllegalMapException {

        parseMaptoGraph(map);
        return Status.SERVER_ERROR;
    }

    private void parseMaptoGraph(String map) throws IllegalMapException {
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
    }

    // Very Rudimentary input checking
    private void validateLine(String[] pieces) throws IllegalMapException {
        if(pieces.length != 3) {
            throw new IllegalMapException("Map does not conform to speciefied format");
        }
    }


}
