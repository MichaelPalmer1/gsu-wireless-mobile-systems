package com.michaelpalmer.stargazer;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


class ImageAdapter extends PagerAdapter {
    private Context context;

    ImageAdapter(Context context){
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, final int position) {
        // Create image view
        ImageView imageView = new ImageView(context);

        // Load image
        Picasso.with(context)
                .load(MainActivity.ITEMS.get(position).getUrl())
                .placeholder(R.drawable.ic_loading)
                .into(imageView);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            private float down = 0.0f, up = 0.0f;

            public boolean onTouch(View v, MotionEvent event) {
                switch(MotionEventCompat.getActionMasked(event)) {
                    case (MotionEvent.ACTION_DOWN):
                        down = event.getAxisValue(MotionEvent.AXIS_Y);
                        break;

                    case (MotionEvent.ACTION_UP):
                        up = event.getAxisValue(MotionEvent.AXIS_Y);
                        if (down > up && Math.abs(down - up) > 200) {
                            // Swiping up - like
                            Log.d("Gesture", "Swiped up " + Math.abs(down - up));
                            MainActivity.ITEMS.get(position).setLike(GalleryImage.LIKE);
                            MainActivity.LIKES.set(position, GalleryImage.LIKE);
                            MainActivity.mLikesListener.onLikesUpdated();
                            Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();

                        } else if (down < up && Math.abs(down - up) > 200) {
                            // Swiping down - dislike
                            Log.d("Gesture", "Swiped down " + Math.abs(down - up));
                            MainActivity.ITEMS.get(position).setLike(GalleryImage.DISLIKE);
                            MainActivity.LIKES.set(position, GalleryImage.DISLIKE);
                            MainActivity.mLikesListener.onLikesUpdated();
                            Toast.makeText(context, "Disliked", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });

        // Add to the view group
        viewGroup.addView(imageView, 0);

        return imageView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals((ImageView) object);
    }

    @Override
    public int getCount() {
        return MainActivity.ITEMS.size();
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int position, Object object) {
        viewGroup.removeView((ImageView) object);
    }

    interface LikesListener {
        void onLikesUpdated();
    }
}