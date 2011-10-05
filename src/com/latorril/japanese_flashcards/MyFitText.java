package com.latorril.japanese_flashcards;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/* Based on 
 * from http://stackoverflow.com/questions/2617266/how-to-adjust-text-font-size-to-fit-textview
 */
public class MyFitText extends TextView {
	
    private static final float MAX_TEXT_SIZE = 150f;
	private final float MIN_TEXT_SIZE = 20f;

    public MyFitText(Context context) {
        super(context);
    }

    public MyFitText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFitText(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    private synchronized void resizeText() {
    	float newSize;
    	float textLength = (float)(this.getText().length());
		if (textLength > 0) 
    	{
    		newSize = Math.round(MAX_TEXT_SIZE/(textLength*(.5f)));
    		if(textLength == 1)
    		{
    			this.setTextSize(MAX_TEXT_SIZE);
    		}
    		if(newSize < MIN_TEXT_SIZE && textLength > 1){
    			this.setTextSize(MIN_TEXT_SIZE);
    		}
    		else{
    			this.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
    					newSize, getResources().getDisplayMetrics()));
    		}
    	}
    }
    
    @Override
    protected void onTextChanged(final CharSequence text, final int start,
            final int before, final int after) {
        super.onTextChanged(text, start, before, after);
        resizeText();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w != oldw) 
        	resizeText();
    }
}