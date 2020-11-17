float shadowScene(vec3 P, vec3 D) {

    Ray ray;
    ray.O = P;
    ray.D = D;

    float result = 1.0;
    float t = 0.0;
    vec3 p = vec3(0.0);

    for(int i = 0; i < RM_MAX_ITERATIONS; i++) {

        p = ray.O + t * ray.D;

        float dScene = csgExecute(p);

        if (dScene <= RM_HIT_DISTANCE) {
            result = 0.0;
            return result;
        }

        t += max(RM_MIN_STEP_DISTANCE, abs(dScene));
        result = min (
        result,
        RM_SHADOW_K * dScene / t
        );

        if (t >= RM_MAX_DISTANCE) {
            return result;
        }

    }

    return result;

}