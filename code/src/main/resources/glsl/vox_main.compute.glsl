void main() {

    ivec3 pInvocationSpace = ivec3(gl_GlobalInvocationID);
    ivec3 outputSize = imageSize(u_positionAndValueOutput);

    vec3 pTextureSpace = vec3(
        float(pInvocationSpace.x) / float(outputSize.x),
        float(pInvocationSpace.y) / float(outputSize.y),
        float(pInvocationSpace.z) / float(outputSize.z)
    );

    vec3 pWorldSpace = (u_transform * vec4(pTextureSpace, 1.0)).xyz;

    float calculatedDistance = csgExecute(pWorldSpace);
    vec3 calculatedNormal = csgNormal(pWorldSpace);

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

}