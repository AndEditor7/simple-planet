#version 100 
#ifdef GL_ES
precision highp float;
#endif

precision mediump float;

attribute vec4 a_position;

varying vec3 v_position;
varying vec3 v_normal;

uniform mat4 u_projTrans;
uniform mat4 u_posTrans;

void main()
{
	vec4 pos = u_posTrans * a_position;
	v_position = pos.xyz;
	v_normal = a_position.xyz;
	gl_Position = u_projTrans * pos;
}
