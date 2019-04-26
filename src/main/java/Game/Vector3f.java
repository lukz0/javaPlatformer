package Game;

public class Vector3f {
    public float[] values;

    public Vector3f(float x, float y, float z) {
        this.values = new float[]{x, y, z};
    }

    public Vector3f add(Vector3f v2) {
        return new Vector3f(this.values[0]+v2.values[0], this.values[1]+v2.values[1], this.values[2]+v2.values[2]);
    }

    Vector3f multiply(float val) {
        return new Vector3f(this.values[0]*val, this.values[1]*val, this.values[2]*val);
    }

    float[] getOpenGLvector() {
        return new float[]{(this.values[0]*2)/16, (this.values[1]*2)/9, this.values[2]};
    }

    public static Vector3f EMPTY = new Vector3f(0, 0, 0);

    void setX(float x) {
        this.values[0] = x;
    }

    void setY(float y) {
        this.values[1] = y;
    }

    void setZ(float z) {
        this.values[2] = z;
    }

}