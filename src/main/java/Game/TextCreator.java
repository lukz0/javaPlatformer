package Game;

import Game.Async;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class TextCreator {
    BufferedImage bufImg;
    Graphics g;
    int width, height;
    Color color;
    Font font;

    TextCreator(int width, int height, Color color) {
        this.color = color;
        this.font = new Font("Consolas", Font.PLAIN, (int)(height/1.5f));
        this.width = width;
        this.height = height;
    }
    TextCreator(int width, int height, Color color, String fontname) {
        this.color = color;
        this.font = new Font(fontname, Font.PLAIN, (int)(height/1.5f));
        this.width = width;
        this.height = height;
    }

    public Async<Texture> renderString(View v, String s) {
        this.bufImg = new BufferedImage(width, height, TYPE_INT_ARGB);
        this.g = bufImg.getGraphics();
        this.g.setColor(this.color);
        this.g.setFont(this.font);
        this.g.drawString(s, 0, (int)(this.height*(3/(float)4)));
        int[] pixelValues = new int[this.width*this.height];
        this.bufImg.getRGB(0, 0, this.width, this.height, pixelValues, 0, this.width);
        /*
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.getGraphics().drawImage(this.bufImg, 10, 10, frame);
        */
        return v.loadTexture(new Texture.ArrayAndSize(pixelValues, this.width, this.height));
    }
}
