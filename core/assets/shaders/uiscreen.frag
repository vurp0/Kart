#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float darkness;

void main()
{
  gl_FragColor = vec4(v_color.rgb * texture2D(u_texture, v_texCoords).rgb * darkness, v_color.a * texture2D(u_texture, v_texCoords).a);
}