package com.michaelpalmer.travelpicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.michaelpalmer.travelpicker.countries.Country;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Country detail screen.
 * This fragment is either contained in a {@link CountryListActivity}
 * in two-pane mode (on tablets) or a {@link CountryDetailActivity}
 * on handsets.
 */
public class CountryDetailFragment extends Fragment implements View.OnClickListener, Callback {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Country.CountryItem mItem;

    private ImageView countryPicture;
    private EditText notes;
    private RatingBar rating;

    private CollapsingToolbarLayout appBarLayout;

    private CountryDatabase db;

    public interface RecyclerViewUpdateListener {
        void onRecyclerViewItemUpdated();
        void onRecyclerViewItemRemoved(int index);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CountryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the country content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = Country.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            db = new CountryDatabase(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.country_detail, container, false);

        if (appBarLayout == null) {
            appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
        }

        if (mItem != null) {
            appBarLayout.setTitle(mItem.getName());
            ((TextView) rootView.findViewById(R.id.country_detail)).setText(mItem.getDetails());
            rating = (RatingBar) rootView.findViewById(R.id.rating);
            notes = (EditText) rootView.findViewById(R.id.notes);
            countryPicture = (ImageView) getActivity().findViewById(R.id.detail_country_picture);
            Button btnYes = (Button) rootView.findViewById(R.id.btn_yes);
            Button btnNo = (Button) rootView.findViewById(R.id.btn_no);
            Button btnViewOnWikipedia = (Button) rootView.findViewById(R.id.btn_view_on_wikipedia);
            btnYes.setOnClickListener(this);
            btnNo.setOnClickListener(this);
            btnViewOnWikipedia.setOnClickListener(this);

            Country.CountryItem dbCountry = db.getCountry(mItem.getId());
            if (dbCountry != null) {
                mItem.setRating(dbCountry.getRating());
                mItem.setNotes(dbCountry.getNotes());
            }

            notes.setText(mItem.getNotes());
            rating.setRating(mItem.getRating());

            // Get image
            Picasso.with(getActivity())
                    .load(mItem.getImage())
                    .into(countryPicture, this);
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mItem.setNotes(notes.getText().toString());
        mItem.setRating(rating.getRating());
        db.insertOrUpdateCountry(mItem);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }

    /**
     * Generate palette colors for the app bar after Picasso loads the parallax image.
     */
    @Override
    public void onSuccess() {
        Bitmap bitmap = ((BitmapDrawable) countryPicture.getDrawable()).getBitmap();
        Palette palette = Palette.from(bitmap)
                .generate();

        Log.d("Country", mItem.getName());
        Log.d("Palette", "Dominant Swatch: " + palette.getDominantSwatch());
        Log.d("Palette", "Vibrant Swatch: " + palette.getVibrantSwatch());
        Log.d("Palette", "Muted Swatch: " + palette.getMutedSwatch());
        Log.d("Palette", "Dark Vibrant Swatch: " + palette.getDarkVibrantSwatch());
        Log.d("Palette", "Dark Muted Swatch: " + palette.getDarkMutedSwatch());
        Log.d("Palette", "Light Vibrant Swatch: " + palette.getLightVibrantSwatch());
        Log.d("Palette", "Light Muted Swatch: " + palette.getLightMutedSwatch());

        if (appBarLayout != null) {
            if (palette.getDarkVibrantSwatch() != null) {
                Log.d("Palette", "Using dark vibrant swatch for content.");
                appBarLayout.setContentScrimColor(palette.getDarkVibrantSwatch().getRgb());
                appBarLayout.setCollapsedTitleTextColor(palette.getDarkVibrantSwatch().getTitleTextColor());
            } else if (palette.getLightVibrantSwatch() != null) {
                Log.d("Palette", "Using light vibrant swatch for content.");
                appBarLayout.setContentScrimColor(palette.getLightVibrantSwatch().getRgb());
                appBarLayout.setCollapsedTitleTextColor(palette.getLightVibrantSwatch().getTitleTextColor());
            } else if (palette.getVibrantSwatch() != null) {
                Log.d("Palette", "Using vibrant swatch for content.");
                appBarLayout.setContentScrimColor(palette.getVibrantSwatch().getRgb());
                appBarLayout.setCollapsedTitleTextColor(palette.getVibrantSwatch().getTitleTextColor());
            } else if (palette.getDominantSwatch() != null) {
                Log.d("Palette", "Using dominant swatch for content.");
                appBarLayout.setContentScrimColor(palette.getDominantSwatch().getRgb());
                appBarLayout.setCollapsedTitleTextColor(palette.getDominantSwatch().getTitleTextColor());
            } else {
                Log.d("Palette", "No swatch for content");
            }

            if (palette.getDarkMutedSwatch() != null) {
                Log.d("Palette", "Using dark muted swatch for status bar.");
                appBarLayout.setStatusBarScrimColor(palette.getDarkMutedSwatch().getRgb());
            } else if (palette.getLightMutedSwatch() != null) {
                Log.d("Palette", "Using light muted swatch for status bar.");
                appBarLayout.setStatusBarScrimColor(palette.getLightMutedSwatch().getRgb());
            } else if (palette.getMutedSwatch() != null) {
                Log.d("Palette", "Using muted swatch for status bar.");
                appBarLayout.setStatusBarScrimColor(palette.getMutedSwatch().getRgb());
            } else if (palette.getDominantSwatch() != null) {
                Log.d("Palette", "Using dominant swatch for status bar.");
                appBarLayout.setStatusBarScrimColor(palette.getDominantSwatch().getRgb());
            } else {
                Log.d("Palette", "No swatch for status bar.");
            }
        }
    }

    /**
     * Called when Picasso failed to load the image
     */
    @Override
    public void onError() {
        // Do nothing
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_no:
                for (Country.CountryItem item: Country.ITEMS) {
                    if (item.getClass().getPackage().getName().equals(mItem.getClass().getPackage().getName())
                            && Country.VISIBLE_ITEMS.contains(item)) {
                        int index = Country.VISIBLE_ITEMS.indexOf(item);
                        Country.VISIBLE_ITEMS.remove(item);
                        CountryListActivity.mRecyclerViewListener.onRecyclerViewItemRemoved(index);
                    }
                }
                getActivity().finish();
                break;

            case R.id.btn_yes:
                for (Country.CountryItem item: Country.ITEMS) {
                    if (item.getClass().getPackage().getName().equals(mItem.getClass().getPackage().getName())) {
                        if (!Country.VISIBLE_ITEMS.contains(item)) {
                            Country.VISIBLE_ITEMS.add(item);
                            CountryListActivity.mRecyclerViewListener.onRecyclerViewItemUpdated();
                        }
                    }
                }
                getActivity().finish();
                break;

            case R.id.btn_view_on_wikipedia:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mItem.getUrl())));
                break;
        }
    }
}
