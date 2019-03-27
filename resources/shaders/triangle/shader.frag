#version 330 core

layout(location = 0) out vec4 color;
in vec2 fragmentColor;

void main()
{
	float red = (fragmentColor[0]+1)/2;
	float red10 = red*10;
	float green = (fragmentColor[1]+1)/2;
	float green10 = green*10;
	color = vec4(red10 - floor(red10), green10 - floor(green10), 0.0, 1.0);
}