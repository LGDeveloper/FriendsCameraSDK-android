uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
attribute vec2 aTexture;
varying vec2 tex;
void main() {
 tex = aTexture;
 gl_Position = uMVPMatrix * vPosition;
}