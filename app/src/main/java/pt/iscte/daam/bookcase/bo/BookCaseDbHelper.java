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

    public BookCaseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        SQLiteDatabase db = getWritableDatabase(); //to create database
    }

    public void insertMockBooks() {

      try {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("Title", "Fahrenheit 451");
        values.put("CodeISBN", "9780007491568");
        values.put("Authors", "Ray Bradbury");
        values.put("ReleaseYear", "2013");
        values.put("ReleaseMonth", "03");
        values.put("ReleaseDay", "28");

        database.insertOrThrow(TABLE_NAME_BOOK, null, values);

        values = new ContentValues();

        values.put("Title", "1984");
        values.put("CodeISBN", "9780451524935");
        values.put("Authors", "George Orwell, Erich Fromm");
        values.put("ReleaseYear", "1950");
        values.put("ReleaseMonth", "07");
        values.put("ReleaseDay", "01");

        database.insertOrThrow(TABLE_NAME_BOOK, null, values);

        database.close();

      } catch (Exception e) {
          Log.e("UTILS", "Error inserting database book mocks. Error:" + e.getMessage());
      }
    }

    public ArrayList<GRBook> GetBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GRBook> books = new ArrayList<GRBook>();

       // Cursor c = db.rawQuery("SELECT Title,CodeISBN,Authors,ReleaseYear FROM book"/* + TABLE_NAME_BOOK*/, null);
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
    }

    public void onCreate(SQLiteDatabase db) {

        try {
            String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + TABLE_NAME_BOOK + " (" +
                            "ID" + " INTEGER PRIMARY KEY," +
                            "Title " + "TEXT NOT NULL," +
                            "CodeISBN " + "TEXT NOT NULL," +
                            "Authors " + "TEXT NOT NULL," +
                            "ReleaseYear " + "TEXT NOT NULL," +
                            "ReleaseMonth " + "TEXT NOT NULL," +
                            "ReleaseDay " + "TEXT NOT NULL" +
                            ")";

            db.execSQL(SQL_CREATE_ENTRIES);

        } catch (Exception e) {
            Log.e("UTILS", "Error creating database. Error:" + e.getMessage());
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
       // db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
