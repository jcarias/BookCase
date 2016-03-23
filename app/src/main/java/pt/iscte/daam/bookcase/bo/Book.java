package pt.iscte.daam.bookcase.bo;

import android.media.Image;

import java.util.List;

/**
 * Created by joaocarias on 23/03/16.
 */
public interface Book {

    public String getTitle();
    public List<String> getAuthors();
    public String getReleaseYear();
    public String getReleaseMonth();
    public String getReleaseDay();
    public Image getCoverImage();

}
