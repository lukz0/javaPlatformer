#version 330 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 tc1;
layout(location = 2) in vec2 tc2;
uniform vec3 translation;
uniform bool firstFrame;

out vec2 TexCoord;

void main()
{
    if (firstFrame) {
        TexCoord = tc1;
    } else {
        TexCoord = tc2;
    }
    gl_Position = vec4(pos+translation, 1);
}