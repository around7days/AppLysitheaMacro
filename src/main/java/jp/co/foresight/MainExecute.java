package jp.co.foresight;

import static com.codeborne.selenide.Selenide.*;
import static jp.co.foresight.Constant.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

/**
 * メインクラス
 */
public class MainExecute {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(MainExecute.class);

    /** PropertyManager */
    private static final PropertyManager prop = PropertyManager.INSTANCE;

    /**
     * プログラム起動
     * @param args [0]：CSVファイル名
     */
    public static void main(String[] args) {
        logger.info("********** 処理開始 **********");

        try {
            // CSVファイル名の取得
            String csvFileNm = args.length > 1 ? args[0] : prop.getString("default.csv.file.name");
            new MainExecute().execute(csvFileNm);
        } catch (LogicException e) {
            logger.error("********** システムチェックエラー **********");
            System.exit(1);
        } catch (Exception e) {
            logger.error("例外エラー情報", e);
            logger.error("********** システム例外エラー **********");
            System.exit(1);
        }

        logger.info("********** 処理終了 **********");
    }

    /**
     * メイン処理
     * @param csvFileNm CSVファイル名
     * @throws IOException 例外エラー
     * @throws LogicException 例外エラー
     */
    private void execute(String csvFileNm) throws IOException, LogicException {
        // selenide設定
        setConfiguration();

        // CSVデータ読込
        CsvDataBean csvData = loadCsv(csvFileNm);

        // ログイン処理
        callLogin(csvData.id, csvData.name, csvData.pass);

        // ログイン以降の処理も継続かチェック
        if (!csvData.loginOnlyFlg.equals(FLG_ON)) {

            // メニュー処理
            callMenu();

            // プロジェクト登録処理
            if (csvData.projectRegistFlg.equals(FLG_ON)) {
                callProjectRegist(csvData.detailList);
            }

            // 勤務表一括登録処理
            callWorkRegist(csvData);
        }
    }

    /**
     * ログイン処理
     * @param id 社員ID
     * @param name 社員名
     * @param pass パスワード
     * @throws LogicException 例外エラー
     */
    private void callLogin(String id,
                           String name,
                           String pass) throws LogicException {
        logger.info("■ログイン処理開始");

        // ログイン画面起動
        open("http://lysithea.foresight.co.jp:82/Lysithea/JSP_Files/authentication/WC010_1.jsp");

        // 接続チェック
        if (!"リシテア".equals(title())) {
            logger.error("リシテアへの接続に失敗しました");
            throw new LogicException();
        }

        // 値設定
        $("[type=text][name=PersonCode]").val(id); // 社員ID
        $("[type=Password][name=Password]").val(pass); // パスワード

        // ログインクリック
        $("img[src$='logon.jpg']").click();

        // アラートダイアログクリック回避
        try {
            switchTo().alert().accept(); // WC01W010：パスワードの期限が切れています。パスワードを変更してください。
            switchTo().alert().accept(); // WC01W030：パスワードが初期パスワードから変更されていません。パスワードを変更してください。
        } catch (Exception e) {
            // アラートダイアログの表示は条件次第になるので、エラーが発生しても無視
        }

        // ログイン成功可否チェック
        if (!WebDriverRunner.url().contains("Main.jsp")) {
            logger.error("ログインに失敗しました");
            throw new LogicException();
        }

        // ユーザチェック
        if (!isMatchUser(name)) {
            logger.error("リシテア内のユーザ名と一致しません：{}", name);
            throw new LogicException();
        }

        logger.info("■ログイン処理終了");
    }

    /**
     * メニュー処理
     */
    private void callMenu() {
        logger.info("■メニュー処理開始");

        // フレーム切り替え
        switchTo().frame("MENU");

        // 作業票部分に前日以前と同じプロジェクトコード・作業コードを表示する
        $("[type=checkbox][name=costInputDivisionDisp]").click();

        // 個人用メニューメニューオープン
        $("#side01i").click();

        // フレーム切り替え
        switchTo().parentFrame();

        logger.info("■メニュー処理終了");
    }

