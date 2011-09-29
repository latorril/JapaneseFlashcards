package com.latorril.japanese_flashcards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
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
	
	TextView 
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
	
	static int pos = 0;
	static int i = 0;
	LayoutInflater myInflater;
	LinearLayout listGroup;
	TextView listItemName;
	EditText questionInput;
	
	private String[] lorem = {"lorem", "ipsum", "dolor",
			"sit", "amet","consectetuer", "adipiscing", "elit", "morbi",
			};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        db = new FlashcardDb(this);
      //---add 2 titles---
        db.open();        
        
        /*Cursor c = db.fetchAllFlashcards();
        if (c.moveToFirst())
        {
            do {          
                DisplayTitle(c);
            } while (c.moveToNext());
        }*/
        // 12 entries so far
        /*id = db.createFlashcard(
        		"foo_1",
        		"bar_1");        
        id = db.createFlashcard(
        		"foo_2",
        		"bar_2"
        		);*/
        db.close();
        
        flipperLayout = (View)findViewById(R.id.card);
        
        viewAnswer = (TextView)findViewById(R.id.answer);
        viewQuestion = (TextView)findViewById(R.id.question);
        nextButton = (Button)findViewById(R.id.nextButton);
        slidingDrawer = (SlidingDrawer)findViewById(R.id.slidingDrawer);
        quiz = (View)findViewById(R.id.quiz);
        closeDrawerButton = (Button)findViewById(R.id.closeDrawerButton);
        
        inflateButton = (Button)findViewById(R.id.inflateButton);
        listGroup = (LinearLayout) findViewById(R.id.listGroup);
        listItemName = (TextView) findViewById(R.id.listItemName);
        myInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		randomNumber = getRandomNumber();
		questionSet = selectQuestionSet(randomNumber);
		
		questionInput = (EditText) findViewById(R.id.questionInput);

		setQuestion();		
		//inflateFromArray();
		inflateFromDb();
		
		nextButton.setOnClickListener(new View.OnClickListener() {
			//set to get a question and answer from db columns upon clicking
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//gets random card from array;
				randomNumber = getRandomNumber();
				questionSet = selectQuestionSet(randomNumber);
				setQuestion();
			}
			
		});
		
		inflateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
	            
	    		if(questionInput.getText().length() > 0)
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
    
    public void DisplayTitle(Cursor c)
    {
        Toast.makeText(this,        		
        		"id: "       + c.getString(0) + "\n" +
                "question: " + c.getString(1) + "\n" +
                "answer: "   + c.getString(2) + "\n",
                Toast.LENGTH_SHORT).show();        
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
            	((TextView) listItem.getChildAt(0)).setText(c.getString(1)  + " - ");
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
		//userInput will also go to the database and be put into the "question" column
		//also create if statements so that fields must have a value
		db.open();
		long id = db.createFlashcard(questionString, "SOMETHING");
		listItem.setId((int) id);
		db.close();
		
		questionInput.setText(null);
		((TextView) listItem.getChildAt(0)).setText(questionString + " - ");
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
		pos++;
		listGroup.addView(listItem);
    }

    //will modify this and create a list that inflates from the database entries
	public void inflateFromArray(){
        
		for(String item: lorem){
			LinearLayout listItem = (LinearLayout) 
				myInflater.inflate(R.layout.list_option, null);
			//set pos to _id in database
			listItem.setId(pos);
			((TextView) listItem.getChildAt(0)).setText(item);
			final Button delete;
		    delete = (Button)listItem.getChildAt(1);
		    delete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//here we will also send query to delete row where _id = listItem.getId() 
					LinearLayout deleteParent = (LinearLayout) v.getParent();
					deleteParent.setVisibility(View.GONE);
				}
			});
		    
		    pos++;
			listGroup.addView(listItem);
		};
	}
    
	public void flipCard(){
		FlipAnimator animator = new FlipAnimator(viewQuestion, viewAnswer,
				viewAnswer.getWidth() / 2, viewAnswer.getHeight() / 2, 600);
		if (viewQuestion.getVisibility() == View.GONE) {
			animator.reverse();
		}
		flipperLayout.startAnimation(animator);
	}
	
	private int getRandomNumber(){
		Random random = new Random();
		int min = 0;
		int max = 14;
		int randomNumber = random.nextInt(max-min+1) + min;
		return randomNumber;
	}
	
	public void arrayTest(){
		String shout = "test";
		String[] s1 = { shout, "two"};
		String[] s2 = { "four", "five"};
		 
		List<String[]> list = new ArrayList<String[]>();
		list.add(s1);
		list.add(s2);
		String[][] arrays = new String[list.size()][];
		list.toArray(arrays);
		String[] something = list.get(0);
		String test = something[0];
		viewQuestion.setText(test);
	}
	
	public String[] selectQuestionSet(int number){
		String[] set = new String[2];
		
		switch(number){
		case 0:
			set[0] = "あ";
			set[1] = "A";
			return set;
		case 1:
			set[0] = "い";
			set[1] = "I";
			return set;
		case 2:
			set[0] = "う";
			set[1] = "U";
			return set;
		case 3:
			set[0] = "え";
			set[1] = "E";
			return set;
		case 4:
			set[0] = "お";
			set[1] = "O";
			return set;
		//KA
		case 5:
			set[0] = "か";
			set[1] = "KA";
			return set;
		case 6:
			set[0] = "き";
			set[1] = "KI";
			return set;
		case 7:
			set[0] = "く";
			set[1] = "KU";
			return set;
		case 8:
			set[0] = "け";
			set[1] = "KE";
			return set;
		case 9:
			set[0] = "こ";
			set[1] = "KO";
			return set;
		//SA
		case 10:
			set[0] = "さ";
			set[1] = "SA";
			return set;
		case 11:
			set[0] = "し";
			set[1] = "SHI";
			return set;
		case 12:
			set[0] = "す";
			set[1] = "SU";
			return set;
		case 13:
			set[0] = "せ";
			set[1] = "SE";
			return set;
		case 14:
			set[0] = "そ";
			set[1] = "SO";
			return set;
		}
		
		return null;
	}
	
	public void setQuestion(){
		question = questionSet[0];
		answer = questionSet[1];
		viewQuestion.setText(question);
		viewAnswer.setText(answer);
	}
    
}