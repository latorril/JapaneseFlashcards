package com.latorril.japanese_flashcards;

import java.util.ArrayList;
import java.util.Random;

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
import android.widget.Toast;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

public class JapaneseFlashcardsActivity extends Activity{
    /** Called when the activity is first created. */
	//ViewFlipper viewFlipper;
	FlashcardDb db;
	long id;
	
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
	
	String 
		answer,
		question;
	int randomNumber;
	String[]questionSet;
	
	LayoutInflater myInflater;
	LinearLayout listGroup;
	TextView listItemName;
	EditText 
		questionInput,
		answerInput;
	
	ArrayList<Integer> idList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        db = new FlashcardDb(this);
        
        flipperLayout     = (View)findViewById(R.id.card);
        
        viewAnswer        = (AutoResizeTextView)findViewById(R.id.answer);
        viewQuestion      = (AutoResizeTextView)findViewById(R.id.question);
        nextButton        = (Button)findViewById(R.id.nextButton);
        slidingDrawer     = (SlidingDrawer)findViewById(R.id.slidingDrawer);
        quiz              = (View)findViewById(R.id.quiz);
        closeDrawerButton = (Button)findViewById(R.id.closeDrawerButton);
        
        inflateButton     = (Button)findViewById(R.id.inflateButton);
        listGroup         = (LinearLayout) findViewById(R.id.listGroup);
        listItemName      = (TextView) findViewById(R.id.listItemName);
        myInflater        = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		questionInput     = (EditText) findViewById(R.id.questionInput);
		answerInput       = (EditText) findViewById(R.id.answerInput);

		inflateFromDb();
		
		showFirstCard();
		
        
		nextButton.setOnClickListener(new View.OnClickListener() {
			//set to get a question and answer from db columns upon clicking
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//gets random card from array;
				idList = getRowIdlist();
				if(idList.size() > 0){
					randomNumber = getRandomNumber(idList.size());
					db.open();
					Cursor c = db.fetchFlashcard(idList.get(randomNumber));
					if (c.moveToFirst())
					{
						setQuestion(c.getString(1), c.getString(2));
					}
					db.close();
				}
				else{
					alertNoCards();
				}
			}
			
		});
		
		inflateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
	            
	    		if((questionInput.getText().length() > 0) && 
	    				(answerInput.getText().length() > 0))
	            {
	            	inflateFromInput();
	            }
	            
	            else{}
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
             // TODO Auto-generated method stub\
             //ImageView view=(ImageView)slidingDrawer.getHandle();
             //view.setImageResource(R.drawable.tray_handle_selected);
             quiz.setVisibility(View.INVISIBLE);
            }
           });
        slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				
			}
		});
        closeDrawerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				quiz.setVisibility(View.VISIBLE);
				slidingDrawer.animateClose();
				
			}
		});
    }
    
    public ArrayList<Integer> getRowIdlist()
    {
    	ArrayList<Integer> rowIdList = new ArrayList<Integer>();
    	db.open();
    	Cursor c = db.fetchAllFlashcards();
    	if (c.moveToFirst())
        {
    		do{
    			rowIdList.add(new Integer(c.getInt(0)));
    		}
    		while(c.moveToNext());
        }
    	db.close();
		return rowIdList;
    }
    
    public void alertNoCards()
    {
    	setQuestion("?", "!");
    }
    public void setQuestion(String question, String answer){
		viewQuestion.setText(question);
		viewAnswer.setText(answer);
    }
    
    public void DisplayTitle(Cursor c)
    {
        Toast.makeText(this,        		
        		"id: "       + c.getString(0) + "\n" +
                "question: " + c.getString(1) + "\n" +
                "answer: "   + c.getString(2) + "\n",
                Toast.LENGTH_SHORT).show();        
    }
    
    public void showFirstCard()
    {
        db.open();
        Cursor c = db.fetchAllFlashcards();
        if (c.moveToFirst())
        {
        	setQuestion(c.getString(1), c.getString(2));
        }
		else{
			alertNoCards();
		}
        db.close();
    }
    
    public void inflateFromDb()
    {
    	db.open();
        Cursor c = db.fetchAllFlashcards();
        if (c.moveToFirst())
        {
            do {          
                
            	final LinearLayout listItem = (LinearLayout) 
            	myInflater.inflate(R.layout.list_option, null);
            	//set pos to _id in database
            	listItem.setId(c.getInt(0));
            	((TextView) listItem.getChildAt(0)).setText(
            			c.getString(1)  + " - " + c.getString(2)
            			);
            	final Button delete;
            	delete = (Button)listItem.getChildAt(1);
            	delete.setOnClickListener(new View.OnClickListener() {
            		
            		@Override
            		public void onClick(View v) {
            			// TODO Auto-generated method stub
            			//here we will also send query to delete row where _id = listItem.getId() 
            			db.open();
            			db.deleteFlashcard(listItem.getId());
            			db.close();
            			LinearLayout deleteParent = (LinearLayout) v.getParent();
            			deleteParent.setVisibility(View.GONE);
            		}
            	});
            	listGroup.addView(listItem);
            	
            } while (c.moveToNext());
        }
        db.close();
        
    }
    
    public void inflateFromInput(){

		final LinearLayout listItem = (LinearLayout) 
		myInflater.inflate(R.layout.list_option, null);
		
		String questionString = questionInput.getText().toString();
		String answerString   = answerInput.getText().toString();
		//userInput will also go to the database and be put into the "question" column
		//also create if statements so that fields must have a value
		db.open();
		long id = db.createFlashcard(questionString, answerString);
		listItem.setId((int)id);
		db.close();
		
		questionInput.setText(null);
		answerInput.setText(null);
		((TextView) listItem.getChildAt(0)).setText(
				questionString + " - " + answerString
				);
		final Button delete;
	    delete = (Button)listItem.getChildAt(1);
	    delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//here we will also send query to delete row where _id = listItem.getId() 
				db.open();
    			db.deleteFlashcard(listItem.getId());
    			db.close();
				LinearLayout deleteParent = (LinearLayout) v.getParent();
				deleteParent.setVisibility(View.GONE);
			}
		});
		listGroup.addView(listItem);
    }
    
	public void flipCard(){
		FlipAnimator animator = new FlipAnimator(viewQuestion, viewAnswer,
				viewAnswer.getWidth() / 2, viewAnswer.getHeight() / 2, 600);
		if (viewQuestion.getVisibility() == View.GONE) {
			animator.reverse();
		}
		flipperLayout.startAnimation(animator);
	}
	
	private int getRandomNumber(int maxSize){
		Random random = new Random();
		int min = 0;
		int max = maxSize-1;
		int randomNumber = random.nextInt(max-min+1) + min;
		return randomNumber;
	}
    
}