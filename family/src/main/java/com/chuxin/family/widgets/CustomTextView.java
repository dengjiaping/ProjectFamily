package com.chuxin.family.widgets;

import com.chuxin.family.libs.gpuimage.utils.PictureUtils;
import com.chuxin.family.utils.InteractiveImageSpan;
import com.chuxin.family.utils.RelativeInteractiveImageSpan;
import com.chuxin.family.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 * @author shichao.wang 2013.05.06
 *
 */
public class CustomTextView extends TextView{
    private int textSize = 20;
    private int textColor = R.color.cx_fa_co_black;
    private String text;
    private Paint paint;
    private boolean hasDrawed = false;
    private List<String> lines;
    private Context mContext;
    private int mImageWidth;
    private int mImageHeight;

    
    // add by shichao 经典表情图文混排
    private static String[] sFaceValues = null;

    private static String[] sFaceTexts = null;

    public static String[] getFaceValues(Resources res) {
        if (sFaceValues == null) {
            sFaceValues = res.getStringArray(R.array.face_static_images_ids);
        }
        return sFaceValues;
    }

    public static String[] getFaceTexts(Resources res) {
        if (sFaceTexts == null) {
            sFaceTexts = res.getStringArray(R.array.face_texts);
        }
        return sFaceTexts;
    }
    
    
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        textSize = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/android", "textSize", 28);
        textColor = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/android", "textColor", R.color.cx_fa_co_black);
        paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (text != null && !text.trim().equals("")) {
            if(lines != null) {             //先把每行文字保存起来，重画的时候加快速度
                for(int i=0; i<lines.size(); i++) {
                    canvas.drawText(lines.get(i), 0, (i+1) * textSize, paint);
                }
                return;
            }
        }
//        if(mImageWidth > 0 && mImageHeight > 0){
//            // 修正行高
//            FontMetrics fm = paint.getFontMetrics();  
//            float fFontHeight = (float)Math.ceil(fm.descent - fm.ascent); 
//            int textViewWidth = this.getWidth();
//            int textViewHeigh = this.getHeight();
//            canvas.drawText(text, 0, mImageHeight + (textViewHeigh - mImageHeight)/2, paint);
//        }
//            int viewWidth = this.getWidth();                                    // 得到view的宽度
//            int textWidth = (int) paint.measureText(text, 0, text.length());    // 得到text的宽度
//            int line = (int) Math.ceil(textWidth / viewWidth) + 1;              //得到行数
//            if(!hasDrawed) {    //通过行数设置view的高度，最后加的1/2行高是为了把p、j这种跨行的字母显示完整
//                this.setLayoutParams(new LayoutParams(viewWidth, textSize * line + textSize / 2));
//            }
//            if (textWidth < viewWidth) {
//                canvas.drawText(text, 0, textSize, paint);
//            } else {
//                int currIndex = 0;
//                int lineNum = 1;
//                String lastLine = "";
//                lines = new ArrayList<String>();
//                int lineWords = (int) ((float)text.length() / ((float) textWidth / (float) viewWidth + 2));
//                for (int i = lineWords; i < text.length(); i++) {
//                    String subStr = text.substring(currIndex, i);
//                    int width = (int) paint.measureText(subStr, 0, subStr.length());
//                    if (width < viewWidth) {
//                        lastLine = subStr;
//                        if (i == text.length() - 1) {
//                            lastLine = text.substring(currIndex, ++i);
//                            canvas.drawText(lastLine, 0, lineNum * textSize, paint);
//                            lines.add(lastLine);
//                        }
//                        continue;
//                    } else {
//                        canvas.drawText(lastLine, 0, lineNum * textSize, paint);
//                        lines.add(lastLine);
//                        currIndex = i - 1;
//                        lineNum++;
//                        i--;
//                        if(i + lineWords < text.length())
//                            i += lineWords;
//                    }
//                }
//                hasDrawed = true;
//            }
//        }

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        hasDrawed = false;
        invalidate();
        fitText(text);
    }

    public void fitText(String text){
        // 图文混排 add by shichao
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            Pattern pattern = Pattern.compile("\\[(\\w+?)\\]");
            Matcher matcher = pattern.matcher(text);
            
            String[] faceValues = getFaceValues(mContext.getResources());
            String[] faceTexts = getFaceTexts(mContext.getResources());
            TypedArray faceImageIds = mContext.getResources().obtainTypedArray(R.array.face_static_images);
            PictureUtils pu = new PictureUtils(mContext);
            while(matcher.find()){
                for(int i = 0; i<matcher.groupCount(); i++){
                    for(int j = 0; j < faceTexts.length; j++){
                       if(matcher.group(i).equals(faceTexts[j])){
//                           Field field = R.drawable.class.getDeclaredField(faceValues[j]);
//                           int resourceId = Integer.parseInt(field.get(null).toString());
                           int resourceId = faceImageIds.getResourceId(j, 0);
                           Drawable drawable = mContext.getResources().getDrawable(resourceId);
//                           Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId);
//                           Bitmap imageBitmap = pu.zoomBitmap(bitmap, 45, 45);
//                           ImageSpan span = new ImageSpan(mContext, imageBitmap, ImageSpan.ALIGN_BASELINE);
                           InteractiveImageSpan span = new RelativeInteractiveImageSpan(drawable, 1.2f, 1);
//                           drawable.setBounds(0, 0, 12, 12);
                           mImageWidth = drawable.getIntrinsicWidth();
                           mImageHeight = drawable.getIntrinsicHeight();
                           builder.setSpan(span, matcher.start(),
                                   matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                           bitmap.recycle();
                           break;
                       }
                    }
                    
                }
            }
            
            this.setText(builder);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } 
    }
}


