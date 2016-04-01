#extension GL_OES_EGL_image_external : require
precision highp float;
uniform samplerExternalOES uTexture;
varying vec2 tex;
void main() {
  gl_FragColor = texture2D(uTexture, tex);
}