package pt.iscte.daam.bookcase;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import pt.iscte.daam.bookcase.bo.BookCaseDbHelper;
import pt.iscte.daam.bookcase.bo.GRBook;
import pt.iscte.daam.bookcase.bo.goodreads.SearchBooksTask;
import pt.iscte.daam.bookcase.goodreads.xml.parsers.BookItemAdapter;
import pt.iscte.daam.bookcase.utils.RequestQueueSingleton;

public class SearchBooks extends AppCompatActivity {

    private static final String TAG = SearchBooks.class.getSimpleName();
    private ProgressDialog progressDialog;
    private SearchBooksTask task;
    private byte[] byteArray;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        final View mySearchView = getWindow().getDecorView();
        task = new SearchBooksTask(this);

        SearchManager manager = (SearchManager) getSystemService(getApplicationContext().SEARCH_SERVICE);
        SearchView search = (SearchView) findViewById(R.id.searchViewBooks);

        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setQueryHint("Title, ISBN, Author...");
        search.setIconifiedByDefault(false);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (task.getStatus() == AsyncTask.Status.RUNNING)
                    return true;

                progressDialog = new ProgressDialog(SearchBooks.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Searching");
                progressDialog.setMessage("Searching the Goodreads platform, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);

                progressDialog.show();

                try {
                    task.execute(query);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }

        });
    }

    public void loadSearchedBooks(GRBook[] books) {

        try {

            final BookItemAdapter adapter = new BookItemAdapter(getApplicationContext(), new ArrayList<GRBook>(Arrays.asList(books)));


            ((ListView) findViewById(R.id.listViewBooksSearchResult)).setAdapter(adapter);

            ((ListView) findViewById(R.id.listViewBooksSearchResult)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GRBook book = adapter.getItem(position);
                    BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
                    bd.insertBook(book);

                    downloadAndUpdateBookCover(book);

                    Intent intent = new Intent(view.getContext(), SelectedBookDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Bundle b = new Bundle();
                    b.putString("bookApplicationId", book.getApplicationID());
                    b.putInt("NewBook", 1);

                    intent.putExtras(b);

                    view.getContext().startActivity(intent);
                    finish();
                }
            });

        } catch (Exception e) {
            Log.e("SearchBooks", "Error loading searched books.\nError: " + e.getMessage());
        }

        task = new SearchBooksTask(this);
        progressDialog.dismiss();
    }

    private void downloadAndUpdateBookCover(final GRBook book) {
        if (book != null) {
            ImageLoader mImageLoader = RequestQueueSingleton.getInstance(getApplicationContext()).getImageLoader();
            mImageLoader.get(book.getImageUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null) {
                        BookCaseDbHelper dbHelper = new BookCaseDbHelper(getApplicationContext());
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        GRBook bookAux = new GRBook();
                        bookAux.setApplicationID(book.getApplicationID());
                        bookAux.setCoverImage(byteArray);
                        dbHelper.updateCoverBook(bookAux);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: ", error);
                }
            });

        }
    }

}

