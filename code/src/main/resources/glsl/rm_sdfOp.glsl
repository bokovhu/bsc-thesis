float CSG_DistanceToCone(vec3 P, float alpha, float H) {
    vec2 c = vec2(sin(alpha), cos(alpha));
    vec2 q = H * vec2(c.x / c.y, -1.0);
    vec2 w = vec2(length(P.xz), P.y);
    vec2 a = w - q * clamp(dot(w, q) / dot(q, q), 0.0, 1.0);
    vec2 b = w - q * vec2(clamp(w.x / q.x, 0.0, 1.0), 1.0);
    float k = sign(q.y);
    float d = min(dot(a, a), dot(b, b));
    float s = max(k * (w.x * q.y - w.y * q.x), k * (w.y - q.y));
    return sqrt(d) * sign(s);
}

float CSG_DistanceToCappedCone(vec3 P, vec3 a, vec3 b, float ra, float rb) {
    float rba  = rb - ra;
    float baba = dot(b - a, b - a);
    float papa = dot(P - a, P - a);
    float paba = dot(P - a, b - a) / baba;
    float x = sqrt(papa - paba * paba * baba);
    float cax = max(0.0, x - ((paba < 0.5) ? ra : rb));
    float cay = abs(paba - 0.5) - 0.5;
    float k = rba * rba + baba;
    float f = clamp((rba * (x - ra) + paba * baba) / k, 0.0, 1.0);
    float cbx = x - ra - f * rba;
    float cby = paba - f;
    float s = (cbx < 0.0 && cay < 0.0) ? -1.0 : 1.0;
    return s * sqrt(
    min(
    cax * cax + cay * cay *baba,
    cbx * cbx + cby * cby * baba
    )
    );
}