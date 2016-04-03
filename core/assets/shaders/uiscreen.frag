#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

const vec3 GB_BLACK = vec3(0.0314, 0.0941, 0.1255);
const vec3 GB_DARK_GREY = vec3(0.1882, 0.4078, 0.3137);
const vec3 GB_LIGHT_GREY = vec3(0.5333, 0.7529, 0.4392);
const vec3 GB_WHITE = vec3(1, 1, 1);

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float darkness;

float grayscale(vec3 rgb) {
  return dot(rgb, vec3(0.299, 0.587, 0.114));
}

void main()
{
  vec4 screenColor = vec4(v_color.rgb * texture2D(u_texture, v_texCoords).rgb * darkness, v_color.a * texture2D(u_texture, v_texCoords).a);
  if (screenColor.a > 0) { screenColor.a = 1; } else { screenColor.a = 0; }

  float gray = grayscale(screenColor.rgb);

  if (gray < 0.25) {
    gl_FragColor = vec4(GB_BLACK, screenColor.a);
  } else if (gray < 0.5) {
    gl_FragColor = vec4(GB_DARK_GREY, screenColor.a);
  } else if (gray < 0.75) {
    gl_FragColor = vec4(GB_LIGHT_GREY, screenColor.a);
  } else {
    gl_FragColor = vec4(GB_WHITE, screenColor.a);
  }
}