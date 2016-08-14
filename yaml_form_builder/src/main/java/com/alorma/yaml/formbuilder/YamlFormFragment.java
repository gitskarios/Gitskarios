package com.alorma.yaml.formbuilder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.yaml.formbuilder.field.FormField;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class YamlFormFragment extends Fragment {

  private List<FormField> formFields;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.form_layout, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.form_recyler);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    YamlFormAdapter adapter = new YamlFormAdapter(getLayoutInflater(getArguments()));
    recyclerView.setAdapter(adapter);

    YamlForm yamlForm = new YamlFormFactory().read(getFormInputStream());
    formFields = createFormFields(yamlForm.yamlFormObjects);
    adapter.addAll(formFields);

    FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getContent();
      }
    });
  }

  private void getContent() {
    Map<String, Object> items = new HashMap<>();
    for (FormField formField : formFields) {
      String key = formField.getFormObject().key;
      Object value = formField.getValue();
      items.put(key, value);
    }

    formSubmitted(items);

  }

  protected abstract void formSubmitted(Map<String, Object> items);

  protected List<FormField> createFormFields(List<YamlFormObject> formObjects) {
    List<FormField> formFields = new ArrayList<>(formObjects.size());
    for (YamlFormObject formObject : formObjects) {
      formFields.add(new FormField(formObject));
    }
    return formFields;
  }

  protected abstract InputStream getFormInputStream();
}
