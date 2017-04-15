package com.michaelpalmer.stargazer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ImageAdapter.LikesListener {

    private RecyclerView recyclerView;
    public static List<GalleryImage> ITEMS = null;
    public static ArrayList<Integer> LIKES = null;

    public static ImageAdapter.LikesListener mLikesListener;

    @Override
    public void onLikesUpdated() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLikesListener = this;

        // Initialize the recycler view
        View recyclerView = findViewById(R.id.recycler_view);
        assert recyclerView != null;
        this.recyclerView = (RecyclerView) recyclerView;
        setupRecyclerView(this.recyclerView);

        // Download data from NASA
        new NasaAPI().execute();
    }

    /**
     * Save like data
     *
     * @param outState Instance state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("likes", LIKES);
    }

    /**
     * Restore like data
     *
     * @param savedInstanceState Instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Integer> savedLikes = savedInstanceState.getIntegerArrayList("likes");
        if (savedLikes != null) {
            LIKES = savedInstanceState.getIntegerArrayList("likes");
        }
    }

    /**
     * Setup the recycler view
     *
     * @param recyclerView Recycler view
     */
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Only set adapter if items is not null
        if (ITEMS != null) {
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ITEMS));
        }
    }

    class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<GalleryImage> mValues;

        SimpleItemRecyclerViewAdapter(List<GalleryImage> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);

            // Download details
            Picasso.with(getBaseContext())
                    .load(holder.mItem.getUrl())
                    .fit()
                    .placeholder(R.drawable.ic_loading)
                    .into(holder.mImageView);

            // Show thumbs up/down conditionally
            switch (holder.mItem.getLike()) {
                case GalleryImage.LIKE:
                    holder.mLikeView.setVisibility(View.VISIBLE);
                    holder.mLikeView.setImageResource(R.drawable.ic_thumb_up_white_24px);
                    break;

                case GalleryImage.DISLIKE:
                    holder.mLikeView.setVisibility(View.VISIBLE);
                    holder.mLikeView.setImageResource(R.drawable.ic_thumb_down_white_24px);
                    break;

                default:
                    holder.mLikeView.setVisibility(View.GONE);
            }

            // Open detail view when clicked
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("item_id", holder.getAdapterPosition());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final ImageView mImageView;
            final ImageView mLikeView;
            GalleryImage mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.image_view);
                mLikeView = (ImageView) view.findViewById(R.id.like_view);
            }
        }
    }


    private class NasaAPI extends AsyncTask<Void, Void, List<GalleryImage>> {

        private static final String TAG = "NasaApi";
        private static final String API_KEY = "rQ8VmVCh5xMJWr9auvdEa6ZxTK5sWLZbzd6xkpDY";

        /**
         * Perform the API call
         *
         * @param params Parameters
         * @return API Response as string
         */
        @Override
        protected List<GalleryImage> doInBackground(Void... params) {
            return fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryImage> items) {
            // Generate like/dislike array
            if (LIKES == null) {
                ArrayList<Integer> intArray = new ArrayList<Integer>();
                for (int i = 0; i < items.size(); i++) {
                    intArray.add(0);
                }
                LIKES = intArray;
            }

            // Update items
            if (ITEMS == null) {
                ITEMS = items;
            }

            // Set adapter
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ITEMS));
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

        private List<GalleryImage> fetchItems() {
            List<GalleryImage> items = new ArrayList<>();

            // Build API url
            String url = Uri.parse("https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos")
                    .buildUpon()
                    .appendQueryParameter("sol", "1000")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("page", "1")
                    .build()
                    .toString();

            // Fetch the data
            String jsonString = GET(url);
            Log.i(TAG, "Received JSON: " + jsonString);

            // Parse the data
            parseItems(items, jsonString);

            return items;
        }

        private void parseItems(List<GalleryImage> items, String data) {
            try {
                // Get base object
                JSONObject jsonBaseObject = new JSONObject(data);

                // Get the photos array
                JSONArray photos = jsonBaseObject.getJSONArray("photos");

                for (int i = 0; i < photos.length(); i++) {
                    // Get photo object
                    JSONObject photo = photos.getJSONObject(i);

                    // Get relevant data
                    int id = photo.getInt("id");
                    String url = photo.getString("img_src");

                    // Instantiate gallery image and add to list
                    GalleryImage item = new GalleryImage(id, url);
                    items.add(item);
                }
            } catch (JSONException e) {
                Log.e("API", "JSON error encountered while processing data: " + e.getMessage());
            }
        }
    }

}
