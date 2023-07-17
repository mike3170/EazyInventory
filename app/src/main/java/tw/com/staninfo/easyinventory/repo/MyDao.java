package tw.com.staninfo.easyinventory.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tw.com.staninfo.easyinventory.model.Location;
import tw.com.staninfo.easyinventory.model.WirLocation;
import tw.com.staninfo.easyinventory.model.MainData;

public class MyDao {
  private String sql;
  SQLiteDatabase db;

  public MyDao(Context context){
    this.db = DbHelper.getInstance(context).getDb();
  }

  /**
   *
   * @param kind            8 things
   * @param locNo
   * @return
   */
  public Location getLocation(String kind, String locNo) {
    String tableName = this.getLocationTableName(kind);

    Location location = null;

    sql = "select * from " + tableName  + " where " +
          " kind = ?   and " +
          " loc_no = ?" +
          " order by kind, loc_no";

    String[] params = new String[] {
        kind,
        locNo
    };

    Cursor cursor = db.rawQuery(sql, params);

    if (cursor.moveToFirst()) {
      location = new Location();
      location.setId(cursor.getLong(0));
      location.setKind(cursor.getString(1));
      location.setLocNo(cursor.getString(2));
      location.setRemark(cursor.getString(3));
    }

    cursor.close();

    return location;
  }

  public List<Location> getLocationList(String kind) {
    List<Location> locList = new ArrayList<>();

    String tableName = this.getLocationTableName(kind);

    // System.out.println("tableName:" + tableName);
    // System.out.println("kind:" + kind);

    sql = "select * from " + tableName  + " where " +
          " kind = ? " +
          " order by kind, loc_no";

    String[] params = new String[] {
        kind
    };

    Cursor cursor = db.rawQuery(sql, params);

    while (cursor.moveToNext()) {
      Location loc = new Location();

      loc.setId(cursor.getLong(0));
      loc.setKind(cursor.getString(1));
      loc.setLocNo(cursor.getString(2));
      loc.setRemark(cursor.getString(3));

      locList.add(loc);
    }
    cursor.close();

    return locList;
  }

  // 線材
  public boolean insertWirLocation(Collection<WirLocation> locList) {
    try {
      db.beginTransaction();

      for (WirLocation loc : locList) {
        ContentValues cv = new ContentValues();
        cv.put("kind", loc.getKind());
        cv.put("loc_no", loc.getLocNo());
        cv.put("remark", loc.getRemark());

        db.insert("wir_location", null, cv);
      }

      // commit
      db.setTransactionSuccessful();

    } catch (Exception ex) {
      return false;
    } finally {
      db.endTransaction();  // rollback if not commit
    }

    return true;
  }

  //  all except 線材
  public boolean insertScrewLocation(Collection<Location> locList) {
    try {
      db.beginTransaction();

      for (Location loc : locList) {
        ContentValues cv = new ContentValues();
        cv.put("kind", loc.getKind());
        cv.put("loc_no", loc.getLocNo());
        cv.put("remark", loc.getRemark());
        db.insert("screw_location", null, cv);
      }

      // commit
      db.setTransactionSuccessful();

    } catch (Exception ex) {
      return false;
    } finally {
      db.endTransaction();  // rollback if not commit
    }

    return true;
  }

  public boolean deleteLocation() {
    try {
      // 線材
      sql = "delete from wir_location";
      db.execSQL(sql);

      // 螺絲鐵桶 ,剪力釘 ,螺帽 ,外購 ,組合品 ,成品棧板 ,餘料
      sql = "delete from screw_location";
      db.execSQL(sql);

    } catch (Exception ex) {
      return false;
    } finally {
    }

    return true;
  }

  public int countMainData(String kind, String locNo) {
    sql = "select count(*) from main_data where kind = ? and loc_no = ?";
    String[] params = new String[] { kind, locNo };

    Cursor cursor = db.rawQuery(sql, params);

    cursor.moveToFirst();
    int count = cursor.getInt(0);
    cursor.close();

    return count;
  }

  public int countMainData(String kind, String locNo, String docNo) {
    sql = "select count(*) from main_data where kind = ? and loc_no = ? and doc_no = ?";
    String[] params = new String[] { kind, locNo, docNo };

    Cursor cursor = db.rawQuery(sql, params);

    cursor.moveToFirst();
    int count = cursor.getInt(0);
    cursor.close();

    return count;
  }



  public List<MainData> getMainDataList() {
    List<MainData> list = new ArrayList<>();

    sql = "select * from main_data order by kind, doc_no, loc_no";
    Cursor cursor = db.rawQuery(sql, null);

    while (cursor.moveToNext()) {
      MainData md = new MainData();

      md.setId(cursor.getLong(cursor.getColumnIndex("_id")));
      md.setProcEmp(cursor.getString(cursor.getColumnIndex("proc_emp")));
      md.setKind(cursor.getString(cursor.getColumnIndex("kind")));
      md.setDocNo(cursor.getString(cursor.getColumnIndex("doc_no")));
      md.setLocation(cursor.getString(cursor.getColumnIndex("loc_no")));
      md.setBarcode(cursor.getString(cursor.getColumnIndex("barcode")));
      md.setScanDate(cursor.getString(cursor.getColumnIndex("scan_date")));

      list.add(md);
    }

    cursor.close();

    return list;
  }

