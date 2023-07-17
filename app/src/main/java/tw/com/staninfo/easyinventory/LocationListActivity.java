package tw.com.staninfo.easyinventory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import tw.com.staninfo.easyinventory.model.Actions.Action;
import tw.com.staninfo.easyinventory.model.Location;
import tw.com.staninfo.easyinventory.repo.MyDao;
import tw.com.staninfo.easyinventory.repo.MyStore;
import tw.com.staninfo.easyinventory.utils.InvUtil;

/**
 * pass kind, locNo as bundle to next activity - scan_work
 */
public class LocationListActivity extends AppCompatActivity {
  private String kind;
  private boolean isLocationMove;

  private ListView mListView;

  // private MyApplication myApplication;
  private MyStore myStore;

  private MyDao myDao;

  List<Location> locationList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location_list);

    myStore = MyStore.getInstance(this);

    this.myDao = new MyDao(this);

    // title
    this.kind = myStore.getKind();
    String kindName = InvUtil.getKindName(kind);
    //System.out.println("kindName:" + kindName);

    TextView titleTv = this.findViewById(R.id.title);
    titleTv.setText(kindName);

    // listView
    mListView = findViewById(R.id.location_list);

    // 載入 庫位資料
    this.isLocationMove = InvUtil.isLocationMove(kind);
    LocationLoadTask loadTask = new LocationLoadTask(kind);
    loadTask.execute();


    mListView.setFocusable(false);

    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Location location = locationList.get(position);

        if (myStore.getCurrAction() == Action.LOCATOIN_MOVE) {
          myStore.setDocNo("");
        }

        Bundle bundle = new Bundle();

        bundle.putString("kind", myStore.getKind());
        bundle.putString("locNo", location.getLocNo());

        Intent intent = new Intent(LocationListActivity.this, ScanWorkActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
        finish();

      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (this.kind == null) {
      this.kind = this.myStore.getKind();
    }

    this.isLocationMove = InvUtil.isLocationMove(this.kind);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  /**
   * 讀取 庫位資料
   */
  private class LocationLoadTask extends AsyncTask<Void, Void, Boolean> {
    private boolean isLocationMove;
    private String kind;
    private String msg;

    public LocationLoadTask(String kind) {
      this.isLocationMove = isLocationMove;
      this.kind = kind;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      try {
        locationList = myDao.getLocationList(kind);

      } catch (Exception e) {
        this.msg = e.getMessage();
        return false;
      }

      return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
      super.onPostExecute(success);

      if (success) {
        MyAdapter myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);
      } else {
        showErrorDialog(msg);
      }
    }
  } // end task


  // inner class
  private class MyAdapter extends BaseAdapter {
    @Override
    public int getCount() {
      return locationList.size();
    }

    @Override
    public Object getItem(int position) {
      return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return locationList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = convertView;

      if (view  == null) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.row_2_columns, null);
      }

      Location location = locationList.get(position);

      TextView tv1 = view.findViewById(R.id.tv1);
      tv1.setText(location.getLocNo());

      TextView tv2 = (TextView) view.findViewById(R.id.tv2);
      tv2.setText(location.getRemark());

      return view;
    }
  } // end inner class


  private void showErrorDialog(String msg) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setTitle("error window")
        .setMessage(msg)
        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
          }
        });

    builder.create().show();

  }


} // end class
