package com.michaelpalmer.travelpicker.countries;

import com.michaelpalmer.travelpicker.countries.africa.*;
import com.michaelpalmer.travelpicker.countries.asia.*;
import com.michaelpalmer.travelpicker.countries.europe.*;
import com.michaelpalmer.travelpicker.countries.north_america.*;
import com.michaelpalmer.travelpicker.countries.south_america.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by Android template wizards.
 */
public class Country {

    /**
     * An array of country items.
     */
    public static final List<CountryItem> ITEMS = new ArrayList<>();

    public static final List<CountryItem> VISIBLE_ITEMS = new ArrayList<>();

    private static final Map<String, Integer> CONTINENTS = new HashMap<>();

    /**
     * A map of country items, by ID.
     */
    public static final Map<String, CountryItem> ITEM_MAP = new HashMap<>();

    static {
        // North America
        addItem(new United_States());
        addItem(new Mexico());
        addItem(new Canada());
        addItem(new Panama());
        addItem(new Cuba());

        // South America
        addItem(new Brazil());
        addItem(new Chile());
        addItem(new Ecuador());
        addItem(new Argentina());
        addItem(new Columbia());

        // Europe
        addItem(new Germany());
        addItem(new France());
        addItem(new Spain());
        addItem(new Belgium());
        addItem(new Italy());

        // Africa
        addItem(new Egypt());
        addItem(new Nigeria());
        addItem(new Kenya());
        addItem(new Morocco());
        addItem(new Ethiopia());

        // Asia
        addItem(new China());
        addItem(new Russia());
        addItem(new Japan());
        addItem(new India());
        addItem(new South_Korea());
    }

    public static void resetContinent(String pkgName) {
        for (CountryItem item : ITEMS) {
            String pkg = item.getClass().getPackage().getName();
            if (pkg.equals(pkgName) && !VISIBLE_ITEMS.contains(item)) {
                VISIBLE_ITEMS.add(item);
            }
        }
    }

    private static void addItem(CountryItem item) {
        ITEMS.add(item);
        String pkg = item.getClass().getPackage().getName();
        if (!CONTINENTS.containsKey(pkg) || CONTINENTS.get(pkg) <= 2) {
            VISIBLE_ITEMS.add(item);
        }
        ITEM_MAP.put(item.getId(), item);
    }

    /**
     * A country item representing a piece of content.
     */
    public static class CountryItem {
        private final String id;
        private final String query = getClass().getSimpleName();
        private String name = "";
        private String details = "";
        private String notes = "";
        private String image = "";
        private String url = "";
        private float rating = 0;

        public CountryItem() {
            this(true);
        }

        public CountryItem(boolean generateId) {
            if (generateId) {
                // Get package name (continent)
                String pkg = getClass().getPackage().getName();

                // Initialize new continents
                if (!CONTINENTS.containsKey(pkg)) {
                    CONTINENTS.put(pkg, 0);
                }

                // Create an id
                int num = CONTINENTS.get(pkg);
                id = String.format(Locale.US, "%s-%s", pkg, query.toLowerCase());
                CONTINENTS.put(pkg, ++num);
            } else {
                id = "dummy-from-db";
            }
        }

        public CountryItem(String id) {
            this.id = id;
        }

        public boolean isPopulated() {
            return !name.equals("") && !details.equals("") && !image.equals("");
        }

        public String getId() {
            return id;
        }

        public String getQuery() {
            return query;
        }

        public String getName() {
            return name;
        }

        public String getDetails() {
            return details;
        }

        public String getNotes() {
            return notes;
        }

        public String getImage() {
            return image;
        }

        public float getRating() {
            return rating;
        }

        public String getUrl() {
            return url;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
