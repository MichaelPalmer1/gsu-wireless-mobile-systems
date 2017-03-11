package com.michaelpalmer.travelpicker;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.michaelpalmer.travelpicker.countries.Country;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Country detail screen.
 * This fragment is either contained in a {@link CountryListActivity}
 * in two-pane mode (on tablets) or a {@link CountryDetailActivity}
 * on handsets.
 */
public class CountryDetailFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Country.CountryItem mItem;

    private EditText notes;
    private RatingBar rating;

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

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.name);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.country_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.country_detail)).setText(mItem.details);
            rating = (RatingBar) rootView.findViewById(R.id.rating);
            notes = (EditText) rootView.findViewById(R.id.notes);
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.detail_country_picture);
            Button btnYes = (Button) rootView.findViewById(R.id.btn_yes);
            Button btnNo = (Button) rootView.findViewById(R.id.btn_no);
            btnYes.setOnClickListener(this);
            btnNo.setOnClickListener(this);

            notes.setText(mItem.notes);
            rating.setRating(mItem.rating);

            // Get image
            Picasso.with(getActivity())
                    .load(mItem.image)
                    .into(imageView);
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        mItem.notes = notes.getText().toString();
        mItem.rating = rating.getRating();
        super.onDestroyView();
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
                break;
        }
    }
}
