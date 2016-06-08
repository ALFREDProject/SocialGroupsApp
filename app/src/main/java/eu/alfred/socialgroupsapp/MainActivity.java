package eu.alfred.socialgroupsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

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
import eu.alfred.api.proxies.interfaces.ICadeCommand;
import eu.alfred.socialgroupsapp.adapter.RecyclerAdapter;
import eu.alfred.socialgroupsapp.helper.PersonalizationArrayResponse;
import eu.alfred.ui.AppActivity;
import eu.alfred.ui.CircleButton;

public class MainActivity extends AppActivity implements ICadeCommand {

    private SearchView searchView;
    private LinkedHashMap<String, Group> myGroups = new LinkedHashMap<String, Group>();
    private Context context = this;
    private RecyclerView groupsRecyclerview;
    private MenuItem searchItem;
	private SharedPreferences preferences;
    private PersonalAssistant PA;

    final static String CREATE_SOCIAL_GROUP = "CreateSocialGroupAction";
    final static String SEARCH_SOCIAL_GROUP = "SearchSocialGroupAction";

    private final static String TAG = "SGA:MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        PA = new PersonalAssistant(this);

        PA.setOnPersonalAssistantConnectionListener(new PersonalAssistantConnection() {
            @Override
            public void OnConnected() {
                Log.i(TAG, "PersonalAssistantConnection connected");
                getMyGroups();
            }

            @Override
            public void OnDisconnected() {
                Log.i(TAG, "PersonalAssistantConnection disconnected");
            }
        });

        PA.Init();

        circleButton = (CircleButton) findViewById(R.id.voiceControlBtn);
        circleButton.setOnTouchListener(new MicrophoneTouchListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        searchItem = menu.findItem(R.id.toolbar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchItem.collapseActionView();
                Intent searchQueryIntent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                searchQueryIntent.putExtra("Query", query);
                Log.d(TAG, "Search: " + query);
                startActivity(searchQueryIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_refresh:
                myGroups.clear();
                getMyGroups();
                return true;
            case R.id.toolbar_create:
                Intent createGroupIntent = new Intent(this, CreateGroupActivity.class);
                startActivity(createGroupIntent);
                return true;
            case R.id.toolbar_logout:
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Intent goToLoginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goToLoginIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getMyGroups() {

        PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());

        PM.retrieveAllGroups(new PersonalizationArrayResponse() {
            @Override
            public void OnSuccess(JSONArray jsonArray) {
	            Log.i(TAG, "retrieveAllGroups succeeded");

	            Type type = new TypeToken<ArrayList<GroupDto>>() {}.getType();
	            List<GroupDto> dto = new Gson().fromJson(jsonArray.toString(), type);

	            for (GroupDto cd : dto) {
		            Group group = GroupMapper.toModel(cd);
		            myGroups.put(group.getId(), group);
		            Log.d(TAG, "Group added: " + group.toString());

	            }

	            groupsRecyclerview = (RecyclerView) findViewById(R.id.groupsRecyclerView);
	            RecyclerAdapter adapter = new RecyclerAdapter(context, myGroups);
	            groupsRecyclerview.setAdapter(adapter);

	            LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getApplicationContext());
	            mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
	            groupsRecyclerview.setLayoutManager(mLinearLayoutManagerVertical);

	            groupsRecyclerview.setItemAnimator(new DefaultItemAnimator());

            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void performAction(String s, Map<String, String> map) {
	    Log.e(TAG, "performAction(" + s + ", " + map + ")");
        switch (s) {
            case CREATE_SOCIAL_GROUP:
                Intent alfredGroupIntent = new Intent(this, CreateGroupActivity.class);
                String groupName = map.get("selected_groupname");
                alfredGroupIntent.putExtra("GroupName", groupName);
                if(map.containsKey("selected_groupdescription")) {
                    String groupDescription = map.get("selected_groupdescription");
                    alfredGroupIntent.putExtra("GroupDescription", groupDescription);
                }
                Log.d(TAG, "DDD Response: " + map.toString());
                Log.d(TAG, "DDD Response 2:" + groupName);
                startActivity(alfredGroupIntent);
                break;
            case SEARCH_SOCIAL_GROUP:
                Log.d(TAG, "Search Group works!");
                break;
            default:
                break;
        }

        cade.sendActionResult(true);
    }

    @Override
    public void performWhQuery(String s, Map<String, String> map) {
	    Log.e(TAG, "performWhQuery(" + s + ", " + map + ")");
        cade.sendActionResult(true);
    }

    @Override
    public void performValidity(String s, Map<String, String> map) {
	    Log.e(TAG, "performValidity(" + s + ", " + map + ")");
    }

    @Override
    public void performEntityRecognizer(String s, Map<String, String> map) {
	    Log.e(TAG, "performEntityRecognizer(" + s + ", " + map + ")");
    }
}

