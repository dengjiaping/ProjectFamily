// Instagram Filter Walden with Radial Gradient
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
    
    // R 0.03705017  -0.13030307   9.19354892 -19.37983083  16.55401408    -5.272487
    pixr = 0.03705017 - 0.13030307 * x + 9.19354892 * x * x - 19.37983083 * x * x * x + 16.55401408 * x * x * x * x - 5.272487 * x * x * x * x * x;
    
    // G 0.13442607  0.79543791  3.23366237 -8.30039772  7.75029452 -2.6351796
    pixg = 0.13442607 + 0.79543791 * y + 3.23366237 * y * y - 8.30039772 * y * y * y + 7.75029452 * y * y * y * y - 2.6351796 * y * y * y * y * y;
    
    // B 0.33344519  0.65790473  1.77127769 -6.58375371  7.89448243 -3.18174969
    pixb = 0.33344519 + 0.65790473 * z + 1.77127769 * z * z - 6.58375371 * z * z * z + 7.89448243 * z * z * z * z - 3.18174969 * z * z * z * z * z;
    
//    // Transparent Radial Gradient
//    vec2 u_lightPosition = vec2(0.5, 0.5);
//    
//    // Radial Gradient Radia
//    highp float u_lightRadia;
//    u_lightRadia = 0.4;
//    
//    // resolution is very important, only the right value insure the linght source on the center.
//    vec2 resolution = vec2(640.0, 640.0);
//    vec2 position = ( gl_FragCoord.xy / resolution.xy );
//    
//    float distanceFromLight = length(position - u_lightPosition);
//    
//    float caculateDistance;
//    
//    // Radial Gradient Start postion
//    caculateDistance = distanceFromLight - u_lightRadia;
//    
//    // y = (2x) ^ 2, multi 2 'cause caculateDistance in [0,0.5], we need transform it to [0,1]
//    caculateDistance = caculateDistance * caculateDistance * 4.0;
//    
//    vec4 u_outerColor;
//    vec4 u_innerColor = vec4(pixr, pixg, pixb, 1.0);
//    
//    // we use the caculateDistance as the transparent value.
//    u_outerColor = vec4(0.0, 0.0, 0.0, caculateDistance);
//    
//    // if point is in u_lightRadia, don't use Radial Gradient
//    if (distanceFromLight <= u_lightRadia)
//    {
//        gl_FragColor = u_innerColor;
//    }
//    else
//    {
//        gl_FragColor = mix(u_innerColor, u_outerColor, caculateDistance);
//    }
    gl_FragColor = vec4(pixr, pixg, pixb, 1.0);//u_innerColor
}
