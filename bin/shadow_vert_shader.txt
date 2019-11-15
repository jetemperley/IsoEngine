#version 430
layout (location = 0) in vec3 position;
uniform mat4 light_cam;
uniform vec3 grid_loc;

void main(void){
    gl_Position = light_cam*vec4(grid_loc.x + position.x, grid_loc.y + position.y, grid_loc.z + position.z, 1.0);
}