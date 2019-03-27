#version 330 core

in vec2 TexCoord;

layout(location = 0) out vec4 color;
uniform sampler2D texture1;

void main()
{
    vec4 tempColor = texture(texture1, TexCoord);
    if (tempColor.a == 0) {
        discard;
    } else {
        color = texture(texture1, TexCoord);
    }
}