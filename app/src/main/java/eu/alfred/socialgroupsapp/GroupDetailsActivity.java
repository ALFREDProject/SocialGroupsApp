package eu.alfred.socialgroupsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Set;

import eu.alfred.api.PersonalAssistant;
import eu.alfred.api.PersonalAssistantConnection;
import eu.alfred.api.personalization.webservice.PersonalizationManager;
import eu.alfred.socialgroupsapp.helper.PersonalizationStringResponse;

//----------- due to work around within getGroupDetails

//__________
public class GroupDetailsActivity extends FragmentActivity {

    private TextView sizeOfGroupTextView, groupDescriptionTextView;
    private Button joinOrLeaveButton;
	private String groupID, userId;
	private boolean isAMember, isAnOwner = false;
    final Context context = this;
    private PersonalAssistant PA;

    private final static String TAG = "SGA:GroupDetailsAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = preferences.getString("id", "");

        sizeOfGroupTextView = (TextView) findViewById(R.id.sizeOfGroupTextView);
        groupDescriptionTextView = (TextView) findViewById(R.id.groupDescriptionTextView);
        joinOrLeaveButton = (Button) findViewById(R.id.joinOrLeaveButton);

        groupID = getIntent().getStringExtra("GroupID");

        PA = new PersonalAssistant(this);

        PA.setOnPersonalAssistantConnectionListener(new PersonalAssistantConnection() {
            @Override
            public void OnConnected() {
                Log.i(TAG, "PersonalAssistantConnection connected");

                if (groupID != null) {
                    Log.d("Group ID", groupID);
                    getGroupDetails(groupID);
                }
            }

            @Override
            public void OnDisconnected() {
                Log.i(TAG, "PersonalAssistantConnection disconnected");
            }
        });

        PA.Init();


        joinOrLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAMember) {
                    joinToGroup();
                }
                if (isAMember && !isAnOwner) {
                    leaveGroup();
                }
                if (isAnOwner && isAMember) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.cannot_leave_group);
                    builder.setMessage(R.string.you_are_owner);
                    /*
                    builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    */
                    builder.setPositiveButton("Delete Group", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteGroup();
                        }
                    });

                    builder.setNegativeButton("Leave Group", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            leaveGroup();
                        }
                    });
                    builder.create().show();
                }
            }
        });
    }

    public void getGroupDetails(String id) {
/*
        PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());

        PM.retrieveGroup(id, new PersonalizationObjectResponse() {
            @Override
            public void OnSuccess(JSONObject jsonObject) {

                Log.i(TAG, "retrieveGroup succeeded");
                Log.i("groupjson", jsonObject.toString());

                Type type = new TypeToken<GroupDto>() {}.getType();
                GroupDto dto = new Gson().fromJson(jsonObject.toString(), type);

                Group group = GroupMapper.toModel(dto);
                Log.i(TAG, "retrieved " + group);

	            isAGroupMember(userId, group.getMemberIds());
                isAGroupOwner(userId, group.getUserID());

                Log.d(TAG, "Created at " + group.getCreationDate());
                if (group.getName() != null) {
                    setTitle(group.getName());
                }
                if (group.getDescription() != null) {
                    groupDescriptionTextView.setText(group.getDescription());
                }
                sizeOfGroupTextView.setText(getString(R.string.size_of_group) + " " + group.getMemberIds().size());
                if (isAMember || isAnOwner) {
                    joinOrLeaveButton.setText(getString(R.string.leave_group));
                }
            };
        });
    */
        String reqURL;
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);

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
                    if (isAMember) { joinOrLeaveButton.setText("Leave this Group"); }
                    if (isAnOwner) { joinOrLeaveButton.setText("Delete or Leave this Group"); }
                    if (!isAMember) { joinOrLeaveButton.setText("Join this Group"); }
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

    public void goBackToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.putExtra("Groups", "");
        startActivity(mainIntent);
    }

    public void joinToGroup() {

	    PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());

        PM.addMemberToGroup(groupID, userId, new PersonalizationStringResponse() {
            @Override
            public void OnSuccess(String s) {
                Log.i(TAG, "addMemberToGroup succeeded: " + s);
/*                getGroupDetails(groupID);
                joinOrLeaveButton.setText(R.string.leave);
*/
                goBackToMainActivity();
            }
        });
    }

    public void leaveGroup() {

	    PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());

        PM.removeMemberFromGroup(groupID, userId, new PersonalizationStringResponse() {
            @Override
            public void OnSuccess(String s) {
                Log.i(TAG, "removeMemberToGroup succeeded: " + s);
/*                getGroupDetails(groupID);
                joinOrLeaveButton.setText(R.string.join);
*/
                goBackToMainActivity();
            }
        });
    }

    public void deleteGroup() {

        PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());

        PM.deleteGroup(groupID, new PersonalizationStringResponse() {
            @Override
            public void OnSuccess(String s) {
                Log.i(TAG, "delete of group succeeded: " + s);
                goBackToMainActivity();

            }
        });
//        finish();
     }

    public void isAGroupOwner(String userId, String ownerId) {
        isAnOwner = userId.contentEquals(ownerId);
    }

    public void isAGroupMember(String userId, Set<String> members) {
	    isAMember = members.contains(userId);
    }

    public void isAGroupMember(String userId, String[] members) {
        if (Arrays.asList(members).contains(userId)) { isAMember = true; }
        else { isAMember = false; }
    }

}
