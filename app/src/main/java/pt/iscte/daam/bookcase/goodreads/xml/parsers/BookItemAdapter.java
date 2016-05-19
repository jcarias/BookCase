package pt.iscte.daam.bookcase.goodreads.xml.parsers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.iscte.daam.bookcase.BookCaseMainActivity;
import pt.iscte.daam.bookcase.R;
import pt.iscte.daam.bookcase.SelectedBookDetailsActivity;
import pt.iscte.daam.bookcase.bo.GRBook;

/**
 * Created by Bruno on 04-05-2016.
 */
public class BookItemAdapter extends ArrayAdapter<GRBook> {
    public BookItemAdapter(Context context, ArrayList<GRBook> books) {
        super(context, 0,books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        GRBook book = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.tvBookTitle)).setText(book.getTitle());
        ((TextView) convertView.findViewById(R.id.tvBookDate)).setText(book.getPublicationDate());
        ((TextView) convertView.findViewById(R.id.tvBookAuthor)).setText(book.getAuthors());

        if(book.getCoverImage() != null) {
            byte[] image = book.getCoverImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            ((ImageView) convertView.findViewById(R.id.imageView)).setImageBitmap(bitmap);
        }

        return convertView;
    }
}
