import javax.swing.*;

class Mario extends Entity {

    Mario() {
        this.posX = 0; // TODO: find initial X and Y value for level
        this.vX = 0;
        this.vY = -10;

        this.icon = new ImageIcon(this.getClass().getResource("Mario.png")); // TODO: add Mario sprite
    }

    public void doMove() {
        this.posX += this.vX;
        this.posY += this.vY;
    }
}