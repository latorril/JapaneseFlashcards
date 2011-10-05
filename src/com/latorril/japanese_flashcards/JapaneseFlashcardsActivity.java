package com.latorril.japanese_flashcards;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class JapaneseFlashcardsActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	static FlashcardDb 
		db;
	long 
		id;
	static Object 
		currentCardId;
	String 
		answer,
		question;
	static MyFitText
		viewAnswer,
		viewQuestion;
	static View 
		flipperLayout;
	View quiz;
	Button 
		nextButton, 
		closeDrawerButton, 
		inflateButton;
	static Button flashcardListText;
	SlidingDrawer 
		slidingDrawer;
	FrameLayout
		mainLayout;
	static LayoutInflater
		myInflater;
	static LinearLayout
		listGroup;
	TextView 
		listItemName;
	EditText 
		questionInput,
		answerInput;
	
    GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Gesture detection
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        
        db = new FlashcardDb(this);
        
        mainLayout        = (FrameLayout)findViewById(R.id.mainLayout);
        flipperLayout     = (View)findViewById(R.id.card);
        viewAnswer        = (MyFitText)findViewById(R.id.answer);
        viewQuestion      = (MyFitText)findViewById(R.id.question);
        nextButton        = (Button)findViewById(R.id.nextButton);
        quiz              = (View)findViewById(R.id.quiz);
        
        slidingDrawer     = (SlidingDrawer)findViewById(R.id.slidingDrawer);
        closeDrawerButton = (Button)findViewById(R.id.closeDrawerButton);
        questionInput     = (EditText)findViewById(R.id.questionInput);
        answerInput       = (EditText)findViewById(R.id.answerInput);

        flashcardListText = (Button)findViewById(R.id.flashcardListText);
        inflateButton     = (Button)findViewById(R.id.inflateButton);
        listGroup         = (LinearLayout)findViewById(R.id.listGroup);
        listItemName      = (TextView)findViewById(R.id.listItemName);
        myInflater        = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		//shows first card in database
		showFirstCard();
		
		viewQuestion.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				slidingDrawer.animateOpen();
			    return true;
			}
		});
		
		viewAnswer.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				slidingDrawer.animateOpen();
				return true;
			}
		});
		
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showRandomCard(currentCardId);
			}
		});
		
		inflateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeIME();
	            
	    		if((questionInput.getText().length() > 0) && 
	    				(answerInput.getText().length() > 0))
	            {
	            	inflateFromInput();
	            }
			}
		});
		
		
		viewQuestion.setOnTouchListener(gestureListener);
		viewAnswer.setOnTouchListener(gestureListener);
        
        slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened()
            {
            	// TODO Auto-generated method stub
            	quiz.setVisibility(View.INVISIBLE);
            }
        });
        
        closeDrawerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				closeIME();
				quiz.setVisibility(View.VISIBLE);
				refreshDeck();
				slidingDrawer.animateClose();
			}
		});
        
        flashcardListText.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!checkVisibility(listGroup))
				{
					setList();
				}
				else
				{
					clearList();
				}
			}
		});
    }

    
    @Override
    protected void onPause() {
    	closeIME();
    }
    
    @Override
    protected void onStop() {
    	closeIME();
    }
    
    public boolean checkVisibility(View v) {
    	int state = v.getVisibility();
    	if(state == 0)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public static void clearList() {
		listGroup.removeViewsInLayout(0, listGroup.getChildCount());
		listGroup.setVisibility(View.GONE);
		flashcardListText.setText(R.string.list_closed_text);
    }
    
    public static void setList() {
    	clearList();
    	listGroup.setVisibility(View.VISIBLE);
    	flashcardListText.setText(R.string.list_opened_text);
		inflateFromDb();
    }
    
    public void refreshDeck() {
    	if (currentCardId == null)
    	{
    		showFirstCard();
    	}
    	else if (!checkDeckForCard())
		{
			showNextCard();
		}
    }
    
    public boolean checkDeckForCard() {
    	db.open();
    	Cursor c = db.fetchFlashcard((Long)currentCardId);
    	if(c.moveToFirst())
    	{
    		db.close();
    		return true;
    	}
    	else
    	{
    		db.close();
    		return false;
    	}
    }
    
    public static void showNextCard() {
    	db.open();
    	Cursor c = db.fetchNextFlashcard((Long)currentCardId);
    	if (c.moveToFirst())
    	{
    		setQuestion(c.getString(1), c.getString(2));
    		currentCardId = c.getLong(0);
    	}
		else
		{
			c = db.fetchAllFlashcards();
	        if (c.moveToFirst())
	        {
	        	setQuestion(c.getString(1), c.getString(2));
	        	currentCardId = c.getLong(0);
	        }
			else
			{
				alertNoCards();
			}
		}
    	db.close();
    }
    
    public static void showPreviousCard() {
    	db.open();
    	Cursor c = db.fetchPreviousFlashcard((Long)currentCardId);
    	if (c.moveToLast())
    	{
    		setQuestion(c.getString(1), c.getString(2));
    		currentCardId = c.getLong(0);
    	}
		else
		{
			c = db.fetchAllFlashcards();
	        if (c.moveToLast())
	        {
	        	setQuestion(c.getString(1), c.getString(2));
	        	currentCardId = c.getLong(0);
	        }
			else
			{
				alertNoCards();
			}
		}
    	db.close();
    }
    
    public void showRandomCard(Object lastCardId) {
		db.open();
		Cursor c = db.fetchRandomFlashcard();
		if(c.moveToFirst())
		{
			currentCardId = c.getLong(0);
			
			if(lastCardId.equals(currentCardId)){
				showNextCard();
			}
			else{
				setQuestion(c.getString(1), c.getString(2));
			}
		}
		else
		{
			alertNoCards();
		}
		db.close();
    }
    
    public void showFirstCard() {
        db.open();
        Cursor c = db.fetchAllFlashcards();
        if (c.moveToFirst())
        {
        	setQuestion(c.getString(1), c.getString(2));
			currentCardId = c.getLong(0);
        }
		else
		{
			alertNoCards();
		}
        db.close();
    }
    
    public static void setQuestion(String question, String answer) {
		viewQuestion.setText(question);
		viewAnswer.setText(answer);
    }
    
    public static void alertNoCards() {
    	setQuestion(
    			"Touch here to flip this card!", 
    			"Use swipe motion to move through the deck." 
    			+"\n \n"+
    			"Long touch to add and manage cards.\n ");
    	currentCardId = null;
    	setList();
    }

    public void closeIME() {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(mainLayout.getApplicationWindowToken(), 0);
    }
    
    public static void inflateFromDb() {
    	db.open();
        Cursor c = db.fetchAllFlashcards();
        if (c.moveToFirst())
        {
            do {
            	inflate(c.getInt(0), c.getString(1),c.getString(2));
            }
            while (c.moveToNext());
        }
        db.close();
    }
    
    public void inflateFromInput() {
    	String questionString = questionInput.getText().toString();
    	String answerString   = answerInput.getText().toString();
    	db.open();
    	int id = (int) db.createFlashcard(questionString, answerString);
    	db.close();
    	inflate(id, questionString, answerString);
    	clearInput();
    }
    
    public static void inflate(int id, String question, String answer) {
    	final LinearLayout listItem = (LinearLayout) 
    	myInflater.inflate(R.layout.list_option, null);
    	listItem.setId(id);
    	((TextView) listItem.getChildAt(0)).setText(
    			question  + " - " + answer
    	);
    	final Button delete;
    	delete = (Button)listItem.getChildAt(1);
    	delete.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			db.open();
    			db.deleteFlashcard(listItem.getId());
    			db.close();
    			LinearLayout deleteParent = (LinearLayout) v.getParent();
    			deleteParent.setVisibility(View.GONE);
    		}
    	});
    	listGroup.addView(listItem);
    }
    
    public void clearInput() {
    	questionInput.setText(null);
    	answerInput.setText(null);
    }
    
	public static void flipCard() {
		FlipAnimator animator = new FlipAnimator(viewAnswer, viewQuestion,
				viewQuestion.getWidth() / 2, viewQuestion.getHeight() / 2, 600);
		if (viewAnswer.getVisibility() == View.GONE)
		{
			animator.reverse();
		}
		flipperLayout.startAnimation(animator);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}