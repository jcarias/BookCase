package pt.iscte.daam.bookcase;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private boolean contactsAreListed;

    private class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            ArrayList<String> contactosAL = new ArrayList<String>();

            Cursor c = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);
            while (c.moveToNext()) {

                String contactName = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));


                contactosAL.add(contactName);
            }
            c.close();

            return contactosAL;
        }

        @Override
        protected void onPostExecute(ArrayList<String> contactosAL) {
            super.onPostExecute(contactosAL);

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getApplicationContext(), android.R.layout.simple_list_item_1, contactosAL);

            ((ListView) findViewById(R.id.listviewpersonalcontacts)).setVisibility(View.VISIBLE);
            ((ListView) findViewById(R.id.listviewpersonalcontacts)).setAdapter(adapter);

            ((ListView) findViewById(R.id.listviewpersonalcontacts)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     String contact = adapter.getItem(position);

                     BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
                     bd.lentBookTo(book, contact);

                     contactsAreListed = false;
                     ((ListView) findViewById(R.id.listviewpersonalcontacts)).setVisibility(View.GONE);
                 }
             });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_item_details);

        this.contactsAreListed = false;

        Bundle extras = getIntent().getExtras();
        String bookId = extras.getString("bookApplicationId");

        BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
        this.book = bd.getBooksById(bookId);

        if(this.book == null)
            return;

        ((TextView) findViewById(R.id.bookTitleDetails)).setText(this.book.getTitle());
        ((TextView) findViewById(R.id.bookAuthorsDetails)).setText(this.book.getAuthors());
      //  ((ImageView) findViewById(R.id.imageProfilePicture)).setImageBitmap(null);

        if(this.book.getLentTo() != null) {
            (findViewById(R.id.buttonLentTo)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.lentToTextBox)).setText("Lent to " + this.book.getLentTo() + " - " + this.book.getLentToDate());
        } else {
            ((TextView) findViewById(R.id.lentToTextBox)).setText("Book available.");
        }

        (findViewById(R.id.buttonLentTo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!contactsAreListed) {
                    contactsAreListed = true;
                    (new LoadContactsAyscn()).execute();
                } else {
                    contactsAreListed = false;
                    ((ListView) findViewById(R.id.listviewpersonalcontacts)).setVisibility(View.GONE);
                }

            }
        });
    }

    public void getContacts(View view) {

        String[] fromColumns = { ContactsContract.Contacts.DISPLAY_NAME };
        int[] toViews = { android.R.id.text1 };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, fromColumns, toViews, 0);;
        ((ListView) findViewById(R.id.listviewpersonalcontacts)).setAdapter(adapter);
    }

}
