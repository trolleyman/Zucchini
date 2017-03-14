#version 150 core

uniform mat4 mvp;

in vec2 position;

out float t_posx;

void main() {
	t_posx = position.x;
	gl_Position = mvp * vec4(position, 0.0, 1.0);
}
