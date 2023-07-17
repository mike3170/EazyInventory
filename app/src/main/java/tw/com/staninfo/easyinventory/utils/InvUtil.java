package tw.com.staninfo.easyinventory.utils;

import tw.com.staninfo.easyinventory.R;
import tw.com.staninfo.easyinventory.model.Actions;
import tw.com.staninfo.easyinventory.model.Actions.Action;

/**
 * 1. getKindName
 * 2. getKindNameShort
 * 3. isLocationMove
 * 4. getKindById(int id, Action currAction)
 *
 * 來源(kind)
 * 1:線材庫位移轉  2:線材卷號盤點 wire
 * 3.鐵桶庫位移轉  4:螺絲鐵桶盤點 metal bucket
 * 5.成品庫位移轉  6:成品棧板盤點 finished goods
 * 7.餘料庫位移轉  8:餘料庫存盤點 oddments
 * 9.外購庫位移轉  A:外購庫存盤點 outsource
 *
 *  盤點單號 length: 6
 *  barcode length: 14
 */
public class InvUtil {
  public static String getKindName(String kind) {
    switch (kind) {
      case "1" :
        return "線材庫位移轉";
      case "2":
        return "線材卷號盤點";

      case "3":
        return "鐵桶庫位移轉";
      case "4":
        return "螺絲鐵桶盤點";

      case "5":
        return "剪力釘庫位移轉";
      case "6":
        return "剪力釘盤點";

      case "7":
        return "螺帽庫位移轉";
      case "8":
        return "螺帽盤點";

      case "9":
        return "外購庫位移轉(華司)";
      case "A":
        return "外購庫存盤點(華司)";

      case "B":
        return "組合品庫位移轉";
      case "C":
        return "組合品盤點";

      case "D":
        return "成品庫位移轉";
      case "E":
        return "成品棧板盤點";

      case "F":
        return "餘料庫位移轉";
      case "G":
        return "餘料庫存盤點";

      default:
        return "NA";
    }
  }

  public static String getKindNameShort(String kind) {
    switch (kind) {
      case "1" :
        return "線材移";
      case "2":
        return "線材盤";

      case "3":
        return "鐵桶移";
      case "4":
        return "鐵桶盤";

      case "5":
        return "剪力釘移";
      case "6":
        return "剪力釘盤";

      case "7":
        return "螺帽移";
      case "8":
        return "螺帽盤";

      case "9":
        return "外購移";
      case "A":
        return "外購盤";

      case "B":
        return "組合品移";
      case "C":
        return "組合品盤";

      case "D":
        return "成品移";
      case "E":
        return "成品盤";

      case "F":
        return "餘料移";
      case "G":
        return "餘料盤";

      default:
        return "NA";
    }
  }
  /**
   * 是否是 庫位移轉?
   * @param kind
   * @return
   */
  public static boolean isLocationMove(String kind) {
    boolean bln = false;

    if (kind.equals("1") ||
        kind.equals("3") ||
        kind.equals("5") ||
        kind.equals("7") ||
        kind.equals("9") ||
        kind.equals("B") ||
        kind.equals("D") ||
        kind.equals("F") ) {
      bln = true;
    } else {
      bln = false;
    }

    return bln;
  }

  /**
   * 取得 kind
   * @param id  R.id.xxx
   * @return kind
   */
  public static String getKindById(int id, Action currAction) {
    String kind = null;

    // 庫位移轉
    if (currAction == Actions.Action.LOCATOIN_MOVE) {
      if (id == R.id.wire) {
        kind = "1";
      } else if (id == R.id.metalBucket) {
        kind = "3";
      } else if (id == R.id.shearNail) {
        kind = "5";
      } else if (id == R.id.screwNut) {
        kind = "7";
      } else if (id == R.id.outsource) {
        kind = "9";
      } else if (id == R.id.combination) {
        kind = "B";
      } else if (id == R.id.finishedSkid) {
        kind = "D";
      } else if (id == R.id.oddments) {
        kind = "F";
      }
//      switch (id) {
//        case R.id.wire :
//          kind = "1";
//          break;
//        case R.id.metalBucket :
//          kind = "3";
//          break;
//        case R.id.shearNail:
//          kind = "5";
//          break;
//        case R.id.screwNut :
//          kind = "7";
//          break;
//        case R.id.outsource :
//          kind = "9";
//          break;
//        case R.id.combination :
//          kind = "B";
//          break;
//        case R.id.finishedSkid :
//          kind = "D";
//          break;
//        case R.id.oddments :
//          kind = "F";
//          break;
//      }
    } else {
      // 盤點
      if (id == R.id.wire) {
        kind = "2";
      } else if (id == R.id.metalBucket) {
        kind = "4";
      } else if (id == R.id.shearNail) {
        kind = "6";
      } else if (id == R.id.screwNut) {
        kind = "8";
      } else if (id == R.id.outsource) {
        kind = "A";
      } else if (id == R.id.combination) {
        kind = "C";
      } else if (id == R.id.finishedSkid) {
        kind = "E";
      } else if (id == R.id.oddments) {
        kind = "G";
      }
//      switch (id) {
//        case R.id.wire:
//          kind = "2";
//          break;
//        case R.id.metalBucket:
//          kind = "4";
//          break;
//        case R.id.shearNail :
//          kind = "6";
//          break;
//        case R.id.screwNut :
//          kind = "8";
//          break;
//        case R.id.outsource :
//          kind = "A";
//          break;
//        case R.id.combination :
//          kind = "C";
//          break;
//        case R.id.finishedSkid :
//          kind = "E";
//          break;
//        case R.id.oddments :
//          kind = "G";
//          break;
//      }
    }

    return kind;
  }

} // end class
