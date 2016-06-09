package pt.iscte.daam.bookcase.goodreads.xml.parsers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import pt.iscte.daam.bookcase.R;
import pt.iscte.daam.bookcase.bo.GRBook;
import pt.iscte.daam.bookcase.utils.RequestQueueSingleton;

/**
 * Created by Bruno on 04-05-2016.
 */
public class BookItemAdapter extends ArrayAdapter<GRBook> {


    private static final String TAG = BookItemAdapter.class.getSimpleName();

    public BookItemAdapter(Context context, ArrayList<GRBook> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final GRBook book = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }


        TextView tvBookTitle = (TextView) convertView.findViewById(R.id.tvBookTitle);
        tvBookTitle.setText(book.getTitle());
        tvBookTitle.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.colorPrimaryDark));

        ImageView ivBookLent = (ImageView) convertView.findViewById(R.id.ivBookLent);
        if (book.getLentTo() == null) {
            ivBookLent.setVisibility(View.INVISIBLE);
        } else {
            ivBookLent.setVisibility(View.VISIBLE);
        }

        TextView tvBookDate = (TextView) convertView.findViewById(R.id.tvBookDate);
        tvBookDate.setText(book.getReleaseYear());
        tvBookDate.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.secondary_text));

        TextView tvBookAuthor = (TextView) convertView.findViewById(R.id.tvBookAuthor);
        tvBookAuthor.setText(book.getAuthors());
        tvBookAuthor.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.primary_text));


        final ImageView mImageView = ((ImageView) convertView.findViewById(R.id.imageView));
        if (book.getCoverImage() != null) {
            byte[] image = book.getCoverImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            mImageView.setImageBitmap(bitmap);
        } else {
            ImageLoader mImageLoader = RequestQueueSingleton.getInstance(getContext()).getImageLoader();
            mImageLoader.get(book.getImageUrl(), ImageLoader.getImageListener(mImageView, R.drawable.ic_book_black_48px, R.drawable.book));
        }

        return convertView;
    }
}
