package com.michaelpalmer.stargazer;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Create view pager and image adapter
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new ImageAdapter(this));

        // Set the current page
        viewPager.setCurrentItem(getIntent().getIntExtra("item_id", 0));
    }

}
