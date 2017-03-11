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

/**
 * Asynchronous task to perform API calls with
 *
 * API Sandbox:
 * https://en.wikipedia.org/wiki/Special:ApiSandbox#action=query&format=json&prop=extracts|pageimages&titles=United+States&exsentences=3&exintro=1&explaintext=1&piprop=thumbnail&pithumbsize=500
 *
 */
public class API extends AsyncTask<Object, Void, String> {
    private Context context;
    private CountryListActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder;
    private Country.CountryItem countryItem;

    /**
     * Perform the API call
     *
     * @param data Object array: {Context, ViewHolder}
     * @return API Response as string
     */
    @Override
    protected String doInBackground(Object... data) {
        String s, response = "";
        context = (Context) data[0];
        holder = (CountryListActivity.SimpleItemRecyclerViewAdapter.ViewHolder) data[1];
        countryItem = holder.mItem;

        // TODO: Sending a POST request would be better than this nasty url
        String url = String.format(
                Locale.US,
                "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts|pageimages&titles=%s" +
                        "&exsentences=3&exintro=1&explaintext=1&piprop=thumbnail&pithumbsize=500",
                countryItem.getQuery()
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
            Log.e("API", "Error encountered while sending API request: " + e.getMessage());
        }
        return response;
    }

    /**
     * Process the response
     *
     * Sample:
     *
     * {
     *      "batchcomplete": "",
     *      "query": {
     *          "pages": {
     *              "3434750": {
     *                  "pageid": 3434750,
     *                  "ns": 0,
     *                  "title": "United States",
     *                  "extract": "The United States of America, commonly referred to as the United States (U.S.) or
     *                              America, is a constitutional federal republic composed of 50 states, a federal
     *                              district, five major self-governing territories, and various possessions.
     *                              Forty-eight of the fifty states and the federal district are contiguous and located
     *                              in North America between Canada and Mexico. The state of Alaska is in the northwest
     *                              corner of North America, bordered by Canada to the east and across the Bering Strait
     *                              from Russia to the west.",
     *                  "thumbnail": {
     *                      "source": "https://upload.wikimedia.org/wikipedia/en/thumb/a/a4/Flag_of_the_United_Statessvg/50px-Flag_of_the_United_States.svg.png",
     *                      "width": 50,
     *                      "height": 26
     *                  },
     *                  "pageimage": "Flag_of_the_United_States.svg"
     *              }
     *          }
     *      }
     *  }
     *
     *
     * @param result String response from the API
     */
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

                countryItem.setName(title);
                countryItem.setDetails(content);
                countryItem.setImage(image);

                // Set country name
                holder.mCountryName.setText(title);
                holder.mCountryDetails.setText(content);

                // Get image
                Picasso.with(context)
                        .load(countryItem.getImage())
                        .placeholder(android.R.drawable.picture_frame)
                        .into(holder.mCountryPicture);

            } catch (JSONException e) {
                Log.e("API", "JSON error encountered while processing data: " + e.getMessage());
            }

        }
    }
}