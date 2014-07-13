package android.widget;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;

/**
 * Created by Bernat on 13/07/2014.
 */
public class NumericTitle extends TextView {

    private int number = 0;
    private int text = 0;

    public NumericTitle(Context context) {
        super(context);
        init();
    }

    public NumericTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumericTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NumericTitle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        isInEditMode();
    }

    public void setCustomNumber(int number) {
        this.number = number;
        setCustom();
    }
    public void setCustomText(int text) {
        this.text = text;
        setCustom();
    }

    private void setCustom() {
        String textS;
        if (text != 0) {
            textS = getResources().getString(text);
        } else if (isInEditMode()) {
            textS = "Demo";
        } else {
            textS = "--";
        }

        setGravity(Gravity.CENTER);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>");
        stringBuilder.append(number);
        stringBuilder.append("</b>");

        stringBuilder.append("<br />");
        stringBuilder.append(textS);

        setText(Html.fromHtml(stringBuilder.toString()));
    }

}