  /**
   * for 庫位移轉
   * @param kind    8 actions
   * @param locNo   庫位
   * @return
   */
  public List<MainData> getMainDataList(String kind, String locNo ) {
    List<MainData> list = new ArrayList<>();

    sql = "select * from main_data where " +
          " kind = ? and loc_no = ?" +
          " order by scan_date desc, kind, loc_no";

    String[] params = new String[] {
        kind,
        locNo
    };

    Cursor cursor = db.rawQuery(sql, params);

    while (cursor.moveToNext()) {
      MainData md = new MainData();

      md.setId(cursor.getLong(cursor.getColumnIndex("_id")));
      md.setProcEmp(cursor.getString(cursor.getColumnIndex("proc_emp")));
      md.setKind(cursor.getString(cursor.getColumnIndex("kind")));
      md.setDocNo(cursor.getString(cursor.getColumnIndex("doc_no")));
      md.setLocation(cursor.getString(cursor.getColumnIndex("loc_no")));
      md.setBarcode(cursor.getString(cursor.getColumnIndex("barcode")));
      md.setScanDate(cursor.getString(cursor.getColumnIndex("scan_date")));

      list.add(md);
    }

    cursor.close();

    return list;
  }

  // inv
  public List<MainData> getMainDataList(String kind, String locNo, String docNo) {
    List<MainData> list = new ArrayList<>();

    sql = "select * from main_data where " +
        " kind = ? and loc_no = ? and doc_no = ?" +
        " order by scan_date desc, kind, loc_no, doc_no";

    String[] params = new String[] {
        kind,
        locNo,
        docNo
    };

    Cursor cursor = db.rawQuery(sql, params);

    while (cursor.moveToNext()) {
      MainData md = new MainData();

      md.setId(cursor.getLong(cursor.getColumnIndex("_id")));
      md.setProcEmp(cursor.getString(cursor.getColumnIndex("proc_emp")));
      md.setKind(cursor.getString(cursor.getColumnIndex("kind")));
      md.setDocNo(cursor.getString(cursor.getColumnIndex("doc_no")));
      md.setLocation(cursor.getString(cursor.getColumnIndex("loc_no")));
      md.setBarcode(cursor.getString(cursor.getColumnIndex("barcode")));
      md.setScanDate(cursor.getString(cursor.getColumnIndex("scan_date")));

      list.add(md);
    }

    cursor.close();

    return list;
  }


  public boolean isExistMainData() {
    sql = "select * from main_data  limit 1";

    Cursor cursor = db.rawQuery(sql, null);
    int count = cursor.getCount();

    cursor.close();

    return count > 0;
  }

  public boolean isExistMainData(String barcode) {
    String sql = "select count(*) from main_data where " +
                 " barcode = ?";

    String[] params = new String[] {
        barcode
    };

    Cursor cursor = db.rawQuery(sql, params);
    cursor.moveToFirst();
    int count = cursor.getInt(0);

    cursor.close();

    return (count > 0) ? true : false;
  }

  public boolean insertMainData(MainData mainData) {
    ContentValues cv = new ContentValues();

    cv.put("proc_emp", mainData.getProcEmp());
    cv.put("kind", mainData.getKind());
    cv.put("doc_no", mainData.getDocNo());
    cv.put("loc_no", mainData.getLocation());
    cv.put("barcode", mainData.getBarcode());
    cv.put("scan_date", mainData.getScanDate());

    // -1 if fail
    long rowId = this.db.insert("main_data", null, cv);
    //System.out.println("rowId:"  + rowId);

    return (rowId != -1) ? true : false;
  }

  public boolean deleteMainData() {
    try {
      int count = db.delete("main_data", null, null);
    } catch (Exception ex) {
      return false;
    } finally {
    }

    return true;
  }

  /**
   * 決定 table name
   * 1:線材庫位移轉     2:線材卷號盤點
   * 3.螺絲鐵桶庫位移轉  4:螺絲鐵桶盤點
   * 5.剪力釘庫位移轉    6:剪力釘盤點
   * 7.螺帽庫位移轉      8:螺帽盤點
   * 9.外購庫位移轉(華司) A:外購庫存盤點(華司)
   * B.組合品庫位移轉     C:組合品盤點
   * D.成品庫位移轉      E:成品棧板盤點
   * F.餘料庫位移轉      G:餘料庫存盤點
   *
   */
  private String getLocationTableName(String kind) {
    String tableName = null;

    switch (kind) {
      case "1":
      case "2":
        tableName = "wir_location";
        break;

      case "3":
      case "4":
      case "5":
      case "6":
      case "7":
      case "8":
      case "9":
      case "A":
      case "B":
      case "C":
      case "D":
      case "E":
      case "F":
      case "G":
        tableName = "screw_location";
        break;
    }

    return tableName;
  }

} // end class
