#version 150 core

uniform mat4 mvp;

uniform vec2 lightPosition;

in vec2 position;

out vec2 t_rpos;

void main() {
	t_rpos = position - lightPosition;
    gl_Position = mvp * vec4(position, 0.0, 1.0);
}
