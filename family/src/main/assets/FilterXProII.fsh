// Instagram Filter XProII
// created by yuzebin20121205
// Fine.

#ifdef GL_ES
precision highp float;
#endif

varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

void main()
{
    highp vec3 pixclr = texture2D(inputImageTexture, textureCoordinate).rgb;
    
    highp float x = pixclr.r;
    highp float y = pixclr.g;
    highp float z = pixclr.b;
    
    highp float pixr = pixclr.r;
    highp float pixg = pixclr.g;
    highp float pixb = pixclr.b;
    
    // RGB transform formula group : 5 order:
    pixr = 0.00438335 + 0.3040995 * x + 2.27853293 * x * x - 1.6980744 * x * x * x - 0.10690687 * x * x * x * x + 0.21483737 * x * x * x * x * x;
    pixg = 1.09276946e-03 + 3.01554570e-01 * y + 2.11266090e+00 * y * y - 1.03834816e+00 * y * y * y - 1.01695678e+00 * y * y * y * y + 6.38214025e-01 * y * y * y * y * y;
    pixb = 0.12010359 + 0.48834371 * z + 2.05328448 * z * z - 4.91583816 * z * z * z + 4.71305252 * z * z * z * z - 1.56386168  * z * z * z * z * z;
    
    gl_FragColor = vec4(pixr, pixg, pixb, 1.0);

}