    /**
     * プロジェクト登録処理
     * @param detailList 明細情報一覧
     */
    private void callProjectRegist(List<DetailBean> detailList) {
        logger.info("■プロジェクト登録処理開始");

        // フレーム切り替え
        switchTo().frame("MENU");

        // プロジェクトコード登録クリック
        $(By.linkText("◆プロジェクトコード登録")).click();

        // 待機
        sleep(prop.getLong("wait.time.short"));

        // フレーム切り替え
        switchTo().parentFrame();
        switchTo().frame("OPERATION");

        // プロジェクトコード一覧の取得
        Set<String> pjCdList = getPjCdSet(detailList);

        // 選択用プロジェクト一覧を取得
        List<String> beforeList = $$("select[name=cmbCostNoL] option").stream()
                                                                      .map(ele -> ele.getText())
                                                                      .map(Util::removeCRLF)
                                                                      .map(Util::removeBlank)
                                                                      .collect(Collectors.toList());
        logger.debug("※選択用プロジェクト一覧はログレベル「trace」で出力");
        logger.trace("↓↓↓ 選択用プロジェクト一覧 ↓↓↓");
        beforeList.forEach(logger::trace);
        logger.trace("↑↑↑ 選択用プロジェクト一覧 ↑↑↑");

        // 登録済みプロジェクト一覧を取得
        List<String> afterList = $$("select[name=cmbCostNoR] option").stream()
                                                                     .map(ele -> ele.getText())
                                                                     .map(Util::removeCRLF)
                                                                     .map(Util::removeBlank)
                                                                     .collect(Collectors.toList());
        logger.debug("登録済みプロジェクト一覧：{}", afterList);

        // プロジェクト登録処理
        for (String pjCd : pjCdList) {
            // 登録済みプロジェクト一覧
            if (afterList.stream().anyMatch(val -> val.contains(pjCd))) {
                // 選択結果に存在する場合は何もせずコンテニュー
                logger.info("プロジェクトコード登録済み：{}", pjCd);
                continue;
            }

            // 選択用プロジェクト一覧に存在するかチェック
            if (beforeList.stream().anyMatch(val -> val.contains(pjCd))) {
                // 存在する場合は選択して追加クリック
                logger.info("プロジェクトコード登録：{}", pjCd);
                $("select[name=cmbCostNoL]").selectOptionContainingText(pjCd);
                $("img[src$='migi_tsuika.jpg']").click();
            } else {
                // 存在しない場合は警告ログ出力
                logger.warn("プロジェクトコード登録失敗：{}", pjCd);
            }
        }

        // フレーム切り替え
        switchTo().parentFrame();

        logger.info("■プロジェクト登録処理終了");
    }

