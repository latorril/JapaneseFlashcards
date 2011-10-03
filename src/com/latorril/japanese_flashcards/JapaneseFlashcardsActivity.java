package com.latorril.japanese_flashcards;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class JapaneseFlashcardsActivity extends Activity{
    /** Called when the activity is first created. */
	FlashcardDb 
		db;
	long 
		id;
	Object currentCardId;
	String 
		answer,
		question;
	AutoResizeTextView 
		viewAnswer,
		viewQuestion;
	View 
		flipperLayout,
		quiz;
	Button 
		nextButton, 
		closeDrawerButton, 
		inflateButton;
	SlidingDrawer 
		slidingDrawer;
	LayoutInflater
		myInflater;
	LinearLayout
		listGroup;
	TextView 
		listItemName;
	EditText 
		questionInput,
		answerInput;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        db = new FlashcardDb(this);
        
        flipperLayout     = (View)findViewById(R.id.card);
        viewAnswer        = (AutoResizeTextView)findViewById(R.id.answer);
        viewQuestion      = (AutoResizeTextView)findViewById(R.id.question);
        nextButton        = (Button)findViewById(R.id.nextButton);
        quiz              = (View)findViewById(R.id.quiz);
        
        slidingDrawer     = (SlidingDrawer)findViewById(R.id.slidingDrawer);
        closeDrawerButton = (Button)findViewById(R.id.closeDrawerButton);
        questionInput     = (EditText) findViewById(R.id.questionInput);
        answerInput       = (EditText) findViewById(R.id.answerInput);

        inflateButton     = (Button)findViewById(R.id.inflateButton);
        listGroup         = (LinearLayout) findViewById(R.id.listGroup);
        listItemName      = (TextView) findViewById(R.id.listItemName);
        myInflater        = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
				showRandomCard();
			}
		});
		
		inflateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeIME(v);
	            
	    		if((questionInput.getText().length() > 0) && 
	    				(answerInput.getText().length() > 0))
	            {
	            	inflateFromInput();
	            }
			}
		});
		
        viewAnswer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flipCard();
			}
        });
        
        viewQuestion.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
        		flipCard();
        	}
        });
        
        slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
            	// TODO Auto-generated method stub
            	quiz.setVisibility(View.INVISIBLE);
                //generates flash card list
        		inflateFromDb();
            }
        });
        
        closeDrawerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeIME(v);
				//minimizes the flash card list
				listGroup.removeViewsInLayout(0, listGroup.getChildCount());
				quiz.setVisibility(View.VISIBLE);
				refreshDeck();
				slidingDrawer.animateClose();
			}
		});
    }
    
    public void refreshDeck()
    {
    	if (currentCardId == null)
    	{
    		showFirstCard();
    	}
    	else if (!checkDeckForCard())
		{
			showNextCard();
		}
    }
    
    public boolean checkDeckForCard()
    {
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
    
    public void showNextCard()
    {
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
    
    public void showRandomCard()
    {
		db.open();
		Cursor c = db.fetchRandomFlashcard();
		if(c.moveToFirst())
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
    
    public void showFirstCard()
    {
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
    
    public void setQuestion(String question, String answer)
    {
		viewQuestion.setText(question);
		viewAnswer.setText(answer);
    }
    
    public void alertNoCards()
    {
    	setQuestion(
    			"Touch here to flip this card!", 
    			"Long touch to get to the flash card manager.");
    	currentCardId = null;
    }

    public void closeIME(View v)
    {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }
    
    public void inflateFromDb()
    {
    	db.open();
        Cursor c = db.fetchAllFlashcards();
        if (c.moveToFirst())
        {
            do
            {
            	inflate(c.getInt(0), c.getString(1),c.getString(2));
            }
            while (c.moveToNext());
        }
        db.close();
    }
    
    public void inflateFromInput()
    {
    	String questionString = questionInput.getText().toString();
    	String answerString   = answerInput.getText().toString();
    	db.open();
    	int id = (int) db.createFlashcard(questionString, answerString);
    	db.close();
    	inflate(id, questionString, answerString);
    	clearInput();
    }
    
    public void inflate(int id, String question, String answer)
    {
    	final LinearLayout listItem = (LinearLayout) 
    	myInflater.inflate(R.layout.list_option, null);
    	listItem.setId(id);
    	((TextView) listItem.getChildAt(0)).setText(
    			question  + " - " + answer
    	);
    	final Button delete;
    	delete = (Button)listItem.getChildAt(1);
    	delete.setOnClickListener(new View.OnClickListener()
    	{
    		@Override
    		public void onClick(View v) 
    		{
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
    
    public void clearInput()
    {
    	questionInput.setText(null);
    	answerInput.setText(null);
    }
    
	public void flipCard()
	{
		FlipAnimator animator = new FlipAnimator(viewQuestion, viewAnswer,
				viewAnswer.getWidth() / 2, viewAnswer.getHeight() / 2, 600);
		if (viewQuestion.getVisibility() == View.GONE)
		{
			animator.reverse();
		}
		flipperLayout.startAnimation(animator);
	}
}