#version 330 core

in vec2 TexCoord;

layout(location = 0) out vec4 color;
uniform sampler2D textureSampler;

void main()
{
    vec4 tempColor = texture(textureSampler, TexCoord);
    if (tempColor.a == 0) {
        discard;
    } else {
        color = tempColor;
    }
}