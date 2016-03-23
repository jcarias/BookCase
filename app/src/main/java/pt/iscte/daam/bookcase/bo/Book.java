package pt.iscte.daam.bookcase.bo;

import android.media.Image;

/**
 * Created by joaocarias on 23/03/16.
 */
public interface Book {

    public String getTitle();
    public String getAuthors();
    public String getPublicationYear();
    public Image getCoverImage();

}
