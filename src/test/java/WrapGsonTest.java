package com.smalltiantian.common.test;

import java.util.List;
import java.util.ArrayList;

import com.smalltiantian.common.*;

import com.google.gson.*;

import org.junit.Test;
import static org.junit.Assert.*;

class ExceptionForTest extends Exception {}

public class WrapGsonTest {

    @Test
    public void assertNormalUsingLikeJsonObject() {
        String NULL = null;

        JsonArray jsonList = new JsonArray();
        List<String> stringList = new ArrayList<String>();

        jsonList.add("list1");
        stringList.add("list1");
        // add a string null
        jsonList.add("null");
        stringList.add("null");
        // add a null
        jsonList.add(NULL);
        stringList.add(NULL);

        JsonObject jsonMap = new JsonObject();
        WrapGson stringMap = new WrapGson();

        jsonMap.addProperty("json_string_key", "value");
        stringMap.add("json_string_key", "value");

        jsonMap.addProperty("json_number_key", 1);
        stringMap.add("json_number_key", 1);

        jsonMap.addProperty("null_key", NULL);
        stringMap.add("null_key", NULL);

        JsonObject nestedJsonObject = new JsonObject();
        WrapGson nestedStringObject = new WrapGson();

        nestedJsonObject.addProperty("key_string", "value");
        nestedStringObject.add("key_string", "value");

        nestedJsonObject.addProperty("key_number", 1);
        nestedStringObject.add("key_number", 1);

        nestedJsonObject.addProperty("key_boolean", false);
        nestedStringObject.add("key_boolean", false);

        nestedJsonObject.add("key_list", jsonList);
        nestedStringObject.add("key_list", stringList);

        nestedJsonObject.addProperty("key_string_null", "null");
        nestedStringObject.add("key_string_null", "null");

        nestedJsonObject.add("key_json", jsonMap);
        nestedStringObject.add("key_json", stringMap);

        nestedJsonObject.addProperty("null_key", NULL);
        nestedStringObject.add("null_key", NULL);

        String msg = String.format("'%s' != '%s'", nestedJsonObject, nestedStringObject);
        assertEquals(msg, nestedJsonObject.toString(), nestedStringObject.toString());

        assertEquals(nestedJsonObject.get("key_string").getAsString(), nestedStringObject.getAsString("key_string"));
        assertEquals(nestedJsonObject.get("key_number").getAsNumber().intValue(), nestedStringObject.getAsNumber("key_number").intValue());
        assertEquals(nestedJsonObject.get("key_boolean").getAsBoolean(), nestedStringObject.getAsBoolean("key_boolean"));

        JsonArray assertJsonArray  = nestedJsonObject.getAsJsonArray("key_list");
        List<WrapGson> assertStringArray = nestedStringObject.getAsList("key_list");
        assertEquals(assertJsonArray.size(), assertStringArray.size());
        assertTrue(assertStringArray.size() > 0);
        for (int i = 0; i < assertJsonArray.size() - 1; i++)
            assertEquals(assertJsonArray.get(i).getAsString(), String.valueOf(assertStringArray.get(i)));
        // test null in array
        assertNull(assertStringArray.get(assertStringArray.size() - 1));

        assertEquals(nestedJsonObject.get("key_string_null").getAsString(), nestedStringObject.getAsString("key_string_null"));
        assertEquals(nestedJsonObject.get("key_json").toString(), nestedStringObject.get("key_json").toString());

        assertEquals(nestedJsonObject.getAsJsonObject("key_json").get("json_string_key").getAsString(), nestedStringObject.get("key_json").getAsString("json_string_key"));
        assertEquals(nestedJsonObject.getAsJsonObject("key_json").get("json_number_key").getAsNumber().intValue(), nestedStringObject.get("key_json").getAsNumber("json_number_key").intValue());
        
        assertNull(nestedStringObject.get("null_key"));
    }

    public void fromJsonWithNull() {
        String json = null;
        WrapGson wrap = WrapGson.fromJson(json);

        assertNull(wrap);
    }

    public void fromJsonWithAnyJsonData() {
        String NULL = null;

        JsonArray jsonList = new JsonArray();
        List<String> stringList = new ArrayList<String>();

        jsonList.add("list1");
        stringList.add("list1");
        // add a string null
        jsonList.add("null");
        stringList.add("null");
        // add a null
        jsonList.add(NULL);
        stringList.add(NULL);

        JsonObject jsonMap = new JsonObject();
        WrapGson stringMap = new WrapGson();

        jsonMap.addProperty("json_string_key", "value");
        stringMap.add("json_string_key", "value");

        jsonMap.addProperty("json_number_key", 1);
        stringMap.add("json_number_key", 1);

        jsonMap.addProperty("null_key", NULL);
        stringMap.add("null_key", NULL);

        JsonObject nestedJsonObject = new JsonObject();
        WrapGson nestedStringObject = new WrapGson();

        nestedJsonObject.addProperty("key_string", "value");
        nestedStringObject.add("key_string", "value");

        nestedJsonObject.addProperty("key_number", 1);
        nestedStringObject.add("key_number", 1);

        nestedJsonObject.addProperty("key_boolean", false);
        nestedStringObject.add("key_boolean", false);

        nestedJsonObject.add("key_list", jsonList);
        nestedStringObject.add("key_list", stringList);

        nestedJsonObject.addProperty("key_string_null", "null");
        nestedStringObject.add("key_string_null", "null");

        nestedJsonObject.add("key_json", jsonMap);
        nestedStringObject.add("key_json", stringMap);

        nestedJsonObject.addProperty("null_key", NULL);
        nestedStringObject.add("null_key", NULL);


        WrapGson wrap = WrapGson.fromJson(nestedJsonObject.toString());
        assertEquals(nestedStringObject, wrap);
    }

