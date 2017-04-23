package androdevians.pilotplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PlacesOfInterestActivity extends AppCompatActivity {
    ArrayList<PlaceOfInterest> placeOfInterests;
    PlaceOfInterestAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_of_interest);
        placeOfInterests = new ArrayList<>();
        adapter = new PlaceOfInterestAdapter(this, placeOfInterests);
        listView = (ListView) findViewById(R.id.places_of_interest_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaceOfInterest placeOfInterest = placeOfInterests.get(position);
                Intent intent = new Intent(PlacesOfInterestActivity.this, PlaceInfoActivity.class);
                Bundle extras = new Bundle();
                extras.putLong("id", placeOfInterest.getId());
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

//        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&type=point_of_interest&radius=500&key=AIzaSyAgxSYBqKSkY9j5nlzfbfQb78-P30CuaVA";
        String url = "https://en.wikipedia.org/w/api.php?action=query&list=geosearch&gscoord=%f|%f&gsradius=10000&gslimit=10&format=json";

        // Log.e("fsk", String.format(url, MainActivity.loc.getLatitude(), MainActivity.loc.getLongitude()));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                String.format(url, MainActivity.loc.getLatitude(), MainActivity.loc.getLongitude()),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseJson(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void parseJson(String json) {
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), "UTF-8"));
            readItemsWiki(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    private void readItemsWiki(JsonReader reader) throws IOException {
        float lat = 0;
        float lon = 0;
        long id = 0;
        String name = "";
        Log.e("readItemsWiki: ", "jfsk");
        reader.beginObject();
        while (reader.hasNext()) {

            if (reader.nextName().equals("query")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    if (reader.nextName().equals("geosearch")) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String name1 = reader.nextName();
                                if (name1.equals("pageid")) {
                                    id = reader.nextLong();
                                } else if (name1.equals("title")) {
                                    name = reader.nextString();
                                } else if (name1.equals("lat")) {
                                    lat = (float) reader.nextDouble();
                                } else if (name1.equals("lon")) {
                                    lon = (float) reader.nextDouble();
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                            placeOfInterests.add(new PlaceOfInterest(lat, lon, id, name));
                            Log.e("readItemsWiki: ", new PlaceOfInterest(lat, lon, id, name).toString());
                        }
                        reader.endArray();
                    }
                }
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void readItems(JsonReader reader) throws IOException {
        float lat = 0;
        float lon = 0;
        String icon = "";
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("results")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name1 = reader.nextName();
                        if (name1.equals("geometry")) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String name2 = reader.nextName();
                                if (name2.equals("location")) {
                                    reader.beginObject();
                                    reader.nextName();
                                    lat = (float) reader.nextDouble();
                                    reader.nextName();
                                    lon = (float) reader.nextDouble();
                                    reader.endObject();
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                        } else if (name1.equals("icon")) {
                            icon = reader.nextString();
                        } else if (name1.equals("name")) {
                            name = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
//                    PlaceOfInterest placeOfInterest = new PlaceOfInterest(lat, lon, icon, name);
//                    placeOfInterests.add(placeOfInterest);
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }
}
