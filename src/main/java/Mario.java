class Mario extends Entity {
    Mario(Async<Texture> texture) {
        this.posX = 0; // TODO: find initial X and Y value for level
        this.vX = 0;
        this.vY = -10;
        this.texture = texture; // TODO: add Mario sprite
    }

    public void doMove() {
        this.posX += this.vX;
        this.posY += this.vY;
    }
}