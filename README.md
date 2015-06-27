[![Build Status](https://travis-ci.org/gitskarios/Gitskarios.svg?branch=develop)](https://travis-ci.org/gitskarios/Gitskarios)

# README #

## FORK ##

Thanks dev for forking Gitskarios!

Since now, if you want to use your own version of Gitskarios, you should generate specific App on Github

[Github OAuth](https://developer.github.com/v3/oauth/).

    If you want distinct apps for debug and release, you should generate two applications.

In order to use this sdk, you must enable three metadata keys in your `AndroidManifest.xml`

``` groovy
<meta-data
    android:name="com.alorma.github.sdk.client"
    android:value="@string/gh_client_id"/>

<meta-data
    android:name="com.alorma.github.sdk.secret"
    android:value="@string/gh_client_secret"/>

<meta-data
    android:name="com.alorma.github.sdk.oauth"
    android:value="@string/gh_client_callback"/>
```

In Gitskarios, this keys are hidden from repository, so you should create your own string files, or place it directly in `value`

## LICENSE ##

The MIT License (MIT)

Copyright (c) 2015 Bernat Borrás Paronella

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.