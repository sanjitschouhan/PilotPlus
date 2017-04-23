package androdevians.pilotplus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class CultureActivity extends AppCompatActivity {
    ListView imageListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture);
        imageListView = (ListView) findViewById(R.id.image_list);


        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://en.wikipedia.org/w/api.php?action=query&titles=Culture_of_" + MainActivity.adminArea + "&prop=revisions&rvprop=content&format=json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("query")
                                    .getJSONObject("pages");
                            Iterator<String> keys = jsonObject.keys();
                            final String id = keys.next();
                            String url = "https://en.wikipedia.org/w/api.php?action=query&pageids=" + id + "&prop=images&format=json";

                            JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(
                                    Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                JSONObject query = response.getJSONObject("query")
                                                        .getJSONObject("pages")
                                                        .getJSONObject(id);
                                                final JSONArray images = query.getJSONArray("images");
                                                String title = "'";
                                                for (int i = 0; i < images.length(); i++) {
                                                    title = images.getJSONObject(i).getString("title");
                                                    if (title.endsWith(".svg") || title.endsWith(".ogg")) {
                                                        continue;
                                                    }
                                                    title = title.replace(" ", "_");
                                                    String url = "https://en.wikipedia.org/w/api.php?action=query&titles=" + title + "&prop=imageinfo&iiprop=url&format=json";

                                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                                            Request.Method.GET, url, null,
                                                            new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    try {
                                                                        JSONArray jsonArray = response.getJSONObject("query")
                                                                                .getJSONObject("pages")
                                                                                .getJSONObject("-1")
                                                                                .getJSONArray("imageinfo");
                                                                        String url1 = jsonArray.getJSONObject(0).getString("url");
                                                                        ImageView imageView = new ImageView(CultureActivity.this);
                                                                        imageListView.addView(imageView);
                                                                        Glide.with(CultureActivity.this)
                                                                                .load(url1)
                                                                                .into(imageView);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                }
                                                            });
                                                    requestQueue.add(jsonObjectRequest);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                        }
                                    });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        requestQueue.add(jsonObjectRequest);

    }
}
