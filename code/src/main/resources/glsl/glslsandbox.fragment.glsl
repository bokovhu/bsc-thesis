#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;



float csgExecute (vec3 CSG_InputPoint) {
vec3 CSG_Root_A_A_0PTranslated = CSG_InputPoint - vec3(0.0000, 0.0000, 0.0000);
vec3 CSG_Root_A_A_0TTranslated_0PScaled = CSG_Root_A_A_0PTranslated / 1.0000;
vec4 CSG_Root_A_A_0TTranslated_0TScaled_Q = vec4(0.7071, 0.0000, 0.0000, 0.7071);
vec3 CSG_Root_A_A_0TTranslated_0TScaled_0PRotated = CSG_Root_A_A_0TTranslated_0PScaled + 2.0000 * cross(CSG_Root_A_A_0TTranslated_0TScaled_Q.xyz, cross(CSG_Root_A_A_0TTranslated_0TScaled_Q.xyz, CSG_Root_A_A_0TTranslated_0PScaled) + CSG_Root_A_A_0TTranslated_0TScaled_Q.w * CSG_Root_A_A_0TTranslated_0PScaled);
vec2 CSG_Root_A_A_0TTranslated_0TScaled_0TRotated_Q = vec2(length(CSG_Root_A_A_0TTranslated_0TScaled_0PRotated.xz) - 1.0000, CSG_Root_A_A_0TTranslated_0TScaled_0PRotated.y);
float CSG_Root_A_A_0TTranslated_0TScaled_0TRotated_Result = length(CSG_Root_A_A_0TTranslated_0TScaled_0TRotated_Q) - 0.2000;
float CSG_Root_A_A_0TTranslated_0TScaled_Result = CSG_Root_A_A_0TTranslated_0TScaled_0TRotated_Result;
float CSG_Root_A_A_0TTranslated_Result = CSG_Root_A_A_0TTranslated_0TScaled_Result * 1.0000;
float CSG_Root_A_A_Result = CSG_Root_A_A_0TTranslated_Result;
vec3 CSG_Root_A_B_0PTranslated = CSG_InputPoint - vec3(0.0000, 0.0000, 0.0000);
vec3 CSG_Root_A_B_0TTranslated_0PScaled = CSG_Root_A_B_0PTranslated / 1.0000;
vec4 CSG_Root_A_B_0TTranslated_0TScaled_Q = vec4(0.0000, 0.0000, 0.7071, 0.7071);
vec3 CSG_Root_A_B_0TTranslated_0TScaled_0PRotated = CSG_Root_A_B_0TTranslated_0PScaled + 2.0000 * cross(CSG_Root_A_B_0TTranslated_0TScaled_Q.xyz, cross(CSG_Root_A_B_0TTranslated_0TScaled_Q.xyz, CSG_Root_A_B_0TTranslated_0PScaled) + CSG_Root_A_B_0TTranslated_0TScaled_Q.w * CSG_Root_A_B_0TTranslated_0PScaled);
vec2 CSG_Root_A_B_0TTranslated_0TScaled_0TRotated_Q = vec2(length(CSG_Root_A_B_0TTranslated_0TScaled_0PRotated.xz) - 1.0000, CSG_Root_A_B_0TTranslated_0TScaled_0PRotated.y);
float CSG_Root_A_B_0TTranslated_0TScaled_0TRotated_Result = length(CSG_Root_A_B_0TTranslated_0TScaled_0TRotated_Q) - 0.2000;
float CSG_Root_A_B_0TTranslated_0TScaled_Result = CSG_Root_A_B_0TTranslated_0TScaled_0TRotated_Result;
float CSG_Root_A_B_0TTranslated_Result = CSG_Root_A_B_0TTranslated_0TScaled_Result * 1.0000;
float CSG_Root_A_B_Result = CSG_Root_A_B_0TTranslated_Result;
float CSG_Root_A_H = clamp(0.5000 + 0.5000 * ( CSG_Root_A_B_Result - CSG_Root_A_A_Result ) / 0.1000, 0.0000, 1.0000);
float CSG_Root_A_Result = mix(CSG_Root_A_B_Result, CSG_Root_A_A_Result, CSG_Root_A_H) - 0.1000 * CSG_Root_A_H * ( 1.0000 - CSG_Root_A_H );
vec2 CSG_Root_B_Q = vec2(length(CSG_InputPoint.xz) - 1.0000, CSG_InputPoint.y);
float CSG_Root_B_Result = length(CSG_Root_B_Q) - 0.2000;
float CSG_Root_H = clamp(0.5000 + 0.5000 * ( CSG_Root_B_Result - CSG_Root_A_Result ) / 0.1000, 0.0000, 1.0000);
float CSG_Root_Result = mix(CSG_Root_B_Result, CSG_Root_A_Result, CSG_Root_H) - 0.1000 * CSG_Root_H * ( 1.0000 - CSG_Root_H );
return CSG_Root_Result;
}



