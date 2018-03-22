package jp.co.foresight;

import java.util.ResourceBundle;

/**
 * PropertyManagerクラス<br>
 * （シングルトン）
 */
@SuppressWarnings("javadoc")
public enum PropertyManager {
    INSTANCE;

    /** リソース名 */
    private static String propertyName = "LysitheaMacro";

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

}
