package pt.iscte.daam.bookcase.bo.goodreads;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.iscte.daam.bookcase.SearchBooks;
import pt.iscte.daam.bookcase.bo.GRBook;
import pt.iscte.daam.bookcase.goodreads.xml.parsers.GRBooksSearchResultsParser;

/**
 * Created by joaocarias on 25/03/16.
 */
public class SearchBooksTask extends AsyncTask<String, Void, GRBook[]> {

    private final String LOG_TAG = SearchBooksTask.class.getSimpleName();
    private final SearchBooks activity;

    public SearchBooksTask(SearchBooks activity) {
        this.activity = activity;
    }

    @Override
    protected GRBook[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

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
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }


            GRBook[] books = getBooksDataFromXml(inputStream);

            /*for(GRBook book : books){
                book.setCoverImage(this.getCoverPhoto(book.getImageUrl()));
            }*/

            return books;

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

    @Override
    protected void onPostExecute(GRBook[] books) {
        if(books == null)
            books = new GRBook[] {};

        this.activity.loadSearchedBooks(books);
    }

    private GRBook[] getBooksDataFromXml(InputStream inputStream) throws XmlPullParserException, IOException {
        GRBooksSearchResultsParser parser = new GRBooksSearchResultsParser();
        parser.parse(inputStream);

        if (parser.getBooks().isEmpty())
            return new GRBook[]{};
        else
            return parser.getBooks().toArray(new GRBook[]{});
    }

    @Nullable
    private byte[] getCoverPhoto(String url) {

        try {

            URL pictureURL = new URL(url);
            ByteArrayOutputStream outputStrean = new ByteArrayOutputStream();
            InputStream is = pictureURL.openConnection().getInputStream();

            byte[] byteChunk = new byte[4096];
            int n;

            while ((n = is.read(byteChunk)) > 0) {
                outputStrean.write(byteChunk, 0, n);
            }

            return outputStrean.toByteArray();

        } catch (Exception e) {
            Log.e("SearchBooksTask", "Error getting picture with url: " + url + "\nError:" + e.getMessage());
            return null;
        }
    }
}