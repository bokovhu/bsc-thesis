Hit hitScene(Ray ray) {

    Hit result;
    result.c = 0;
    result.T = 0.0;

    for(int i = 0; i < RM_MAX_ITERATIONS; i++) {

        result.P = ray.O + result.T * ray.D;

        float dScene = csgExecute(result.P);

        if (dScene <= RM_HIT_DISTANCE) {
            result.c = 1;
            result.N = csgNormal(result.P);
            result.C = csgColor(result.P, result.N);
            result.S = csgShininess(result.P, result.N);
            break;
        }

        result.T += max(RM_MIN_STEP_DISTANCE, abs(dScene));

        if (result.T >= RM_MAX_DISTANCE) {
            result.c = 0;
            break;
        }

    }

    return result;

}
