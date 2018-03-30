package jp.co.foresight.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * CSV明細情報
 */
public class CsvDataDetailBean {

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
