package eu.alfred.socialgroupsapp;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateGroupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button createGroupButton;
    private EditText subjectEditText, descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create a new group!");

        subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        createGroupButton = (Button) findViewById(R.id.createGroupButton);

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectEditText.getText().toString().isEmpty()) { subjectEditText.setError(getResources().getString(R.string.subject_input_error)); }
                //else { subjectEditText.setErrorEnabled(false); }
            }
        });

    }



    /**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    } **/
}
