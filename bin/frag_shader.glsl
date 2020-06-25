#version 430
out vec4 color;
uniform vec4 altcolor;
uniform vec3 ambient_light;
layout (binding = 0) uniform samplerCube cubeMap;
layout (binding = 1) uniform sampler2D tex;

in vec4 shadow_coord;
in float lighting;
in vec2 tc;

void main(void){
    float tempLight = 0.4;
    vec4 coord = vec4(
        (shadow_coord.x + 20)/40, 
        1-((shadow_coord.y + 20)/40), 
        shadow_coord.z, 
        shadow_coord.w
    );
    // float shadow = textureProj(shadow_tex, coord);
    
    tempLight += lighting;
    tempLight = min(tempLight, 1.0);
    tempLight *= 0.9;
    
    vec4 texcol = texture(tex, tc);

    color = vec4((altcolor.x+texcol.x)*tempLight, (altcolor.y+texcol.y)*tempLight, (altcolor.z+texcol.z)*tempLight, altcolor.w);
       
}