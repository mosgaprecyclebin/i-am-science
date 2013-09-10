package ch.ethz.iamscience;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String[] levels = {"Student", "Master", "Doctor",
		"Professor", "Nobel Laureate"};
	private static final Integer[] levelScores = {10, 50, 250, 1000};

    private ArrayAdapter<String> apps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		apps = new ArrayAdapter<String>(this, R.layout.app_element);
        ListView appListView = (ListView) findViewById(R.id.apps);
        appListView.setAdapter(apps);
        apps.add("nervous");
        appListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (isAppInstalled("ch.ethz.nervous")) {
				    Intent launch = getPackageManager().getLaunchIntentForPackage("ch.ethz.nervous");
	                startActivity(launch);
				} else {
					Intent install = new Intent(Intent.ACTION_VIEW)
				        .setData(Uri.parse("market://details?id=ch.ethz.nervous"));
				    startActivity(install);
				}
			}
        	
        });
        
        refreshGui();
	}

	private void refreshGui() {
		int score = getScore();
	    TextView scoreText = (TextView) findViewById(R.id.score);
	    scoreText.setText(score + "");
	    String level = levels[0];
	    int i = 0;
	    while (i+1 < levels.length && score > levelScores[i]) {
	    	i++;
	    	level = levels[i];
	    }
	    TextView levelText = (TextView) findViewById(R.id.level);
	    levelText.setText(level);
	    String nextLevel = null;
	    int r = 0;
	    if (i+1 <= levels.length) {
	    	nextLevel = levels[i+1];
	    	r = levelScores[i] - score;
	    }
	    if (nextLevel != null) {
		    TextView nextLevelText = (TextView) findViewById(R.id.nextlevel);
		    nextLevelText.setText("(" + r + " more points to get to level '" + nextLevel + "')");
	    }
	}

	@Override
	protected void onResume() {
		super.onResume();
        refreshGui();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public int getScore() {
		SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
		int score = prefs.getInt("score", 0);
		Log.i("i-am-science", "Score: " + score);
		return score;
	}

	private boolean isAppInstalled(String id) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(id, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException ex) {
        }
        return installed;
    }

}
