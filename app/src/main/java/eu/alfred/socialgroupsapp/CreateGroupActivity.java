package eu.alfred.socialgroupsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

import eu.alfred.api.PersonalAssistant;
import eu.alfred.api.PersonalAssistantConnection;
import eu.alfred.api.personalization.model.Group;
import eu.alfred.api.personalization.webservice.PersonalizationManager;
import eu.alfred.socialgroupsapp.helper.PersonalizationStringResponse;

public class CreateGroupActivity extends FragmentActivity {

	private EditText subjectEditText, descriptionEditText;
	private String userId;
	private PersonalAssistant PA;

	private final static String TAG = "SGA:CreateGroupAct";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		userId = preferences.getString("id", "");
		Log.d("UserID", userId);

		subjectEditText = (EditText) findViewById(R.id.subjectEditText);
		descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		Button createGroupButton = (Button) findViewById(R.id.createGroupButton);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			subjectEditText.setText(extras.getString("GroupName"));
			descriptionEditText.setText(extras.getString("GroupDescription"));
		}

		createGroupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (subjectEditText.getText().toString().isEmpty()) {
					subjectEditText.setError(getResources().getString(R.string.subject_input_error));
				} else {
					createNewGroup(userId, subjectEditText.getText().toString(), descriptionEditText.getText().toString());
				}
			}
		});
	}

	public void goBackToMainActivity() {
		Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
		mainIntent.putExtra("Groups", "");
		startActivity(mainIntent);
	}

	private void createNewGroup(final String userID, final String subject, final String description) {

		PA = new PersonalAssistant(this);

		PA.setOnPersonalAssistantConnectionListener(new PersonalAssistantConnection() {
			@Override
			public void OnConnected() {
				Log.i(TAG, "PersonalAssistantConnection connected");

				Set<String> member = new HashSet<String>();
				member.add(userID);

				Group newGroup = new Group();
				newGroup.setDescription(description);
				newGroup.setName(subject);
				newGroup.setUserID(userID);
				newGroup.setMemberIds(member);

				PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());

				PM.createGroup(newGroup, new PersonalizationStringResponse() {
					@Override
					public void OnSuccess(String s) {
						Log.i(TAG, "created group with id " + s);
					}
				});
			}

			@Override
			public void OnDisconnected() {
				Log.i(TAG, "PersonalAssistantConnection disconnected");
			}

		});

		PA.Init();
//		finish();
		goBackToMainActivity();
	}
}
