#version 430
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texcoord;

// layout (binding = 0) uniform samplerCube cubeMap;

uniform mat4 pv;
// uniform mat4 light_cam;
uniform vec3 draw_offset;
uniform vec3 sun_dir;
uniform mat4 model_form;

out float lighting;
out vec4 shadow_coord; 
out vec2 tc;

void main(void) {
    // shadow_coord = light_cam*vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);
	// mat4 off = mat4(1.0);
    // off[3] = vec4(draw_offset, 1.0);
	gl_Position = pv * vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);
    // gl_Position = pv *off* vec4(position, 1.0);

	vec4 sun_dir4 = vec4(sun_dir, 0.0);
    lighting = max(dot(normal, sun_dir), 0.0);
    // gl_Position = pv*vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);
    tc = texcoord;
}