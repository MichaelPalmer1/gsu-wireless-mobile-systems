package com.michaelpalmer.travelpicker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.michaelpalmer.travelpicker.countries.Country;

import java.util.ArrayList;
import java.util.Locale;


public class CountryDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Countries";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_COUNTRY = "Country";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_NOTES = "notes";

    public CountryDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Initialize the database
     *
     * @param db Database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSql = String.format(Locale.US,
                "CREATE TABLE %s (" +
                    "%s VARCHAR(255) PRIMARY KEY, " +
                    "%s FLOAT, " +
                    "%s TEXT" +
                ")",
                TABLE_COUNTRY, COLUMN_ID, COLUMN_RATING, COLUMN_NOTES
        );
        db.execSQL(createTableSql);
    }

    /**
     * Upgrade the database when there is a version change
     *
     * @param db Database
     * @param oldVersion Old version number
     * @param newVersion New version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRY);
        onCreate(db);
    }

    /**
     * Add a country to the database
     *
     * @param countryItem CountryItem to add
     */
    public void addCountry(Country.CountryItem countryItem) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, countryItem.getId());
        values.put(COLUMN_RATING, countryItem.getRating());
        values.put(COLUMN_NOTES, countryItem.getNotes());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_COUNTRY, null, values);
        db.close();
    }

    /**
     * Get all countries from the database
     *
     * @return ArrayList of CountryItems
     */
    public ArrayList<Country.CountryItem> getAll() {
        ArrayList<Country.CountryItem> rows = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // Perform query
        Cursor cursor = db.query(
                TABLE_COUNTRY,
                null, null, null,
                null, null, null
        );

        // Check for null
        if (cursor == null) {
            return null;
        }

        // Populate results
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            float rating = cursor.getFloat(cursor.getColumnIndex(COLUMN_RATING));
            String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));

            Country.CountryItem countryItem = new Country.CountryItem(id);
            countryItem.setNotes(notes);
            countryItem.setRating(rating);

            rows.add(countryItem);
        }

        // Close connections
        cursor.close();
        db.close();

        return rows;
    }

    /**
     * Clear all data in the database
     */
    public void truncateData() {
        SQLiteDatabase db = getWritableDatabase();

        // Perform query
        db.delete(TABLE_COUNTRY, null, null);

        // Close connection
        db.close();
    }

    /**
     * Get a country from the database
     *
     * @param id Identifier
     * @return CountryItem or null if it does not exist in the database
     */
    public Country.CountryItem getCountry(String id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_COUNTRY,
                new String[] {COLUMN_RATING, COLUMN_NOTES},
                COLUMN_ID + "=?",
                new String[] {id},
                null, null, null
        );

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        // Select first row
        cursor.moveToFirst();

        // Get data
        Country.CountryItem countryItem = new Country.CountryItem(false);
        float rating = cursor.getFloat(cursor.getColumnIndex(COLUMN_RATING));
        String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
        countryItem.setRating(rating);
        countryItem.setNotes(notes);

        // Close connections
        cursor.close();
        db.close();

        return countryItem;
    }

    /**
     * Update existing country in database if it exists, otherwise insert it.
     *
     * @param countryItem CountryItem with data to update/create
     */
    public void insertOrUpdateCountry(Country.CountryItem countryItem) {
        if (getCountry(countryItem.getId()) == null) {
            addCountry(countryItem);
        } else {
            updateCountry(countryItem);
        }
    }


    /**
     * Update a country in the database
     *
     * @param countryItem CountryItem with data to update
     */
    public void updateCountry(Country.CountryItem countryItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RATING, countryItem.getRating());
        values.put(COLUMN_NOTES, countryItem.getNotes());

        // Update the row
        db.update(TABLE_COUNTRY, values, COLUMN_ID + "=?", new String[] {countryItem.getId()});

        // Close connection
        db.close();
    }

    /**
     * Delete a country from the database
     *
     * @param countryItem CountryItem to remove
     */
    public void deleteCountry(Country.CountryItem countryItem) {
        SQLiteDatabase db = getWritableDatabase();

        // Delete the row
        db.delete(TABLE_COUNTRY, COLUMN_ID + "=?", new String[] {countryItem.getId()});

        // Close connection
        db.close();
    }
}
