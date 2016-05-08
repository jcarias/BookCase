package pt.iscte.daam.bookcase.bo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by DVF on 05-05-2016.
 */
public class BookCaseDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
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

          this.insertBook(book);

          book = new GRBook();
          book.setTitle("1984");
          book.setCodeISBN("9780451524935");
          book.setAuthors("George Orwell, Erich Fromm");
          book.setReleaseYear("1950");
          book.setReleaseMonth("07");
          book.setReleaseDay("01");

          this.insertBook(book);

         book = new GRBook();
         book.setTitle("A Metamorfose");
         book.setCodeISBN("9789722637114");
         book.setAuthors("Franz Kafka");
         book.setReleaseYear("2015");
         book.setReleaseMonth("02");
         book.setReleaseDay("01");

         this.insertBook(book);

        this.lentBookTo(this.getBooks().get(0), "David Fernandes");
    }

    public void insertBook(GRBook book) {
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

    public void lentBookTo(GRBook book, String userName) {
        try {

            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("LentTo", userName);
            values.put("LentToDate", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));

            database.update(TABLE_NAME_BOOK, values, "ID" + "= ?", new String[]{book.getApplicationID()});

            database.close();

        } catch (Exception e) {
            Log.e("UTILS", "Error updating book, lent to. Error:" + e.getMessage());
        }
    }

    public ArrayList<GRBook> getBooks() {
        return this.getBooksFiltered(0);
    }

    public ArrayList<GRBook> getAvailableBooks() {
        return this.getBooksFiltered(1);
    }

    public ArrayList<GRBook> getLentBooks() {
        return this.getBooksFiltered(2);
    }

    private ArrayList<GRBook> getBooksFiltered(int filter) {

    try{

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GRBook> books = new ArrayList<GRBook>();

        String whereClause = null;
        String[] whereArgs  = null;

        switch (filter) {
            case 2:
                whereClause = "LentTo IS NOT NULL";
                whereArgs = null;
                break;
            case 1:
                whereClause = "LentTo IS NULL";
                whereArgs = null;
                break;
            default:
                whereArgs = null;
                whereClause = null;
                break;
        }

        Cursor c = db.query(TABLE_NAME_BOOK,
                            new String[] {"Title", "CodeISBN", "Authors", "ReleaseYear", "ID", "LentTo", "LentToDate" },
                            whereClause,
                            whereArgs,
                            null, null, null);

        if(c.moveToFirst()){
            do{

                GRBook newBook = new GRBook();

                newBook.setTitle(c.getString(0));
                newBook.setCodeISBN(c.getString(1));
                newBook.setAuthors(c.getString(2));
                newBook.setReleaseYear(c.getString(3));
                newBook.setApplicationID(c.getString(4));
                newBook.setLentTo(c.getString(5));
                newBook.setLentToDate(c.getString(6));

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

    public void deleteBook(GRBook book) {
        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(TABLE_NAME_BOOK, "ID" + "= ?", new String[] { book.getApplicationID() });
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
                            "ReleaseDay " + "TEXT NOT NULL," +
                            "LentTo " + "TEXT NULL," +
                            "LentToDate " + "TEXT NULL" +
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
