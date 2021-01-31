package com.blogspot.skferdous.justdigitaldiary.NotePad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

public class LinedEditText extends EditText {

    private static Paint linePaint;

    static {
        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setTextSize(20);
    }

    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect bounds = new Rect();

        int firstLineY = getLineBounds(0, bounds);
        int lineHeight = getLineHeight();

        int totalLines = Math.max(getLineCount(), (getHeight()) / lineHeight);

        for (int i = 0; i < totalLines; i++) {
            int lineY = firstLineY + i * lineHeight;
            canvas.drawLine(bounds.left, lineY, bounds.right, lineY, linePaint);
        }
        super.onDraw(canvas);
    }
}
