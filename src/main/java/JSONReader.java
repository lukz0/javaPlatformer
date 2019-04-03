
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class JSONReader {
    private static final JSONParser parser = new JSONParser();

    //Object here is actually a JSON object, the parser to make them tiles is yet to be implemented given that we don't have a Tile class
    static JSONObject ReadFile(String path) {
        JSONObject obj = null;
        try (FileReader reader = new FileReader(path)) {

            obj = (JSONObject) parser.parse(reader);


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
