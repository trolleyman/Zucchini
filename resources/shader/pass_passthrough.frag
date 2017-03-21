#version 150 core

uniform sampler2D tex;

in vec2 t_uv;

out vec4 out_color;

void main() {
	out_color = texture(tex, t_uv);
}
