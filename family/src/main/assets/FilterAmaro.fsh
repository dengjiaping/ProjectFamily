// Instagram Filter Amaro
// created by yuzebin20121203
// Fine.
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
    
    // RGB transform formula group: 5 order:
    pixr = 0.1171567 + 0.32352087 * x + 8.0539349 * x * x - 20.10139187 * x * x * x + 18.1569047 * x * x * x * x - 5.55041103 * x * x * x * x * x;
    pixg = 8.31981953e-03 + 5.07932403e-01 * y + 9.17830362e+00 * y * y - 2.33444088e+01 * y * y * y + 2.13474086e+01 * y * y * y * y - 6.70040345e+00 * y * y * y * y * y;
    pixb = 0.10387709 + 1.44879968 * z + 2.58496602 * z * z - 10.90666423 * z * z * z + 11.44086488 * z * z * z * z - 3.69472061  * z * z * z * z * z;
    
    gl_FragColor = vec4(pixr, pixg, pixb, 1.0);
}
