#version 150 core

uniform mat4 mvp;

uniform vec2 lightPosition;

in vec2 position;

out vec2 t_fromLight;

void main() {
	t_fromLight = position - lightPosition;
	gl_Position = mvp * vec4(position, 0.0, 1.0);
}
