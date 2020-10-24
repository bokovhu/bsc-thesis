#version 410

layout(location = 0) in vec4 a_NDC;
layout(location = 1) in vec2 a_texCoords;
layout(location = 2) in vec4 a_color;

layout(location = 0) out vec2 v_texCoords;
layout(location = 1) out vec4 v_color;

uniform mat4 u_MVP;

void main() {
    gl_Position = u_MVP * a_NDC;
    v_texCoords = a_texCoords;
    v_color = a_color;
}