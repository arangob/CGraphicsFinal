#version 330 core

in vec4 normal;
in vec4 position;
in vec4 color;

uniform mat4 normalMatrix;
uniform mat4 modelviewMatrix;
uniform mat4 projectionMatrix;
uniform vec4 lightPosition;

out vec4 vertex_Color;
out vec4 vertex_eyespace;
out vec4 vertex_normal;
out vec2 fragment_UV;

void main(){
    gl_Position = projectionMatrix * modelviewMatrix * position;
    vertex_eyespace = modelviewMatrix * position;
    vertex_normal = normalMatrix * normal;

    vertex_Color = color;
}
