// Instagram Filter Nashville
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
    
    // RGB transform formula group 3 : 5 order:
    // R 0.02462566  -1.05948926   9.74437326 -13.89232563   7.34619141     -1.1639039
    pixr = 0.02462566 - 1.05948926 * x + 9.74437326 * x * x - 13.89232563 * x * x * x + 7.34619141 * x * x * x * x - 1.1639039 * x * x * x * x * x;
    // G -1.04402644e-02   4.00736264e-01   6.67135609e+00  -1.76814429e+01   1.87013211e+01  -7.18856880e+00
    pixg = -1.04402644e-02 + 4.00736264e-01 * y + 6.67135609e+00 * y * y - 1.76814429e+01 * y * y * y + 1.87013211e+01 * y * y * y * y - 7.18856880e+00 * y * y * y * y * y;
    // B 0.25803628  0.19598923  3.06604804 -7.65636831  8.16960121 -3.23494517
    pixb = 0.25803628 + 0.19598923 * z + 3.06604804 * z * z - 7.65636831 * z * z * z + 8.16960121 * z * z * z * z - 3.23494517  * z * z * z * z * z;
        
     //pixr = 1.0;
     //pixg = 0.0;
     //pixb = 0.0;
    
    gl_FragColor = vec4(pixr, pixg, pixb, 1.0);
    
}