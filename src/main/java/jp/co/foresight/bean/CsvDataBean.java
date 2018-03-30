package jp.co.foresight.bean;

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
    public List<CsvDataDetailBean> detailList = new ArrayList<>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
