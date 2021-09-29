precision lowp float;
uniform sampler2D texture;
uniform float alphaThreshold;
varying vec2 uv1;

void main() {
    gl_FragColor = texture2D(texture, uv1);
    gl_FragColor.a = step(alphaThreshold, gl_FragColor.a) * gl_FragColor.a;
}