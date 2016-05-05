package pt.iscte.daam.bookcase.bo;

import android.media.Image;

import java.util.List;

/**
 * Created by joaocarias on 23/03/16.
 */
public interface Book {

    String getTitle();

    String getAuthors();

    String getReleaseYear();

    String getReleaseMonth();

    String getReleaseDay();

    String getCodeISBN();

    Image getCoverImage();

}
