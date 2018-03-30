package jp.co.foresight.com;

import java.io.File;

/**
 * 共通関数
 */
public class LysitheaUtil {
    /**
     * 空白除去
     * @param val 文字列
     * @return 空白除去後の文字列
     */
    public static String removeBlank(String val) {
        return val.replaceAll("　", "").replaceAll(" ", "");
    }

    /**
     * 改行除去
     * @param val 文字列
     * @return 空白除去後の文字列
     */
    public static String removeCRLF(String val) {
        return val.replaceAll("\r\n", "").replaceAll("\n", "");
    }


    /**
     * クラスパス内から指定したファイルパスのリソースを取得<br>
     * 取得できない場合はnullを返却
     * @param filePath ファイルパス
     * @return ファイル
     */
    public static File getResource(String filePath) {
        try {
            File file = new File(LysitheaUtil.class.getClassLoader().getResource(filePath).toURI());
            return file;
        } catch (Exception e) {
            return null;
        }
    }
}
