// #define RM_SOFT_SHADOWS

float shadowScene(vec3 P, vec3 D) {

    #ifdef RM_SOFT_SHADOWS

    Ray ray;
    ray.O = P;
    ray.D = D;

    float result = 1.0;
    float t = 0.0;
    vec3 p = vec3(0.0);
    float ph = RM_SHADOW_PH;

    for(int i = 0; i < RM_MAX_ITERATIONS; i++) {

        p = ray.O + t * ray.D;

        float dScene = csgExecute(p);

        if (dScene <= RM_HIT_DISTANCE) {
            result = 0.0;
            return result;
        }

        float y = dScene * dScene / (2.0 * ph);
        float d = sqrt(dScene * dScene - y * y);

        result = min (
        result,
        RM_SHADOW_K * d / max(0.0, t - y)
        );
        ph = dScene;

        t += max(RM_MIN_STEP_DISTANCE, abs(dScene));

        if (t >= RM_MAX_DISTANCE) {
            return result;
        }

    }

    return result;

    #else

    Ray ray;
    ray.O = P;
    ray.D = D;

    float t = 0.0;
    vec3 p = vec3(0.0);

    for(int i = 0; i < RM_MAX_ITERATIONS; i++) {

        p = ray.O + t * ray.D;

        float dScene = csgExecute(p);

        if (dScene <= RM_HIT_DISTANCE) {
            return 0.0;
        }

        t += max(RM_MIN_STEP_DISTANCE, abs(dScene));

        if (t >= RM_MAX_DISTANCE) {
            return 1.0;
        }

    }

    return 1.0;

    #endif
}