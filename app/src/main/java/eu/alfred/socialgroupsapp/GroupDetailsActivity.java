package eu.alfred.socialgroupsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class GroupDetailsActivity extends FragmentActivity {

    private TextView sizeOfGroupTextView, groupDescriptionTextView;
    private Button joinOrLeaveButton;
    private RequestQueue requestQueue;
    private String reqURL, groupID, userId, tempUserId;
    private boolean isAMember, isAnOwner = false;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = preferences.getString("id", "");
        tempUserId = "\"" + userId + "\"";

        requestQueue = Volley.newRequestQueue(this);
        sizeOfGroupTextView = (TextView) findViewById(R.id.sizeOfGroupTextView);
        groupDescriptionTextView = (TextView) findViewById(R.id.groupDescriptionTextView);
        joinOrLeaveButton = (Button) findViewById(R.id.joinOrLeaveButton);

        groupID = getIntent().getStringExtra("GroupID");
        if (groupID != null) {
            Log.d("Group ID", groupID);
            getGroupDetails(groupID);
        }

        joinOrLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAMember) { joinToGroup(); }
                if (isAMember && !isAnOwner) { leaveGroup(); }
                if (isAnOwner) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("You can not leave!");
                    builder.setMessage("You are the owner of this group...");
                    builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }
            }
        });

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
                    isAGroupMember(userId, memberIds);
                    isAGroupOwner(userId, response.getString("userID"));
                    Log.d("Created at", response.getString("creationDate"));
                    if (response.getString("name") != null) { setTitle(response.getString("name")); }
                    if (response.getString("description") != null) { groupDescriptionTextView.setText(response.getString("description")); }
                    sizeOfGroupTextView.setText("Size of group: " + memberIdsJson.length());
                    if (isAMember || isAnOwner) { joinOrLeaveButton.setText("Leave this Group"); }
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

    public void joinToGroup() {
        reqURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/groups/" + groupID + "/addMember";

        StringRequest request = new StringRequest(Request.Method.PUT, reqURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getGroupDetails(groupID);
                joinOrLeaveButton.setText("Leave");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.getMessage());
            }
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError { return tempUserId.getBytes(); }
        };

        requestQueue.add(request);
    }

    public void leaveGroup() {
        reqURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/groups/" + groupID + "/removeMember";

        StringRequest request = new StringRequest(Request.Method.PUT, reqURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getGroupDetails(groupID);
                joinOrLeaveButton.setText("Join");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.getMessage());
            }
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError { return tempUserId.getBytes(); }
        };

        requestQueue.add(request);
    }

    public void isAGroupOwner(String userId, String ownerId) {
        if ((userId.contentEquals(ownerId))) isAnOwner = true;
        else isAnOwner = false;
    }

    public void isAGroupMember(String userId, String[] members) {
        if (Arrays.asList(members).contains(userId)) { isAMember = true; }
        else { isAMember = false; }
    }







}
