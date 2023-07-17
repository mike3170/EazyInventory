package tw.com.staninfo.easyinventory.repo;
import android.content.Context;
import android.content.SharedPreferences;

import tw.com.staninfo.easyinventory.MyInfo;
import tw.com.staninfo.easyinventory.model.Actions.Action;

/**
 * mini store for easyInventory
 */
public class MyStore {
  private Action currAction;
  private String docNo;  // 盤點單號
  private String kind;

  private String name;   // login id

  // SharedPreferences key constant
  private static final String _CURRACTION = "currAction";
  private static final String _DOCNO = "docNo";
  private static final String _KIND = "kind";
  private static final String _NAME = "name";

  private SharedPreferences sp;

  private static MyStore instance;

  // constructor
  private MyStore(Context context) {
    this.sp = context.getSharedPreferences(MyInfo.SPKey, Context.MODE_PRIVATE);

    // load from previous save, if onCreate
    // currAction
    String _actionAsString = sp.getString(_CURRACTION, null);
    if (_actionAsString != null) {
      this.currAction = Action.valueOf(_actionAsString);
    }

    // docNo
    String _docNo = sp.getString(_DOCNO, null);
    if (_docNo != null) {
      this.docNo = _docNo;
    }

    // kind
    String _kind = sp.getString(_KIND, null);
    if (_kind != null) {
      this.kind = _kind;
    }

    // name, login_id
    String _name = sp.getString(_NAME, null);
    if (_name != null) {
      this.name = _name;
    }
  }

  public synchronized static MyStore getInstance(Context context) {
    if (instance == null) {
      instance = new MyStore(context);
    }

    return instance;
  }

  public SharedPreferences getSharedPreferences() {
    return this.sp;
  }

  // currAction
  public Action getCurrAction() {
    if (currAction == null) {
      String actionAsString = sp.getString(_CURRACTION, "NA");
      if (actionAsString.equalsIgnoreCase("NA")) {
        currAction = Action.valueOf(actionAsString);
      } else {
        currAction = null;
      }
    }
    return currAction;
  }

  public void setCurrAction(Action currAction) {
    this.currAction = currAction;
    sp.edit().putString(_CURRACTION, currAction.toString()).apply();
  }

  // docNo
  public String getDocNo() {
    if (this.docNo == null) {
      this.docNo = sp.getString(_DOCNO, "NA");
    }
    return this.docNo;
  }

  public void setDocNo(String docNo) {
    this.docNo = docNo;
    sp.edit().putString(_DOCNO, docNo).apply();
  }

  // kind
  public String getKind() {
    if (this.kind == null) {
      this.kind = sp.getString(_KIND, "NA");
    }
    return this.kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
    sp.edit().putString(_KIND, kind).apply();
  }

  // name
  public String getName() {
    if (this.name == null) {
      this.name = sp.getString(_NAME, "NA");
    }
    return this.kind;
  }

  public void setName(String name) {
    this.name = name;
    sp.edit().putString(_NAME, name).apply();
  }

} // end class
