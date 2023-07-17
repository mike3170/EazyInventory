package tw.com.staninfo.easyinventory.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Singleton
 * getInstance to this instance
 * getDB to get Database
 */
public class DbHelper extends SQLiteOpenHelper {
  private static final String TAG = DbHelper.class.getSimpleName();

  private static final int SCHEMA_VERSION = 1;

  private static final String DB_NAME = "mydb.db";

  private final Context context;

  private static DbHelper instance;  // singleton design pattern
  private static SQLiteDatabase db;

  public synchronized static DbHelper getInstance(Context ctx) {
    if (instance == null) {
      instance = new DbHelper(ctx.getApplicationContext());
    }
    return instance;
  }

  private DbHelper(Context context) {
    super(context, DB_NAME, null, SCHEMA_VERSION);

    this.context = context;

    // This will happen in onConfigure for API >= 16
    // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
    // SQLiteDatabase db = getWritableDatabase();
    // db.enableWriteAheadLogging();
    // db.execSQL("PRAGMA foreign_keys = ON;");
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String sql = null;

    // for (int i = 1; i <= SCHEMA_VERSION; i++) {
    //   applySqlFile(db, i);
    //}

    // 線材 ------------------------------
    sql = "create table wir_location ("
        + " _id integer primary key autoincrement, "
        + " kind text, "
        + " loc_no text, "
        + " remark text)";
    db.execSQL(sql);

    sql = "create index wir_location_idx1 on wir_location(kind, loc_no)";
    db.execSQL(sql);

     // 螺絲鐵桶, 剪力釘, 螺帽, 外購 , 組合品 ,  成品棧板, 餘料
    sql = "create table screw_location ("
        + " _id integer primary key autoincrement, "
        + " kind text, "
        + " loc_no text, "
        + " remark text)";
    db.execSQL(sql);

    sql = "create index screw_location_idx1 on screw_location(kind, loc_no)";
    db.execSQL(sql);

    // 條碼
    sql = "create table main_data ("
        + " _id integer primary key autoincrement, "
        + " proc_emp text, "
        + " kind text, "
        + " doc_no text, "
        + " loc_no text, "
        + " barcode text, "
        + " scan_date text)";
    db.execSQL(sql);

    sql = "create index main_data_idx1 on main_data(barcode, loc_no)";
    db.execSQL(sql);

    sql = "create index main_data_idx2 on main_data(kind, loc_no)";
    db.execSQL(sql);

    //sql = "create index main_data_idx3 on main_data(doc_no)";
    //db.execSQL(sql);

    // dept test data
    sql = "create table dept ("
        + " dept_no   integer, "
        + " dept_name text, "
        + " loc       text,"
        + " constraint dept_pk primary key (dept_no) "
        + ")";

    db.execSQL(sql);

    insertDept(db, 100, "甲骨文", "USA");
    insertDept(db, 200, "Oracle", "USA");
    insertDept(db, 300, "Stit", "高雄");
    insertDept(db, 500, "Android", "屏東");
    insertDept(db, 600, "Asus", "台中");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db,
                        int oldVersion,
                        int newVersion) {
    // for (int i = (oldVersion + 1); i <= newVersion; i++) {
    //   applySqlFile(db, i);
    //  }
  }

  // helper method
  public synchronized SQLiteDatabase getDb() {
    if (db == null) {
      db = super.getWritableDatabase();
    } else if (! db.isOpen()) {
      db = super.getWritableDatabase();
    }

    return db;
  }

  @Override
  public SQLiteDatabase getWritableDatabase() {
    throw new UnsupportedOperationException("請使用 getDb()");
  }

  @Override
  public SQLiteDatabase getReadableDatabase() {
    throw new UnsupportedOperationException("請使用 getDb()");
  }

  private void applySqlFile(SQLiteDatabase db, int version) {
    BufferedReader reader = null;

    try {
      String filename = String.format("%s.%d.sql", DB_NAME, version);
      final InputStream inputStream = context.getAssets().open(filename);
      reader = new BufferedReader(new InputStreamReader(inputStream));

      final StringBuilder statement = new StringBuilder();

      for (String line; (line = reader.readLine()) != null; ) {
        //if (BuildConfig.DEBUG) {
        //  Log.d(TAG, "Reading line -> " + line);
        //}

        // Ignore empty lines
        if (!TextUtils.isEmpty(line) && !line.startsWith("--")) {
          statement.append(line.trim());
        }

        if (line.endsWith(";")) {
          //if (BuildConfig.DEBUG) {
          //  Log.d(TAG, "Running statement " + statement);
          //}

          db.execSQL(statement.toString());
          statement.setLength(0);
        }
      }

    } catch (IOException e) {
      Log.e(TAG, "Could not apply SQL file", e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          Log.w(TAG, "Could not close reader", e);
        }
      }
    }
  }

  private static void insertDept(SQLiteDatabase db, int deptNo, String deptName, String loc) {
    ContentValues deptValues = new ContentValues();
    deptValues.put("dept_no", deptNo);
    deptValues.put("dept_name", deptName);
    deptValues.put("loc", loc);

    db.insert("dept", null, deptValues);
  }

}  // end class
