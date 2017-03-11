package com.michaelpalmer.travelpicker.countries;

import com.michaelpalmer.travelpicker.countries.africa.Egypt;
import com.michaelpalmer.travelpicker.countries.africa.Ethiopia;
import com.michaelpalmer.travelpicker.countries.africa.Kenya;
import com.michaelpalmer.travelpicker.countries.africa.Morocco;
import com.michaelpalmer.travelpicker.countries.africa.Nigeria;
import com.michaelpalmer.travelpicker.countries.asia.China;
import com.michaelpalmer.travelpicker.countries.asia.India;
import com.michaelpalmer.travelpicker.countries.asia.Japan;
import com.michaelpalmer.travelpicker.countries.asia.Russia;
import com.michaelpalmer.travelpicker.countries.asia.South_Korea;
import com.michaelpalmer.travelpicker.countries.europe.Belgium;
import com.michaelpalmer.travelpicker.countries.europe.France;
import com.michaelpalmer.travelpicker.countries.europe.Germany;
import com.michaelpalmer.travelpicker.countries.europe.Italy;
import com.michaelpalmer.travelpicker.countries.europe.Spain;
import com.michaelpalmer.travelpicker.countries.north_america.Canada;
import com.michaelpalmer.travelpicker.countries.north_america.Cuba;
import com.michaelpalmer.travelpicker.countries.north_america.Mexico;
import com.michaelpalmer.travelpicker.countries.north_america.Panama;
import com.michaelpalmer.travelpicker.countries.north_america.United_States;
import com.michaelpalmer.travelpicker.countries.south_america.Argentina;
import com.michaelpalmer.travelpicker.countries.south_america.Brazil;
import com.michaelpalmer.travelpicker.countries.south_america.Chile;
import com.michaelpalmer.travelpicker.countries.south_america.Columbia;
import com.michaelpalmer.travelpicker.countries.south_america.Ecuador;

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

    private static void addItem(CountryItem item) {
        ITEMS.add(item);
        String pkg = item.getClass().getPackage().getName();
        if (!CONTINENTS.containsKey(pkg) || CONTINENTS.get(pkg) <= 2) {
            VISIBLE_ITEMS.add(item);
        }
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A country item representing a piece of content.
     */
    public static class CountryItem {
        public final String id;
        public final String query = getClass().getSimpleName();
        public String name = "";
        public String details = "";
        public String notes = "";
        public String image = "";
        public float rating = 0;

        public CountryItem() {
            // Get package name (continent)
            String pkg = getClass().getPackage().getName();

            // Initialize new continents
            if (!CONTINENTS.containsKey(pkg)) {
                CONTINENTS.put(pkg, 0);
            }

            // Create an id
            int num = CONTINENTS.get(pkg);
            id = String.format(Locale.US, "%s-%d", pkg, ++num);
            CONTINENTS.put(pkg, num);
        }

        public void setTitle(String title) {
            this.name = title;
        }

        public void setContent(String content) {
            this.details = content;
        }

        public void setImage(String image) {
            this.image = image;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
