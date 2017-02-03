#version 150

uniform sampler2D tex;
uniform vec4 color;

in vec2 t_uv;

out vec4 out_color;

void main() {
    out_color = color * texture(tex, t_uv);
}
