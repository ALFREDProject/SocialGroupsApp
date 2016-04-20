package eu.alfred.socialgroupsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import eu.alfred.socialgroupsapp.model.Group;

public class CreateGroupActivity extends FragmentActivity {

    private Button createGroupButton;
    private EditText subjectEditText, descriptionEditText;
    private RequestQueue requestQueue;
    private String userId;
    private String reqURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/groups/new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = preferences.getString("id", "");
        requestQueue = Volley.newRequestQueue(this);

        subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        createGroupButton = (Button) findViewById(R.id.createGroupButton);

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectEditText.getText().toString().isEmpty()) { subjectEditText.setError(getResources().getString(R.string.subject_input_error)); }
                else { createNewGroup(userId, subjectEditText.getText().toString(), descriptionEditText.getText().toString()); }
            }
        });
    }

    private void createNewGroup(final String userID, final String subject, final String description ) {

        Gson gson = new Gson();
        Group newGroup = new Group(description, subject, userID);
        final String json = gson.toJson(newGroup);

        //final String json = "{\"description\": \"" + description + "\" ,\"name\": \"" + subject + "\" ,\"userID\": \""+ userID + "\"}";
        Log.d("JSON: ", json);

        StringRequest request = new StringRequest(Request.Method.POST, reqURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.v("Response:%n %s", response);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.toString());
            }
        })
            {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError { return json.getBytes(); }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "*/*");
                return params;
            }

        };

        requestQueue.add(request);
    }
}
