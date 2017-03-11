# Travel Picker

## Android versions
Android 5.0 - Lollipop (API 21) or greater

## Permissions
This app requires internet access to pull the country data from [Wikipedia](https://en.wikipedia.org).

## Data Retrieval
Country data and images are retrieved via the [Wikipedia API](https://en.wikipedia.org/w/api.php). Images are
downloaded using the [Picasso](http://square.github.io/picasso/) library. Once downloaded, images are then processed 
using Google's [Palette](https://developer.android.com/training/material/palette-colors.html) API to determine the best 
scrim colors to use in the app bar layout. In addition, each country detail screen includes a button to view the 
information on Wikipedia.

## Features
- Country data pulled from Wikipedia
- Rate and make notes about countries
- Data saved to a SQLite database when app is closed
- Hide countries from continents that are not of interest
- Re-display all countries from a continent by selecting the corresponding option from the menu
- Switches to a grid layout when 4 or fewer countries are displayed

## Supported Countries

### North America
- Canada
- Cuba
- Mexico
- Panama
- United States

### South America
- Argentina
- Brazil
- Chile
- Columbia
- Equador

### Europe
- Beligum
- France
- Germany
- Italy
- Spain

### Africa
- Egypt
- Ethiopia
- Kenya
- Morocco
- Nigeria

### Asia
- China
- India
- Japan
- Russia
- South Korea
