package Game;

import Level.Level;
import Level.Tilemap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class JSONReader {
    private static final JSONParser parser = new JSONParser();

    private static JSONObject ReadFile(String path) {
        JSONObject obj = null;
        try (FileReader reader = new FileReader(path)) {
            obj = (JSONObject) parser.parse(reader);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static void WriteOptions(Renderer.Options options){
        JSONObject filecontent = new JSONObject();
        filecontent.put("width",options.width);
        filecontent.put("heigth",options.height);
        filecontent.put("Vsync", options.vsync);
        filecontent.put("fullscreen", options.fullscreen);
        try (FileWriter writer = new FileWriter("config.json")){
            writer.write(filecontent.toJSONString());
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Renderer.Options ReadOptions(){
        JSONObject filecontent = ReadFile("config.json");
        //it's null if the readfile failed, which means we fix it with default arguments
        if (filecontent==null){
            WriteOptions(new Renderer.Options(1280,720,false,false));
        }
        int width = (int)(long)filecontent.get("width");
        int height = (int)(long)filecontent.get("heigth");
        boolean vsync = (boolean)filecontent.get("Vsync");
        boolean fullscreen = (boolean)filecontent.get("fullscreen");
        return new Renderer.Options(width,height,fullscreen,vsync);
    }

    static Level ReadLevel(String path) {
        JSONObject tempmap = ReadFile(path);
        int width = (int) (long) tempmap.get("width");
        int chunkAmount = Integer.divideUnsigned(width, 9);

        Tilemap tilemap = null;
        ArrayList<Level.LevelBackground> levelBackgrounds = new ArrayList<>();
        JSONArray tilesets = (JSONArray) tempmap.get("tilesets");
        String tilesetSource = ((String) ((JSONObject) tilesets.get(0)).get("source"));
        tilesetSource = Paths.get(path.substring(0, path.lastIndexOf("/")), tilesetSource).toString();
        HashMap<Integer, String> tileSet = ReadTileset(tilesetSource);


        JSONArray layers = (JSONArray) tempmap.get("layers");
        for (Object object : layers) {
            JSONObject layer = (JSONObject) object;

            String layertype = (String) layer.get("type");

            if ("imagelayer".equals(layertype)) {
                levelBackgrounds.add(readBackground(layer, path));
            }

            if ("tilelayer".equals(layertype)) {
                JSONArray tempblocks = (JSONArray) layer.get("data");

                ArrayList<String> blocknames = new ArrayList<>();
                for (int i = 8; i >= 0; i--) {
                    for (int j = 0; j < width; j++) {
                        blocknames.add(tileSet.get((int) (long) tempblocks.get(j + i * width)));
                    }
                }
                String[] blocknamearray = new String[blocknames.size()];
                blocknames.toArray(blocknamearray);
                tilemap = new Tilemap(chunkAmount, blocknamearray);

            }

        }
        if (tilemap == null) {
            tilemap = new Tilemap(chunkAmount, new String[9 * 9]);
        }

        return new Level(levelBackgrounds, tilemap);
    }


    static HashMap<Integer, String> ReadTileset(String path) {
        HashMap<Integer, String> tileset = new HashMap<>();
        tileset.put(0, null);

        JSONObject tileFile = ReadFile(path);
        JSONArray tileList = (JSONArray) tileFile.get("tiles");
        for (Object temptile : tileList) {
            JSONObject tile = (JSONObject) temptile;
            //add 1 so it maps to the data correctly and allows 0 to be null
            Integer id = (int) (long) (Long) tile.get("id") + 1;
            JSONArray properties = (JSONArray) tile.get("properties");
            String name = "";
            for (Object obj : properties) {
                JSONObject property = (JSONObject) obj;
                if (property.get("name").equals("name")) {
                    name = (String) property.get("value");
                }
            }
            tileset.put(id, name);
        }

        return tileset;
    }

    private static Level.LevelBackground readBackground(JSONObject jsonObject, String mainpath) {
        String imgpath = (String) jsonObject.get("image");
        String actualpath = Paths.get(mainpath.substring(0, mainpath.lastIndexOf("/")), imgpath).toAbsolutePath().toString();
        int height = 1;
        int width = 1;
        float z_index = 0f;
        float relative_move = 0f;
        float x_move = 0f;
        float y_move = 0f;
        float z_move = 0f;
        for (Object property : (JSONArray) jsonObject.get("properties")) {
            JSONObject jsonProperty = (JSONObject) property;
            String name = (String) jsonProperty.get("name");
            switch (name) {
                case "width":
                    width = (int) (long) jsonProperty.get("value");
                    break;
                case "height":
                    height = (int) (long) jsonProperty.get("value");
                    break;
                case "x_move":
                    x_move = (float) (double) jsonProperty.get("value");
                    break;
                case "y_move":
                    y_move = (float) (long) jsonProperty.get("value");
                    break;
                case "z_move":
                    z_move = (float) (long) jsonProperty.get("value");
                    break;
                case "z_index":
                    z_index = (float) (double) jsonProperty.get("value");
                    break;
                case "relative_move":
                    relative_move = (float) (long) jsonProperty.get("value");
                    break;
                default:
            }
        }
        Vector3f ticktranslation = new Vector3f(x_move, y_move, z_move);
        return new Level.LevelBackground(actualpath, (float) width / height, z_index, relative_move, ticktranslation);
    }
}