package com.bignerdranch.android.geoquiz;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends Activity {

	private Button mTrueButton;
	private Button mFalseButton;
	private Button mCheatButton;
	private Button mScoreButton;
	private ImageButton mPrevButton;
	private ImageButton mNextButton;
	private TextView mQuestionTextView;
	
	private static final String TAG = "QuizActivity";
	private static final String KEY_INDEX = "index";
	public static final String KEY_CHEATER = "didCheat";
	public static final String KEY_NUM_CORRECT = "numCorrect";
	public static final String KEY_NUM_INCORRECT = "numIncorrect";
	public static final String KEY_NUM_UNANSWERED = "numUnanswered";
	public static final String KEY_NUM_CHEATED = "numCheated";
	
	private TrueFalse[] mQuestionBank = new TrueFalse[] {
		new TrueFalse(R.string.question_oceans, true),
		new TrueFalse(R.string.question_mideast, false),
		new TrueFalse(R.string.question_africa, false),
		new TrueFalse(R.string.question_americas, true),
		new TrueFalse(R.string.question_asia, true),
	};
	
	private int mCurrentIndex = 0, cheated = 0, correct = 0, incorrect = 0, unanswered = mQuestionBank.length;
	private ArrayList<Integer> didCheat, answered = new ArrayList<Integer>();
	
	private void updateQuestion() {
//		Log.d(TAG, "Updating question text for question #" + mCurrentIndex, new Exception());
		int question = mQuestionBank[mCurrentIndex].getQuestion();
		mQuestionTextView.setText(question);
	}
	
	private void checkAnswer(boolean userPressedTrue) {
		boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
		if (!answered.contains(mCurrentIndex)) {
			unanswered -= 1;
		}
		
		int messageResId = 0;
		
		if (didCheat.contains(mCurrentIndex)) {
			messageResId = R.string.judgment_toast;
		} else {
			if (userPressedTrue == answerIsTrue) {
				messageResId = R.string.correct_toast;
				if (!answered.contains(mCurrentIndex)) {
					correct += 1;
					answered.add(mCurrentIndex);
				}
			} else {
				messageResId = R.string.incorrect_toast;
				if (!answered.contains(mCurrentIndex)) {
					incorrect += 1;
					answered.add(mCurrentIndex);
				}
			}
		}
		Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
	}
	
	private void nextQuestion()
	{
		mPrevButton.setEnabled(mCurrentIndex + 1 > 0);
		mNextButton.setEnabled(mCurrentIndex + 2 != mQuestionBank.length);
		mCurrentIndex = (mCurrentIndex +1) % mQuestionBank.length;
		updateQuestion();
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.setIntent(intent);
	}
	
	@TargetApi(11)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        ActionBar actionBar = getActionBar();
	        actionBar.setSubtitle("Bodies of water");
        }
        
        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nextQuestion();
			}
		});
        
        if (savedInstanceState != null) {
        	mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        	didCheat = (ArrayList<Integer>) savedInstanceState.get(KEY_CHEATER);
			correct = (Integer) savedInstanceState.get(KEY_NUM_CORRECT);
			incorrect = (Integer) savedInstanceState.get(KEY_NUM_INCORRECT);
			unanswered = (Integer) savedInstanceState.get(KEY_NUM_UNANSWERED);
			cheated = (Integer) savedInstanceState.get(KEY_NUM_CHEATED);
        } else {
        	didCheat = new ArrayList<Integer>();
        }
        
        updateQuestion();
        
        mTrueButton = (Button)findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkAnswer(true);
			}
		});
        
        mFalseButton = (Button)findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checkAnswer(false);
			}
		});
        
        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Start Cheat Activity
				Intent i = new Intent(QuizActivity.this, CheatActivity.class);
				boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
				i.putExtra(CheatActivity.EXTRA_ANSWER_IS_TRUE, answerIsTrue);
				startActivityForResult(i, 0);
			}
		});

		mScoreButton = (Button) findViewById(R.id.show_score_button);
		mScoreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Start Score Activity
				Intent scoreIntent = new Intent(QuizActivity.this, ScoreActivity.class);
				scoreIntent.putExtra(KEY_NUM_CORRECT, correct);
				scoreIntent.putExtra(KEY_NUM_INCORRECT, incorrect);
				scoreIntent.putExtra(KEY_NUM_UNANSWERED, unanswered);
				scoreIntent.putExtra(KEY_NUM_CHEATED, cheated);
				startActivity(scoreIntent);
			}
		});
        
        mPrevButton = (ImageButton)findViewById(R.id.previous_button);
		mPrevButton.setEnabled(mCurrentIndex != 0);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPrevButton.setEnabled(mCurrentIndex - 1 != 0);
				mNextButton.setEnabled(mCurrentIndex < mQuestionBank.length);
				mCurrentIndex = (mCurrentIndex -1) % mQuestionBank.length;
				if (mCurrentIndex < 0)
				{
					mCurrentIndex += mQuestionBank.length;
				}
				updateQuestion();
			}
		});
        
        mNextButton = (ImageButton)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nextQuestion();
			}
		});
		mNextButton.setEnabled(mCurrentIndex + 2 != mQuestionBank.length);
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	super.onActivityResult(requestCode, resultCode, data);
    	if (data == null) {
    		return;
    	}
    	if(data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_IS_SHOWN, false))
    	{
    		didCheat.add(mCurrentIndex);
			if (!answered.contains(mCurrentIndex)) {
				cheated += 1;
			}
    	}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	Log.i(TAG, "onSaveInstanceState");
    	savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    	savedInstanceState.putIntegerArrayList(KEY_CHEATER, didCheat);
		savedInstanceState.putInt(KEY_NUM_CORRECT, correct);
		savedInstanceState.putInt(KEY_NUM_INCORRECT, incorrect);
		savedInstanceState.putInt(KEY_NUM_UNANSWERED, unanswered);
		savedInstanceState.putInt(KEY_NUM_CHEATED, cheated);
    }

	@Override
    protected void onStart() {
    	super.onStart();
    	Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	Log.d(TAG, "onPause() called");

    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.d(TAG, "onResume() called");
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	Log.d(TAG, "onStop() called");
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	Log.d(TAG, "onDestroy() called");
    }
}