    /**
     * 勤務表一括登録処理
     * @param csvData CSVデータ情報
     * @throws LogicException 例外エラー
     */
    private void callWorkRegist(CsvDataBean csvData) throws LogicException {
        logger.info("■勤務表一括登録処理開始");

        // フレーム切り替え
        switchTo().frame("MENU");

        // 勤務表一括登録クリック
        $(By.linkText("■勤務表一括登録")).click();

        // 待機
        sleep(prop.getLong("wait.time.long"));

        // フレーム切り替え
        switchTo().parentFrame();
        switchTo().frame("OPERATION");

        // 年月度の切換処理
        switchWorkRegistYm(csvData.targetYear, csvData.targetMonth);

        // 勤務表入力処理
        for (DetailBean detail : csvData.detailList) {
            if (!detail.execFlg.equals(FLG_ON)) {
                // 実行フラグがOFFの場合は何もせずにコンテニュー
                continue;
            }

            // 対象日付の生成
            String targetDate = csvData.targetMonth + "/" + detail.day;

            logger.info("対象日付のデータ入力開始:{}", targetDate);

            // 対象日付の行エレメント(trタグ)を取得
            SelenideElement trElement = $(By.partialLinkText(targetDate)).closest("tr");

            // 選択チェックボックスクリック
            trElement.find("input[name=targetChk]").click();

            // 勤務区分セレクトボックスの選択＋勤務区分クリック
            $("select[name=workRuleList]").selectOptionContainingText(detail.workKbn);
            trElement.find("input[name=workRuleNameDisp]").click();

            // 休暇区分セレクトボックスの選択＋休暇区分クリック
            $("select[name=holidayList]").selectOption(detail.holidayKbn);
            trElement.find("input[name=holidayNameDisp]").click();

            // 始業時刻入力
            trElement.find("input[name=attStartTime]").setValue(detail.startTime);

            // 就業時刻入力
            trElement.find("input[name=attEndTime]").setValue(detail.endTime);

            // プロジェクトコード・作業・時間のエレメント取得
            ElementsCollection pjCdElement = trElement.findAll("input[name=costNoNameDisp]");
            ElementsCollection pjCostElement = trElement.findAll("input[name=costDetailCode]");
            ElementsCollection pjTimeElement = trElement.findAll("input[name=costQuantity]");

            // プロジェクトコード・作業・時間 その1
            $("select[name=costNoList]").selectOptionContainingText(detail.pjCd1);
            pjCdElement.get(0).click();
            if (detail.pjCd1.isEmpty()) {
                pjCostElement.get(0).setValue("");
                pjTimeElement.get(0).setValue("");
            } else {
                pjCostElement.get(0).setValue("00");
                pjTimeElement.get(0).setValue(detail.pjTime1);
            }

            // プロジェクトコード・作業・時間 その2
            $("select[name=costNoList]").selectOptionContainingText(detail.pjCd2);
            pjCdElement.get(1).click();
            if (detail.pjCd2.isEmpty()) {
                pjCostElement.get(1).setValue("");
                pjTimeElement.get(1).setValue("");
            } else {
                pjCostElement.get(1).setValue("00");
                pjTimeElement.get(1).setValue(detail.pjTime2);
            }

            // プロジェクトコード・作業・時間 その3
            $("select[name=costNoList]").selectOptionContainingText(detail.pjCd3);
            pjCdElement.get(2).click();
            if (detail.pjCd3.isEmpty()) {
                pjCostElement.get(2).setValue("");
                pjTimeElement.get(2).setValue("");
            } else {
                pjCostElement.get(2).setValue("00");
                pjTimeElement.get(2).setValue(detail.pjTime3);
            }
        }

        // 変更／取消ボタンクリック
        $("img[src$='henko_torikeshi.jpg']").click();

        // フレーム切り替え
        // switchTo().parentFrame();

        logger.info("■勤務表一括登録処理終了");
    }

    /**
     * CSVデータ読込<br>
     * 文字コードはSJIS固定
     * @param csvFileNm CSVファイル名
     * @return CSVデータ情報
     * @throws IOException 例外エラー
     * @throws LogicException 例外エラー
     */
    protected CsvDataBean loadCsv(String csvFileNm) throws IOException, LogicException {
        logger.info("■CSVデータ読込開始");

        logger.info("csvファイル名：{}", csvFileNm);
        File csvFile = Util.getResource(csvFileNm);

        if (csvFile == null) {
            // ファイルが存在しない場合はエラー
            logger.error("CSVファイルが見つかりません：{}", csvFileNm);
            throw new LogicException();
        }

        // CSVデータ情報
        CsvDataBean csvData = new CsvDataBean();

        try (Stream<String> stream = Files.lines(csvFile.toPath(), Charset.forName("MS932"))) {
            stream.filter(line -> (line.startsWith(DATA_KBN_H) || line.startsWith(DATA_KBN_D))) // ヘッダ情報 or 明細情報
                  .peek(logger::debug)
                  .map(line -> line.split(",", -1)) // カンマで分割
                  .forEach(data -> {
                      switch (data[0]) {
                      case DATA_KBN_H: // ヘッダ情報
                          csvData.targetYear = data[1];
                          csvData.targetMonth = data[2];
                          csvData.name = data[3];
                          csvData.id = data[4];
                          csvData.pass = data[5];
                          csvData.loginOnlyFlg = data[6];
                          csvData.projectRegistFlg = data[7];
                          break;
                      case DATA_KBN_D: // 明細情報
                          // 明細情報の生成
                          DetailBean detail = new DetailBean();
                          detail.day = data[1];
                          detail.execFlg = data[2];
                          detail.workKbn = data[3];
                          detail.holidayKbn = data[4];
                          detail.startTime = data[5];
                          detail.endTime = data[6];
                          detail.biko = data[7];
                          detail.pjCd1 = data[8];
                          detail.pjTime1 = data[9];
                          detail.pjCd2 = data[10];
                          detail.pjTime2 = data[11];
                          detail.pjCd3 = data[12];
                          detail.pjTime3 = data[13];

                          // 勤務情報一覧に追加
                          csvData.detailList.add(detail);
                          break;
                      }
                  });
        }

        logger.debug("CSVデータ：{}", csvData.toString());
        logger.info("■CSVデータ読込終了");
        return csvData;
    }

