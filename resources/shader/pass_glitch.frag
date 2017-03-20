#version 150 core

uniform sampler2D frame;
uniform sampler2D effect;

uniform vec2 rdir;
uniform vec2 gdir;
uniform vec2 bdir;

in vec2 t_uv;

out vec4 out_color;

void main() {
	vec2 rdiff = rdir * texture(effect, t_uv).r;
	vec2 gdiff = gdir * texture(effect, t_uv).g;
	vec2 bdiff = bdir * texture(effect, t_uv).b;
	
	float r = texture(frame, t_uv+rdiff).r;
	float g = texture(frame, t_uv+gdiff).g;
	float b = texture(frame, t_uv+bdiff).b;
	
	out_color = vec4(r, g, b, texture(frame, t_uv).a);
}
