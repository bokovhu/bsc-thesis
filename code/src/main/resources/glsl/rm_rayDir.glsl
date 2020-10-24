vec3 rayDir(in vec2 uv) {
    vec2 ndc = vec2(
        2.0 * (uv.x - 0.5) * u_aspect,
        2.0 * ((1.0 - uv.y) - 0.5)
    );
    vec3 camFw = u_forward;
    vec3 camRg = normalize(cross(u_up, camFw));
    vec3 camUp = normalize(cross(camFw, camRg));

    return normalize(
        ndc.x * camRg + ndc.y * camUp + u_fovy * camFw
    );
}