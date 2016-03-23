package eu.alfred.socialgroupsapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.alfred.socialgroupsapp.adapter.RecyclerAdapter;
import eu.alfred.socialgroupsapp.model.Group;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private SearchView searchView;
    private RequestQueue requestQueue;
    private List<Group> myGroups = new ArrayList<Group>();
    private RecyclerView groupsRecyclerview;
    private String reqURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/users/56e6ad24e4b0fadc1367b665/groups";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        getMyGroups();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createGroupIntent = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(createGroupIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        final MenuItem searchItem = menu.findItem(R.id.toolbar_search);

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
/**
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.toolbar_search:
                //handleMenuSearch();
                break;
            case R.id.toolbar_add:
                Intent createGroupIntent = new Intent(this, CreateGroupActivity.class);
                startActivity(createGroupIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
**/

    public void getMyGroups() {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, reqURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject group = response.getJSONObject(i);
                        myGroups.add(new Group(group.getString("description"), group.getString("name"), group.getString("userID")));
                        Log.d("Group added: ", group.toString());
                    }

                    groupsRecyclerview = (RecyclerView) findViewById(R.id.groupsRecyclerView);
                    RecyclerAdapter adapter = new RecyclerAdapter(getApplicationContext(), myGroups);
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


}

