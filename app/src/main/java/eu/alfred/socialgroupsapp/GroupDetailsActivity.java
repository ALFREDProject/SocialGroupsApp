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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Set;

import eu.alfred.api.PersonalAssistant;
import eu.alfred.api.personalization.client.GroupDto;
import eu.alfred.api.personalization.client.GroupMapper;
import eu.alfred.api.personalization.model.Group;
import eu.alfred.api.personalization.webservice.PersonalizationManager;
import eu.alfred.socialgroupsapp.helper.PersonalizationObjectResponse;
import eu.alfred.socialgroupsapp.helper.PersonalizationStringResponse;

public class GroupDetailsActivity extends FragmentActivity {

    private TextView sizeOfGroupTextView, groupDescriptionTextView;
    private Button joinOrLeaveButton;
	private String groupID;
	private String userId;
	private boolean isAMember, isAnOwner = false;
    final Context context = this;

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
                    builder.setTitle(R.string.cannot_leave_group);
                    builder.setMessage(R.string.you_are_owner);
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

        PersonalAssistant PA = PersonalAssistantProvider.getPersonalAssistant(this);
        PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());

        PM.retrieveGroup(id, new PersonalizationObjectResponse() {
            @Override
            public void OnSuccess(JSONObject jsonObject) {

                Log.i(TAG, "retrieveGroup succeeded");

                Type type = new TypeToken<GroupDto>() {}.getType();
                GroupDto dto = new Gson().fromJson(jsonObject.toString(), type);

                Group group = GroupMapper.toModel(dto);

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
            }
        });
    }

    public void joinToGroup() {

	    PersonalAssistant PA = PersonalAssistantProvider.getPersonalAssistant(this);
	    PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());

	    PM.addMemberToGroup(groupID, userId, new PersonalizationStringResponse() {
		    @Override
		    public void OnSuccess(String s) {
			    Log.i(TAG, "addMemberToGroup succeeded: " + s);
			    getGroupDetails(groupID);
			    joinOrLeaveButton.setText(R.string.leave);
		    }
	    });
    }

    public void leaveGroup() {

	    PersonalAssistant PA = PersonalAssistantProvider.getPersonalAssistant(this);
	    PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());

	    PM.removeMemberFromGroup(groupID, userId, new PersonalizationStringResponse() {
		    @Override
		    public void OnSuccess(String s) {
			    Log.i(TAG, "removeMemberToGroup succeeded: " + s);
			    getGroupDetails(groupID);
			    joinOrLeaveButton.setText(R.string.join);
		    }
	    });
    }

    public void isAGroupOwner(String userId, String ownerId) {
        isAnOwner = userId.contentEquals(ownerId);
    }

    public void isAGroupMember(String userId, Set<String> members) {
	    isAMember = members.contains(userId);
    }
}
