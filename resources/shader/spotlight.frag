#version 150 core

uniform vec4 color;
uniform float attenuationFactor;
uniform float coneAngleMin;
uniform float coneAngleMax;
uniform vec2 coneDirection;

in vec2 t_fromLight;

out vec4 out_color;

/*                     a = 1 / (1 + k * d^2);

Where a = attenuation, k = attenuation factor, and d = distance. */

void main() {
	// Calc if in spotlight
	vec2 fromLightNormal = normalize(t_fromLight);
	float angle = acos(dot(fromLightNormal, coneDirection));
	if (angle > coneAngleMax) {
		out_color = vec4(0.0f);
	} else {
		// Calc attenuation
		float dist = length(t_fromLight);
		float attenuation = 1 / (1 + attenuationFactor * dist * dist);
		
		// Calc edges
		float p = 1.0f - clamp((angle - coneAngleMin) / (coneAngleMax - coneAngleMin), 0.0f, 1.0f);
		
		// Calc color
		out_color = vec4(color.rgb, color.a * p * attenuation);
	}
}
