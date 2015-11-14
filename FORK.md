# CONTRIBUTE #

## CREDENTIALS ##

First step you need is to create a new Pair of API credentials.

Go to [Settings](https://github.com/settings/profile) > [Applications](https://github.com/settings/developers) and create a new Application.

**Callback**

You should provide a url for callback, when login and auth is done.

This url should follow the structure: `gitskariosgithubdevel://{domain}` It's mandatory that the `SCHEME` is `gitskariosgithubdevel`.

## FORK ##

Once you have a valid Credentials, you can FORK this project in your account / organization.

This repository uses `develop` branch as default, and follow [Git-Flow](https://es.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).

I WILL NOT accept Pull Request with a base branch `master`or `develop`, you should create ONE branch per feature.

> Is important that you pull this repository `develop` branch before create a new Feature, so no outdated commits are requested to be merged.

### SDK ###

Gitskarios uses [GithubAndroidSDK](https://github.com/gitskarios/GithubAndroidSdk) as submodule.

I recommend you to use the official GitHub desktop client, or SourceTree, in order to clone your fork.

If you clone using plain command line, you should init submodules:

`git submodule init`
`git submodule update`

> Please, take care of what you modify, if you need to modify the SDK itself, clone it, and modify the Submodule origin URL to point to your fork.
> When you push a change in your own copy of SDK, describe it in PullRequest. (Make a PR in SDK too).

## Android Studio / Gradle ##

Gitskarios (SDK) provide some ways to use credentials.

** {USER_HOME}/.gradle/gradle.properties ** or ** {PROJECT_ROOT} gradle.properties **

GH_DEV_ID=
GH_DEV_SECRET=
GH_DEV_CALLBACK=

GH_PRO_ID=
GH_PRO_SECRET=
GH_PRO_CALLBACK=

** build.gradle **

``` groovy
        debug {
            buildConfigField "String", "CLIENT_ID", "\"" + API_CLIENT_ID + "\""
            buildConfigField "String", "CLIENT_SECRET", "\"" + API_CLIENT_SECRET + "\""
            buildConfigField "String", "CLIENT_CALLBACK", "\"" + API_CLIENT_CALLBACK + "\""
        }
        release {
            buildConfigField "String", "CLIENT_ID", "\"" + API_CLIENT_ID + "\""
            buildConfigField "String", "CLIENT_SECRET", "\"" + API_CLIENT_SECRET + "\""
            buildConfigField "String", "CLIENT_CALLBACK", "\"" + API_CLIENT_CALLBACK + "\""
            ext.betaDistributionGroupAliases="gitskarios-alpha-testers"
        }
```

** AndroidManifest.xml**

AndroidManifest.xml
```xml
 <application>
...
    <meta-data
        android:name="com.alorma.github.sdk.client"
        android:value="YOUR_CLIENT_ID_HERE"/>

    <meta-data
        android:name="com.alorma.github.sdk.secret"
        android:value="YOUR_SECRET_HERE"/>

    <meta-data
        android:name="com.alorma.github.sdk.oauth"
        android:value="YOUR_URL_HERE"/>
...
</application>
```

GitskariosApplication.java
``` java
GithubDeveloperCredentials.init(new MetaDeveloperCredentialsProvider(this));
```

** Your implementation **

Create a class that extends from implements ```GithubDeveloperCredentialsProvider```

And pass it to
``` java
GithubDeveloperCredentials.init(new MyCredentialsProvider(this));
```

## DEBUG / IMPROVE / TEST ##

Here you should be ready to develop new Awesome features!

## ISSUES ##

If you can not compile Gitskarios, please open an issue explaining what is the problem.

