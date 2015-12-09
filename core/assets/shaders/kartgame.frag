// The shader used in the kart game

#ifdef GL_ES
    precision mediump float;
#endif

const float VIGNETTE_OUTER_RADIUS = 0.75;
const float VIGNETTE_INNER_RADIUS = 0.3;

const vec4 FILTER_COLOR = vec4(1, 0.9, 0.65, 1.0);

uniform mat4 u_projTrans;
uniform vec2 u_resolution;
uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
        vec4 texColor = texture2D(u_texture, v_texCoords);

        vec2 position = (gl_FragCoord.xy / u_resolution.xy) - vec2(0.5);
        float len = length(position);

        float vignette = smoothstep(VIGNETTE_OUTER_RADIUS, VIGNETTE_INNER_RADIUS, len);

        texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, 0.5);

        gl_FragColor = texColor * FILTER_COLOR * v_color;
}