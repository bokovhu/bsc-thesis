#version 410

layout(location = 0) in vec3 a_vertexPosition;
layout(location = 1) in vec3 a_vertexNormal;
layout(location = 2) in vec4 a_vertexColor;

layout(location = 0) out vec3 v_worldPosition;
layout(location = 1) out vec3 v_normal;
layout(location = 2) out vec4 v_color;

uniform mat4 u_MVP;
uniform mat4 u_M;

void main() {
    gl_Position = (u_MVP * vec4(a_vertexPosition, 1.0));
    v_worldPosition = (u_M * vec4(a_vertexPosition, 1.0)).xyz;
    v_normal = normalize((u_M * vec4(a_vertexNormal, 0.0)).xyz);
    v_color = a_vertexColor;
}