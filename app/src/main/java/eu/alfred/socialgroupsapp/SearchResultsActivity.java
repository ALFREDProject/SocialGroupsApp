package eu.alfred.socialgroupsapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.alfred.socialgroupsapp.model.Group;

public class SearchResultsActivity extends FragmentActivity {

    private ListView searchResultsListView;
    private Map<String, String> searchResults = new LinkedHashMap<String, String>();
    private List<String> groupNames = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private RequestQueue requestQueue;
    private String reqURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/groups/retrieve";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        requestQueue = Volley.newRequestQueue(this);
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

        String query = getIntent().getStringExtra("Query");
        if (query != null) {
            Log.d("Query Intent Received", query);
            getSearchResults(query);
        }

    }

    public void getSearchResults(String query){

        final String requestString = "{\"name\": \"" + query + "\"}";
        Log.d("request", requestString);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, reqURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        Log.d("onResponse", "works!");
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject group = response.getJSONObject(i);
                            searchResults.put(group.getString("id"), group.getString("name"));
                            groupNames.add(group.getString("name"));
                            Log.d("Group", group.toString());
                        }
                        searchResultsListView.setAdapter(adapter);
                    }
                    //VolleyLog.v("Response:%n %s", response.toString());
                } catch (JSONException e) { e.printStackTrace(); }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.toString());
            }
        })
        {
            @Override
            public String getBodyContentType()  {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() { return requestString.getBytes(); }

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
