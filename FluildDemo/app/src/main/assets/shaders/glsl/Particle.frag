precision lowp float;
uniform sampler2D texture;
varying vec4 outColor;

void main() {
    gl_FragColor = texture2D(texture, gl_PointCoord);
    gl_FragColor *= outColor;
}