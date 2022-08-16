#version 100
#ifdef GL_ES
precision mediump float;
#endif

varying vec3 v_position;
varying vec4 v_color;
varying vec3 v_normal;
varying float v_shininess;
varying float v_specular; // Specular Strength

//const vec3 c_lightDir = normalize(vec3(-0.6, 0.8, -0.3));

uniform vec3 u_lightDir;
uniform vec3 u_position;

void main()
{
	vec4 pix = v_color;
	
	vec3 normal = normalize(v_normal);
	vec3 viewDir = normalize(u_position - v_position);
    vec3 reflectDir = reflect(-u_lightDir, normal);  
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), v_shininess) * v_specular;
	
	float amb = 0.5;
    float light = mix(dot(normal, u_lightDir), 1.0, amb);
    if (light < amb) {
    	light = mix(amb, light, amb);
    }
    light = pow(light, 2.2);
	gl_FragColor = pix * light + spec;
}
