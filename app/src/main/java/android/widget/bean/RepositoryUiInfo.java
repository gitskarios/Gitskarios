package android.widget.bean;

import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 19/07/2014.
 */
public class RepositoryUiInfo {

    private Iconify.IconValue icon;
    private int num;
    private String text;

    public RepositoryUiInfo(Iconify.IconValue icon, int num, String text) {
        this.icon = icon;
        this.num = num;
        this.text = text;
    }

    public Iconify.IconValue getIcon() {
        return icon;
    }

    public void setIcon(Iconify.IconValue icon) {
        this.icon = icon;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
