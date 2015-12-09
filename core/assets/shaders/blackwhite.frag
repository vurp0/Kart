// Simple example shader that renders in grayscale

#ifdef GL_ES
    precision mediump float;
#endif

uniform mat4 u_projTrans;
uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
        vec4 color = v_color * texture2D(u_texture, v_texCoords);
        float gray = (color.r * 0.299 + color.g * 0.587 + color.b * 0.114);//(color.r + color.g + color.b) / 3.0;
        gl_FragColor = vec4(gray, gray, gray, color.a);
}