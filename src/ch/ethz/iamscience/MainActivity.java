package ch.ethz.iamscience;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

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
				Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("ch.ethz.nervous");
                startActivity(LaunchIntent);
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
