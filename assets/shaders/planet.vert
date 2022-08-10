#version 100 
#ifdef GL_ES
precision highp float;
#endif

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec3 a_normal;
attribute float a_shininess;
attribute float a_specular; // Specular Strength

varying vec3 v_position;
varying vec4 v_color;
varying vec3 v_normal;
varying float v_shininess;
varying float v_specular; // Specular Strength

uniform mat4 u_mat;

void main()
{
	v_position = a_position.xyz;
	v_color = a_color;
	v_normal = a_normal;
	v_shininess = a_shininess;
	v_specular = a_specular;
	gl_Position = u_mat * a_position;
}
