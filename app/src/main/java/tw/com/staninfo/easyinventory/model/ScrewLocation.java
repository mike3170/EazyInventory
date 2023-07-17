package tw.com.staninfo.easyinventory.model;

public class ScrewLocation {
  private long id;
  private String kind;
  private String locNo;
  private String remark;

  public ScrewLocation() {
  }

  public ScrewLocation(long id, String kind, String locNo, String remark) {
    this.id = id;
    this.kind = kind;
    this.locNo = locNo;
    this.remark = remark;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getLocNo() {
    return locNo;
  }

  public void setLocNo(String locNo) {
    this.locNo = locNo;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Override
  public String toString() {
    return "WirLocation{" +
        "id=" + id +
        ", kind='" + kind + '\'' +
        ", locNo='" + locNo + '\'' +
        ", remark='" + remark + '\'' +
        '}';
  }
}
