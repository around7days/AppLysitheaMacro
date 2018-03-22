package jp.co.foresight;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class MainExecuteTest {

    // テスト対象クラス
    MainExecute main = new MainExecute();

    @Test
    public final void test_loadCsv_01() throws Exception {

        // 処理実行
        CsvDataBean csvData = main.loadCsv("test.csv");

        // 結果確認
        assertThat(csvData.targetYear, is("2018"));
        assertThat(csvData.targetMonth, is("03"));
        assertThat(csvData.name, is("FS太郎"));
        assertThat(csvData.id, is("ID01"));
        assertThat(csvData.pass, is("PASS01"));
        assertThat(csvData.loginOnlyFlg, is("0"));
        assertThat(csvData.projectRegistFlg, is("1"));

        assertThat(csvData.detailList.size(), is(3));

        assertThat(csvData.detailList.get(0).day, is("01"));
        assertThat(csvData.detailList.get(0).execFlg, is("1"));
        assertThat(csvData.detailList.get(0).workKbn, is("103"));
        assertThat(csvData.detailList.get(0).holidayKbn, is("休日"));
        assertThat(csvData.detailList.get(0).startTime, is("0900"));
        assertThat(csvData.detailList.get(0).endTime, is("2200"));
        assertThat(csvData.detailList.get(0).biko, is("備考1"));
        assertThat(csvData.detailList.get(0).pjCd1, is("PJ01"));
        assertThat(csvData.detailList.get(0).pjCd2, is("PJ02"));
        assertThat(csvData.detailList.get(0).pjCd3, is("PJ03"));
        assertThat(csvData.detailList.get(0).pjTime1, is("0130"));
        assertThat(csvData.detailList.get(0).pjTime2, is("0230"));
        assertThat(csvData.detailList.get(0).pjTime3, is("0330"));

        assertThat(csvData.detailList.get(1).day, is("02"));
        assertThat(csvData.detailList.get(1).execFlg, is(""));
        assertThat(csvData.detailList.get(1).workKbn, is("001"));
        assertThat(csvData.detailList.get(1).holidayKbn, is(""));
        assertThat(csvData.detailList.get(1).startTime, is("1000"));
        assertThat(csvData.detailList.get(1).endTime, is("2300"));
        assertThat(csvData.detailList.get(1).biko, is("備考2"));
        assertThat(csvData.detailList.get(1).pjCd1, is("PJ11"));
        assertThat(csvData.detailList.get(1).pjCd2, is(""));
        assertThat(csvData.detailList.get(1).pjCd3, is(""));
        assertThat(csvData.detailList.get(1).pjTime1, is("1130"));
        assertThat(csvData.detailList.get(1).pjTime2, is(""));
        assertThat(csvData.detailList.get(1).pjTime3, is(""));

        assertThat(csvData.detailList.get(2).pjCd3, is("PJ03"));
        assertThat(csvData.detailList.get(2).pjTime3, is("0330"));
    }

    @Test
    public final void test_getPjCdSet_01() throws IOException {

        // テストデータ定義
        List<DetailBean> list = new ArrayList<>();
        DetailBean detail1 = new DetailBean();
        detail1.pjCd1 = "PJ01";
        detail1.pjCd2 = "PJ02";
        detail1.pjCd3 = "PJ03";
        DetailBean detail2 = new DetailBean();
        detail2.pjCd1 = "PJ11";
        detail2.pjCd2 = "PJ02";
        detail2.pjCd3 = "";
        list.add(detail1);
        list.add(detail2);

        // 処理実行
        Set<String> pjCdList = main.getPjCdSet(list);

        // 結果確認
        assertThat(pjCdList.size(), is(4));
        assertThat(pjCdList, is(hasItems("PJ01", "PJ02", "PJ03", "PJ11")));
    }
}
