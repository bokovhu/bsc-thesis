#version 410

layout(location = 1) in vec4 v_color;

out vec4 out_color;

void main() {
    float v = v_color.x;
    out_color = vec4(v, v, v, 1.0);
}