    @Test(expected = ExceptionForTest.class)
    public void getAsBooleanOnlyInIsPropertyEqualsTrue() throws Exception {
        WrapGson wrap = new WrapGson();

        assertFalse(wrap.isProperty());

        try {
            wrap.getAsBoolean();
        } catch (UnsupportedOperationException e) {
            throw new ExceptionForTest();
        }
    }

    @Test(expected = ExceptionForTest.class)
    public void getAsListOnlyInIsPropertyEqualsTrue() throws Exception {
        WrapGson wrap = new WrapGson();

        assertFalse(wrap.isProperty());

        try {
            wrap.getAsList();
        } catch (UnsupportedOperationException e) {
            throw new ExceptionForTest();
        }
    }

    @Test(expected = ExceptionForTest.class)
    public void getAsNumberOnlyInIsPropertyEqualsTrue() throws Exception {
        WrapGson wrap = new WrapGson();

        assertFalse(wrap.isProperty());

        try {
            wrap.getAsNumber();
        } catch (UnsupportedOperationException e) {
            throw new ExceptionForTest();
        }
    }

    @Test(expected = ExceptionForTest.class)
    public void getAsListOnlyInIsPropertyEqualsFalse() throws Exception {
        List<String> list = new ArrayList() {{
            add("list1");
            add("list2");
        }};
        WrapGson wrap = new WrapGson();
        wrap.add("key", list);

        //this is a property WrapGson
        WrapGson property = wrap.get("key");
        assertNotNull(property);
        assertTrue(property.isProperty());

        try {
            property.getAsList("key");
        } catch (UnsupportedOperationException e) {
            throw new ExceptionForTest();
        }
    }

    @Test(expected = ExceptionForTest.class)
    public void getAsBooleanOnlyInIsPropertyEqualsFalse() throws Exception {
        boolean flag = false;
        WrapGson wrap = new WrapGson();
        wrap.add("key", flag);

        //this is a property WrapGson
        WrapGson property = wrap.get("key");
        assertNotNull(property);
        assertTrue(property.isProperty());

        try {
            property.getAsBoolean("key");
        } catch (UnsupportedOperationException e) {
            throw new ExceptionForTest();
        }
    }

    @Test(expected = ExceptionForTest.class)
    public void getAsNumberOnlyInIsPropertyEqualsFalse() throws Exception {
        Number number = 1;
        WrapGson wrap = new WrapGson();
        wrap.add("key", number);

        //this is a property WrapGson
        WrapGson property = wrap.get("key");
        assertNotNull(property);
        assertTrue(property.isProperty());

        try {
            property.getAsNumber("key");
        } catch (UnsupportedOperationException e) {
            throw new ExceptionForTest();
        }
    }

    @Test(expected = ExceptionForTest.class)
    public void entrySetOnlyInIsPropertyEqualsFalse() throws Exception {
        String value = "hello";
        WrapGson wrap = new WrapGson();
        wrap.add("key", value);

        assertFalse(wrap.isProperty());
        wrap.entrySet();

        WrapGson property = wrap.get("key");
        assertNotNull(property);
        assertTrue(property.isProperty());

        try {
            property.entrySet();
        } catch (UnsupportedOperationException e) {
            throw new ExceptionForTest();
        }
    }

    @Test(expected = ExceptionForTest.class)
    public void hasMethodOnlyInIsPropertyEqualsFalse() throws Exception {
        String value = "hello";
        WrapGson wrap = new WrapGson();
        wrap.add("key", value);

        assertFalse(wrap.isProperty());

        boolean flag = wrap.has("key");
        assertTrue(flag);

        WrapGson property = wrap.get("key");
        assertNotNull(property);
        assertTrue(property.isProperty());

        try {
            property.has("key");
        } catch (UnsupportedOperationException e) {
            throw new ExceptionForTest();
        }
    }

    @Test(expected = ExceptionForTest.class)
    public void removeOnlyInIsPropertyEqualsFalse() throws Exception {
        String value = "hello";
        WrapGson wrap = new WrapGson();
        wrap.add("key", value);

        assertFalse(wrap.isProperty());

        WrapGson property = wrap.remove("key");
        assertNotNull(property);
        assertTrue(property.isProperty());

        try {
            property.remove("key");
        } catch (UnsupportedOperationException e) {
            throw new ExceptionForTest();
        }
    }

    @Test(expected = ExceptionForTest.class)
    public void sizeOnlyInIsPropertyEqualsFalse() throws Exception {
        String value = "hello";
        WrapGson wrap = new WrapGson();
        wrap.add("key", value);

        assertFalse(wrap.isProperty());
        assertEquals(1, wrap.size());

        WrapGson property = wrap.get("key");
        assertNotNull(property);
        assertTrue(property.isProperty());

        try {
            property.size();
        } catch (UnsupportedOperationException e) {
            throw new ExceptionForTest();
        }
    }
}
