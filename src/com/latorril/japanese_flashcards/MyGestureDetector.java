package com.latorril.japanese_flashcards;

import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MyGestureDetector extends SimpleOnGestureListener {
    
	private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 300;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    
	public boolean onSingleTapConfirmed(MotionEvent e){
		JapaneseFlashcardsActivity.flipCard();
		return false;
	}
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > 
            SWIPE_THRESHOLD_VELOCITY) {
            	JapaneseFlashcardsActivity.showNextCard();
            	Animation left = AnimationUtils.loadAnimation(JapaneseFlashcardsActivity.flipperLayout.getContext(), R.anim.left_in);
            	JapaneseFlashcardsActivity.flipperLayout.startAnimation(left);
            }  
            else if (e2.getX() - e1.getX() > 
            SWIPE_MIN_DISTANCE && Math.abs(velocityX) > 
            SWIPE_THRESHOLD_VELOCITY) {
            	JapaneseFlashcardsActivity.showPreviousCard();
            	Animation right = AnimationUtils.loadAnimation(JapaneseFlashcardsActivity.flipperLayout.getContext(), R.anim.right_in);
            	JapaneseFlashcardsActivity.flipperLayout.startAnimation(right);
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }
}
