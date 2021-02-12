void main() {

    Hit hit;
    hit.c = 1;
    hit.P = v_worldPosition;
    hit.N = v_normal;
    hit.C = csgColor(hit.P, hit.N);
    hit.S = csgShininess(hit.P, hit.N);

    out_finalColor = vec4(toneMap(illuminate(hit)), 1.0);

}