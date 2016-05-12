package pt.iscte.daam.bookcase;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.daam.bookcase.bo.BookCaseDbHelper;
import pt.iscte.daam.bookcase.bo.GRBook;

/**
 * Created by Bruno on 09-05-2016.
 */
public class SelectedBookDetailsActivity extends AppCompatActivity {

    private GRBook book;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_item_details);

        Bundle extras = getIntent().getExtras();
        String bookId = extras.getString("bookApplicationId");

        BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
        this.book = bd.getBooksById(bookId);

        if(this.book == null)
            return;

        ((TextView) findViewById(R.id.bookTitleDetails)).setText(this.book.getTitle());
        ((TextView) findViewById(R.id.bookAuthorsDetails)).setText(this.book.getAuthors());
      //  ((ImageView) findViewById(R.id.imageProfilePicture)).setImageBitmap(null);
    }

    public void getContacts(View view) {

        String[] fromColumns = { ContactsContract.Contacts.DISPLAY_NAME };
        int[] toViews = { android.R.id.text1 };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, fromColumns, toViews, 0);;
        ((ListView) findViewById(R.id.listviewpersonalcontacts)).setAdapter(adapter);
    }

}
