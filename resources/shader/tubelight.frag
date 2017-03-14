#version 150 core

uniform vec4 color;
uniform float attenuationFactor;

in float t_posx;

out vec4 out_color;

/*                     a = 1 / (1 + k * d^2);

Where a = attenuation, k = attenuation factor, and d = distance. */

void main() {
	float dist = abs(0.5f - t_posx);
	float attenuation = 1 / (1 + attenuationFactor * dist * dist);
	float a = attenuation * color.a;
	out_color = vec4(color.rgb * a, a);
}
