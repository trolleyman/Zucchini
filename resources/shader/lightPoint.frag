#version 150 core

uniform vec4 color;
uniform float attenuationFactor;

in vec2 t_rpos;

out vec4 out_color;

/*                     a = 1 / (1 + k * d^2);

Where a = attenuation, k = attenuation factor, and d = distance. */

void main() {
	float dist = length(t_rpos);
	float attenuation = 1 / (1 + attenuationFactor * dist * dist);
    out_color = color * attenuation;
}
