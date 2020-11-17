void main() {

    Hit hit;
    hit.c = 1;
    hit.P = v_worldPosition;
    hit.N = v_normal;
    hit.C = vec3(v_color.xyz);
    hit.S = v_color.w * 200.0;

    out_finalColor = vec4(toneMap(illuminate(hit)), 1.0);

}