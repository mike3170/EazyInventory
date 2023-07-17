package tw.com.staninfo.easyinventory;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import tw.com.staninfo.easyinventory.model.Actions.Action;
import tw.com.staninfo.easyinventory.repo.MyStore;
import tw.com.staninfo.easyinventory.utils.InvUtil;

/**
 *  輸入盤點單號, 來源
 */
public class DocNoKindActivity extends AppCompatActivity {
  // private MyApplication myApplication;
  private MyStore myStore;

  private TextView docNoLabel;
  private EditText docNoEt;
  private Action currAction;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_docno_kind);

    this.init();
  }


  void init() {
    //this.myApplication = (MyApplication) this.getApplication();
    this.myStore = MyStore.getInstance(this);

    this.docNoLabel = this.findViewById(R.id.docNoLabel);
    this.docNoEt = this.findViewById(R.id.docNo);

    TextView title = this.findViewById(R.id.title);

    // currAction = this.myApplication.getCurrAction();
    currAction = this.myStore.getCurrAction();

    if (currAction == Action.LOCATOIN_MOVE) {
      title.setText("庫位移轉");
      this.docNoEt.setEnabled(false);
      this.docNoLabel.setEnabled(false);
    } else {
      title.setText("盤點");
      this.docNoEt.setEnabled(true);
      this.docNoLabel.setEnabled(true);
    }

    //----
    RadioGroup radioGroupSrc = this.findViewById(R.id.rg1);

    // not yet, for future
    //this.findViewById(R.id.finishedSkid).setEnabled(false);
    //this.findViewById(R.id.oddments).setEnabled(false);

    //----------------------
    Button locationBtn = this.findViewById(R.id.locationBtn);
    locationBtn.setOnClickListener(view -> {
      // Action action = myApplication.getCurrAction();
      Action action = myStore.getCurrAction();
      if (action == Action.INVENTORY) {
        String docNo = docNoEt.getText().toString();
        if (TextUtils.isEmpty(docNo) || docNo.length() == 0) {
          this.showToast("盤點,請輸入盤點單號");
          return;
        }
      }

      int checkedRadioButtonId = radioGroupSrc.getCheckedRadioButtonId();
      if (checkedRadioButtonId == -1) {
        this.showToast("請選擇來源");
      } else {
        this.setupKindAndDocNo(checkedRadioButtonId, docNoEt.getText().toString());
        Intent intent = new Intent(this, LocationListActivity.class);
        startActivity(intent);
      }

    });
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (this.currAction == null) {
      // this.currAction = this.myApplication.getCurrAction();
      this.currAction = this.myStore.getCurrAction();
    }
  }

  /**
   *
   * @param id      R.id.xxx  線材/鐵桶/成品/餘料/外購  radio button id
   * @param docNo
   */
  private void setupKindAndDocNo(int id, String docNo) {
    //Action currAction = this.myApplication.getCurrAction();
    Action currAction = this.myStore.getCurrAction();
    String kind = null;

    //kind = myApplication.getKindById(id);
    //kind = myStore.getKindById(id);

    kind = InvUtil.getKindById(id, currAction);

     //System.out.println("setupKindAndDocNo");
     //System.out.println("mia kind: " + kind);

    if (currAction == Action.LOCATOIN_MOVE) {
      docNo = "";
    }
    // myApplication.setKind(kind);
    // myApplication.setDocNo(docNo);
    myStore.setKind(kind);
    myStore.setDocNo(docNo);
  }

  private void showToast(String msg) {
    Toast toast = Toast.makeText(this, msg , Toast.LENGTH_LONG);
    LinearLayout layout = (LinearLayout) toast.getView();
    TextView toastTV = (TextView) layout.getChildAt(0);
    toastTV.setTextSize(20);
    toast.show();
  }
} // end class
