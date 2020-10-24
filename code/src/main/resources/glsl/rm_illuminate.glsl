vec3 illuminate(Hit hit) {

    vec3 Kd = hit.C;
    vec3 totalEnergy = u_La * Kd;

    Ray shadowRay;
    shadowRay.O = hit.P + 0.02 * hit.N;
    shadowRay.D = 1.0 * u_Ld;

    Hit shadowHit;
    shadowHit = hitScene(shadowRay);

    if (shadowHit.c == 0) {

        vec3 viewDir = normalize(u_eye - hit.P);
        float cosTheta = dot(hit.N, u_Ld);

        if (cosTheta > 0.0) {

            totalEnergy += u_Le * Kd * cosTheta;

            vec3 halfwayDir = normalize(u_Ld + viewDir);
            float cosDelta = dot(hit.N, halfwayDir);

            if (cosDelta > 0.0) {
                totalEnergy += u_Le * Kd * pow(cosDelta, hit.S);
            }

        }

    }

    return totalEnergy;

}