#version 410

uniform sampler2D u_texture;

layout(location = 0) in vec2 v_UV;

void main() {
    gl_FragColor = texture(u_texture, v_UV);
}