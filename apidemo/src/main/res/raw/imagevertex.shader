uniform mat4 uMatrix;
attribute vec4 aPosition;
attribute vec2 aTexture;
varying vec2 tex;
void main() {
tex = aTexture;
gl_Position = uMatrix * aPosition;
}