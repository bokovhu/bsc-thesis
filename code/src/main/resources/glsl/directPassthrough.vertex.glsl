#version 410

layout(location = 0) in vec4 a_NDC;
layout(location = 1) in vec2 a_texCoords;
layout(location = 2) in vec4 a_color;

layout(location = 0) out vec2 v_texCoords;

void main() {
    gl_Position = a_NDC;
    v_texCoords = a_texCoords;
}