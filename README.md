## MyUpdateApk
本项目的存在是因为友盟关闭了自动更新服务，所以自己结合友盟所说的一些自动更新方案，利用Rxjava + Retrofit2 + Okhttp3 + Rxandroid + Service + Fim实现app自动更新

### Introduction
适用于api 14及以上，且加入了Android6.0的权限判断,由于本人很懒就没开发后台apk管理功能，就直接采用fim的公共api实现版本比对

## ScreenShot

## Gradle Config
 first add dependences
```
  dependencies {
    compile 'com.trycatch.android:myupdateapklibrary:1.0.0'
  }
```

## use
Activity

```
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UpdateKey.API_TOKEN = "160105e0223dec1896a6648b8265cc95";
        UpdateKey.APP_ID = "57d75987ca87a87c89000b91";
        UpdateKey.WITH_DIALOG=true;
        UpdateKey.WITH_NOTIFITION=true;
        UpdateApk.init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateApk.destory();
    }
}
```

AndroidManifest.xml
```
<service android:name="com.trycath.myupdateapklibrary.service.DownloadFileService" />

```
## License

```
Copyright 2016 trycatch

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```