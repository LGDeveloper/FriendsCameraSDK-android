attribute vec4 aPosition;
 attribute vec2 aTexture;
 uniform mat4 uMatrix;
 uniform mat4 uSTMatrix;
 varying vec2 tex;
 void main() {
  vec2 texCoord = (uSTMatrix * vec4(aTexture, 0.0, 1.0)).st;
  tex = vec2(texCoord.s, 1.0 - texCoord.t);
  gl_Position = uMatrix * aPosition ;
 }