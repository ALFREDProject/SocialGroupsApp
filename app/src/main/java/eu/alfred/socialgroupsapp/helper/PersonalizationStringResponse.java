package eu.alfred.socialgroupsapp.helper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import eu.alfred.api.personalization.responses.PersonalizationResponse;

/**
 * Created by robert.lill on 19.04.16.
 */
public abstract class PersonalizationStringResponse implements PersonalizationResponse {

	private final static String TAG = "PMSResponse";

	@Override
	public void OnSuccess(JSONObject jsonObject) {
		Log.e(TAG, "Unexpected JSONObject response");
	}

	@Override
	public void OnSuccess(JSONArray jsonArray) {
		Log.e(TAG, "Unexpected JSONArray response");
	}

	@Override
	public void OnSuccess(Object object) {
		Log.e(TAG, "Unexpected Object response");
	}

	@Override
	public void OnError(Exception e) {
		Log.e(TAG, e.getClass().getSimpleName() + ": " + e.getMessage());
	}
}
