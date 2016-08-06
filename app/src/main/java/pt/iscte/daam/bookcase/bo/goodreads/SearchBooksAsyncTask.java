package pt.iscte.daam.bookcase.bo.goodreads;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.iscte.daam.bookcase.SearchBooksActivity;
import pt.iscte.daam.bookcase.goodreads.xml.parsers.GRBooksSearchResultsParser;

/**
 * Async task to search the books
 * Created by joaocarias on 25/03/16.
 */
public class SearchBooksAsyncTask extends AsyncTask<String, Void, BookSearchResult> {

    private final String LOG_TAG = SearchBooksAsyncTask.class.getSimpleName();
    private final SearchBooksActivity activity;

    public SearchBooksAsyncTask(SearchBooksActivity activity) {
        this.activity = activity;
    }

    @Override
    protected BookSearchResult doInBackground(String... params) {

        HttpURLConnection urlConnection = null;

        try {

            final String APP_ID = "hw7ty9ZhTw3SNqw6tCDPKQ";
            final String BOOK_SEARCH_BASE_URL = "https://www.goodreads.com/search/index.xml?";
            final String QUERY_PARAM = "q";
            final String PAGE = "page";
            final String API_KEY_PARAM = "key";

            Uri.Builder builtUri = Uri.parse(BOOK_SEARCH_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, APP_ID)
                    .appendQueryParameter(QUERY_PARAM, params[0]);

            if (params != null && params.length > 1) {
                builtUri.appendQueryParameter(PAGE, params[1]);
            }


            URL url = new URL(builtUri.build().toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream == null) {
                return null;
            } else {
                return getBooksDataFromXml(inputStream);
            }

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
        }
    }

    @Override
    protected void onPostExecute(BookSearchResult searchResult) {
        this.activity.loadSearchedBooks(searchResult);
    }

    private BookSearchResult getBooksDataFromXml(InputStream inputStream) throws XmlPullParserException, IOException {
        GRBooksSearchResultsParser parser = new GRBooksSearchResultsParser();
        parser.parse(inputStream);
        return new BookSearchResult(parser.getBooks(), parser.hasMoreBooks());
    }
}