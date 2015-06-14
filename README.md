[![Build Status](https://travis-ci.org/gitskarios/Gitskarios.svg?branch=develop)](https://travis-ci.org/gitskarios/Gitskarios)

# README #

## FORK ##

Thanks dev for forking Gitskarios!

Since now, if you want to use your own version of Gitskarios, you should generate specific App on Github

[Github OAuth](https://developer.github.com/v3/oauth/).

    If you want distinc apps for debug and release, you should generate two applications.

Then, you need to define a gradle.properties in your home folder (C:\User, /home/user....), or export variables to your System, with the following content:

``` groovy
SIGN_FILE={keystore_path}
SIGN_KEYSTORE_PASS={keystore_password}
SIGN_KEYSTORE_ALIAS={keystore_alias}
SIGN_KEYSTORE_ALIAS_PASS={keystore_alias_password}


# API CLIENTS
GH_DEV_ID={dev_app_id}
GH_DEV_SECRET={dev_app_secret}
GH_DEV_CALLBACK={dev_app_callback}

GH_PRO_ID={pro_app_id}
GH_PRO_SECRET={pro_app_secret}
GH_PRO_CALLBACK={pro_ap_callback}
```

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