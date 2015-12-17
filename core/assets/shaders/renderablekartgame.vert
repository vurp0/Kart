attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform vec4 u_diffuseUVTransform;

uniform float u_opacity;
varying float v_opacity;

uniform float u_alphaTest;
varying float v_alphaTest;

//varying vec2 v_texCoord0;
varying vec2 v_diffuseUV;

void main() {
    //v_texCoord0 = a_texCoord0;
    v_opacity = u_opacity;
    v_alphaTest = u_alphaTest;
	v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}