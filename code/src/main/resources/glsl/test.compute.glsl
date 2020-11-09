#version 460

layout(local_size_x = 1, local_size_y = 1) in;
layout(rgba32f, binding = 0) uniform image2D u_image;

void main() {
    ivec2 p = ivec2(gl_GlobalInvocationID.xy);
    vec4 pixel = vec4(
         ((p.x / 32) % 2) == 0 ? 0.0 : 1.0,
         ((p.y / 32) % 2) == 0 ? 1.0 : 0.0,
         1.0,
         1.0
     );
    imageStore(
        u_image,
        p,
        pixel
    );
}