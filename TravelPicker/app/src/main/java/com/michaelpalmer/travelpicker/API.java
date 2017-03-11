package com.michaelpalmer.travelpicker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.michaelpalmer.travelpicker.countries.Country;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class API extends AsyncTask<Object, Void, String> {
    private Context context;
    private CountryListActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder;
    private Country.CountryItem values;

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... data) {
        String s, response = "";
        context = (Context) data[0];
        holder = (CountryListActivity.SimpleItemRecyclerViewAdapter.ViewHolder) data[1];
        values = (Country.CountryItem) data[2];

        String url = String.format(
                Locale.US,
                "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts|pageimages&titles=%s&exsentences=3&exintro=1&explaintext=1",
                values.query
        );

        try {
            // Create connection
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            // Check response code
            if(conn.getResponseCode() != 200) {
                throw new Exception(
                        String.format(Locale.US,
                                "Could not get data from remote source. HTTP response: %s (%d)",
                                conn.getResponseMessage(), conn.getResponseCode()
                        )
                );
            }

            // Save response to a string
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
            while ((s = buffer.readLine()) != null)
                response += s;

        } catch (Exception e) {
            Log.e("DownloadJSON_Status", "Error encountered while downloading JSON");
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        // Check for cancellations
        if ( isCancelled() )
            result = null;

        // Check result
        if (result != null && result.length() > 0) {

            try {
                JSONObject json = new JSONObject(result);
                JSONObject pages = json.getJSONObject("query").getJSONObject("pages");
                JSONObject page = pages.getJSONObject(pages.names().getString(0));

                String title = page.getString("title");
                String content = page.getString("extract");
                String image = page.getJSONObject("thumbnail").getString("source");

                values.setTitle(title);
                values.setContent(content);
                values.setImage(image);

                // Set country name
                holder.mCountryName.setText(title);
                holder.mCountryDetails.setText(content);

                // Get image
                Picasso.with(context)
                        .load(image)
                        .placeholder(android.R.drawable.picture_frame)
                        .into(holder.mCountryPicture);

            } catch (JSONException e) {
                Log.e("CountryListActivity", "JSON Exception: " + e.toString());
                e.printStackTrace();
            }

        }
    }
}