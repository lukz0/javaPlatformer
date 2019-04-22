package Game;

import Level.Chunk;
import Level.Level;
import Level.Tilemap;
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

    static Level ReadLevel(String path){
        JSONObject tempmap = ReadFile(path);
        int width = (int)tempmap.get("width");
        int chunkAmount = (int)Math.floor(width/9);

        Tilemap tilemap;
        ArrayList<Level.LevelBackground> levelBackgrounds = new ArrayList<>();


        JSONArray layers = (JSONArray)tempmap.get("layers");
        for(Object object:layers){
            JSONObject layer = (JSONObject)object;

            String layertype = (String)layer.get("type");
            if("imagelayer".equals(layertype)){
                String imgpath = (String)layer.get("image");
                float ratio = 0.16f;
                float zindex = -1f;
                float movetranslation = 0;
                Vector3f ticktranslation = new Vector3f(0,0,0);
                levelBackgrounds.add(new Level.LevelBackground(imgpath,ratio,zindex,movetranslation,ticktranslation));
            }
            if ("tilelayer".equals(layertype)){
                int[] tempblocks = (int[])layer.get("data");
                ArrayList<String> blocknames = new ArrayList<>();
                for (int i = 8; i > 0; i--) {
                    for (int j = 0; j<width;j++){
                        blocknames.add(tempblocks[i+j*width]==1?"Ground":"");
                    }
                }

                tilemap = new Tilemap(chunkAmount,(String[]) blocknames.toArray());

            }

        }
        if (tilemap == null){
            tilemap = new Tilemap(chunkAmount,new String[9*9]);
        }

        return new Level(levelBackgrounds,tilemap);
    }
}