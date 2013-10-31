package ch.ethz.iamscience;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String[] levels = {"Student", "Master", "Doctor", "Professor", "Nobel Laureate"};
	private static final Integer[] levelScores = {10, 50, 250, 1000};

    private ScienceAppAdapter appAdapter;
    private IAmScienceUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        user = IAmScienceUser.get(getApplicationContext());

		appAdapter = new ScienceAppAdapter(this);
        ListView appListView = (ListView) findViewById(R.id.apps);
        appListView.setAdapter(appAdapter);
        appListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				try {
					String appId = appAdapter.getItem(position).getString("id");
					if (isAppInstalled(appId)) {
					    Intent launch = getPackageManager().getLaunchIntentForPackage(appId);
		                startActivity(launch);
					} else {
						Intent install = new Intent(Intent.ACTION_VIEW)
					        .setData(Uri.parse("market://details?id=" + appId));
					    startActivity(install);
					}
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}

        });
        
        refreshGui();
        updateData();
	}

	private void updateData() {
		(new UpdateDataTask()).execute();
	}

	private void refreshGui() {
		int score = user.getTotalScore();
	    TextView scoreText = (TextView) findViewById(R.id.score);
		if (score < 0) {
		    scoreText.setText("...");
		} else {
		    scoreText.setText(score + "");
		}
	    String level = levels[0];
	    int i = 0;
	    while (i+1 < levels.length && score >= levelScores[i]) {
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

	    if (user.getData() != null) {
		    try {
		    	appAdapter.setApps(user.getData().getJSONArray("apps"));
		    } catch (JSONException ex) {
		    	ex.printStackTrace();
		    }
	    }
	}

	@Override
	protected void onResume() {
		super.onResume();
        updateData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

	private class UpdateDataTask extends AsyncTask<Object,Void,Void> {

	    protected Void doInBackground(Object... objs) {
	        user.updateData();
	        return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	    	super.onPostExecute(result);
	    	refreshGui();
	    }

	}

}
