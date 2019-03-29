#version 330 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 tc;
uniform vec3 translation;

out vec2 TexCoord;

void main()
{
    TexCoord = tc;
    gl_Position = vec4(pos+translation, 1);
}