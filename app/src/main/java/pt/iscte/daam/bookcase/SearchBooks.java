package pt.iscte.daam.bookcase;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import pt.iscte.daam.bookcase.bo.BookCaseDbHelper;
import pt.iscte.daam.bookcase.bo.GRBook;
import pt.iscte.daam.bookcase.bo.goodreads.DownloadFileFromUrl;
import pt.iscte.daam.bookcase.bo.goodreads.SearchBooksTask;
import pt.iscte.daam.bookcase.goodreads.xml.parsers.BookItemAdapter;

public class SearchBooks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        final View mySearchView = getWindow().getDecorView();

        SearchManager manager = (SearchManager) getSystemService(getApplicationContext().SEARCH_SERVICE);
        SearchView search = (SearchView) findViewById(R.id.searchViewBooks);

        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                Snackbar.make(mySearchView, "Searching the Goodreads books...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                GRBook[] books = new GRBook[] {};
                try {

                    books = (new SearchBooksTask()).execute(query).get();

                    for(GRBook book : books){
                        byte[] image = (new DownloadFileFromUrl()).execute(book.getImageUrl()).get();
                        book.setCoverImage(image);
                    }

                    final BookItemAdapter adapter = new BookItemAdapter(getApplicationContext(), new ArrayList<GRBook>(Arrays.asList(books)));
                    ((ListView) mySearchView.findViewById(R.id.listViewBooksSearchResult)).setAdapter(adapter);

                    ((ListView) mySearchView.findViewById(R.id.listViewBooksSearchResult)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            GRBook book = adapter.getItem(position);

                            BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
                            bd.insertBook(book);
                            bd.updateCoverBook(book);

                            Intent intent = new Intent(view.getContext(), SelectedBookDetailsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            Bundle b = new Bundle();
                            b.putString("bookApplicationId", book.getApplicationID());

                            intent.putExtras(b);

                            view.getContext().startActivity(intent);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                return true;
            }

        });
    }

}
