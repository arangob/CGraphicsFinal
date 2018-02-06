#version 330 core

in vec4 vertex_Color;
in vec4 vertex_eyespace;
in vec4 vertex_normal;

uniform mat4 modelviewMatrix;

out vec4 fragColor;

void main(){
    vec3 light = normalize((modelviewMatrix * vec4(0,0,10,0)).xyz);

    vec3 surface_normal = normalize(vertex_normal.xyz);
    vec3 eyespace_normal = normalize(vertex_eyespace.xyz);
    vec4 diffuse = vertex_Color * clamp(dot(light, surface_normal), 0 ,1);
    
    fragColor = vec4(diffuse.xyz, 1);
}

