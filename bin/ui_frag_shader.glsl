
#version 430

layout (binding = 0) uniform sampler2D tex;
uniform vec4 alt_color;
out vec4 color;
in vec2 tc;

void main(void){
    

    vec4 tcolor = texture(tex, tc);
    color = tcolor + alt_color;
    
    
}