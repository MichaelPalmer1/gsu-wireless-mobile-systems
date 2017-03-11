package com.michaelpalmer.travelpicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.michaelpalmer.travelpicker.countries.Country;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * An activity representing a list of Countries. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CountryDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CountryListActivity extends AppCompatActivity implements CountryDetailFragment.RecyclerViewUpdateListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;

    public static CountryDetailFragment.RecyclerViewUpdateListener mRecyclerViewListener;

    @Override
    public void onRecyclerViewItemUpdated() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onRecyclerViewItemRemoved(int index) {
        recyclerView.getAdapter().notifyItemRemoved(index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        mRecyclerViewListener = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.country_list);
        assert recyclerView != null;
        this.recyclerView = (RecyclerView) recyclerView;

        setupRecyclerView(this.recyclerView);

        if (findViewById(R.id.country_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reset:
                Country.VISIBLE_ITEMS.clear();
                Country.VISIBLE_ITEMS.addAll(Country.ITEMS);
                this.onRecyclerViewItemUpdated();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Country.VISIBLE_ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Country.CountryItem> mValues;

        SimpleItemRecyclerViewAdapter(List<Country.CountryItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.country_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mCountryName.setText(mValues.get(position).name);

            // Download details
            if (holder.mItem.details.equals("") || holder.mItem.image.equals("")) {
                new API().execute(getBaseContext(), holder, mValues.get(position));
            } else {
                Picasso.with(getBaseContext())
                        .load(holder.mItem.image)
                        .placeholder(android.R.drawable.picture_frame)
                        .into(holder.mCountryPicture);

                holder.mCountryDetails.setText(holder.mItem.name);
                holder.mCountryDetails.setText(holder.mItem.details);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CountryDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        CountryDetailFragment fragment = new CountryDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.country_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CountryDetailActivity.class);
                        intent.putExtra(CountryDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mCountryName;
            public final TextView mCountryDetails;
            public final ImageView mCountryPicture;
            public Country.CountryItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mCountryName = (TextView) view.findViewById(R.id.list_country_name);
                mCountryDetails = (TextView) view.findViewById(R.id.list_country_details);
                mCountryPicture = (ImageView) view.findViewById(R.id.list_country_picture);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mCountryDetails.getText() + "'";
            }
        }
    }



}
