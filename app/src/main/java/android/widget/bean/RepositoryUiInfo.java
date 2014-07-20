package android.widget.bean;

import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 19/07/2014.
 */
public class RepositoryUiInfo {

    public int id;
    public Iconify.IconValue icon;
    public int num;
    public int text;

    public RepositoryUiInfo(int id, Iconify.IconValue icon, int num, int text) {
        this.id = id;
        this.icon = icon;
        this.num = num;
        this.text = text;
    }
}
