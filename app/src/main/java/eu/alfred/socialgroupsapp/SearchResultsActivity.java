package eu.alfred.socialgroupsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.alfred.api.PersonalAssistant;
import eu.alfred.api.PersonalAssistantConnection;
import eu.alfred.api.personalization.client.GroupDto;
import eu.alfred.api.personalization.client.GroupMapper;
import eu.alfred.api.personalization.model.Group;
import eu.alfred.api.personalization.webservice.PersonalizationManager;
import eu.alfred.socialgroupsapp.helper.PersonalizationArrayResponse;

public class SearchResultsActivity extends FragmentActivity {

    private ListView searchResultsListView;
    private Map<String, String> searchResults = new LinkedHashMap<String, String>();
    private List<String> groupNames = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private PersonalAssistant PA;

    private final static String TAG = "SGA:SearchResultsAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        searchResultsListView = (ListView) findViewById(R.id.searchResultsListView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, groupNames);

        searchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent groupDetailsIntent = new Intent(getApplicationContext(), GroupDetailsActivity.class);
                String groupID = (new ArrayList<String>(searchResults.keySet())).get(position);
                Log.d("GroupID", groupID);
                groupDetailsIntent.putExtra("GroupID", groupID);
                startActivity(groupDetailsIntent);
            }
        });

        final String query = getIntent().getStringExtra("Query");

        PA = new PersonalAssistant(this);
        PA.setOnPersonalAssistantConnectionListener(new PersonalAssistantConnection() {
            @Override
            public void OnConnected() {
                Log.i(TAG, "PersonalAssistantConnection connected");
                getSearchResults(query);
/*
                if (query != null) {
                    Log.d("Query Intent Received", query);
                    getSearchResults(query);
                }
*/
            }

            @Override
            public void OnDisconnected() {
                Log.i(TAG, "PersonalAssistantConnection disconnected");
            }
        });

        PA.Init();
    }

    public void getSearchResults(String query) {

        final String requestString = "{\"name\": \"" + query + "\"}";
        Log.d("request", requestString);

        PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());
        PM.retrieveFilteredGroups(requestString, new PersonalizationArrayResponse() {
            @Override
            public void OnSuccess(JSONArray jsonArray) {
                Log.i(TAG, "retrieveFilteredGroups succeeded");
/*
                Type type = new TypeToken<ArrayList<GroupDto>>() {
                }.getType();
                List<GroupDto> dto = new Gson().fromJson(jsonArray.toString(), type);

                for (GroupDto cd : dto) {
                    Group group = GroupMapper.toModel(cd);

                    searchResults.put(group.getId(), group.getName());
                    groupNames.add(group.getName());
                    Log.d(TAG, group.toString());
                }

                searchResultsListView.setAdapter(adapter);
                */
                collectSearchResults(jsonArray);
            }
            @Override
            public void OnError(Exception e) {
                Log.e(TAG, e.getClass().getSimpleName() + ": " + e.getMessage());
                getAllSearchResults();
            }

        });
    }

    private void getAllSearchResults() {
        PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());
        PM.retrieveAllGroups(new PersonalizationArrayResponse() {
            @Override
            public void OnSuccess(JSONArray jsonArray) {
                Log.i(TAG, "retrieveAllGroups succeeded");
                collectSearchResults(jsonArray);
            }
        });
    }

    private void collectSearchResults(JSONArray jsonArray) {
        Type type = new TypeToken<ArrayList<GroupDto>>() {
        }.getType();
        List<GroupDto> dto = new Gson().fromJson(jsonArray.toString(), type);

        for (GroupDto cd : dto) {
            Group group = GroupMapper.toModel(cd);

            searchResults.put(group.getId(), group.getName());
            groupNames.add(group.getName() + ", " + group.getDescription());
            Log.d(TAG, group.toString());
        }

        searchResultsListView.setAdapter(adapter);

    }
}
