package ch.ethz.iamscience;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScienceAppAdapter extends BaseAdapter {

	private List<JSONObject> apps = new ArrayList<JSONObject>();
	private LayoutInflater inflater;
	private Activity activity;

	public ScienceAppAdapter(Activity activity) {
		this.activity = activity;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addApp(JSONObject app) {
		apps.add(app);
	}

	public void setApps(JSONArray appArray) {
		apps.clear();
        for (int i = 0; i < appArray.length(); i++) {
    		try {
    			apps.add(appArray.getJSONObject(i));
    		} catch (JSONException ex) {
    			ex.printStackTrace();
    		}
        }
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return apps.size();
	}

	@Override
	public JSONObject getItem(int position) {
		return apps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		JSONObject app = apps.get(position);
		TextView view = (TextView) inflater.inflate(R.layout.app_element, parent, false);
		String name = "...";
		try {
			if (app.has("name")) {
				name = app.getString("name");
			} else {
				name = app.getString("id");
			}
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		view.setText(name);
		view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.app, 0, 0, 0);
		try {
			GetLogoTask getLogoTask = new GetLogoTask();
			getLogoTask.execute(app.getString("logo"), view);
		} catch (JSONException ex) {}
		return view;
	}

	private class GetLogoTask extends AsyncTask<Object,Void,Void> {

		Drawable logo;
		TextView view;

		protected Void doInBackground(Object... objs) {
			String urlString = (String) objs[0];
			view = (TextView) objs[1];
			try {
				URL logoUrl = new URL(urlString);
				InputStream s = logoUrl.openConnection().getInputStream();
				logo = new BitmapDrawable(activity.getResources(), s);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

	    @Override
	    protected void onPostExecute(Void result) {
	    	if (logo != null) {
				view.setCompoundDrawablesWithIntrinsicBounds(logo, null, null, null);
	    	}
	    }

	}

}
