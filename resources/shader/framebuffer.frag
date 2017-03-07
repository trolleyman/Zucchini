#version 150 core

uniform sampler2D tex;

in vec2 t_uv;

out vec4 out_color;

void main() {
    vec4 tex_col = texture(tex, t_uv);
    float avg = (tex_col.r + tex_col.g + tex_col.b) / 3.0f;
    out_color = vec4(vec3(avg, avg, avg), tex_col.a);
}
