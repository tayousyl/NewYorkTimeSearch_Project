package com.example.sonel.newyorktimessearch.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

;

/**
 * Created by sonel on 7/27/2017.
 */

public class ResizableImageView extends android.support.v7.widget.AppCompatImageView {

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();

        if(d!=null){
            // ceil not round - avoid thin vertical gaps along the left/right edges
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() /
                    (float) d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


}
