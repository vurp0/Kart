#ifdef GL_ES
#define LOWP lowp
#define MED mediump
precision lowp float;
#else
#define LOWP
#define MED
#endif

const vec3 GB_BLACK = vec3(0.0314, 0.0941, 0.1255);
const vec3 GB_DARK_GREY = vec3(0.1882, 0.4078, 0.3137);
const vec3 GB_LIGHT_GREY = vec3(0.5333, 0.7529, 0.4392);
const vec3 GB_WHITE = vec3(1, 1, 1);

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform float darkness;

varying MED vec2 v_texCoord0;

float grayscale(vec3 rgb) {
    return dot(rgb, vec3(0.299, 0.587, 0.114));
}

void main(){
    vec4 texColor = texture2D(u_texture, v_texCoord0);
    texColor.rgb = texColor.rgb * darkness;
    
    if (texColor.a > 0) { texColor.a = 1; } else { texColor.a = 0; }

    float gray = grayscale(texColor.rgb);
  
    if (gray < 0.25) {
      gl_FragColor = vec4(GB_BLACK, texColor.a);
    } else if (gray < 0.5) {
      gl_FragColor = vec4(GB_DARK_GREY, texColor.a);
    } else if (gray < 0.75) {
      gl_FragColor = vec4(GB_LIGHT_GREY, texColor.a);
    } else {
      gl_FragColor = vec4(GB_WHITE, texColor.a);
    }
}