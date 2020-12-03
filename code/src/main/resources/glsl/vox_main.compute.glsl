void main() {

    ivec3 pInvocationSpace = ivec3(gl_GlobalInvocationID) + u_voxelOffset;

    vec3 pTextureSpace = vec3(
    float(pInvocationSpace.x) * u_voxelSize.x,
    float(pInvocationSpace.y) * u_voxelSize.y,
    float(pInvocationSpace.z) * u_voxelSize.z
    );

    vec3 pWorldSpace = (u_transform * vec4(pTextureSpace, 1.0)).xyz;

    float calculatedDistance = csgExecute(pWorldSpace);
    vec3 calculatedNormal = csgNormal(pWorldSpace);
    vec3 calculatedDiffuse = csgColor(pWorldSpace);
    float calculatedShininess = csgShininess(pWorldSpace);

    imageStore(
    u_positionAndValueOutput,
    pInvocationSpace,
    vec4(pWorldSpace, calculatedDistance)
    );

    imageStore(
    u_normalOutput,
    pInvocationSpace,
    vec4(calculatedNormal, 1.0)
    );

    imageStore(
    u_colorShininessOutput,
    pInvocationSpace,
    vec4(calculatedDiffuse, calculatedShininess / 200.0)
    );

    memoryBarrierImage();

}