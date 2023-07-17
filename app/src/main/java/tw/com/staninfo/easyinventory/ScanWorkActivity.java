package tw.com.staninfo.easyinventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import tw.com.staninfo.easyinventory.model.Location;
import tw.com.staninfo.easyinventory.model.MainData;
import tw.com.staninfo.easyinventory.repo.DbHelper;
import tw.com.staninfo.easyinventory.repo.MyDao;
import tw.com.staninfo.easyinventory.repo.MyStore;
import tw.com.staninfo.easyinventory.utils.InvUtil;

/**
 * 2 input is kind, locNo
 */
public class ScanWorkActivity extends AppCompatActivity {
  private final static String TAG = ScanWorkActivity.class.getName();

  private String procEmp;

  private String kind;
  private String locNo;
  private String docNo;

  private TextView titleTv;
  private TextView locationTv;
  private TextView docNoTv;
  private TextView scanCountTv;

  //private MyApplication myApplication;
  private MyStore myStore;

  private boolean isLocationMove;

  private ListView barcodeLv;

  private EditText barcodeEt;

  private SharedPreferences mPreferences;

  private Location location;

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  //String scanDate= DateFormat.format("yyyyMMddHHmmss", new Date()).toString();

  private MyDao myDao;
  private SQLiteDatabase db;

  private ArrayAdapter<String> myAdapter;
  private MiaAdapter miaAdapter;

  private String[] mdArr = null;

