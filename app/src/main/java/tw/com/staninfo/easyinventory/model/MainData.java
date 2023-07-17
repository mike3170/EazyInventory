package tw.com.staninfo.easyinventory.model;

public class MainData {
  private long id;
  private String procEmp;        // empno
  private String kind;        // kind
  private String location;    //
  private String docNo;        // 盤點單號
  private String barcode;
  private String scanDate;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getDocNo() {
    return docNo;
  }

  public void setDocNo(String docNo) {
    this.docNo = docNo;
  }

  public String getProcEmp() {
    return procEmp;
  }

  public void setProcEmp(String procEmp) {
    this.procEmp = procEmp;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public String getScanDate() {
    return scanDate;
  }

  public void setScanDate(String scanDate) {
    this.scanDate = scanDate;
  }

  @Override
  public String toString() {
    return "MainData{" +
        "id=" + id +
        ", docNo='" + docNo + '\'' +
        ", procEmp='" + procEmp + '\'' +
        ", kind='" + kind + '\'' +
        ", location='" + location + '\'' +
        ", barcode='" + barcode + '\'' +
        ", scanDate='" + scanDate + '\'' +
        '}';
  }
}
