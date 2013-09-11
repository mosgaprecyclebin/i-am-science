package ch.ethz.iamscience;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScienceAppAdapter extends BaseAdapter {

	private List<ScienceApp> apps = new ArrayList<ScienceApp>();
	private LayoutInflater inflater;

	public ScienceAppAdapter(Context context) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addApp(ScienceApp app) {
		apps.add(app);
	}

	public void addAllApps(Collection<ScienceApp> newApps) {
		apps.addAll(newApps);
	}

	@Override
	public int getCount() {
		return apps.size();
	}

	@Override
	public ScienceApp getItem(int position) {
		return apps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ScienceApp app = apps.get(position);
		TextView v = (TextView) inflater.inflate(R.layout.app_element, parent, false);
		v.setText(app.getName());
		v.setCompoundDrawablesWithIntrinsicBounds(app.getDrawable(), 0, 0, 0);
		return v;
	}

}
