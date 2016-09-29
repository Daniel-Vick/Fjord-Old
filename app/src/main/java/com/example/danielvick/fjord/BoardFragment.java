package com.example.danielvick.fjord;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by danielvick on 7/8/16.
 */
public class BoardFragment extends Fragment {
    private ArrayAdapter<String> mLeaderboardAdapter;

    public BoardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.leaderboardfragment, menu);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            updatePlaylist();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast




        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mLeaderboardAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_leaderboard, // The name of the layout ID.
                        R.id.list_item_leaderboard_textview, // The ID of the textview to populate.
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_leaderboard);
        listView.setAdapter(mLeaderboardAdapter);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String forecast = mLeaderboardAdapter.getItem(i);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);


            }
        });*/

        return rootView;
    }


    private void updatePlaylist() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();

        weatherTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePlaylist();
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();





        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         * */

        private String[] getWeatherDataFromJson(String tracksJsonStr)
                throws JSONException {


            JSONObject tracksJson = new JSONObject(tracksJsonStr).getJSONObject("tracks");

            JSONArray tracks = tracksJson.getJSONArray("items");

            String[] resultStrs = new String[tracks.length()];
            for(int i = 0; i < tracks.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String Name;
                String Artist;


                // Get the JSON object representing the day
                JSONObject track = tracks.getJSONObject(i);
                JSONObject nameJSON = track.getJSONObject("track");
                JSONObject artistJSON = track.getJSONObject("track").getJSONArray("artists").getJSONObject(0);

                Name = nameJSON.getString("name");
                Artist = artistJSON.getString("name");

                resultStrs[i] = Name + " " + Artist;




            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {


            // If there's no zip code, there's nothing to look up.  Verify size of params.


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                Log.e(LOG_TAG, "#########");
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String url =
                        "https://api.spotify.com/v1/users/moonpie51/playlists/3wSlrUxooIpVbDtsGDjaQ9?market=ES";


                URL obj = new URL(url);
                Log.v(LOG_TAG, "Built URI " + url);
                urlConnection = (HttpURLConnection) obj.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Authorization", "Bearer BQA-ejJnFY6S9MS6gaSaI7IAjTcCm28mWdSIQ8HII2GL9mcq-C9yPnmdqN82HTON2MGFiLIBuBIJiZZxV0h86LzetlQdYd77_8sGgkelQzX0xReQqiwmvVfnS5zt4Koe-UO93YLzsGYHcVZk_i-JYoUkq9vXUQ");
                urlConnection.connect();








                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v(LOG_TAG, "Empty Buffer1");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    Log.v(LOG_TAG, "Empty Buffer2");
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "########Playlist: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getWeatherDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }



            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

            if (result != null) {

                for(String dayForecastStr : result) {
                    mLeaderboardAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
