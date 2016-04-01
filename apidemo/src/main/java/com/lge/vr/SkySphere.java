package com.lge.vr;

public class SkySphere {
    private final static short NUM_OF_BAND = 100;
    private final static float radius = 1.0f;
    public static final int NUM_PER_VERTEX = 3;
    public static final int NUM_PER_NORMAL = 3;
    public static final int NUM_PER_COLOR = 4;
    public static final int NUM_PER_TEXCOORD = 2;

    public float[] vertices = new float[(NUM_OF_BAND + 1) * (NUM_OF_BAND + 1) * NUM_PER_VERTEX];
    public float[] colors = new float[(NUM_OF_BAND + 1) * (NUM_OF_BAND + 1) * NUM_PER_COLOR];
    public float[] textcoords = new float[(NUM_OF_BAND + 1) * (NUM_OF_BAND + 1) * NUM_PER_TEXCOORD];
    public float[] normals = new float[(NUM_OF_BAND + 1) * (NUM_OF_BAND + 1) * NUM_PER_NORMAL];
    public short[] indices = new short[NUM_OF_BAND * NUM_OF_BAND * 6];

    public SkySphere() {
        for (short latitude = 0; latitude <= NUM_OF_BAND; latitude++) {
            float theta = (float) (latitude * Math.PI / NUM_OF_BAND);
            float sinTheta = (float) (Math.sin(theta));
            float cosTheta = (float) (Math.cos(theta));

            for (short longitude = 0; longitude <= NUM_OF_BAND; longitude++) {
                float phi = (float) (longitude * 2 * Math.PI / NUM_OF_BAND);
                float sinPhi = (float) (Math.sin(phi));
                float cosPhi = (float) (Math.cos(phi));

                float x = cosPhi * sinTheta;
                float y = cosTheta;
                float z = sinPhi * sinTheta;
                float u = ((float) longitude / NUM_OF_BAND);
                float v = ((float) latitude / NUM_OF_BAND);

                vertices[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_VERTEX + 0] = radius * x;
                vertices[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_VERTEX + 1] = radius * y;
                vertices[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_VERTEX + 2] = radius * z;

                colors[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_COLOR + 0] = x * x;
                colors[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_COLOR + 1] = x * x;
                colors[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_COLOR + 2] = x * x;
                colors[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_COLOR + 3] = 1.0f;

                textcoords[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_TEXCOORD + 0] = u;
                textcoords[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_TEXCOORD + 1] = v;

                normals[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_NORMAL + 0] = x;
                normals[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_NORMAL + 1] = y;
                normals[(latitude * (NUM_OF_BAND + 1) + longitude) * NUM_PER_NORMAL + 2] = z;
            }
        }

        for (short latitude = 0; latitude < NUM_OF_BAND; latitude++) {
            for (short longitude = 0; longitude < NUM_OF_BAND; longitude++) {
                short first = (short) ((latitude * (NUM_OF_BAND + 1)) + longitude);
                short second = (short) (first + NUM_OF_BAND + 1);

                indices[(latitude * NUM_OF_BAND + longitude) * 6 + 0] = first;
                indices[(latitude * NUM_OF_BAND + longitude) * 6 + 1] = second;
                indices[(latitude * NUM_OF_BAND + longitude) * 6 + 2] = (short) (first + 1);

                indices[(latitude * NUM_OF_BAND + longitude) * 6 + 3] = second;
                indices[(latitude * NUM_OF_BAND + longitude) * 6 + 4] = (short) (second + 1);
                indices[(latitude * NUM_OF_BAND + longitude) * 6 + 5] = (short) (first + 1);
            }
        }
    }
}
