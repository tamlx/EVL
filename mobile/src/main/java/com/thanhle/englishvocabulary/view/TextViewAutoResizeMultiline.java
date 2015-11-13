package com.thanhle.englishvocabulary.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by thanhle on 9/22/2014.
 */

public class TextViewAutoResizeMultiline extends TextView {

    private Typeface mTypeFace;

    private int mMaxTextSize;
    private TextPaint mPaint;

    public TextViewAutoResizeMultiline(Context context) {
        super(context);
        init();
    }

    public TextViewAutoResizeMultiline(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewAutoResizeMultiline(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Initialize data.
     */
    private void init() {
        mMaxTextSize = (int) getTextSize();
        mPaint = new TextPaint();
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int lengthBefore, final int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (!isInEditMode()) {
            setResizeText(text.toString(), this.getWidth(), this.getHeight());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!isInEditMode()) {
            if (w != oldw || h != oldh) {
                setResizeText(getText().toString(), w, h);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isInEditMode()) {
            int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
            setResizeText(getText().toString(), parentWidth, parentHeight);
        }
    }
    // private boolean mIsEmpty;

    /**
     * Set text and image size to resize.
     *
     * @param text
     * @param imgWidth
     * @param imgHeight
     */
    public void setResizeText(String text, int imgWidth, int imgHeight) {
        if (imgWidth > 0 && imgHeight > 0) {
            int width = imgWidth - (getPaddingLeft() + getPaddingRight());
            int height = imgHeight - (getPaddingTop() + getPaddingBottom());

            int textSize = mMaxTextSize;
            if (!TextUtils.isEmpty(text)) {
                while (getHeightOfMultiLineText(text, textSize, width) >= height) {
                    textSize -= 4;
                }
            }
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    private int getHeightOfMultiLineText(String text, int textSize, int maxWidth) {
        int result = 0;
        int index = 0;
        int lineCount = 0;
        String firstLineText = "";
        mPaint.set(getPaint());
        mPaint.setTextSize(textSize);
        // Tra ve so dong tuong ung voi chuoi TEXT va TEXT_SIZE.
        while (index < text.length()) {
            // Break text : Do chuoi text va dung lai khi chieu rong > maxWidth. Tra ve so ki tu da do, va neu
            // measuredWidth != null, tre ve chieu rong do duoc thuc te.
            index += mPaint.breakText(text, index, text.length(), true, maxWidth, null);
            if (TextUtils.isEmpty(firstLineText)) firstLineText = text.substring(0, index);
            lineCount++;
        }

        Rect bound = new Rect();
        mPaint.getTextBounds(firstLineText, 0, firstLineText.length(), bound);

        // Obtain space between lines.
        double lineSpacing = Math.max(0, (lineCount - 1) * mPaint.getFontSpacing());
        // Tinh tong chieu cao cua : khoang cach cac dong + so dong * chi cao 1 dong.
        result = (int) Math.floor(lineSpacing + lineCount * bound.height());
        return result;
    }
}