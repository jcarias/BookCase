package pt.iscte.daam.bookcase.BookInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by jpafernandes on 05/04/16.
 */
public class author {

    private int id;
    private String name;
    private String link;
    private int fans_count;
    private int author_followers_count;
    private String large_image_url;
    private String image_url;
    private String small_image_url;
    private String about;
    private String influences;
    private int works_count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getFans_count() {
        return fans_count;
    }

    public void setFans_count(int fans_count) {
        this.fans_count = fans_count;
    }

    public int getAuthor_followers_count() {
        return author_followers_count;
    }

    public void setAuthor_followers_count(int author_followers_count) {
        this.author_followers_count = author_followers_count;
    }

    public String getLarge_image_url() {
        return large_image_url;
    }

    public void setLarge_image_url(String large_image_url) {
        this.large_image_url = large_image_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSmall_image_url() {
        return small_image_url;
    }

    public void setSmall_image_url(String small_image_url) {
        this.small_image_url = small_image_url;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getInfluences() {
        return influences;
    }

    public void setInfluences(String influences) {
        this.influences = influences;
    }

    public int getWorks_count() {
        return works_count;
    }

    public void setWorks_count(int works_count) {
        this.works_count = works_count;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public Date getBorn_at() {
        return born_at;
    }

    public void setBorn_at(Date born_at) {
        this.born_at = born_at;
    }

    public Date getDied_at() {
        return died_at;
    }

    public void setDied_at(Date died_at) {
        this.died_at = died_at;
    }

    public boolean isGoodreads_author() {
        return goodreads_author;
    }

    public void setGoodreads_author(boolean goodreads_author) {
        this.goodreads_author = goodreads_author;
    }


    public List<book> getBooks() {
        return books;
    }

    public void setBooks(List<book> books) {
        this.books = books;
    }

    private String gender;
    private String hometown;
    private Date born_at;
    private Date died_at;
    private boolean goodreads_author;
    private List<book> books;


  //changed

}
