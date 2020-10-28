#version 410

varying vec2 v_UV;
varying vec4 v_color;

uniform sampler2D u_fontTexture;

out vec4 out_color;

void main() {
    out_color = texture2D(u_fontTexture, v_UV).x * v_color;
}