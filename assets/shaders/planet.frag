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
uniform samplerCube u_dataMap;
uniform mat3 u_normTrans;
uniform float u_specularScl;
uniform float u_shininessScl;
uniform float u_gamma; // 2.0
uniform float u_amb; // ambient

void main()
{
	vec3 point = normalize(v_normal);
	vec4 colourPix = textureCube(u_colorMap, point);
	vec4 normalPix = textureCube(u_normalMap, point);
	vec4 dataPix = textureCube(u_dataMap, point);
	
	vec3 sphereNor = normalize(u_normTrans * point);
	float sphereDot = dot(sphereNor, u_lightDir);
	
	vec3 norDat = (normalPix.xyz-0.5)*2.0;
	vec3 normal = normalize(u_normTrans * norDat);
	normal = normalize(mix(normal, -u_lightDir, clamp(-sphereDot, 0.0, 1.0))); // Fix lighting behide the planet from sunlight
	float noiseDot = dot(normal, u_lightDir);
	
	vec3 viewDir = normalize(u_position - v_position);
    vec3 reflectDir = reflect(-u_lightDir, normal); 
    
    float specular = dataPix.r * u_specularScl;
    float shininess = dataPix.g * u_shininessScl;
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess) * specular;
	
    float light = mix(max(noiseDot, 0.0), 1.0, max(sphereDot, 0.0) * u_amb);

	vec3 pix = pow(colourPix.rgb, vec3(1.0/u_gamma));
	gl_FragColor = vec4(pow(pix * max(light, dataPix.b) + spec, vec3(u_gamma)), 1.0);
}
