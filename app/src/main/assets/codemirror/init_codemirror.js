/**
 * This file contains portions from GitHub app for loading
 * correct syntax highlighting into CodeMirror source viewer.
 *
 * Copyright 2012 GitHub Inc.
 * Licensed under the Apache License, Version 2.0
 *
 * See https://raw.github.com/github/android/master/app/assets/source-editor.js
 */

function getExtension(name) {
  if (!name)
    return null;

  var lastDot = name.lastIndexOf(".");
  if (lastDot == -1 || lastDot + 1 == name.length)
    return null;
  else
    return name.substring(lastDot + 1).toLowerCase();
}

function getMode(extension) {
  var mode = {};
  if (!extension)
    return mode;

  switch (extension) {
  case "cc":
  case "h":
    mode.mode = "text/x-csrc";
    mode.file = "clike";
    break;
  case "clj":
    mode.mode = "text/x-clojure";
    mode.file = "clojure";
    break;
  case "coffee":
    mode.mode = "text/x-coffeescript";
    mode.file = "coffeescript";
    break;
  case "cpp":
    mode.mode = "text/x-c++src";
    mode.file = "clike";
    break;
  case "cs":
    mode.mode = "text/x-csharp";
    mode.file = "clike";
    break;
  case "css":
    mode.mode = "text/css";
    mode.file = "css";
    break;
  case "erl":
    mode.mode = "text/x-erlang";
    mode.file = "erlang";
    break;
  case "hs":
  case "hsc":
    mode.mode = "text/x-haskell";
    mode.file = "haskell";
    break;
  case "html":
    mode.mode = "text/html";
    mode.file = "htmlmixed";
    break;
  case "ini":
  case "prefs":
    mode.mode = "text/x-properties";
    mode.file = "properties";
    break;
  case "java":
    mode.mode = "text/x-java";
    mode.file = "clike";
    break;
  case "gyp":
  case "js":
  case "json":
    mode.mode = "text/javascript";
    mode.file = "javascript";
    break;
  case "md":
  case "markdown":
    mode.mode = "gfm";
    mode.file = "gfm";
    break;
  case "pl":
    mode.mode = "text/x-perl";
    mode.file = "perl";
    break;
  case "py":
    mode.mode = "text/x-python";
    mode.file = "python";
    break;
  case "r":
    mode.mode = "text/x-rsrc";
    mode.file = extension;
    break;
  case "rb":
    mode.mode = "text/x-ruby";
    mode.file = "ruby";
    break;
  case "sh":
  case "zsh":
    mode.mode = "text/x-sh";
    mode.file = "shell";
    break;
  case "sql":
    //TODO: multiple MIME types defined in mode/sql.js
    mode.mode = "text/x-mysql";
    mode.file = "sql";
    break;
  case "xq":
  case "xqy":
  case "xquery":
    mode.mode = "application/xquery";
    mode.file = "xquery";
    break;
  case "project":
  case "classpath":
  case "xib":
  case "xml":
    mode.mode = "application/xml";
    mode.file = "xml";
    break;
  case "yml":
    mode.mode = "text/x-yaml";
    mode.file = "yaml";
    break;
  case "v":
  case "vh":
  case "sv":
  case "svh":
    mode.mode = "text/x-verilog";
    mode.file = "verilog";
    break;
  case "f":
  case "f90":
    mode.mode = "text/x-fortran";
    mode.file = "fortran";
    break;
  case "m":
    mode.mode = "text/x-octave";
    mode.file = "octave";
    break;
  case "apl":
    mode.mode = "text/apl";
    mode.file = extension;
    break;
  case "cbl":
  case "cob":
    mode.mode = "text/x-cobol";
    mode.file = "cobol";
    break;
  case "d":
    mode.mode = "text/x-d";
    mode.file = extension;
    break;
  case "dtd":
    mode.mode = "application/xml-dtd";
    mode.file = extension;
    break;
  case "tpl":
    mode.mode = "text/x-smarty";
    mode.file = "smartymixed";
    break;
  case "ls":
    mode.mode = "text/x-livescript";
    mode.file = "livescript";
    break;
  case "jade":
    mode.mode = "text/x-jade";
    mode.file = extension;
    break;
  case "haml":
    mode.mode = "text/x-haml";
    mode.file = extension;
    break;
  case "sass":
  case "scss":
    mode.mode = "text/x-sass";
    mode.file = "sass";
    break;
  default:
    mode.mode = "text/x-" + extension;
    mode.file = extension;
  }
  return mode;
}

window.onload = function() {
  var name =  bitbeaker.getFilename();
  var extension = getExtension(name);

  CodeMirror.modeURL = "file:///android_asset/codemirror/mode/%N.js";

  var myCodeMirror = CodeMirror(document.body, {
    value: bitbeaker.getRawCode(),
    lineNumbers: true,
    readOnly: "nocursor"
  });

  var mode = getMode(extension);
  if (mode.mode)
    myCodeMirror.setOption("mode", mode.mode);
  if (mode.file)
    CodeMirror.autoLoadMode(myCodeMirror, mode.file);
  myCodeMirror.refresh();
}
