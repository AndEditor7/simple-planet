#version 100 
#ifdef GL_ES
precision highp float;
#endif

attribute vec4 a_position;

varying vec3 v_position;
varying vec3 v_normal;

uniform mat4 u_mat;

void main()
{
	v_position = a_position.xyz;
	v_normal = normalize(a_position.xyz);
	gl_Position = u_mat * a_position;
}
