#version 150 core

uniform mat4 trans;

in vec2 position;
in vec2 uv;

out vec2 t_uv;

void main() {
    t_uv = uv;
    gl_Position = trans * vec4(position, 0.0, 1.0);
}
