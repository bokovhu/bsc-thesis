vec3 interpolate(vec3 a, float av, vec3 b, float bv) {
    if (abs(u_isoLevel - av) < EPSILON) {
        return a;
    }
    if (abs(u_isoLevel - bv) < EPSILON) {
        return b;
    }
    if (abs(av - bv) < EPSILON) {
        return a;
    }
    float alpha = abs((u_isoLevel - av) / (max(av, bv) - min(av, bv)));
    return a + alpha * (b - a);
}

float interpolate(float a, float av, float b, float bv) {
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

    ivec3 pInvocationSpace = ivec3(gl_GlobalInvocationID);

    ivec3 c000 = pInvocationSpace + ivec3(0, 0, 0);
    ivec3 c001 = pInvocationSpace + ivec3(0, 0, 1);
    ivec3 c010 = pInvocationSpace + ivec3(0, 1, 0);
    ivec3 c011 = pInvocationSpace + ivec3(0, 1, 1);
    ivec3 c100 = pInvocationSpace + ivec3(1, 0, 0);
    ivec3 c101 = pInvocationSpace + ivec3(1, 0, 1);
    ivec3 c110 = pInvocationSpace + ivec3(1, 1, 0);
    ivec3 c111 = pInvocationSpace + ivec3(1, 1, 1);


    vec4 posval000 = texelFetch(in_positionAndValue, c000, 0);
    vec4 posval001 = texelFetch(in_positionAndValue, c001, 0);
    vec4 posval010 = texelFetch(in_positionAndValue, c010, 0);
    vec4 posval011 = texelFetch(in_positionAndValue, c011, 0);
    vec4 posval100 = texelFetch(in_positionAndValue, c100, 0);
    vec4 posval101 = texelFetch(in_positionAndValue, c101, 0);
    vec4 posval110 = texelFetch(in_positionAndValue, c110, 0);
    vec4 posval111 = texelFetch(in_positionAndValue, c111, 0);

    #ifndef RESAMPLE_NORMALS
    vec3 norm000 = texelFetch(in_normal, c000, 0).xyz;
    vec3 norm001 = texelFetch(in_normal, c001, 0).xyz;
    vec3 norm010 = texelFetch(in_normal, c010, 0).xyz;
    vec3 norm011 = texelFetch(in_normal, c011, 0).xyz;
    vec3 norm100 = texelFetch(in_normal, c100, 0).xyz;
    vec3 norm101 = texelFetch(in_normal, c101, 0).xyz;
    vec3 norm110 = texelFetch(in_normal, c110, 0).xyz;
    vec3 norm111 = texelFetch(in_normal, c111, 0).xyz;
    #endif

    int cubeIndex = 0;

    if (posval010.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 1);
    }
    if (posval110.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 2);
    }
    if (posval111.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 4);
    }
    if (posval011.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 8);
    }

    if (posval000.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 16);
    }
    if (posval100.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 32);
    }
    if (posval101.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 64);
    }
    if (posval001.w < u_isoLevel) {
        cubeIndex = (cubeIndex | 128);
    }

    if (cubeIndex > 0 && cubeIndex < 256) {

        uint edge = edgeTableData[cubeIndex];

        if (edge == 0) return;

        vertices[0] = interpolate(posval010.xyz, posval010.w, posval110.xyz, posval110.w);
        vertices[1] = interpolate(posval110.xyz, posval110.w, posval111.xyz, posval111.w);
        vertices[2] = interpolate(posval111.xyz, posval111.w, posval011.xyz, posval011.w);
        vertices[3] = interpolate(posval011.xyz, posval011.w, posval010.xyz, posval010.w);




        vertices[4] = interpolate(posval000.xyz, posval000.w, posval100.xyz, posval100.w);
        vertices[5] = interpolate(posval100.xyz, posval100.w, posval101.xyz, posval101.w);
        vertices[6] = interpolate(posval101.xyz, posval101.w, posval001.xyz, posval001.w);
        vertices[7] = interpolate(posval001.xyz, posval001.w, posval000.xyz, posval000.w);




        vertices[8] = interpolate(posval000.xyz, posval000.w, posval010.xyz, posval010.w);
        vertices[9] = interpolate(posval100.xyz, posval100.w, posval110.xyz, posval110.w);
        vertices[10] = interpolate(posval101.xyz, posval101.w, posval111.xyz, posval111.w);
        vertices[11] = interpolate(posval001.xyz, posval001.w, posval011.xyz, posval011.w);


        #ifdef RESAMPLE_NORMALS
        normals[0] = csgNormal(vertices[0]);
        normals[1] = csgNormal(vertices[1]);
        normals[2] = csgNormal(vertices[2]);
        normals[3] = csgNormal(vertices[3]);
        normals[4] = csgNormal(vertices[4]);
        normals[5] = csgNormal(vertices[5]);
        normals[6] = csgNormal(vertices[6]);
        normals[7] = csgNormal(vertices[7]);
        normals[8] = csgNormal(vertices[8]);
        normals[9] = csgNormal(vertices[9]);
        normals[10] = csgNormal(vertices[10]);
        normals[11] = csgNormal(vertices[11]);
        #else
        normals[0] = interpolate(norm010, posval010.w, norm110, posval110.w);
        normals[1] = interpolate(norm110, posval110.w, norm111, posval111.w);
        normals[2] = interpolate(norm111, posval111.w, norm011, posval011.w);
        normals[3] = interpolate(norm011, posval011.w, norm010, posval010.w);
        normals[4] = interpolate(norm000, posval000.w, norm100, posval100.w);
        normals[5] = interpolate(norm100, posval100.w, norm101, posval101.w);
        normals[6] = interpolate(norm101, posval101.w, norm001, posval001.w);
        normals[7] = interpolate(norm001, posval001.w, norm000, posval000.w);
        normals[8] = interpolate(norm000, posval000.w, norm010, posval010.w);
        normals[9] = interpolate(norm100, posval100.w, norm110, posval110.w);
        normals[10] = interpolate(norm101, posval101.w, norm111, posval111.w);
        normals[11] = interpolate(norm001, posval001.w, norm011, posval011.w);
        #endif

        int triCount = 0;

        for (int i = 0; i < 5; i++) {

            int v1i = triangleTableData[16 * cubeIndex + 3 * i + 0];
            int v2i = triangleTableData[16 * cubeIndex + 3 * i + 1];
            int v3i = triangleTableData[16 * cubeIndex + 3 * i + 2];

            if (v1i != -1 && v2i != -1 && v3i != -1) {
                triCount += 1;

                triPoints[triCount - 1][0] = vertices[v1i];
                triNormals[triCount - 1][0] = normals[v1i];

                triPoints[triCount - 1][1] = vertices[v2i];
                triNormals[triCount - 1][1] = normals[v2i];

                triPoints[triCount - 1][2] = vertices[v3i];
                triNormals[triCount - 1][2] = normals[v3i];
            }

        }

        int baseIndex = atomicAdd(out_vertexCount, int(3 * triCount));

        for (uint i = 0; i < triCount; i++) {
            OutputVertex v1, v2, v3;

            v1.posx = triPoints[i][0].x;
            v1.posy = triPoints[i][0].y;
            v1.posz = triPoints[i][0].z;

            v1.nx = triNormals[i][0].x;
            v1.ny = triNormals[i][0].y;
            v1.nz = triNormals[i][0].z;


            v2.posx = triPoints[i][1].x;
            v2.posy = triPoints[i][1].y;
            v2.posz = triPoints[i][1].z;

            v2.nx = triNormals[i][1].x;
            v2.ny = triNormals[i][1].y;
            v2.nz = triNormals[i][1].z;


            v3.posx = triPoints[i][2].x;
            v3.posy = triPoints[i][2].y;
            v3.posz = triPoints[i][2].z;

            v3.nx = triNormals[i][2].x;
            v3.ny = triNormals[i][2].y;
            v3.nz = triNormals[i][2].z;

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

}