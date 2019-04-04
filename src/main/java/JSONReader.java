
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
    //TODO: when a Tile class/Chunk class is created, make a converter from JSONObject to that
    static ArrayList<Object> ReadFile(String path) {
        ArrayList<Object> result = new ArrayList<>();

        static JSONObject ReadFile(String path) {
            JSONObject obj = null;
            try (FileReader reader = new FileReader(path)) {

                JSONArray parsed = (JSONArray) parser.parse(reader);
                //parsed.forEach(obj -> result.add(obj));
                //parsed.forEach(result::add);
                obj = (JSONObject) parser.parse(reader);

                //i think this does the same as the commented out version, if it doesn't work inverse it, else just use the forEach over.
                Collections.addAll(result,parsed);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return result;
            return obj;
        }

    }