package pt.iscte.daam.bookcase;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pt.iscte.daam.bookcase.bo.BookCaseDbHelper;
import pt.iscte.daam.bookcase.bo.GRBook;
import pt.iscte.daam.bookcase.utils.RequestQueueSingleton;

/**
 * Created by Bruno on 09-05-2016.
 */
public class SelectedBookDetailsActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private GRBook book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.book_item_details);

        Bundle extras = getIntent().getExtras();
        String bookId = extras.getString("bookApplicationId");

        BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
        this.book = bd.getBooksById(bookId);

        if (this.book == null)
            return;

        TextView tvBookTitle = (TextView) findViewById(R.id.bookTitleDetails);
        if (tvBookTitle != null) {
            tvBookTitle.setText(this.book.getTitle());
        }

        TextView tvBookAuthor = (TextView) findViewById(R.id.bookAuthorsDetails);
        if (tvBookAuthor != null) {
            tvBookAuthor.setText(this.book.getAuthors());
        }

        TextView tvBookYear = (TextView) findViewById(R.id.tvYear);
        if (tvBookYear != null) {
            tvBookYear.setText(this.book.getReleaseYear());
        }

        TextView tvRatingLabel = (TextView) findViewById(R.id.tvRatingLabel);
        if (tvRatingLabel != null) {
            String ratingLabel = getResources().getString(R.string.ratingLabel);
            tvRatingLabel.setText(String.format("%s: (%s/5)", ratingLabel, this.book.getAverageRating()));
        }

        setRatingStars();

        ImageView ivCoverPhoto = (ImageView) findViewById(R.id.coverPhoto);
        if (this.book.getCoverImage() != null) {
            byte[] image = this.book.getCoverImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            if (ivCoverPhoto != null) {
                ivCoverPhoto.setImageBitmap(bitmap);
            }
        } else {
            ImageLoader mImageLoader = RequestQueueSingleton.getInstance(getApplicationContext()).getImageLoader();
            mImageLoader.get(book.getImageUrl(), ImageLoader.getImageListener(ivCoverPhoto, R.drawable.ic_book_black_48px, R.drawable.book));
        }

        ImageButton imgBtnDeleteBook = (ImageButton) findViewById(R.id.bookDeleteButton);
        if (imgBtnDeleteBook != null) {
            imgBtnDeleteBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete Book")
                            .setMessage("Are you sure you want to delete this book?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
                                    bd.deleteBook(book);

                                    String message = String.format(getResources().getString(R.string.message_book_deleted), book.getTitle());
                                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    toast.show();


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
        }

        Button btnLend = (Button) findViewById(R.id.buttonLentTo);
        View panelLentTo = findViewById(R.id.panelLentTo);
        if (this.book.getLentTo() != null) {
            if (btnLend != null) {
                btnLend.setText(R.string.btn_label_Retake_Book);
            }

            if (panelLentTo != null) {
                panelLentTo.setVisibility(View.VISIBLE);
            }

            TextView tvLentTo = (TextView) findViewById(R.id.tvLentTo);
            if (tvLentTo != null) {
                tvLentTo.setText(this.book.getLentTo());
            }

            TextView tvLentToDate = (TextView) findViewById(R.id.tvLentToDate);
            if (tvLentToDate != null) {
                tvLentToDate.setText(this.book.getLentToDate());
            }
        } else {
            if (panelLentTo != null) {
                panelLentTo.setVisibility(View.GONE);
            }
        }

        if (btnLend != null) {
            btnLend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (book.getLentTo() != null) {
                        BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
                        bd.lentBookTo(book, null, null);

                        finish();
                        startActivity(getIntent());

                        String message = String.format(getResources().getString(R.string.message_book_returned), book.getTitle());
                        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                        toast.show();

                        return;
                    } else {

                        // Check the SDK version and whether the permission is already granted or not.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                        } else {
                            //Carrega a lista de Contactos e Mostra o dialog
                            (new LoadContactsAyscn()).execute();
                        }

                    }
                }
            });
        }


        if (extras.getInt("NewBook") == 1) {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Book Added!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                (new LoadContactsAyscn()).execute();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setRatingStars() {
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBookBar);
        if (ratingBar != null) {
            Float rating = Float.valueOf(this.book.getAverageRating());
            ratingBar.setRating(rating);
        }
    }

    private class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            ArrayList<String> contactosAL = new ArrayList<>();


            Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            while (c != null && c.moveToNext()) {
                String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (contactosAL.contains(contactName))
                    continue;
                contactosAL.add(contactName);
            }
            if (c != null) {
                c.close();
            }

            return contactosAL;
        }

        @Override
        protected void onPostExecute(ArrayList<String> contactosAL) {
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.contact_list_item, contactosAL);

            final Dialog dialog = new Dialog(SelectedBookDetailsActivity.this);
            dialog.setContentView(R.layout.contacts_list);

            ListView lv = (ListView) dialog.findViewById(R.id.lv);
            lv.setAdapter(adapter);
            dialog.setCancelable(true);
            dialog.setTitle(R.string.pick_contact_title);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String contact = adapter.getItem(position);
                    BookCaseDbHelper bd = new BookCaseDbHelper(getApplicationContext());
                    bd.lentBookTo(book, contact, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());

                    String message = String.format(getResources().getString(R.string.message_book_lent), book.getTitle(), contact);
                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
            });

            Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

}
