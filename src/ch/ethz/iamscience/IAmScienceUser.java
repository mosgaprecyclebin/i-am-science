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
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class IAmScienceUser {

	private static String serverUrl = "http://streaming.coenosense.com:8092/get_i_am_science_data?uid=<UID>";
//	private static String serverUrl = "https://raw.github.com/mosgap/i-am-science/master/src/ch/ethz/iamscience/data-example.json?uid=<UID>";

	private static String updateUrl = "http://streaming.coenosense.com:8092/update?uid=<UID>&appid=<APPID>&score=<SCORE>";

	private static IAmScienceUser user;

	public static IAmScienceUser get(Context context) {
		if (user == null) {
			user = new IAmScienceUser(context);
		}
		return user;
	}

	private Context context;
	private String userId;
	private JSONObject data;

	protected IAmScienceUser(Context context) {
		this.context = context;
        try {
        	File dir = new File(Environment.getExternalStorageDirectory(), "i-am-science");
        	dir.mkdir();
        	File file = new File(dir, "userid");
        	if (file.exists()) {
        		BufferedReader r = new BufferedReader(new FileReader(file));
        		userId = r.readLine();
        		r.close();
        	} else {
        		userId = Math.abs(new Random().nextLong()) + "";
        		BufferedWriter w = new BufferedWriter(new FileWriter(file));
        		w.write(userId + "\n");
        		w.close();
        	}
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
	}

	public String getId() {
		return userId;
	}

	public JSONObject getData() {
		return data;
	}

	public void updateData() {
        try {
        	String urlString = serverUrl.replaceFirst("<UID>", userId);
            URL url = new URL(urlString);
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
        } catch (IOException ex) {
        	ex.printStackTrace();
        } catch (JSONException ex) {
        	ex.printStackTrace();
        }
	}

	public void newScore(int score) {
        try {
        	String urlString = updateUrl.replaceFirst("<UID>", userId)
        			.replaceFirst("<APPID>", context.getPackageName())
        			.replaceFirst("<SCORE>", score + "");
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            (new InputStreamReader(urlConnection.getInputStream())).close();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        updateData();
	}

	public int getTotalScore() {
		try {
            JSONArray appList = data.getJSONArray("apps");
            int score = 0;
            for (int i = 0; i < appList.length(); i++) {
            	JSONObject a = appList.getJSONObject(i);
            	score += a.getInt("score");
            }
            return score;
		} catch (Exception ex) {
			return -1;
		}
	}

	public int getScore() {
		try {
            JSONArray appList = data.getJSONArray("apps");
            for (int i = 0; i < appList.length(); i++) {
            	JSONObject a = appList.getJSONObject(i);
            	if (a.getString("id").equals(context.getPackageName())) {
            		return a.getInt("score");
            	}
            }
		} catch (Exception ex) {}
		return 0;
	}

}
