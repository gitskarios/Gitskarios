package com.alorma.yaml.formbuilder;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import com.alorma.yaml.formbuilder.field.FormField;
import java.util.List;

public class YamlFormAdapter extends RecyclerArrayAdapter<FormField, YamlFormAdapter.FormHolder> {

  private static final int TYPE_DEFAULT = 0;
  private static final int TYPE_TEXTFIELD = 1;
  private static final int TYPE_SELECTOR = 2;
  private static final int TYPE_BIG_TEXTFIELD = 3;
  private static final int TYPE_CHECKBOX = 4;

  public YamlFormAdapter(LayoutInflater inflater) {
    super(inflater);
  }

  @Override
  protected FormHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
    FormHolder holder;
    switch (viewType) {
      case TYPE_TEXTFIELD:
        holder = new TextFieldFormHolder(inflater.inflate(R.layout.form_textfield, parent, false));
        break;
      case TYPE_BIG_TEXTFIELD:
        holder = new BigTextFieldFormHolder(inflater.inflate(R.layout.form_big_textfield, parent, false));
        break;
      case TYPE_SELECTOR:
        holder = new SelectorFieldFormHolder(inflater.inflate(R.layout.form_selector, parent, false));
        break;
      case TYPE_CHECKBOX:
        holder = new CheckboxFieldFormHolder(inflater.inflate(R.layout.form_checkbox, parent, false));
        break;
      case TYPE_DEFAULT:
      default:
        holder = new EmptyFormHolder(new View(parent.getContext()));
    }
    return holder;
  }

  @Override
  protected int getItemViewType(FormField formField) {
    switch (formField.getFormObject().type) {
      case textfield:
        if (formField.getFormObject().params.multiline) {
          return TYPE_BIG_TEXTFIELD;
        } else {
          return TYPE_TEXTFIELD;
        }
      case selector:
        return TYPE_SELECTOR;
      case checkbox:
        return TYPE_CHECKBOX;
      default:
        return TYPE_DEFAULT;
    }
  }

  @Override
  protected void onBindViewHolder(FormHolder formHolder, FormField formField) {
    formHolder.fill(formField.getFormObject());
    formField.setHolder(formHolder);
  }

  private class SelectorFieldFormHolder extends FormHolder<String> {

    private final Spinner spinner;
    private ArrayAdapter<String> adapter;

    SelectorFieldFormHolder(View itemView) {
      super(itemView);
      spinner = (Spinner) itemView.findViewById(R.id.spinner);
    }

    @Override
    public void fill(YamlFormObject formItem) {
      List<String> values = formItem.params.values;
      adapter = new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, values);
      spinner.setAdapter(adapter);
    }

    @Override
    public String getValue() {
      return adapter.getItem(spinner.getSelectedItemPosition());
    }
  }

  private class CheckboxFieldFormHolder extends FormHolder<Boolean> {

    private final CheckBox checkBox;

    CheckboxFieldFormHolder(View itemView) {
      super(itemView);
      checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
    }

    @Override
    public void fill(YamlFormObject formItem) {
      checkBox.setText(formItem.label);
      checkBox.setChecked(formItem.params.checked);
    }

    @Override
    public Boolean getValue() {
      return checkBox.isChecked();
    }
  }

  private class TextFieldFormHolder extends FormHolder<String> {

    private final TextInputLayout editText;

    TextFieldFormHolder(View itemView) {
      super(itemView);
      editText = (TextInputLayout) itemView.findViewById(R.id.edit);
    }

    @Override
    public void fill(YamlFormObject formItem) {
      editText.setHint(formItem.params.hint);
    }

    @Override
    public String getValue() {
      return editText.getEditText().getText().toString();
    }
  }

  private class BigTextFieldFormHolder extends FormHolder<String> {

    private final TextInputLayout editText;

    BigTextFieldFormHolder(View itemView) {
      super(itemView);
      editText = (TextInputLayout) itemView.findViewById(R.id.edit);
    }

    @Override
    public void fill(YamlFormObject formItem) {
      editText.setHint(formItem.params.hint);
    }

    @Override
    public String getValue() {
      return editText.getEditText().getText().toString();
    }
  }

  private class EmptyFormHolder extends FormHolder<Void> {

    EmptyFormHolder(View itemView) {
      super(itemView);
    }

    @Override
    public void fill(YamlFormObject formItem) {

    }

    @Override
    public Void getValue() {
      return null;
    }
  }

  public abstract class FormHolder<K> extends RecyclerView.ViewHolder {

    FormHolder(View itemView) {
      super(itemView);
    }

    public abstract void fill(YamlFormObject formItem);

    public abstract K getValue();
  }
}
