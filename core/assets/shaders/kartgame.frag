// The shader used in the kart game

#ifdef GL_ES
    precision mediump float;
#endif

const float VIGNETTE_OUTER_RADIUS = 0.75;
const float VIGNETTE_INNER_RADIUS = 0.3;

const vec3 FILTER_COLOR = vec3(1, 0.87, 0.62);
const vec3 MIX_COLORS   = vec3(0, 0, 0); //mix RG, GB, RB
const float BRIGHTNESS  = 0.1;

uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
uniform vec2 u_resolution;
uniform float fadeDark;
uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
/*        vec4 texColor = texture2D(u_texture, v_texCoords);

        vec2 position = (gl_FragCoord.xy / u_resolution.xy) - vec2(0.5);
        float len = length(position);

        float vignette = smoothstep(VIGNETTE_OUTER_RADIUS, VIGNETTE_INNER_RADIUS, len);

        texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, 0.5);
        texColor.rgb = texColor.rgb * FILTER_COLOR;

        vec4 tmpColor = vec4((texColor.r*(1.0-MIX_COLORS.x-MIX_COLORS.z) + texColor.g*MIX_COLORS.x + texColor.b*MIX_COLORS.z),
                             (texColor.r*MIX_COLORS.x + texColor.g*(1.0-MIX_COLORS.x-MIX_COLORS.y) + texColor.b*MIX_COLORS.y),
                             (texColor.r*MIX_COLORS.y + texColor.g*MIX_COLORS.z + texColor.b*(1.0-MIX_COLORS.y-MIX_COLORS.z)),
                             1.0);

        tmpColor += BRIGHTNESS;

        gl_FragColor.rgb = tmpColor.rgb * v_color.rgb * fadeDark;
        gl_FragColor.a = texColor.a;*/
        gl_FragColor = texture2D(u_texture, v_texCoords);
}