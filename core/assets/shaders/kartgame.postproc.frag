#ifdef GL_ES
#define LOWP lowp
#define MED mediump
precision lowp float;
#else
#define LOWP
#define MED
#endif

uniform sampler2D u_texture;
varying MED vec2 v_texCoord0;

void main(){
    vec4 color = texture2D(u_texture, v_texCoord0);
    gl_FragColor = vec4(color.rgb, color.a);
}