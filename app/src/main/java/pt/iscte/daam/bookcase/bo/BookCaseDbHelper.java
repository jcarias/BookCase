package pt.iscte.daam.bookcase.bo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by DVF on 05-05-2016.
 */
public class BookCaseDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BookCase.db";

    //Table Names
    public static final String TABLE_NAME_BOOK = "book";
    public static final String TABLE_NAME_USERPROFILE = "userprofile";

    public BookCaseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.getWritableDatabase(); //force creation of database
    }

    public void insertMockBooks() {

          GRBook book = new GRBook();
          book.setTitle("Fahrenheit 451");
          book.setCodeISBN("9780007491568");
          book.setAuthors("Ray Bradbury");
          book.setReleaseYear("2013");
          book.setReleaseMonth("03");
          book.setReleaseDay("28");

          this.InsertBook(book);

          book = new GRBook();
          book.setTitle("1984");
          book.setCodeISBN("9780451524935");
          book.setAuthors("George Orwell, Erich Fromm");
          book.setReleaseYear("1950");
          book.setReleaseMonth("07");
          book.setReleaseDay("01");

          this.InsertBook(book);
    }

    public void InsertBook(GRBook book) {
        try {

            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("Title", book.getTitle());
            values.put("CodeISBN", book.getCodeISBN());
            values.put("Authors", book.getAuthors());
            values.put("ReleaseYear", book.getReleaseYear());
            values.put("ReleaseMonth", book.getReleaseMonth());
            values.put("ReleaseDay", book.getReleaseDay());

            database.insertOrThrow(TABLE_NAME_BOOK, null, values);

            database.close();

        } catch (Exception e) {
            Log.e("UTILS", "Error inserting database book. Error:" + e.getMessage());
        }
    }

    public void InsertUser(UserProfile profile) {
        try {

            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("Name", profile.getName());
            values.put("Email", profile.getEmail());
            values.put("FacebookId", profile.getFacebookId());

            database.insertOrThrow(TABLE_NAME_USERPROFILE, null, values);

            database.close();

        } catch (Exception e) {
            Log.e("UTILS", "Error inserting database user. Error:" + e.getMessage());
        }
    }

    public ArrayList<GRBook> GetBooks() {

    try{

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GRBook> books = new ArrayList<GRBook>();

        Cursor c = db.query(TABLE_NAME_BOOK, new String[] {"Title", "CodeISBN", "Authors", "ReleaseYear"}, null, null, null, null, null);
        if(c.moveToFirst()){
            do{

                GRBook newBook = new GRBook();

                newBook.setTitle(c.getString(0));
                newBook.setCodeISBN(c.getString(1));
                newBook.setAuthors(c.getString(2));
                newBook.setReleaseYear(c.getString(3));

                books.add(newBook);

            }while(c.moveToNext());
        }
        c.close();
        db.close();

        return books;

        } catch (Exception e) {
            Log.e("UTILS", "Error getting books. Error:" + e.getMessage());
            return null;
        }
    }

    public UserProfile GetUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        UserProfile userProfile = new UserProfile();

        Cursor c = db.query(TABLE_NAME_USERPROFILE, new String[] {"Name", "Email", "FacebookId" }, null, null, null, null, null);
        if(c.moveToFirst()){

            userProfile.setName(c.getString(0));
            userProfile.setEmail(c.getString(1));
            userProfile.setFacebookId(c.getString(2));

        } else {
            userProfile = null;
        }
        c.close();
        db.close();

        return userProfile;
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(TABLE_NAME_USERPROFILE, null, null);
    }

    public void onCreate(SQLiteDatabase db) {

        try {
            String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + TABLE_NAME_BOOK + " (" +
                            "ID " + "INTEGER PRIMARY KEY  AUTOINCREMENT," +
                            "Title " + "TEXT NOT NULL," +
                            "CodeISBN " + "TEXT UNIQUE NOT NULL," +
                            "Authors " + "TEXT NOT NULL," +
                            "ReleaseYear " + "TEXT NOT NULL," +
                            "ReleaseMonth " + "TEXT NOT NULL," +
                            "ReleaseDay " + "TEXT NOT NULL" +
                            ")";

            db.execSQL(SQL_CREATE_ENTRIES);

            SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + TABLE_NAME_USERPROFILE + " (" +
                            "Name " + "TEXT NOT NULL," +
                            "FacebookId " + "TEXT PRIMARY KEY," +
                            "Email " + "TEXT NOT NULL" +
                    ")";

            db.execSQL(SQL_CREATE_ENTRIES);

        } catch (Exception e) {
            Log.e("UTILS", "Error creating database. Error:" + e.getMessage());
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BOOK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USERPROFILE);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
