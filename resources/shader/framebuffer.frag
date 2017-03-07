#version 150 core

uniform sampler2D tex;

in vec2 t_uv;

out vec4 out_color;

void main() {
    vec4 tex_col = texture(tex, t_uv);
    out_color = vec4(vec3(1.0f, 1.0f, 1.0f) - tex_col.rgb, tex_col.a);
}
