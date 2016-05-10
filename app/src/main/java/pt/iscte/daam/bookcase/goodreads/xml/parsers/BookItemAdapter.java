package pt.iscte.daam.bookcase.goodreads.xml.parsers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import pt.iscte.daam.bookcase.R;

/**
 * Created by Bruno on 04-05-2016.
 */
public class BookItemAdapter extends ArrayAdapter<String> {
    public BookItemAdapter(Context context, String[] names) {
        super(context, R.layout.book_list_item,names);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View x = inflater.inflate(R.layout.book_list_item, parent, false);

        String elementName = getItem(position);
        TextView nomeAutor =(TextView) x.findViewById(R.id.tvBookTitle);
        ImageView capa = (ImageView) x.findViewById(R.id.imageView);

        nomeAutor.setText(elementName);
        capa.setImageResource(R.drawable.book);

        return x;
    }
}
