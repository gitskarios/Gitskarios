package com.alorma.yaml.formbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class YamlFormFactory {

  public YamlForm read(InputStream inputStream) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    int i;
    try {
      i = inputStream.read();
      while (i != -1) {
        byteArrayOutputStream.write(i);
        i = inputStream.read();
      }
      inputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String text = byteArrayOutputStream.toString();

    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    try {
      return objectMapper.readValue(text, YamlForm.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
