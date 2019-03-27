import org.lwjgl.BufferUtils;

import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.concurrent.ArrayBlockingQueue;

import static org.lwjgl.opengl.GL45.*;

public class Texture {
    private int width, height;
    private int textureID;

    public Texture(String path) {
        textureID = load(path);
    }

    private int load(String path) {
        int pixels[] = null;
        try {
            BufferedImage image = Model.loadImage(path);
            this.width = image.getWidth();
            this.height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            // Convert the texture from bgra to rgbe
            int[] data = new int[width * height];
            for (int i = 0; i < width * height; i++) {
                int b = (pixels[i] & 0xff);
                int g = (pixels[i] & 0xff00) >> 8;
                int r = (pixels[i] & 0xff0000) >> 16;
                int a = (pixels[i] & 0xff000000) >> 24;

                data[i] = r | (g << 8) | (b << 16) | (a << 24);
            }

            int result = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, result);

            // Don't blend
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, Renderer.BufferUtils.createIntBuffer(data));

            glBindTexture(GL_TEXTURE_2D, 0);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, this.textureID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    static class StringAndTexReturnQueue {
        public String path;
        public ArrayBlockingQueue<Texture> returnQueue;
        StringAndTexReturnQueue(String path, ArrayBlockingQueue<Texture> returnQueue) {
            this.path = path;
            this.returnQueue = returnQueue;
        }
    }
}
