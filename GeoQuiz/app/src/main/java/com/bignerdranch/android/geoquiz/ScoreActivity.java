package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ScoreActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int correct = 0, incorrect = 0, unanswered = 0, cheated = 0;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);

		correct = getIntent().getIntExtra(QuizActivity.KEY_NUM_CORRECT, 0);
		incorrect = getIntent().getIntExtra(QuizActivity.KEY_NUM_INCORRECT, 0);
		unanswered = getIntent().getIntExtra(QuizActivity.KEY_NUM_UNANSWERED, 0);
		cheated = getIntent().getIntExtra(QuizActivity.KEY_NUM_CHEATED, 0);

		TextView mCorrectTextView = (TextView) findViewById(R.id.num_correct);
		TextView mIncorrectTextView = (TextView) findViewById(R.id.num_incorrect);
		TextView mUnansweredTextView = (TextView) findViewById(R.id.num_unanswered);
		TextView mCheatedTextView = (TextView) findViewById(R.id.num_cheated);

		mCorrectTextView.setText(getString(R.string.num_correct, correct));
		mIncorrectTextView.setText(getString(R.string.num_incorrect, incorrect));
		mUnansweredTextView.setText(getString(R.string.num_unanswered, unanswered));
		mCheatedTextView.setText(getString(R.string.num_cheated, cheated));
	}

}
