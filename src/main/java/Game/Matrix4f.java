package Game;

import java.nio.FloatBuffer;

class Matrix4f {
    float[] values;

    // v(row)(column)
    Matrix4f(float v11, float v21, float v31, float v41,
             float v12, float v22, float v32, float v42,
             float v13, float v23, float v33, float v43,
             float v14, float v24, float v34, float v44) {
        this.values = new float[]{
                v11, v21, v31, v41,
                v12, v22, v32, v42,
                v13, v23, v33, v43,
                v14, v24, v34, v44};
    }

    public static Matrix4f createIdentity() {
        return new Matrix4f(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    public static Matrix4f createOrtographic(float left, float right, float bottom, float top, float near, float far) {
            /*Matrix4f result = createIdentity();

            //14
            result.values[0 + 3*4] = -(left + right)/2; // center x
            //24
            result.values[1 + 3*4] = -(top + bottom)/2; // center y
            //34
            result.values[2 + 3*4] = -(near + far)/2; // center z

            //11
            result.values[0 + 0*4] = 2.0f / (right - left); // scale x
            //22
            result.values[1 + 1*4] = 2.0f / (top - bottom); // scale y
            //33
            result.values[2 + 2*4] = 2.0f / (far - near); // scale z

            return result;*/
        // The code above is a more verbose way of doing this:
        return new Matrix4f(
                2.0f / (right - left), 0, 0, 0,
                0, 2.0f / (top - bottom), 0, 0,
                0, 0, 2.0f / (far - near), 0,
                -(left + right) / 2.0f, -(top + bottom) / 2.0f, -(near + far) / 2.0f, 1
        );
    }

    public static Matrix4f createTranslate(Vector3f vector) {
        return new Matrix4f(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                vector.values[0], vector.values[1], vector.values[2], 1
        );
    }

    public Matrix4f multiply(Matrix4f matrix) {
        return new Matrix4f(
                // first row this, first column matrix
                this.values[0 + 0 * 4] * matrix.values[0 + 0 * 4] + this.values[0 + 1 * 4] * matrix.values[1 + 0 * 4] + this.values[0 + 2 * 4] * matrix.values[2 + 0 * 4] + this.values[0 + 3 * 4] * matrix.values[3 + 0 * 4],
                // second row this, first column matrix
                this.values[1 + 0 * 4] * matrix.values[0 + 0 * 4] + this.values[1 + 1 * 4] * matrix.values[1 + 0 * 4] + this.values[1 + 2 * 4] * matrix.values[2 + 0 * 4] + this.values[1 + 3 * 4] * matrix.values[3 + 0 * 4],
                // third row this, first column matrix
                this.values[2 + 0 * 4] * matrix.values[0 + 0 * 4] + this.values[2 + 1 * 4] * matrix.values[1 + 0 * 4] + this.values[2 + 2 * 4] * matrix.values[2 + 0 * 4] + this.values[2 + 3 * 4] * matrix.values[3 + 0 * 4],
                // fourth row this, first column matrix
                this.values[3 + 0 * 4] * matrix.values[0 + 0 * 4] + this.values[3 + 1 * 4] * matrix.values[1 + 0 * 4] + this.values[3 + 2 * 4] * matrix.values[2 + 0 * 4] + this.values[3 + 3 * 4] * matrix.values[3 + 0 * 4],
                //
                // first row this, second column matrix
                this.values[0 + 0 * 4] * matrix.values[0 + 1 * 4] + this.values[0 + 1 * 4] * matrix.values[1 + 1 * 4] + this.values[0 + 2 * 4] * matrix.values[2 + 1 * 4] + this.values[0 + 3 * 4] * matrix.values[3 + 1 * 4],
                // second row this, second column matrix
                this.values[1 + 0 * 4] * matrix.values[0 + 1 * 4] + this.values[1 + 1 * 4] * matrix.values[1 + 1 * 4] + this.values[1 + 2 * 4] * matrix.values[2 + 1 * 4] + this.values[1 + 3 * 4] * matrix.values[3 + 1 * 4],
                // third row this, second column matrix
                this.values[2 + 0 * 4] * matrix.values[0 + 1 * 4] + this.values[2 + 1 * 4] * matrix.values[1 + 1 * 4] + this.values[2 + 2 * 4] * matrix.values[2 + 1 * 4] + this.values[2 + 3 * 4] * matrix.values[3 + 1 * 4],
                // fourth row this, second column matrix
                this.values[3 + 0 * 4] * matrix.values[0 + 1 * 4] + this.values[3 + 1 * 4] * matrix.values[1 + 1 * 4] + this.values[3 + 2 * 4] * matrix.values[2 + 1 * 4] + this.values[3 + 3 * 4] * matrix.values[3 + 1 * 4],
                //
                // first row this, third column matrix
                this.values[0 + 0 * 4] * matrix.values[0 + 2 * 4] + this.values[0 + 1 * 4] * matrix.values[1 + 2 * 4] + this.values[0 + 2 * 4] * matrix.values[2 + 2 * 4] + this.values[0 + 3 * 4] * matrix.values[3 + 2 * 4],
                // second row this, third column matrix
                this.values[1 + 0 * 4] * matrix.values[0 + 2 * 4] + this.values[1 + 1 * 4] * matrix.values[1 + 2 * 4] + this.values[1 + 2 * 4] * matrix.values[2 + 2 * 4] + this.values[1 + 3 * 4] * matrix.values[3 + 2 * 4],
                // third row this, third column matrix
                this.values[2 + 0 * 4] * matrix.values[0 + 2 * 4] + this.values[2 + 1 * 4] * matrix.values[1 + 2 * 4] + this.values[2 + 2 * 4] * matrix.values[2 + 2 * 4] + this.values[2 + 3 * 4] * matrix.values[3 + 2 * 4],
                // fourth row this, third column matrix
                this.values[3 + 0 * 4] * matrix.values[0 + 2 * 4] + this.values[3 + 1 * 4] * matrix.values[1 + 2 * 4] + this.values[3 + 2 * 4] * matrix.values[2 + 2 * 4] + this.values[3 + 3 * 4] * matrix.values[3 + 2 * 4],
                //
                // first row this, fourth column matrix
                this.values[0 + 0 * 4] * matrix.values[0 + 3 * 4] + this.values[0 + 1 * 4] * matrix.values[1 + 3 * 4] + this.values[0 + 2 * 4] * matrix.values[2 + 3 * 4] + this.values[0 + 3 * 4] * matrix.values[3 + 3 * 4],
                // second row this, fourth column matrix
                this.values[1 + 0 * 4] * matrix.values[0 + 3 * 4] + this.values[1 + 1 * 4] * matrix.values[1 + 3 * 4] + this.values[1 + 2 * 4] * matrix.values[2 + 3 * 4] + this.values[1 + 3 * 4] * matrix.values[3 + 3 * 4],
                // third row this, fourth column matrix
                this.values[2 + 0 * 4] * matrix.values[0 + 3 * 4] + this.values[2 + 1 * 4] * matrix.values[1 + 3 * 4] + this.values[2 + 2 * 4] * matrix.values[2 + 3 * 4] + this.values[2 + 3 * 4] * matrix.values[3 + 3 * 4],
                // fourth row this, fourth column matrix
                this.values[3 + 0 * 4] * matrix.values[0 + 3 * 4] + this.values[3 + 1 * 4] * matrix.values[1 + 3 * 4] + this.values[3 + 2 * 4] * matrix.values[2 + 3 * 4] + this.values[3 + 3 * 4] * matrix.values[3 + 3 * 4]
        );
    }

    public FloatBuffer toFloatBuffer() {
        return Renderer.BufferUtils.createFloatBuffer(this.values);
    }
}