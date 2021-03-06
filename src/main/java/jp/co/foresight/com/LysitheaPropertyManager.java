package jp.co.foresight.com;

import java.util.ResourceBundle;

/**
 * PropertyManagerクラス<br>
 * （シングルトン）
 */
public enum LysitheaPropertyManager {
    INSTANCE;

    /** リソース名 */
    private static String propertyName = "Lysithea";

    /** プロパティ */
    private static ResourceBundle bundle = null;
    static {
        bundle = ResourceBundle.getBundle(propertyName);
    }

    /**
     * プロパティから値を取得
     * @param key キー
     * @return keyに対応する値
     */
    public String getString(String key) {
        return bundle.getString(key);
    }

    /**
     * プロパティから値を取得
     * @param key キー
     * @return keyに対応する値
     */
    public Long getLong(String key) {
        return Long.valueOf(bundle.getString(key));
    }

    /**
     * 値の存在チェック
     * @param key キー
     * @return keyに対応する値
     */
    public boolean isExist(String key) {
        if (!bundle.containsKey(key)) {
            return false;
        }
        if (bundle.getString(key).isEmpty()) {
            return false;
        }
        return true;
    }
}
