#version 460

precision highp float;

layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

struct OutputVertex {
    float posx, posy, posz;
    float nx, ny, nz;
    float r, g, b, a;
};

layout(std430, binding = 0) buffer out_output {
    OutputVertex[] out_vertices;
};

layout(std430, binding = 1) buffer out_vertexCountBuffer { uint out_vertexCount; };

layout(rgba32f, binding = 2) uniform image3D in_positionAndValue;
layout(rgba32f, binding = 3) uniform image3D in_normal;

layout(std430, binding = 4) buffer in_triangleTable {
    int triangleTableData [];
};

layout(std430, binding = 5) buffer in_edgeTable {
    uint edgeTableData [];
};

const int STORAGE_TYPE_LATTICE = 0;
const int STORAGE_TYPE_VOXELS = 1;
const float EPSILON = 0.0001;

uniform float u_isoLevel = 0.0;
uniform int u_storageType = 0;
uniform int u_voxelCount = 0;

vec3 interpolate(vec3 a, float av, vec3 b, float bv) {
    if(abs(u_isoLevel - av) < EPSILON) {
        return a;
    }
    if(abs(u_isoLevel - bv) < EPSILON) {
        return b;
    }
    if(abs(av - bv) < EPSILON) {
        return a;
    }
    float alpha = abs((u_isoLevel - av) / (max(av, bv) - min(av, bv)));
    return a + alpha * (b - a);
}

void executeVoxels() {

}

