#version 410

layout(location = 0) in vec4 a_vertexPosition;
layout(location = 1) in vec2 a_vertexUV;
layout(location = 2) in vec4 a_vertexColor;

varying vec2 v_UV;
varying vec3 v_rayDir;

uniform vec3 u_eye;
uniform vec3 u_forward;
uniform vec3 u_up;
uniform vec3 u_right;
uniform mat4 u_VPinv;

void main() {
    gl_Position = a_vertexPosition;
    v_UV = a_vertexUV;

    vec2 ndc = a_vertexPosition.xy;

    vec3 camFw = u_forward;
    vec3 camRg = normalize(cross(u_up, camFw));
    vec3 camUp = normalize(cross(camFw, camRg));

    v_rayDir = normalize(
        ndc.x * camRg + ndc.y * camUp + 2.0 * camFw
    );
}