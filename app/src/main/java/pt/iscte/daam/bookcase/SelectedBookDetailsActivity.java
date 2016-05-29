package pt.iscte.daam.bookcase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pt.iscte.daam.bookcase.bo.BookCaseDbHelper;
import pt.iscte.daam.bookcase.bo.GRBook;

/**
 * Created by Bruno on 09-05-2016.
 */
public class SelectedBookDetailsActivity extends AppCompatActivity {

    private GRBook book;
    private boolean contactsAreListed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_item_details);

        this.contactsAreListed = false;

        Bundle extras = getIntent().getExtras();
        String bookId = extras.getString("bookApplicationId");

        BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
        this.book = bd.getBooksById(bookId);

        if (this.book == null)
            return;

        ((TextView) findViewById(R.id.bookTitleDetails)).setText(this.book.getTitle());
        ((TextView) findViewById(R.id.bookAuthorsDetails)).setText(this.book.getAuthors());

        String ratingLabel = getResources().getString(R.string.ratingLabel);
        ((TextView) findViewById(R.id.tvRatingLabel)).setText(String.format("%s: (%s/5)", ratingLabel, this.book.getAverageRating()));
        setRatingStars(this.book);

        if (this.book.getCoverImage() != null) {
            byte[] image = this.book.getCoverImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            ((ImageView) findViewById(R.id.coverPhoto)).setImageBitmap(bitmap);
        }

        ((ImageButton) findViewById(R.id.bookDeleteButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Book")
                        .setMessage("Are you sure you want to delete this book?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
                                bd.deleteBook(book);

                                Intent intent = new Intent(getApplicationContext(), BookCaseMainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_warning_black_48px)
                        .show();
            }
        });

        if (this.book.getLentTo() != null) {
            ((Button) (findViewById(R.id.buttonLentTo))).setText("Retake book");

            ((LinearLayout) findViewById(R.id.panelLentTo)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tvLentTo)).setText(this.book.getLentTo());
            ((TextView) findViewById(R.id.tvLentToDate)).setText(this.book.getLentToDate());
        } else {
            ((TextView) findViewById(R.id.lentToTextBox)).setText("Book available.");
            ((LinearLayout) findViewById(R.id.panelLentTo)).setVisibility(View.GONE);
        }

        (findViewById(R.id.buttonLentTo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (book.getLentTo() != null) {
                    BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
                    bd.lentBookTo(book, null, null);

                    finish();
                    startActivity(getIntent());

                    return;
                }

                if (!contactsAreListed) {
                    contactsAreListed = true;
                    (new LoadContactsAyscn()).execute();
                } else {
                    contactsAreListed = false;
                    ((ListView) findViewById(R.id.listviewpersonalcontacts)).setVisibility(View.GONE);
                }

            }
        });

        if (extras.getInt("NewBook") == 1)
            Snackbar.make(getWindow().getDecorView(), "Book Added!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void setRatingStars(GRBook book) {
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBookBar);
        Float rating = new Float(this.book.getAverageRating());
        ratingBar.setRating(rating);
    }

    private class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            ArrayList<String> contactosAL = new ArrayList<String>();

            Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
            while (c.moveToNext()) {
                String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (contactosAL.contains(contactName))
                    continue;
                contactosAL.add(contactName);
            }
            c.close();

            return contactosAL;
        }

        @Override
        protected void onPostExecute(ArrayList<String> contactosAL) {
            super.onPostExecute(contactosAL);

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.contact_list_item, contactosAL);


            Dialog dialog = new Dialog(SelectedBookDetailsActivity.this);
            dialog.setContentView(R.layout.contacts_list);

            ListView lv = (ListView ) dialog.findViewById(R.id.lv);
            lv.setAdapter(adapter);
            dialog.setCancelable(true);
            dialog.setTitle(R.string.pick_contact_title);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                    startActivity(getIntent());
                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String contact = adapter.getItem(position);

                    BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
                    bd.lentBookTo(book, contact, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));

                    finish();
                    startActivity(getIntent());
                }
            });


        }
    }

}
