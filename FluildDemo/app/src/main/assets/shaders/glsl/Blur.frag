precision mediump float;
uniform sampler2D blurTexture;
uniform float blurBufferSize;
varying vec2 uv1[5];

void main()
{

//    float weight[5];
//    weight[0] = 0.227027;
//    weight[1] = 0.1945946;
//    weight[2] = 0.1216216;
//    weight[3] = 0.054054;
//    weight[4] = 0.016216;

    vec4 sum = vec4(0.0);
    // Gaussian blur. Sigma: 2.3, kernel size: 5.
    sum += texture2D(blurTexture, uv1[0]) * 0.164074;
    sum += texture2D(blurTexture, uv1[1]) * 0.216901;
    sum += texture2D(blurTexture, uv1[2]) * 0.23805;
    sum += texture2D(blurTexture, uv1[3]) * 0.216901;
    sum += texture2D(blurTexture, uv1[4]) * 0.164074;
    gl_FragColor = sum;
}
