package eu.alfred.socialgroupsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.alfred.api.proxies.interfaces.ICadeCommand;
import eu.alfred.api.speech.Cade;
import eu.alfred.socialgroupsapp.adapter.RecyclerAdapter;
import eu.alfred.socialgroupsapp.model.Group;
import eu.alfred.ui.AppActivity;
import eu.alfred.ui.CircleButton;

public class MainActivity extends AppActivity implements ICadeCommand {

    private SearchView searchView;
    private RequestQueue requestQueue;
    private LinkedHashMap<String, Group> myGroups = new LinkedHashMap<String, Group>();
    private Context context = this;
    private RecyclerView groupsRecyclerview;
    private MenuItem searchItem;
    private String userId, reqURL;
    private SharedPreferences preferences;

    final static String CREATE_SOCIAL_GROUP = "CreateSocialGroupAction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = preferences.getString("id", "");

        requestQueue = Volley.newRequestQueue(this);
        getMyGroups();

        circleButton = (CircleButton) findViewById(R.id.voiceControlBtn);
        circleButton.setOnTouchListener(new CircleTouchListener());

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
                Log.d("Search: ", query);
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

        reqURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/users/" + userId + "/membergroups";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, reqURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject group = response.getJSONObject(i);

                        JSONArray memberIdsJson = group.getJSONArray("memberIds");
                        String[] memberIds = new String[memberIdsJson.length()];
                        for (int k = 0; k < memberIdsJson.length(); k++) {
                            memberIds[k] = memberIdsJson.getString(k);
                        }

                        myGroups.put(group.getString("id"), new Group(group.getString("description"), group.getString("name"),
                                group.getString("userID"), memberIds, group.getString("creationDate"), group.getString("lastUpdated")));
                        Log.d("Group added: ", group.toString());
                    }

                    groupsRecyclerview = (RecyclerView) findViewById(R.id.groupsRecyclerView);
                    RecyclerAdapter adapter = new RecyclerAdapter(context, myGroups);
                    groupsRecyclerview.setAdapter(adapter);

                    LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getApplicationContext());
                    mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
                    groupsRecyclerview.setLayoutManager(mLinearLayoutManagerVertical);

                    groupsRecyclerview.setItemAnimator(new DefaultItemAnimator());

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

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void performAction(String s, Map<String, String> map) {
        Log.d("Perform Action", "works!");
        Log.d("Perform Action string", s);
        switch (s) {
            case CREATE_SOCIAL_GROUP:
                Log.d("DDD Response", map.toString());
                break;
            default:
                break;
        }

        cade.sendActionResult(true);
    }

    @Override
    public void performWhQuery(String s, Map<String, String> map) {
        Log.d("Wh Query", s);
        cade.sendActionResult(true);
    }

    @Override
    public void performValidity(String s, Map<String, String> map) {
        Log.d("Perform Validity", "works!");
    }

    @Override
    public void performEntityRecognizer(String s, Map<String, String> map) {
        Log.d("Perform Entity Recog", "works!");
    }
}

