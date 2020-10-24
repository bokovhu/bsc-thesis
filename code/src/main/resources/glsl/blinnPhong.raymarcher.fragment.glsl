#version 410

#define RAYMARCHER_MAX_ITERATIONS 196
#define RAYMARCHER_HIT_DISTANCE 0.01
#define RAYMARCHER_NORMAL_EPSILON 0.001
#define RAYMARCHER_MAX_DISTANCE 50.0

varying vec2 v_UV;
varying vec3 v_rayDir;

struct Ray {
	vec3 origin;
	vec3 direction;
};

struct Hit {
    int collision;
	vec3 point;
	vec3 normal;
	vec3 color;
	float shininess;
};

uniform vec3 u_eye;
uniform vec3 u_forward;
uniform vec3 u_right;
uniform vec3 u_up;
uniform float u_aspect;
uniform float u_fovy;

uniform vec3 u_Le;
uniform vec3 u_Ls = vec3(1.0, 1.0, 1.0);
uniform vec3 u_La;
uniform vec3 u_Ld;
uniform vec3 u_Kd = vec3(1.0, 1.0, 1.0);
uniform vec3 u_Ks = vec3(1.0, 1.0, 1.0);
uniform vec3 u_Ka = vec3(1.0, 1.0, 1.0);
uniform float u_shininess = 100.0;

float sceneDistance(vec3 p) {
	{<MARCHER_DISTANCE_EXPRESSION>}
}

vec3 sceneNormal(vec3 p) {
	const float EPSILON = RAYMARCHER_NORMAL_EPSILON;
	return normalize(
		vec3(
			sceneDistance(vec3(p.x + EPSILON, p.y, p.z)) - sceneDistance(vec3(p.x - EPSILON, p.y, p.z)),
			sceneDistance(vec3(p.x, p.y + EPSILON, p.z)) - sceneDistance(vec3(p.x, p.y - EPSILON, p.z)),
			sceneDistance(vec3(p.x, p.y, p.z  + EPSILON)) - sceneDistance(vec3(p.x, p.y, p.z - EPSILON))
		)
	);
}

vec3 sceneColor(vec3 p) {
	return u_Kd;
}

float sceneShininess(vec3 p) {
	return u_shininess;
}

Hit hitScene(Ray ray) {

	Hit result;
	result.collision = 0;
	float totalDistance = 0.0;

    const int iterations = RAYMARCHER_MAX_ITERATIONS;

	for(int i = 0; i < iterations; i++) {
		vec3 p = ray.origin + totalDistance * ray.direction;
		float dScene = sceneDistance(p);

		if (dScene <= RAYMARCHER_HIT_DISTANCE) {
			result.point = p;
			result.normal = sceneNormal(p);
			result.color = u_Kd;
			result.shininess = u_shininess;
			result.collision = 1;
		}

		if (totalDistance >= RAYMARCHER_MAX_DISTANCE) break;

		if(result.collision == 1) break;

		totalDistance += max(0.01, dScene);
	}

	return result;

}

vec3 illuminate(Hit hit) {

    vec3 Kd = sceneColor(hit.point);
    vec3 totalEnergy = u_La * Kd * u_Ka;

    Ray shadowRay;
    shadowRay.origin = hit.point + 0.02 * hit.normal;
    shadowRay.direction = 1.0 * u_Ld;

    Hit shadowHit;
    shadowHit = hitScene(shadowRay);

    if (shadowHit.collision == 0) {

        vec3 viewDir = normalize(u_eye - hit.point);
        float cosTheta = dot(hit.normal, u_Ld);

        if (cosTheta > 0.0) {

            totalEnergy += u_Le * Kd * cosTheta;

            vec3 halfwayDir = normalize(u_Ld + viewDir);
            float cosDelta = dot(hit.normal, halfwayDir);

            if (cosDelta > 0.0) {
                totalEnergy += u_Ks * u_Le * Kd * pow(cosDelta, sceneShininess(hit.point));
            }

        }

    }

    return totalEnergy;

}

void main( void ) {

	vec2 uv = v_UV;
	vec3 color = vec3(0.0);

	Ray ray;

	ray.origin = u_eye;
	vec2 ndc = vec2(
	    2.0 * (uv.x - 0.5) * u_aspect,
	    2.0 * ((1.0 - uv.y) - 0.5)
	);
    vec3 camFw = u_forward;
    vec3 camRg = normalize(cross(u_up, camFw));
    vec3 camUp = normalize(cross(camFw, camRg));

    ray.direction = normalize(
        ndc.x * camRg + ndc.y * camUp + u_fovy * camFw
    );

	Hit primaryHit = hitScene(ray);

	if (primaryHit.collision == 1) {
		color = illuminate(primaryHit);
	}

	gl_FragColor = vec4( color, 1.0 );

}