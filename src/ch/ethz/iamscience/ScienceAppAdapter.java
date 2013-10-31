package ch.ethz.iamscience;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScienceAppAdapter extends BaseAdapter {

	private List<JSONObject> apps = new ArrayList<JSONObject>();
	private LayoutInflater inflater;

	public ScienceAppAdapter(Context context) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		TextView v = (TextView) inflater.inflate(R.layout.app_element, parent, false);
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
		v.setText(name);
		v.setCompoundDrawablesWithIntrinsicBounds(R.drawable.nervous, 0, 0, 0);
		return v;
	}

}
