#version 100 
#ifdef GL_ES
precision highp float;
#endif

varying vec3 v_position;
varying vec3 v_normal;

uniform vec3 u_lightDir;
uniform vec3 u_position;

// vec3(255.0/255.0, 201.0/255.0, 178.0/255.0)
uniform vec3 u_color;
uniform float u_strength; // 2.0
uniform float u_gamma; // 2.5

void main()
{
	vec3 normal = normalize(v_normal);
	float sphereDot = pow(max(dot(normal, u_lightDir), 0.0), 1.0/1.2);
	sphereDot *= u_strength;
	vec3 viewDir = normalize(v_position - u_position);
	gl_FragColor = vec4(u_color, pow(dot(normal, viewDir), u_gamma) * sphereDot);
}
