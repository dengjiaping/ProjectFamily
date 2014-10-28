// Instagram Filter Sierra
// created by yuzebin20121206
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
    
    // R 0.02810048   2.92013717  -7.96843128  14.87784915 -13.54316343    4.65626381
    pixr = 0.02810048 + 2.92013717 * x - 7.96843128 * x * x + 14.87784915 * x * x * x - 13.54316343 * x * x * x * x + 4.65626381 * x * x * x * x * x;
    
    // G 0.03369578   2.53917202  -6.57928432  12.17990854 -11.02246567    3.75552369
    pixg = 0.03369578 + 2.53917202 * y - 6.57928432 * y * y + 12.17990854 * y * y * y - 11.02246567 * y * y * y * y + 3.75552369 * y * y * y * y * y;
    
    // B 0.09917633  2.12457002 -5.25754099  9.68526174 -8.87890505  3.09026674
    pixb = 0.09917633 + 2.12457002 * z - 5.25754099 * z * z + 9.68526174 * z * z * z - 8.87890505 * z * z * z * z + 3.09026674  * z * z * z * z * z;
    
    gl_FragColor = vec4(pixr, pixg, pixb, 1.0);
}