struct Hit {
    int c;
    vec3 P;
    vec3 N;
    vec3 C;
    float S;
    float T;
};

struct Ray {
    vec3 O;
    vec3 D;
};

mat3 calcLookAtMatrix(vec3 origin, vec3 target, float roll) {
    vec3 rr = vec3(sin(roll), cos(roll), 0.0);
    vec3 ww = normalize(target - origin);
    vec3 uu = normalize(cross(ww, rr));
    vec3 vv = normalize(cross(uu, ww));
    return mat3(uu, vv, ww);
}

vec3 cameraPosition() {
    return vec3(cos(time * 0.3) * 5.0, 4.0, sin(time * 0.3) * 5.0);
}

vec3 cameraTarget() {
    return vec3(0.0);
}

vec3 La() { return vec3(0.1); }
vec3 Le() { return vec3(1.8); }
vec3 Ld() { return normalize(vec3(-1.5, 2.5, 1.0)); }

vec3 missed(Ray ray) {
    return vec3(0.0);
}

const int RM_MAX_ITERATIONS = 256;
const float RM_MAX_DISTANCE = 50.0;
const float RM_HIT_DISTANCE = 0.001;
const float RM_MIN_STEP_DISTANCE = 0.001;

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

vec3 csgColor(vec3 P) {
    return vec3(
	    (cos(P.x * 9.0) * 0.5) + 0.5,
	    (sin(P.z * 4.0) * 0.5) + 0.5,
	    (cos(P.y * 13.0) * 0.5) + 0.5
    );
}

float csgShininess(vec3 P) {
    return 16.0 + clamp(cos(P.y * 8.0) * 4.0, 0.0, 1.0) * 64.0 + 192.0 * clamp(tan(P.z * P.x * 19.0), 0.0, 1.0) + 104.0 * clamp(tan(P.y * P.x * 34.0), 0.0, 1.0);
}

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


vec3 illuminate(Hit hit) {

	vec3 la = La();
	vec3 le = Le();
	vec3 ld = Ld();
	vec3 cam = cameraPosition();


    vec3 Kd = hit.C;
    vec3 totalEnergy = la * Kd;

    Ray shadowRay;
    shadowRay.O = hit.P + 0.02 * hit.N;
    shadowRay.D = 1.0 * ld;

    Hit shadowHit;
    shadowHit = hitScene(shadowRay);

    if (shadowHit.c == 0) {

        vec3 viewDir = normalize(cam - hit.P);
        float cosTheta = dot(hit.N, ld);

        if (cosTheta > 0.0) {

            totalEnergy += le * Kd * cosTheta;

            vec3 halfwayDir = normalize(ld + viewDir);
            float cosDelta = dot(hit.N, halfwayDir);

            if (cosDelta > 0.0) {
                totalEnergy += le * Kd * pow(cosDelta, hit.S);
            }

        }

    }

    return totalEnergy;

}

vec3 toneMap(in vec3 color) {
    return pow(color, vec3(1.0 / 2.2));
}

void main( void ) {

	vec2 uv = ( 2.0 * gl_FragCoord.xy - resolution.xy ) / resolution.y;

	mat3 V = calcLookAtMatrix(cameraPosition(), cameraTarget(), 0.0);
	vec3 ro = cameraPosition();
	vec3 rd = normalize(V * vec3(uv, 2.0));

	vec3 color = vec3(0.0);

	Ray ray;

	ray.O = ro;
	ray.D = rd;

	Hit primaryHit = hitScene(ray);

    if (primaryHit.c == 1) {
        color = illuminate(primaryHit);
        gl_FragColor = vec4 (toneMap(color), 1.0);
    } else {
        discard;
    }

}