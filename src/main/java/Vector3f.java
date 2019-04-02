class Vector3f {
    float[] values;

    Vector3f(float x, float y, float z) {
        this.values = new float[]{x, y, z};
    }

    Vector3f add(Vector3f v2) {
        return new Vector3f(this.values[0]+v2.values[0], this.values[1]+v2.values[1], this.values[2]+v2.values[2]);
    }

    Vector3f multiply(float val) {
        return new Vector3f(this.values[0]*val, this.values[1]*val, this.values[2]*val);
    }

    float[] getOpenGLvector() {
        return new float[]{(this.values[0]*2)/19, (this.values[1]*2)/9, this.values[2]};
    }
}