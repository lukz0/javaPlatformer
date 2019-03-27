#version 330 core

layout(location = 0) in vec4 position;
out vec2 fragmentColor;
uniform mat2 rotation;

void main()
{
	fragmentColor = vec2(position[0], position[1]);
	gl_Position = vec4(rotation * vec2(position[0], position[1]), position[2], position[3]);
}