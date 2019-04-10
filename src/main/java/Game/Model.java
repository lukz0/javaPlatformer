package Game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;

public class Model {
    static Renderer.ShaderUtils.DoubleString loadShaderFiles(String vertPath, String fragPath) {
        return new Renderer.ShaderUtils.DoubleString(loadFileAsString(vertPath), loadFileAsString(fragPath));
    }

    static String loadFileAsString(String filePath) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String buffer = null;
            while ((buffer = reader.readLine()) != null) {
                builder.append(buffer.concat("\n"));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    static BufferedImage loadImage(String path) throws Exception {
        return ImageIO.read(new FileInputStream(path));
    }
}
