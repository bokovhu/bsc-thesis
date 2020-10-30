void main() {

	vec2 uv = v_UV;
	vec3 color = vec3(0.0);

	Ray ray;

	ray.O = u_eye;
	ray.D = rayDir(uv);

	Hit primaryHit = hitScene(ray);

    if (primaryHit.c == 1) {
        color = illuminate(primaryHit);
        out_finalColor = vec4 (toneMap(color), 1.0);
    } else {
        discard;
    }

}