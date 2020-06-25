#version 430
layout (location = 0) in vec3 position;
layout (location = 2) in vec2 texCoord;
uniform mat4 pv;

out vec2 tc;
void main(void) {
    tc = texCoord;
    gl_Position = pv*vec4(position, 1.0);
}