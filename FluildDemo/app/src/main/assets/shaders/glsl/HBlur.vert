attribute vec4 position;
attribute vec2 uv;
uniform float blurBufferSize;
varying vec2 uv1[5];

void main() {
    gl_Position = position;
    uv1[0] = uv - vec2(2.0 * blurBufferSize, 0.0);
    uv1[1] = uv - vec2(1.0 * blurBufferSize, 0.0);
    uv1[2] = uv;
    uv1[3] = uv + vec2(1.0 * blurBufferSize, 0.0);
    uv1[4] = uv + vec2(2.0 * blurBufferSize, 0.0);
}