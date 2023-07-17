package tw.com.staninfo.easyinventory;

import com.google.gson.reflect.TypeToken;

import com.koushikdutta.ion.Ion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import tw.com.staninfo.easyinventory.model.Actions.Action;
import tw.com.staninfo.easyinventory.model.MainData;
import tw.com.staninfo.easyinventory.model.ApiResponse;
import tw.com.staninfo.easyinventory.repo.DbHelper;
import tw.com.staninfo.easyinventory.repo.MyDao;
import tw.com.staninfo.easyinventory.repo.MyStore;

public class MainActivity extends AppCompatActivity {

  private final static String TAG = MainActivity.class.getName();

  private TextView mTextViewName;

  private SharedPreferences mPreferences;

  private Button mButtonUpdate;
  private Button mBtnScanQuery;

  private MyApplication mMyApplication;
  private MyStore myStore;

  private ProgressBar mProgressBar;

  private MyDao myDao;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //System.out.println("onCreated");

    //----------------------------------
    mMyApplication = (MyApplication) getApplication();

    myStore = MyStore.getInstance(this);
    myStore.setDocNo("");

    this.myDao = new MyDao(this);

    mButtonUpdate = findViewById(R.id.data_update_button);

    // david
    mBtnScanQuery = findViewById(R.id.btnScanQuery);

    mProgressBar = findViewById(R.id.progressBar);

    mPreferences = getSharedPreferences(MyInfo.SPKey, Context.MODE_PRIVATE);

    mTextViewName = findViewById(R.id.textViewName);
    mTextViewName.setText(mPreferences.getString("name", ""));

    // test button
    Button testBtn = (Button) findViewById(R.id.testBtn);
    // todo
    testBtn.setVisibility(View.INVISIBLE);
    //testBtn.setOnClickListener(this::doTest);

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  protected void onStart() {
    super.onStart();

    AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
      @Override
      protected Boolean doInBackground(Void... voids) {
        boolean isExist = myDao.isExistMainData();
        return isExist;
      }

      @Override
      protected void onPostExecute(Boolean isExist) {
        super.onPostExecute(isExist);
        mButtonUpdate.setEnabled(isExist);
        mBtnScanQuery.setEnabled(isExist);
      }
    };

