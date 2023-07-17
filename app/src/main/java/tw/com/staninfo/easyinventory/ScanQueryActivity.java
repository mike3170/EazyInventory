package tw.com.staninfo.easyinventory;

import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import tw.com.staninfo.easyinventory.model.MainData;
import tw.com.staninfo.easyinventory.repo.MyDao;
import tw.com.staninfo.easyinventory.utils.InvUtil;

public class ScanQueryActivity extends AppCompatActivity {
  private MyApplication myApplication;

  private TextView scanCountTv;
  private ListView queryLv;

  private List<MainData> mainDataList;

  private MyDao myDao;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scan_query);

    //-------------------------------
    myApplication = (MyApplication) this.getApplication();

    scanCountTv = (TextView) findViewById(R.id.scanCount);
    queryLv = (ListView) findViewById(R.id.queryListView);

    myDao = new MyDao(this);

    this.init();
  }

  private void init() {
    try {
      LoadTask loadTask = new LoadTask();
      loadTask.execute();
    } catch (Exception ex) {
      ex.printStackTrace();
      Toast toast = Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
      toast.show();
    } finally {
    }
  }

  private class LoadTask extends AsyncTask<Void, Void, Boolean> {
    private String errMessage;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      try {
        mainDataList = myDao.getMainDataList();

      } catch (Exception e) {
        this.errMessage = e.getMessage();
        return false;
      }

      return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
      super.onPostExecute(aBoolean);
      if (aBoolean) {
        scanCountTv.setText(String.valueOf(mainDataList.size()) + " 筆");

        MyAdapter myAdapter = new MyAdapter();
        queryLv.setAdapter(myAdapter);
      } else {
        Toast.makeText(
            ScanQueryActivity.this,
            this.errMessage,
            Toast.LENGTH_LONG).show();
      }
    }
  } // end task

  // inner class
  private class MyAdapter extends BaseAdapter {
    @Override
    public int getCount() {
      return mainDataList.size();
    }

    @Override
    public Object getItem(int position) {
      return mainDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
      return mainDataList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = convertView;

      if (view  == null) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.row_scan_query, null);
      }

      TextView tv1 = view.findViewById(R.id.tv1);
      tv1.setPadding(0, 0,3, 0);

      TextView tv2 = view.findViewById(R.id.tv2);
      tv2.setPadding(0, 0,3, 0);

      TextView tv3 = view.findViewById(R.id.tv3);
      tv3.setPadding(0, 0,3, 0);

      TextView tv4 = view.findViewById(R.id.tv4);
      tv4.setPadding(0, 0,0, 0);

      MainData mainData = mainDataList.get(position);

      String kind = mainData.getKind();
      //String kindName = myApplication.getKindName(kind);
      //String kindNameShort = myApplication.getKindNameShort(kind);
      //System.out.println("mia");
      String kindNameShort = InvUtil.getKindNameShort(kind);

      tv1.setText(kindNameShort);
      tv2.setText(mainData.getDocNo());
      tv3.setText(mainData.getLocation());
      tv4.setText(mainData.getBarcode());

      return view;
    }
  } // end class

} // end class
