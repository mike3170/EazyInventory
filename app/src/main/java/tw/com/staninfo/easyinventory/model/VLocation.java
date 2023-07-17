package tw.com.staninfo.easyinventory.model;

public class VLocation {
  private String tag;
  private long id;
  private String kind;
  private String locNo;
  private String remark;

  public VLocation() {
  }

  public VLocation(long id, String kind, String locNo, String remark) {
    this.id = id;
    this.kind = kind;
    this.locNo = locNo;
    this.remark = remark;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
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
    return "VLocation{" +
        "tag='" + tag + '\'' +
        ", id=" + id +
        ", kind='" + kind + '\'' +
        ", locNo='" + locNo + '\'' +
        ", remark='" + remark + '\'' +
        '}';
  }
}
