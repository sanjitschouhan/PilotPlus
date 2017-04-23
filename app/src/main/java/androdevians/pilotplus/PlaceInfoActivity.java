package androdevians.pilotplus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PlaceInfoActivity extends AppCompatActivity {
    long id = 0;
    String title = "";
    TextView info;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        info = (TextView) findViewById(R.id.info);
        imageView = (ImageView) findViewById(R.id.img);
        id = getIntent().getExtras().getLong("id");

        String url = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&pageids=" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
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
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

        url = "https://en.wikipedia.org/w/api.php?action=query&pageids=" + id + "&prop=images&format=json";

        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject query = response.getJSONObject("query")
                                    .getJSONObject("pages")
                                    .getJSONObject(String.valueOf(id));
                            JSONArray images = query.getJSONArray("images");
                            String title="'";
                            for (int i = 0; i < images.length(); i++) {
                                title = images.getJSONObject(i).getString("title");
                                if (!title.endsWith(".svg") && !title.endsWith(".ogg")) {
                                    break;
                                }
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
                                                Glide.with(PlaceInfoActivity.this).load(url1).into(imageView);
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
        requestQueue.add(jsonObjectRequest1);
    }

    private void parseJson(String json) {
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), "UTF-8"));
            readData(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView TitleTextView = (TextView) findViewById(R.id.title);
        TitleTextView.setText(title);
    }

    private void readData(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("query")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    if (reader.nextName().equals("pages")) {
                        reader.beginObject();
                        if (reader.nextName().equals(String.valueOf(id))) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String name = reader.nextName();
                                if (name.equals("title")) {
                                    title = reader.nextString();
                                } else if (name.equals("revisions")) {
                                    reader.beginArray();
                                    while (reader.hasNext()) {
                                        reader.beginObject();
                                        while (reader.hasNext()) {
                                            if (reader.nextName().equals("*")) {
                                                parseInfo(reader.nextString());
                                            } else {
                                                reader.skipValue();
                                            }
                                        }
                                        reader.endObject();
                                    }
                                    reader.endArray();
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                        } else {
                            reader.skipValue();
                        }


                        reader.endObject();
                    } else {
                        reader.skipValue();
                    }
                    reader.endObject();
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void parseInfo(String s) {
        s = s.replace("[", "").replace("]", "").replace("'''", "").replaceAll("<ref>[.]*</ref>", "");
        s = s.trim();
        info.setText(s.split("\n\n")[1].split("\\.")[0]);
    }
}
