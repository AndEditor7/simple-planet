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
uniform mat3 u_normTrans;

void main()
{
	vec3 point = normalize(v_normal);
	
	vec3 norDat = (textureCube(u_normalMap, point).xyz-0.5)*2.0;
	vec3 normal = normalize(u_normTrans * norDat);
	float away = -dot(normalize(u_normTrans * point), u_lightDir);
	normal = normalize(mix(normal, -u_lightDir, clamp(away, 0.0, 1.0)));
	
	vec3 viewDir = normalize(u_position - v_position);
    vec3 reflectDir = reflect(-u_lightDir, normal);  
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), 8.0) * 0.1;
    float light = max(dot(normal, u_lightDir), 0.0);

	vec4 pix = pow(textureCube(u_colorMap, point), vec4(1.0/2.2));
	gl_FragColor = pow(pix * light + spec, vec4(2.2));
}
