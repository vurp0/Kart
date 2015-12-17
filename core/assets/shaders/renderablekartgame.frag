#ifdef GL_ES
precision mediump float;
#endif

const vec3 FILTER_COLOR = vec3(1, 0.87, 0.62);
const float BRIGHTNESS  = 0.1;

uniform sampler2D u_diffuseTexture;

varying float v_opacity;

varying float v_alphaTest;

//varying vec2 v_texCoord0;
varying vec2 v_diffuseUV;

void main() {
    vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);
    diffuse.a = diffuse.a * v_opacity;
        if (diffuse.a <= v_alphaTest)
            discard;

    /*diffuse.rgb = diffuse.rgb * FILTER_COLOR;
    diffuse.rgb += BRIGHTNESS;*/

    gl_FragColor = diffuse;
}