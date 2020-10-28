const int RM_MAX_ITERATIONS = 256;
const float RM_MAX_DISTANCE = 50.0;
const float RM_HIT_DISTANCE = 0.001;
const float RM_MIN_STEP_DISTANCE = 0.001;

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
            result.C = csgColor(result.P);
            result.S = csgShininess(result.P);
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