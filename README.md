# wrap_gson

对 `Gson` 的包装及用法演示。

在不想暴露 `Gson` 相关类的情况下可以采用本项目。



### 项目环境要求

1. Java 7+
2. Gradle 4.0+

### 项目相关用法

1. `gradle javadoc`		获取 JavaDoc
2. `gradle test`	        项目测试
3. `gradle jar`			生成相关 Jar 包



### 使用示例


```java
WrapGson json = new WrapGson();
json.add("key-string", "value");
json.add("key-number", 1); 			// @see java.lang.Number
json.add("key-boolean", false);
json.add("key-list", aExistList);
json.add("key-string-null", "null");
json.add("key-json", "{\"json-key\":\"json-value\",\"json-key-number\":1}");
// json.add("key-null", (String or another type) null); // NullPointerException

WrapGson stringProp = json.get("key-string");
assertEquals(stringProp.toString(), json.getAsString("key-string", stringProp.getAsString()));
WrapGson numberProp = json.get("key-number");
assertEquals(Integer.valueOf(numberProp.toString()), json.getAsNumber("key-number"), numberProp.getAsNumber());
...more

WrapGson listProp = json.get("key-list");
assertEquals(aExistList, json.getAsList(), listProp.getAsList());

WrapGson jsonProp = json.get("key-json");
boolean flag = jsonProp.has("not_exist"); 		// false
WrapGson jsonPropChild = jsonProp.get("json-key");
flag = jsonPropChild.isProperty(); 				// true
// json.get("no_key"); // UnsupportedOperationException

WrapGson jsonObject = WrapGson.fromJson(jsonObjectString);
WrapGson jsonProperty = WrapGson.fromJson(justAString);

WrapGson removed = jsonObject.remove("key");
// jsonProperty.remove("key"); // UnsupportedOperationException
int size = jsonObject.size();
// jsonProperty.size();        // UnsupportedOperationException
// jsonProperty.has("key");    // UnsupportedOperationException
```
