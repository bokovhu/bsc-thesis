#version 460

layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

struct OutputVertex {
    vec3 vPosition;
    vec3 vNormal;
    vec4 vColor;
}

layout(std430) buffer {
    OutputVertex[] out_vertices;
}

layout(binding = 0) uniform atomic_uint out_vertexCount;

layout(rgba32f, binding = 0) uniform image3D in_positionAndValue;
layout(rgba32f, binding = 1) uniform image3D in_normal;
layout(r32ui, binding = 2) uniform image3D in_triangleTable;
layout(r32ui, binding = 3) uniform image3D in_edgeTable;

uniform float u_isoLevel = 0.0;

void main() {

    ivec3 pInvocationSpace = ivec3(gl_GlobalInvocationID);

    ivec3 c000 = pInvocationSpace + ivec3(0, 0, 0);
    ivec3 c001 = pInvocationSpace + ivec3(0, 0, 1);
    ivec3 c010 = pInvocationSpace + ivec3(0, 1, 0);
    ivec3 c011 = pInvocationSpace + ivec3(0, 1, 1);
    ivec3 c100 = pInvocationSpace + ivec3(1, 0, 0);
    ivec3 c101 = pInvocationSpace + ivec3(1, 0, 1);
    ivec3 c110 = pInvocationSpace + ivec3(1, 1, 0);
    ivec3 c111 = pInvocationSpace + ivec3(1, 1, 1);

    vec4 posval000 = imageLoad(in_positionAndValue, c000);
    vec4 posval001 = imageLoad(in_positionAndValue, c001);
    vec4 posval010 = imageLoad(in_positionAndValue, c010);
    vec4 posval011 = imageLoad(in_positionAndValue, c011);
    vec4 posval100 = imageLoad(in_positionAndValue, c100);
    vec4 posval101 = imageLoad(in_positionAndValue, c101);
    vec4 posval110 = imageLoad(in_positionAndValue, c110);
    vec4 posval111 = imageLoad(in_positionAndValue, c111);

    int cubeIndex = 0;

    if(posval000.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 1);
    }
    if(posval001.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 2);
    }
    if(posval010.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 4);
    }
    if(posval011.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 8);
    }

    if(posval100.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 16);
    }
    if(posval101.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 32);
    }
    if(posval110.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 64);
    }
    if(posval111.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 128);
    }

    if(cubeIndex > 0 && cubeIndex < 256) {

        // TODO:    1) Calculate number of triangles
        //          2) Increment out_vertexCount by that amount
        //          3) Use the previous value of the atomic counter to index the outgoing vertices buffer
        //          4) Generate the N triangle faces via interpolating the sampled normals and corners

    }

}