void executeLattice() {

vec3 vertices [12];
vec3 normals [12];
vec3 triPoints [5][3];
vec3 triNormals [5][3];
vec4 triColors [5][3];

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
    
    vec3 norm000 = imageLoad(in_normal, c000).xyz;
    vec3 norm001 = imageLoad(in_normal, c001).xyz;
    vec3 norm010 = imageLoad(in_normal, c010).xyz;
    vec3 norm011 = imageLoad(in_normal, c011).xyz;
    vec3 norm100 = imageLoad(in_normal, c100).xyz;
    vec3 norm101 = imageLoad(in_normal, c101).xyz;
    vec3 norm110 = imageLoad(in_normal, c110).xyz;
    vec3 norm111 = imageLoad(in_normal, c111).xyz;

    int cubeIndex = 0;

    if(posval010.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 1);
    }
    if(posval110.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 2);
    }
    if(posval111.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 4);
    }
    if(posval011.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 8);
    }

    if(posval000.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 16);
    }
    if(posval100.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 32);
    }
    if(posval101.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 64);
    }
    if(posval001.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 128);
    }

    if(cubeIndex > 0 && cubeIndex < 256) {

        uint edge = edgeTableData[cubeIndex];

        if(edge == 0) return;

            vertices[0] = interpolate(posval010.xyz, posval010.w, posval110.xyz, posval110.w);
            normals[0] = interpolate(norm010, posval010.w, norm110, posval110.w);


            vertices[1] = interpolate(posval110.xyz, posval110.w, posval111.xyz, posval111.w);
            normals[1] = interpolate(norm110, posval110.w, norm111, posval111.w);


            vertices[2] = interpolate(posval111.xyz, posval111.w, posval011.xyz, posval011.w);
            normals[2] = interpolate(norm111, posval111.w, norm011, posval011.w);


            vertices[3] = interpolate(posval011.xyz, posval011.w, posval010.xyz, posval010.w);
            normals[3] = interpolate(norm011, posval011.w, norm010, posval010.w);



            vertices[4] = interpolate(posval000.xyz, posval000.w, posval100.xyz, posval100.w);
            normals[4] = interpolate(norm000, posval000.w, norm100, posval100.w);

            vertices[5] = interpolate(posval100.xyz, posval100.w, posval101.xyz, posval101.w);
            normals[5] = interpolate(norm100, posval100.w, norm101, posval101.w);

            vertices[6] = interpolate(posval101.xyz, posval101.w, posval001.xyz, posval001.w);
            normals[6] = interpolate(norm101, posval101.w, norm001, posval001.w);

            vertices[7] = interpolate(posval001.xyz, posval001.w, posval000.xyz, posval000.w);
            normals[7] = interpolate(norm001, posval001.w, norm000, posval000.w);



            vertices[8] = interpolate(posval000.xyz, posval000.w, posval010.xyz, posval010.w);
            normals[8] = interpolate(norm000, posval000.w, norm010, posval010.w);

            vertices[9] = interpolate(posval100.xyz, posval100.w, posval110.xyz, posval110.w);
            normals[9] = interpolate(norm100, posval100.w, norm110, posval110.w);

            vertices[10] = interpolate(posval101.xyz, posval101.w, posval111.xyz, posval111.w);
            normals[10] = interpolate(norm101, posval101.w, norm111, posval111.w);

            vertices[11] = interpolate(posval001.xyz, posval001.w, posval011.xyz, posval011.w);
            normals[11] = interpolate(norm001, posval001.w, norm011, posval011.w);

        int triCount = 0;

        for(int i = 0; i < 5; i++) {

            int v1i = triangleTableData[16 * cubeIndex + 3 * i + 0];
            int v2i = triangleTableData[16 * cubeIndex + 3 * i + 1];
            int v3i = triangleTableData[16 * cubeIndex + 3 * i + 2];

            if(v1i != -1 && v2i != -1 && v3i != -1) {
                triCount += 1;

                triPoints[triCount - 1][0] = vertices[v1i];
                triNormals[triCount - 1][0] = normals[v1i];
                triColors[triCount - 1][0] = vec4(1.0);

                triPoints[triCount - 1][1] = vertices[v2i];
                triNormals[triCount - 1][1] = normals[v2i];
                triColors[triCount - 1][1] = vec4(1.0);

                triPoints[triCount - 1][2] = vertices[v3i];
                triNormals[triCount - 1][2] = normals[v3i];
                triColors[triCount - 1][2] = vec4(1.0);
            }

        }

    barrier();
    memoryBarrier();

        uint baseIndex = atomicAdd(out_vertexCount, uint(3 * triCount));

    barrier();
    memoryBarrier();

        for(uint i = 0; i < triCount; i++) {
            OutputVertex v1, v2, v3;

            v1.posx = triPoints[i][0].x;
            v1.posy = triPoints[i][0].y;
            v1.posz = triPoints[i][0].z;

            v1.nx = triNormals[i][0].x;
            v1.ny = triNormals[i][0].y;
            v1.nz = triNormals[i][0].z;

            v1.r = triColors[i][0].x;
            v1.g = triColors[i][0].y;
            v1.b = triColors[i][0].z;
            v1.a = triColors[i][0].w;


            v2.posx = triPoints[i][1].x;
            v2.posy = triPoints[i][1].y;
            v2.posz = triPoints[i][1].z;

            v2.nx = triNormals[i][1].x;
            v2.ny = triNormals[i][1].y;
            v2.nz = triNormals[i][1].z;

            v2.r = triColors[i][1].x;
            v2.g = triColors[i][1].y;
            v2.b = triColors[i][1].z;
            v2.a = triColors[i][1].w;


            v3.posx = triPoints[i][2].x;
            v3.posy = triPoints[i][2].y;
            v3.posz = triPoints[i][2].z;

            v3.nx = triNormals[i][2].x;
            v3.ny = triNormals[i][2].y;
            v3.nz = triNormals[i][2].z;

            v3.r = triColors[i][2].x;
            v3.g = triColors[i][2].y;
            v3.b = triColors[i][2].z;
            v3.a = triColors[i][2].w;

            out_vertices[baseIndex + 3 * i + 0] = v1;
            out_vertices[baseIndex + 3 * i + 1] = v2;
            out_vertices[baseIndex + 3 * i + 2] = v3;
        }

    }
    
}

void main() {



    if (u_storageType == STORAGE_TYPE_LATTICE) {
        executeLattice();    
    } else {
        executeVoxels();
    }

    barrier();
    memoryBarrier();

}