package ch.ethz.iamscience;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class IncreaseScoreActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_increase_score);
	}

    @Override
    protected void onStart() {
    	super.onStart();
		SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		int score = prefs.getInt("score", 0);
    	Log.i("i-am-science", "Score + 1 = " + (score + 1));
		prefsEditor.putInt("score", score + 1);
		prefsEditor.commit();
		finish();
    }

}
