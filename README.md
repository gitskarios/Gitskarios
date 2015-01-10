# README #

## FORK ##

Thanks dev for forking Gitskarios!

Since now, if you want to use your own version of Gitskarios, you should generate specific App on Github

[Github OAuth](https://developer.github.com/v3/oauth/).

    If you want distinc apps for debug and release, you should generate two applications.

Then, you need to define a gradle.properties in your home folder (C:\User, /home/user....), with the following content:

``` groovy
SIGN_FILE={keystore_path}
SIGN_KEYSTORE_PASS={keystore_password}
SIGN_KEYSTORE_ALIAS={keystore_alias}
SIGN_KEYSTORE_ALIAS_PASS={keystore_alias_password}


# API CLIENTS
DEV_ID={dev_app_id}
DEV_SECRET={dev_app_secret}
DEV_CALLBACK={dev_app_callback}

PRO_ID={pro_app_id}
PRO_SECRET={pro_app_secret}
PRO_CALLBACK={pro_ap_callback}
```