#version 100 
#ifdef GL_ES
precision highp float;
#endif

attribute vec4 a_position;

varying vec3 v_position;
varying vec3 v_normal;

uniform mat4 u_projTrans;
uniform mat4 u_posTrans;
uniform mat3 u_normTrans;

void main()
{
	v_position = a_position.xyz;
	v_normal = normalize(a_position.xyz);
	vec4 pos = u_posTrans * a_position;
	gl_Position = u_projTrans * pos;
}
