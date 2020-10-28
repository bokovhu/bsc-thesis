/* Credit: https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83 */

float CSG_Noise_mod289(float x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 CSG_Noise_mod289(vec4 x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 CSG_Noise_perm(vec4 x){return CSG_Noise_mod289(((x * 34.0) + 1.0) * x);}

float CSG_Noise_Simple3(vec3 p){
    vec3 a = floor(p);
    vec3 d = p - a;
    d = d * d * (3.0 - 2.0 * d);

    vec4 b = a.xxyy + vec4(0.0, 1.0, 0.0, 1.0);
    vec4 k1 = CSG_Noise_perm(b.xyxy);
    vec4 k2 = CSG_Noise_perm(k1.xyxy + b.zzww);

    vec4 c = k2 + a.zzzz;
    vec4 k3 = CSG_Noise_perm(c);
    vec4 k4 = CSG_Noise_perm(c + 1.0);

    vec4 o1 = fract(k3 * (1.0 / 41.0));
    vec4 o2 = fract(k4 * (1.0 / 41.0));

    vec4 o3 = o2 * d.z + o1 * (1.0 - d.z);
    vec2 o4 = o3.yw * d.x + o3.xz * (1.0 - d.x);

    return o4.y * d.y + o4.x * (1.0 - d.y);
}