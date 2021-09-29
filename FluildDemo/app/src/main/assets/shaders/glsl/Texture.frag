//#extension GL_OES_EGL_image_external : require

precision lowp float;
uniform sampler2D texture;
uniform float alphaFactor;
varying vec2 uv1;

void main() {
    gl_FragColor = texture2D(texture, uv1);
    gl_FragColor.a *= alphaFactor;
}