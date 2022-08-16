#version 100 
#ifdef GL_ES
precision highp float;
#endif

varying vec3 v_position;
varying vec3 v_normal;

uniform vec3 u_lightDir;
uniform vec3 u_position;

uniform samplerCube u_colorMap;
uniform samplerCube u_normalMap;

void main()
{
	vec3 point = normalize(v_normal);
	vec4 pix = textureCube(u_colorMap, point);
	
	vec3 normal = normalize((textureCube(u_normalMap, point).xyz-0.5)*2.0);
	vec3 viewDir = normalize(u_position - v_position);
    vec3 reflectDir = reflect(-u_lightDir, normal);  
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 8.0) * 0.5;
	
	float amb = 0.5;
    float light = mix(dot(normal, u_lightDir), 1.0, amb);
    if (light < amb) {
    	light = mix(amb, light, amb);
    }
    light = pow(light, 2.2);
    spec = pow(spec, 2.2);
	gl_FragColor = pix * light + spec;
}
