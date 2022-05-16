# Auth Manager

[![](https://jitpack.io/v/orangesoft-co/auth_manager.svg)](https://jitpack.io/#orangesoft-co/auth_manager)

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency:

```groovy
dependencies {
	implementation 'com.github.orangesoft-co:auth_manager:1.x.y'
}
```
or separately

```groovy
dependencies {
	implementation 'com.github.orangesoft-co.auth_manager:auth:1.x.y'
	implementation 'com.github.orangesoft-co.auth_manager:firebase:1.x.y'
	implementation 'com.github.orangesoft-co.auth_manager:google:1.x.y'
	implementation 'com.github.orangesoft-co.auth_manager:facebook:1.x.y'
	implementation 'com.github.orangesoft-co.auth_manager:apple:1.x.y'
}
```

## Working scheme
![AuthManager Diagram](https://raw.githubusercontent.com/Orangesoft-Development/auth_manager/develop/app/src/main/res/drawable/AuthManager%20Diagram.png)

## License

    Copyright 2020 OrangeSoft

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

