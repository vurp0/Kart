#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_diffuseTexture;

varying vec2 v_texCoord0;

void main() {
    gl_FragColor = texture2D(u_diffuseTexture, v_texCoord0);
}