const vec3 RM_EPS_X = vec3(0.001, 0.0, 0.0);
const vec3 RM_EPS_Y = vec3(0.0, 0.001, 0.0);
const vec3 RM_EPS_Z = vec3(0.0, 0.0, 0.001);

vec3 csgNormal(vec3 P) {
    return normalize (
        vec3 (
            csgExecute(P + RM_EPS_X) - csgExecute(P - RM_EPS_X),
            csgExecute(P + RM_EPS_Y) - csgExecute(P - RM_EPS_Y),
            csgExecute(P + RM_EPS_Z) - csgExecute(P - RM_EPS_Z)
        )
    );
}