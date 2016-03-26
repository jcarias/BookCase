package pt.iscte.daam.bookcase.bo.goodreads;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.iscte.daam.bookcase.bo.Book;
import pt.iscte.daam.bookcase.bo.GRBook;
import pt.iscte.daam.bookcase.goodreads.xml.parsers.GRBooksSearchResultsParser;

/**
 * Created by joaocarias on 25/03/16.
 */
public class SearchBooksTask extends AsyncTask<String, Void, Book[]> {

    private final String LOG_TAG = SearchBooksTask.class.getSimpleName();

    @Override
    protected Book[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String goodreadsBookSearch = null;


        String appid = "hw7ty9ZhTw3SNqw6tCDPKQ";

        try {

            final String BOOK_SEARCH_BASE_URL = "https://www.goodreads.com/search/index.xml?";
            final String QUERY_PARAM = "q";
            final String PAGE = "page";
            final String SEARCH_FIELD = "search";
            final String DAYS_PARAM = "cnt";
            final String API_KEY_PARAM = "key";


            Uri builtUri = Uri.parse(BOOK_SEARCH_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, appid)
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            return getBooksDataFromXml(inputStream);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
    }


    private Book[] getBooksDataFromXml(InputStream inputStream) throws XmlPullParserException, IOException {
        GRBooksSearchResultsParser parser = new GRBooksSearchResultsParser();
        parser.parse(inputStream);

        if (parser.getBooks().isEmpty())
            return new Book[]{};
        else
            return parser.getBooks().toArray(new GRBook[]{});
    }

    @Override
    protected void onPostExecute(Book[] result) {

        if (result != null) {

        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}