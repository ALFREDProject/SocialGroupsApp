package eu.alfred.socialgroupsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GroupDetailsActivity extends AppCompatActivity {

    private TextView sizeOfGroupTextView, groupDescriptionTextView;
    private Button joinOrLeaveButton;
    private RequestQueue requestQueue;
    private String reqURL, groupID;
    private boolean isAMember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        requestQueue = Volley.newRequestQueue(this);
        sizeOfGroupTextView = (TextView) findViewById(R.id.sizeOfGroupTextView);
        groupDescriptionTextView = (TextView) findViewById(R.id.groupDescriptionTextView);

        groupID = getIntent().getStringExtra("GroupID");
        if (groupID != null) {
            Log.d("Group ID", groupID);
            getGroupDetails(groupID);
        }

    }

    public void getGroupDetails(String id) {
        reqURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/groups/" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, reqURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray memberIdsJson = response.getJSONArray("memberIds");
                    String[] memberIds = new String[memberIdsJson.length()];
                    for (int i = 0; i < memberIdsJson.length(); i++) {
                        memberIds[i] = memberIdsJson.getString(i);
                    }
                    if (response.getString("name") != null) { setTitle(response.getString("name")); }
                    if (response.getString("description") != null) { groupDescriptionTextView.setText(response.getString("description")); }
                    sizeOfGroupTextView.setText("Size of group: " + memberIdsJson.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    



}
