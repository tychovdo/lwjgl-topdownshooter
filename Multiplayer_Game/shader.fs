#version 110

uniform sampler2D texture1;
uniform sampler2D texture2; //Look at that, two samplers.

varying vec4 vertColor;

void main() {
	vec4 color1 = texture2D(texture1, gl_TexCoord[0].st);
	vec4 color2 = texture2D(texture2, gl_TexCoord[0].st);
    gl_FragColor = (color1 + color2)/2; //Vector addition is per component.
}