    task.execute();
  }

  // BACK action
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      this.confirmLogout();
      return false;
    }
    return true;
  }

  @Override
  protected void onPause() {
    super.onPause();
    //System.out.println("onPause");
  }

  @Override
  protected void onResume() {
    super.onResume();
    //System.out.println("onResume");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.logout) {
      mPreferences.edit().putString("name", "").apply();
      //finish();
      //startActivity(new Intent(this, LoginActivity.class));
      this.doLogout(null);
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
//    switch (item.getItemId()) {
//      case R.id.logout:
//        mPreferences.edit().putString("name", "").apply();
//
//        this.doLogout(null);
//        return true;
//      default:
//        return super.onOptionsItemSelected(item);
//    }
  }

  private void showProgressBar(final boolean value) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (value) {
          getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
          mProgressBar.setVisibility(View.VISIBLE);
        } else {
          mProgressBar.setVisibility(View.INVISIBLE);
          getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
      }
    });
  }

  /**
   * 盤點
   */
  public void onClickInventory(View view) {
    myStore.setDocNo("");
    myStore.setCurrAction(Action.INVENTORY);

    Intent intent = new Intent(this, DocNoKindActivity.class);
    this.startActivity(intent);
  }


  /**
   * 庫位移轉
   * @param view
   */
  public void onClickLocationMove(View view) {
    myStore.setDocNo("");
    myStore.setCurrAction(Action.LOCATOIN_MOVE);

    Intent intent = new Intent(this, DocNoKindActivity.class);
    this.startActivity(intent);
  }

  /**
   * 資料上傳
   * @param view
   */
  public void onClickDataUpdate(View view) {
    if (! mMyApplication.isConnected()) {
      Toast.makeText(this, getString(R.string.check_network)
          , Toast.LENGTH_SHORT).show();
      return;
    }

    showProgressBar(true);
    new UserUploadTask().execute();
  }

  /**
   * 開始掃描 barcode
   * @param v
   */
  public void scanQuery(View v) {
    Intent intent = new Intent(this, ScanQueryActivity.class);
    this.startActivity(intent);
  }

  private void showLog(final int res) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(MainActivity.this
            , getString(res), Toast.LENGTH_SHORT).show();
      }
    });
  }

  /**
   * 登出
   */
  public void doLogout(View v) {
    mPreferences.edit().remove("name").commit();
    finishAffinity();
  }

  // 清除 main_data,暫用
  public void confirmClearData(View v) {
    final AlertDialog alertDialog = new AlertDialog.Builder(this)
        .setTitle("確定清除掃描資料?")
        .setPositiveButton("確定", new AlertDialog.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            clearScanData();
          }
        })
        .setNegativeButton("取消", new AlertDialog.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
          }
        })
        .create();

    alertDialog.show();;
  }

  void clearScanData() {
    SQLiteDatabase db = DbHelper.getInstance(this).getDb();

    AsyncTask<Void, Void, Boolean > clearTask = new AsyncTask<Void, Void, Boolean>() {
      String sql;
      String mesg;

      @Override
      protected Boolean doInBackground(Void... voids) {
        sql = "select count(*) from main_data";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
          mesg = "無資料, 不用清除";
          return true;
        }

        sql = "delete from main_data";
        db.execSQL(sql);

        sql = "select count(*) from main_data";
        Cursor countCusor = db.rawQuery(sql, null);
        countCusor.moveToFirst();
        count = countCusor.getInt(0);
        countCusor.close();

        if (count == 0) {
          mesg = "清除成功";
          return true;
        } else {
          mesg = "清除失敗";
          return false;
        }

      }

      @Override
      protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        if (success) {
          mBtnScanQuery.setEnabled(false);
          mButtonUpdate.setEnabled(false);
        }

        showToast(mesg);
      }
    };

    clearTask.execute();
  }

  /**
   * big text size
   *
   * @param msg
   */
  private void showToast(String msg) {
    Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
    LinearLayout toastLayout = (LinearLayout) toast.getView();
    TextView toastTV = (TextView) toastLayout.getChildAt(0);
    //toastTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.TEXT_SIZE));
    toastTV.setTextSize(20);

    toast.show();
  }

  private void showAlert(String message) {
    final AlertDialog alertDialog = new AlertDialog.Builder(this)
        .setTitle(message)
        .setPositiveButton("關閉視窗", new AlertDialog.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
          }
        })
        .create();

    alertDialog.show();
  }

  private void confirmLogout() {
    final AlertDialog alertDialog = new AlertDialog.Builder(this)
        .setTitle("確定要登出?")
        .setCancelable(false)
        .setPositiveButton("確定", new AlertDialog.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            MainActivity.this.doLogout(null);
          }
        })
        .setNegativeButton("取消", new AlertDialog.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
          }
        })
        .create();

    alertDialog.show();
  }

  private void beep() {
    MediaPlayer mp = MediaPlayer.create(this, R.raw.beep);
    mp.start();
  }

  /**
   * 上傳 async task
   */
  public class UserUploadTask extends AsyncTask<Void, Void, Boolean> {
    private int okRowCount = 0;
    private String errMessage = null;

    @Override
    protected void onCancelled() {
      super.onCancelled();
      showProgressBar(false);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
      super.onProgressUpdate(values);
      mProgressBar.setProgress(values.length);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
      String filename = DateFormat.format("yyyyMMddHHmmss-sss", new Date()).toString() + ".csv";

      File file = new File(getCacheDir().getPath(), filename);

      StringBuffer sb = null;
      FileWriter fileWriter = null;

      // step 1
      try {
        sb = new StringBuffer();
        fileWriter = new FileWriter(file);

        List<MainData> mainDataList = myDao.getMainDataList();

        for (MainData mainData : mainDataList) {
          // david add 6/24 2 lines
          String barcode = mainData.getBarcode();
          if (barcode == null || barcode.trim().isEmpty()) continue;

          sb.append(mainData.getProcEmp());
          sb.append(",");
          sb.append(mainData.getKind());
          sb.append(",");
          sb.append(mainData.getDocNo());
          sb.append(",");
          sb.append(mainData.getLocation());
          sb.append(",");
          sb.append(mainData.getBarcode());

          sb.append(",");
          sb.append(mainData.getScanDate());
          sb.append("\n");
        }

        fileWriter = new FileWriter(file);
        fileWriter.append(sb.toString());

      } catch (Exception ex) {
        this.errMessage = ex.getMessage();
        return false;

      } finally {
        try {
          fileWriter.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      // step 2
      ApiResponse<Integer> apiResponse = null;
      try {
        apiResponse = Ion.with(MainActivity.this)
            .load(MyInfo.API_URL + "upload")
            .uploadProgressBar(mProgressBar)
            .setMultipartFile("file", "application/csv", file)
            .as(new TypeToken<ApiResponse<Integer>>() {
            })
            .get();

      } catch (InterruptedException e) {
        this.errMessage = "網路發生錯誤";
        return false;
      } catch (ExecutionException e) {
        this.errMessage = "網路逾時";
        return false;
      } catch (Exception e) {
        this.errMessage = e.getMessage();
        return false;
      }

      // step 3
      if (apiResponse.status == ApiResponse.Status.ERROR) {
        this.errMessage = apiResponse.error.desc;
        return false;
      }

      // step 4
      // 處理筆數
      try {
        if (apiResponse.data == null) {
          throw new IllegalArgumentException("處理筆數錯誤, call stit.");
        }
        okRowCount = apiResponse.data;
      } catch (NumberFormatException e) {
        this.errMessage = e.getMessage();
        return false;
      } catch (Exception e) {
        this.errMessage = e.getMessage();
        return false;
      }

      // step 5
      try {
        boolean isOk = myDao.deleteMainData();
        if (!isOk) {
          this.errMessage = "刪除表格失敗";
          return false;
        }
      } catch (Exception ex) {
        this.errMessage = ex.getMessage();
        return false;
      }

      // ---------------------
      try {
        file.delete();
      } catch (Exception ex) {
        this.errMessage = "檔案刪除失敗!";
        return false;
      }

      showProgressBar(false);

      return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
      super.onPostExecute(success);
      showProgressBar(false);

      if (success) {
        String msg = okRowCount + " 筆資料, " + getString(R.string.upload_success);
        mButtonUpdate.setEnabled(false);
        mBtnScanQuery.setEnabled(false);
        showAlert(msg);
        beep();
      } else {
        showAlert(this.errMessage);
        beep();
      }
    }
  }  // end task

  // testing
  public void doTest(View v) {
    MyStore myStore = MyStore.getInstance(this);
    SharedPreferences sp = myStore.getSharedPreferences();

    Map<String, ?> map = sp.getAll();
    System.out.println(map);

  }

  private void foo() {
  }

}  // class