    /**
     * プロジェクトコード一覧の取得（重複なし）<br>
     * @param detailList 明細情報一覧
     * @return プロジェクトコード一覧
     */
    protected Set<String> getPjCdSet(List<DetailBean> detailList) {

        // プロジェクトコード一覧生成
        Set<String> pjCdList = new TreeSet<>();
        detailList.forEach(detail -> {
            pjCdList.add(detail.pjCd1);
            pjCdList.add(detail.pjCd2);
            pjCdList.add(detail.pjCd3);
        });
        pjCdList.remove("");

        logger.debug("プロジェクトコード一覧：{}", pjCdList);
        return pjCdList;
    }

    /**
     * 年月度の切換処理
     * @param targetYear 対象年
     * @param targetMonth 対象月
     * @throws LogicException 例外エラー
     */
    private void switchWorkRegistYm(String targetYear,
                                    String targetMonth) throws LogicException {
        // 年月度の一致チェック
        String targetYm = targetYear + "/" + targetMonth;
        boolean b = isMatchYm(targetYm);
        if (!b) {
            // 年月度が一致しない場合は次月に切換
            logger.info("次月に切換");

            // 次月クリック
            $(By.partialLinkText("次月")).click();

            // 待機
            sleep(prop.getLong("wait.time.long"));

            // 年月度の一致チェック・再
            b = isMatchYm(targetYm);
            if (!b) {
                // 年度が一致しない場合はエラーとして処理終了
                logger.error("年月度が一致しません：{}", targetYm);
                throw new LogicException();
            }
        }
    }

    /**
     * 年月度の一致チェック
     * @param targetYm 対象年月
     * @return 結果[true:一致 false:不一致]
     */
    private boolean isMatchYm(String targetYm) {

        // yyyy/mm形式の値を取得
        Optional<String> dispYm = $$("form[name=FORM_COMMON] td").stream()
                                                                 .map(ele -> ele.getText())
                                                                 .map(Util::removeCRLF)
                                                                 .map(Util::removeBlank)
                                                                 .filter(StringUtils::isNotEmpty)
                                                                 .peek(logger::trace)
                                                                 .filter(val -> val.matches("..../.."))
                                                                 .findFirst();
        // 取得した値の比較
        boolean b = dispYm.isPresent() && targetYm.equals(dispYm.get());

        logger.debug("対象年月：{}　|　画面上の年月：{}", targetYm, dispYm.orElse(""));
        logger.info("年月度の一致チェック結果：{}", b);
        return b;
    }

    /**
     * ユーザ名一致チェック
     * @param userNm ユーザ名
     * @return 結果[true:一致 false:不一致]
     * @throws LogicException 例外エラー
     */
    private boolean isMatchUser(String userNm) throws LogicException {

        // フレーム切り替え
        switchTo().frame("MENU");

        // ユーザ名らしき一覧の取得
        List<String> maybeUserNmList = $$("form[name=FORM_COMMON] td").stream()
                                                                      .map(ele -> ele.getText())
                                                                      .map(Util::removeCRLF)
                                                                      .map(Util::removeBlank)
                                                                      .filter(StringUtils::isNotEmpty)
                                                                      .collect(Collectors.toList());
        boolean b = maybeUserNmList.contains(Util.removeBlank(userNm));

        // フレーム切り替え
        switchTo().parentFrame();

        logger.debug("対象ユーザ名：{}　|　画面上のユーザ名らしき一覧：{}", userNm, maybeUserNmList);
        logger.info("ユーザ名一致チェック結果：{}", b);
        return b;
    }

    /**
     * selenide設定
     */
    private void setConfiguration() {
        logger.info("■selenide設定開始");

        // ブラウザ指定
        Configuration.browser = prop.getString("browser");
        // ブラウザサイズ
        Configuration.browserSize = prop.getString("browser.size");
        // ブラウザを閉じない
        Configuration.holdBrowserOpen = true;
        // キー入力高速化
        Configuration.fastSetValue = true;

        logger.debug("ブラウザ：", Configuration.browser);
        logger.debug("ブラウザサイズ：", Configuration.browserSize);
        logger.info("■selenide設定終了");
    }

}
