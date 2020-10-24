#version 410

layout(location = 0) in vec3 v_worldPosition;
layout(location = 1) in vec3 v_normal;
layout(location = 2) in vec4 v_color;

uniform vec3 u_Le;
uniform vec3 u_La;
uniform vec3 u_Ls;
uniform vec3 u_Ld = vec3(1.0, 1.0, 1.0);
uniform float u_shininess = 180.0;
uniform vec3 u_Ka = vec3(1.0, 1.0, 1.0);
uniform vec3 u_Ks = vec3(1.0, 1.0, 1.0);
uniform vec3 u_eye;

vec3 blinnPhong(vec3 p, vec3 n) {

    vec3 energy = vec3(0.0);

    energy += u_La * u_Ka;

    float cosTheta = dot(n, u_Ld);
    if (cosTheta > 0.0) {
        energy += u_Le * v_color.xyz * cosTheta;

        vec3 viewDir = normalize(u_eye - p);
        vec3 halfwayDir = normalize(u_Ld + viewDir);
        float cosDelta = dot(n, halfwayDir);
        if (cosDelta > 0.0) {
            energy += u_Le * u_Ls * u_Ks * v_color.xyz * pow(cosDelta, u_shininess);
        }
    }

    return energy;

}

out vec4 out_color;

void main() {
    out_color = vec4(blinnPhong(v_worldPosition, v_normal), 1.0);
}