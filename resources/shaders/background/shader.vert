#version 330 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 tc;
uniform vec3 translation;

out vec2 TexCoord;

void main()
{
    //TexCoord = vec2(tc);//vec2(tc.x+pos.x, tc.y+pos.y);
    TexCoord = vec2(tc+translation.xy);
    gl_Position = vec4(pos, 1);
}