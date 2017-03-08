#version 150 core

uniform sampler2D world;
uniform sampler2D light;

in vec2 t_uv;

out vec4 out_color;

void main() {
    vec4 tex_col = texture(world, t_uv) * texture(light, t_uv);
    out_color = tex_col;
}
