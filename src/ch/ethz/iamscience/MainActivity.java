package ch.ethz.iamscience;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	// dummy JSON file (to be replaced by web service call):
	private static String serverURL = "https://raw.github.com/mosgap/i-am-science/master/src/ch/ethz/iamscience/data-example.json";

	private static final String[] levels = {"Student", "Master", "Doctor", "Professor", "Nobel Laureate"};
	private static final Integer[] levelScores = {10, 50, 250, 1000};
	private static final List<ScienceApp> apps = new ArrayList<ScienceApp>();

    private ScienceAppAdapter appAdapter;
    private String userId;
    private JSONObject data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        try {
        	File dir = new File(Environment.getExternalStorageDirectory(), "i-am-science");
        	dir.mkdir();
        	File file = new File(dir, "userid");
        	if (file.exists()) {
        		BufferedReader r = new BufferedReader(new FileReader(file));
        		userId = r.readLine();
        		r.close();
        	} else {
        		Log.i("FOO", file.toString());
        		userId = Math.abs(new Random().nextLong()) + "";
        		BufferedWriter w = new BufferedWriter(new FileWriter(file));
        		w.write(userId + "\n");
        		w.close();
        	}
        } catch (IOException ex) {
        	ex.printStackTrace();
        }

		appAdapter = new ScienceAppAdapter(this);
        ListView appListView = (ListView) findViewById(R.id.apps);
        appListView.setAdapter(appAdapter);
        appListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				String appId = appAdapter.getItem(position).getId();
				if (isAppInstalled(appId)) {
				    Intent launch = getPackageManager().getLaunchIntentForPackage(appId);
	                startActivity(launch);
				} else {
					Intent install = new Intent(Intent.ACTION_VIEW)
				        .setData(Uri.parse("market://details?id=" + appId));
				    startActivity(install);
				}
			}

        });
        
        refreshGui();
        
        GetDataTask getDataTask = new GetDataTask();
        getDataTask.execute();
	}

	private void refreshGui() {
		int score = getScore();
	    TextView scoreText = (TextView) findViewById(R.id.score);
		if (score < 0) {
		    scoreText.setText("...");
		} else {
		    scoreText.setText(score + "");
		}
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
		try {
			return data.getInt("score");
		} catch (Exception ex) {
			return -1;
		}
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

	private class GetDataTask extends AsyncTask<Object,Integer,Boolean> {

	    protected Boolean doInBackground(Object... objs) {
	        try {
	            URL url = new URL(serverURL);
	            URLConnection urlConnection = url.openConnection();
	            BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	            String jsonData = "";
	            try {
	            	String line;
	            	while ((line = r.readLine()) != null) {
	    				jsonData = jsonData + line;
	    			}
	            } catch (IOException ex) {
	            	ex.printStackTrace();
	            } finally {
	            	try {
	            		r.close();
	            	} catch (IOException ex) {
	            		ex.printStackTrace();
	            	}
	            }
	            data = new JSONObject(jsonData);
	            Log.i("i-am-science", "Data: " + data.toString());
	            JSONArray appList = data.getJSONArray("apps");
	            for (int i = 0; i < appList.length(); i++) {
	            	JSONObject a = appList.getJSONObject(i);
	            	int icon = R.drawable.nervous;
	            	if (a.has("icon")) {
	            		icon = getResources().getIdentifier(a.getString("icon"), "drawable", getPackageName());
	            	}
	            	apps.add(new ScienceApp(a.getString("id"), a.getString("name"), icon));
	            }
	            return true;
	        } catch (IOException ex) {
	        	ex.printStackTrace();
	        } catch (JSONException ex) {
	        	ex.printStackTrace();
	        }
	        return false;
	    }

	    @Override
	    protected void onPostExecute(Boolean result) {
	    	super.onPostExecute(result);
            appAdapter.addAllApps(apps);
	    	refreshGui();
	    }

	}

}
