package com.michaelpalmer.travelpicker;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.michaelpalmer.travelpicker.countries.Country;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

/**
 * Asynchronous task to perform API calls with
 *
 * API Sandbox:
 * https://en.wikipedia.org/wiki/Special:ApiSandbox#action=query&format=json&prop=extracts&indexpageids=1&titles=United+States&exsentences=3&exintro=1&explaintext=1
 *
 */
public class API extends AsyncTask<Object, Void, HashMap<String, String>> {
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
    protected HashMap<String, String> doInBackground(Object... data) {
        HashMap<String, String> result = new HashMap<>();
        context = (Context) data[0];
        holder = (CountryListActivity.SimpleItemRecyclerViewAdapter.ViewHolder) data[1];
        countryItem = holder.mItem;

        String extractUrl = String.format(
                Locale.US,
                "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts|info&titles=%s" +
                        "&exintro=1&explaintext=1&inprop=url",
                countryItem.getQuery()
        );

        String imageUrl = String.format(
                Locale.US,
                "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=imageinfo&indexpageids=1" +
                        "&titles=File:Flag of %s.svg|File:Flag of the %s.svg&iiprop=url&iilimit=1&iiurlwidth=500",
                countryItem.getQuery(), countryItem.getQuery()
        );

        result.put("extract", GET(extractUrl));
        result.put("image", GET(imageUrl));

        return result;
    }

    /**
     * Perform a GET request for the specified URL.
     *
     * @param url URL to request
     * @return String response or null
     */
    @Nullable
    private String GET(String url) {
        String s, response = "";
        try {
            // Create connection
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            // Check response code
            if (conn.getResponseCode() != 200) {
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
            while ((s = buffer.readLine()) != null) {
                response += s;
            }

            return response;

        } catch (Exception e) {
            Log.e("API", "Error encountered while sending API request: " + e.getMessage());
        }

        return null;
    }

    /**
     * Process the response
     *
     * @param result String response from the API
     */
    @Override
    protected void onPostExecute(HashMap<String, String> result) {
        // Check for cancellations and a valid result
        if (!isCancelled() && result != null) {
            // Process extract data
            String extractData = result.get("extract");
            if (extractData != null) {
                processExtractData(extractData);
            }

            // Process image data
            String imageData = result.get("image");
            if (imageData != null) {
                processImageData(imageData);
            }
        }
    }

    /**
     * Process the extract data
     *
     * @param data String of JSON data
     */
    private void processExtractData(String data) {
        try {
            // Get extract
            JSONObject extractJson = new JSONObject(data);
            JSONObject pages = extractJson.getJSONObject("query").getJSONObject("pages");
            JSONObject page = pages.getJSONObject(pages.names().getString(0));

            // Get the title and extract
            String title = page.getString("title");
            String extract = page.getString("extract");
            String url = page.getString("fullurl");

            // Set country name and summary
            countryItem.setName(title);
            countryItem.setDetails(extract);
            countryItem.setUrl(url);
            holder.mCountryName.setText(title);
            holder.mCountryDetails.setText(extract);
        } catch (JSONException e) {
            Log.e("API", "JSON error encountered while processing data: " + e.getMessage());
        }
    }

    /**
     * Process the image data
     *
     * @param data String of JSON data
     */
    private void processImageData(String data) {
        try {
            String image = null;

            // Get base object
            JSONObject jsonObject = new JSONObject(data);

            // Get the list page ids
            JSONArray pageIds = jsonObject.getJSONObject("query").getJSONArray("pageids");

            // Get object containing pages
            JSONObject pages = jsonObject.getJSONObject("query").getJSONObject("pages");

            // Loop through page ids
            for (int i = 0; i < pageIds.length(); i++) {
                String pageId = pageIds.getString(i);

                // Get the current page
                JSONObject currentPage = pages.getJSONObject(pageId);

                // Check if this page has an imageinfo field
                if (currentPage.has("imageinfo")) {
                    // Get the image info object
                    JSONArray imageinfo = currentPage.getJSONArray("imageinfo");

                    // Pick the first image
                    JSONObject thumbnail = imageinfo.getJSONObject(0);

                    // Get the url
                    image = thumbnail.getString("thumburl");

                    // Exit the loop
                    break;
                }

            }

            // Set the image if one was found
            if (image != null) {
                countryItem.setImage(image);

                // Get image
                Picasso.with(context)
                        .load(countryItem.getImage())
                        .placeholder(android.R.drawable.picture_frame)
                        .into(holder.mCountryPicture);
            }

        } catch (JSONException e) {
            Log.e("API", "JSON error encountered while processing data: " + e.getMessage());
        }
    }
}