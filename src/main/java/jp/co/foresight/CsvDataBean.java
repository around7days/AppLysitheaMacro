package jp.co.foresight;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * CSV情報
 */
public class CsvDataBean {

    /** 対象年 */
    public String targetYear;
    /** 対象月 */
    public String targetMonth;
    /** 社員名 */
    public String name;
    /** 社員ID */
    public String id;
    /** パスワード */
    public String pass;
    /** ログインのみフラグ（1:ログインのみ） */
    public String loginOnlyFlg;
    /** プロジェクト登録フラグ（1:登録する） */
    public String projectRegistFlg;
    /** 明細情報一覧 */
    public List<DetailBean> detailList = new ArrayList<>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

/**
 * 明細情報
 */
class DetailBean {

    /** 日付 */
    public String day;
    /** 実行フラグ */
    public String execFlg;
    /** 勤務区分 */
    public String workKbn;
    /** 休暇区分 */
    public String holidayKbn;
    /** 始業時刻 */
    public String startTime;
    /** 就業時刻 */
    public String endTime;
    /** 備考 */
    public String biko;
    /** プロジェクトコード1 */
    public String pjCd1;
    /** 時間1 */
    public String pjTime1;
    /** プロジェクトコード2 */
    public String pjCd2;
    /** 時間2 */
    public String pjTime2;
    /** プロジェクトコード3 */
    public String pjCd3;
    /** 時間3 */
    public String pjTime3;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}