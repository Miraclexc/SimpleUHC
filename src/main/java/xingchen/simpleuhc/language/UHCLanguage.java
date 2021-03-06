package xingchen.simpleuhc.language;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class UHCLanguage {
    private static final UHCLanguage instance = new UHCLanguage();

    private Locale locale;
    private ResourceBundle resourceBundle;

    public UHCLanguage() {
        this.locale = Locale.getDefault();
        try {
            this.resourceBundle = ResourceBundle.getBundle("languages.lang", this.locale);
        } catch(MissingResourceException e) {
            this.locale = new Locale("zh", "CN");
            this.resourceBundle = ResourceBundle.getBundle("languages.lang", this.locale);
        }
    }

    public String translate(String raw) {
        if(resourceBundle == null) {
            return raw;
        }
        return this.resourceBundle.getString(raw);
    }

    public static UHCLanguage getInstance() {
        return instance;
    }
}