  private List<String> barcodeList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scan_work);

    // ----------------------------------------------------
    //myApplication = (MyApplication) getApplication();
    myStore = MyStore.getInstance(this);

    this.db = DbHelper.getInstance(this).getDb();
    this.myDao = new MyDao(this);

    mPreferences = getSharedPreferences(MyInfo.SPKey, Context.MODE_PRIVATE);

    // login user
    this.procEmp = mPreferences.getString("name", "");

    titleTv = findViewById(R.id.title);
    barcodeEt = findViewById(R.id.barcode);
    locationTv = findViewById(R.id.location);
    docNoTv = findViewById(R.id.docNo);
    scanCountTv = findViewById(R.id.scanCount);


    Bundle bundle = getIntent().getExtras();

    // david API 15
    // inputs
    this.kind = bundle.getString("kind");
    this.locNo = bundle.getString("locNo");
    this.docNo = myStore.getDocNo();

    // title
    //String title = myApplication.getKindName(kind);
    String title = InvUtil.getKindName(kind);
    titleTv.setText(title);

    // 庫位移轉不顯示 盤點單號
    // this.isLocationMove = myApplication.isLocationMove(kind);
    this.isLocationMove = InvUtil.isLocationMove(kind);

    if (isLocationMove) {
      docNoTv.setVisibility(View.INVISIBLE);
    } else {
      docNoTv.setText("盤點單號: " + this.docNo);
    }

    // 讀取 庫位資料
    AsyncTask.execute(() -> {
      this.location = this.myDao.getLocation(kind, locNo);
      runOnUiThread(() -> {
        String docNoRemark = location.getLocNo() + " - " + location.getRemark();
        locationTv.setText(docNoRemark);
      });
    });

    // -------------------------------------------------------
    barcodeLv = findViewById(R.id.barcodeLv);
    barcodeLv.setFocusable(false);

    AsyncTask<Void, Void, Boolean> mainDataLoadTask = new AsyncTask<Void, Void, Boolean>() {
      List<MainData> mainDataList;
      private String errMessage;

      @Override
      protected Boolean doInBackground(Void... params) {
        try {
          if (isLocationMove) {
            mainDataList = myDao.getMainDataList(kind, locNo);
          } else {
            mainDataList = myDao.getMainDataList(kind, locNo, docNo);
          }

          barcodeList = new ArrayList<>();
          for (MainData mainData : mainDataList) {
            barcodeList.add(mainData.getBarcode());
          }
          return true;
        } catch (Exception e) {
          e.printStackTrace();
          errMessage = e.getMessage();
          return false;
        }
      }

      @Override
      protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success) {
          //myAdapter = new ArrayAdapter(ScanWorkActivity.this, android.R.layout.simple_list_item_1, barcodeList);
          //barcodeLv.setAdapter(myAdapter);
          miaAdapter = new MiaAdapter(barcodeList);
          barcodeLv.setAdapter(miaAdapter);
          scanCountTv.setText("筆數: " + barcodeList.size());
        } else {
          Toast.makeText(ScanWorkActivity.this, this.errMessage, Toast.LENGTH_LONG)
              .show();
        }
      }
    };

    mainDataLoadTask.execute();

    //------------------------
    barcodeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
          v.requestFocus();
        }
      }
    });

    barcodeEt.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() != KeyEvent.ACTION_UP) {

          // empty check
          String barcode = ScanWorkActivity.this.barcodeEt.getText().toString().trim();
          if (TextUtils.isEmpty(barcode) || barcode.length() == 0) {
            String msg = getString(R.string.barcode_is_empty);
            showToast(msg);
            return false;
          }

          if (barcode.contains(",")) {
            String msg = "條碼中含有 \",\" 字元.";
            showToast(msg);
            return false;
          }

          insertMainData();
        }

        return false;
      }
    });

  }  // onCreate


  @Override
  protected void onResume() {
    super.onResume();

    /**  locNo is passin as bundle */
    if (this.kind == null) {
      this.kind = this.myStore.getKind();
    }

    if (this.docNo == null) {
      this.docNo = this.myStore.getDocNo();
    }

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  // click to add data
  public void onClickAdd(View view) {
    String barcode = this.barcodeEt.getText().toString().trim();
    if (TextUtils.isEmpty(barcode) || barcode.length() == 0) {
      String msg = getString(R.string.barcode_is_empty);
      showToast(msg);
      return;
    }

    if (barcode.contains(",")) {
      String msg = "條碼中含有 \",\" 字元.";
      showToast(msg);
      return ;
    }

    this.insertMainData();
  }

  /**
   * 新增資料
   */
  private void insertMainData() {
    String locNo = this.location.getLocNo();

    String barcode = this.barcodeEt.getText()
        .toString()
        .replaceAll("\r", "")
        .replaceAll("\n", "");


    AsyncTask<EditText, Void, Boolean> insertMainDataTask = new AsyncTask<EditText, Void, Boolean>() {
      EditText barcodeEt;
      MainData mainData;
      String message;

      @Override
      protected Boolean doInBackground(EditText... editTexts) {
        this.barcodeEt = editTexts[0];

        // barcode 存在檢查
        if (myDao.isExistMainData(barcode)) {
          message = barcode + " - 條碼已重複掃入! ";
          return false;
        }

        // 新增到 SQLite
        mainData = ScanWorkActivity.this.createMainData();
        boolean result = myDao.insertMainData(mainData);

        if (result) {
          return true;
        } else {
          message = "新增資料失敗!";
          return false;
        }
      }

      @Override
      protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if (result) {
          //ScanWorkActivity.this.barcodeList.add(mainData.getBarcode());
          //myAdapter.notifyDataSetChanged();
          ScanWorkActivity.this.barcodeList.add(0, mainData.getBarcode());
          miaAdapter.notifyDataSetChanged();
          updateDataCount(ScanWorkActivity.this.isLocationMove);  // 掃描筆數更改
        } else {
          showToast(message);
        }

        barcodeEt.setText("");
      }
    };

    insertMainDataTask.execute(this.barcodeEt);
  }

  // 產生  MainData bean
  private MainData createMainData() {
    MainData mainData = new MainData();

    mainData.setProcEmp(this.procEmp);
    mainData.setKind(myStore.getKind());
    mainData.setLocation(this.location.getLocNo());

    String _kind = myStore.getKind();
    //if (myApplication.isLocationMove(_kind)) {
    if (InvUtil.isLocationMove(_kind)) {
      mainData.setDocNo("");
    } else {
      mainData.setDocNo(myStore.getDocNo());
    }

    mainData.setBarcode(barcodeEt.getText().toString()
        .replaceAll("\r", "")
        .replaceAll("\n", ""));

    String scanDate = this.dateFormat.format(new java.util.Date());
    mainData.setScanDate(scanDate);

    return mainData;
  }

  // 掃描筆數更改
  private void updateDataCount(boolean isLocationMove) {
    AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
      private int scanCount;
      private String docNo;

      @Override
      protected Void doInBackground(Void... voids) {
        docNo = isLocationMove ? null : ScanWorkActivity.this.docNo;
        if (isLocationMove) {
          scanCount = myDao.countMainData(kind, locNo);
        } else {
          scanCount = myDao.countMainData(kind, locNo, docNo);
        }

        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ScanWorkActivity.this.scanCountTv.setText("筆數: " + scanCount);
      }
    };

    asyncTask.execute();
  }

  // inner class
  private class MiaAdapter extends BaseAdapter {
    List<String> barcodeList;

    public MiaAdapter(List<String> barcodeList) {
      this.barcodeList = barcodeList;
    }

    @Override
    public int getCount() {
      return barcodeList.size();
    }

    @Override
    public Object getItem(int position) {
      return barcodeList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return barcodeList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = convertView;

      if (view == null) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.row_barcode_scan, null);
      }

      String barcode = barcodeList.get(position);

      TextView text1 = view.findViewById(R.id.text1);
      int seq = barcodeList.size() - position;
      text1.setText(seq + "");

      TextView text2 = view.findViewById(R.id.text2);
      text2.setText(barcode);

      return view;
    }
  } // end inner class

  private void showToast(String msg) {
    Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
    LinearLayout toastLayout = (LinearLayout) toast.getView();
    TextView toastTV = (TextView) toastLayout.getChildAt(0);
    toastTV.setTextSize(20);

    toast.show();
  }


}  // end class
