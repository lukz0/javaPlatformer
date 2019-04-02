
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class JSONReader {
    private static final JSONParser parser = new JSONParser();

    static ArrayList<Object> ReadFile(String path) {
        ArrayList<Object> result = new ArrayList<>();

        try (FileReader reader = new FileReader(path)) {

            JSONArray parsed = (JSONArray) parser.parse(reader);
            parsed.forEach(obj -> result.add(obj));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
