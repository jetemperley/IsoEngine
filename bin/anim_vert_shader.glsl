#version 430
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texcoord;
layout (location = 3) in vec3 boneID;
layout (location = 4) in vec3 boneWeight;

uniform mat4 pv;
// uniform mat4 light_cam;
uniform vec3 draw_offset;
uniform vec3 sun_dir;
uniform mat4 bones[20];
uniform mat4 model_form;

out float lighting;
out vec4 shadow_coord; 
out vec2 tc;

void main(void) {
    // shadow_coord = light_cam*vec4(draw_offset.x + position.x, draw_offset.y + position.y, draw_offset.z + position.z, 1.0);

	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);

    for(int i=0;i<3;i++){
		
		mat4 jointTransform = bones[int(boneID[i])];

		vec4 posePosition = jointTransform * vec4(position, 1.0);
		totalLocalPos += posePosition * boneWeight[i];
		
		vec4 worldNormal = jointTransform * vec4(normal, 0.0);
		totalNormal += worldNormal * boneWeight[i];
				
	}

	totalNormal = model_form*totalNormal;
	
	gl_Position = pv * vec4(draw_offset.x + totalLocalPos.x, draw_offset.y + totalLocalPos.y, draw_offset.z + totalLocalPos.z, totalLocalPos.w);
	// gl_Position = pv*totalLocalPos;
	vec4 sun_dir4 = vec4(sun_dir, 0.0);
    lighting = max(dot(totalNormal, sun_dir4), 0.0);
    tc = texcoord;
}