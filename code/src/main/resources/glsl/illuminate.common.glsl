vec3 illuminate(Hit hit) {

    return calculateLighting(
        hit.P, hit.N,
        hit.C, hit.S
    );

}