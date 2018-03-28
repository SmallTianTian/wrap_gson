package com.smalltiantian.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.LazilyParsedNumber;

public final class WrapGson {
    private static final Gson GSON = new Gson();

    /**
     * 将任意类型数据转换为 WrapGson。
     *
     * 我们将对数据做 final 处理，对传入数据的更改不影响 WrapGson 内部数据。
     *
     * @param  value 任意类型数据。
     * @return WrapGson
     */
    public static final WrapGson fromJson(Object value) {
        if (value == null) {
            return null;
        }
        final Object finalObject = value;

        WrapGson json;
        String instant;
        if (finalObject instanceof WrapGson) {
            json = new WrapGson((WrapGson) finalObject);
        } else if (finalObject instanceof String && ((instant = String.valueOf(finalObject)).trim().isEmpty() || instant.equalsIgnoreCase("null"))) {
            json = new WrapGson(finalObject);
        } else {
            JsonElement element = GSON.fromJson(String.valueOf(finalObject), JsonElement.class);
            json = jsonElementToWrapGson(element);
        }

        return json;
    }

    private final Object value;

    private WrapGson(final Object value) {
        this.value = value;
        this.cache = null;
    }

    /**
     * 得到 {@code boolean} 类型的数据。
     *
     * 转换结果参见 <a href="https://docs.oracle.com/javase/7/docs/api/java/lang/Boolean.html#parseBoolean(java.lang.String)">Boolean.parseBoolean(String)</a>。
     *
     * <strong>注意：只能在 {@link #isProperty()} 返回值为 {@code true} 是才能调用此方法，否则会出错。</strong>
     * @return {@code boolean} 类型的数据。
     * @see #isProperty()
     */
    public boolean getAsBoolean() {
        checkPropertyOperation();

        return this.value instanceof Boolean ? (Boolean) this.value : Boolean.valueOf(String.valueOf(this.value));
    }

    /**
     * 得到 {@code List} 类型的数据。
     *
     * <strong>注意：只能在 {@link #isProperty()} 返回值为 {@code true} 是才能调用此方法，否则会出错。</strong>
     * @return {@code List} 类型的数据。
     * @see #isProperty()
     */
    public List<WrapGson> getAsList() {
        checkPropertyOperation();

        if (this.value instanceof List) {
            @SuppressWarnings("unchecked")
            List<WrapGson> returnList = new ArrayList<WrapGson>((List<WrapGson>) this.value);
            return returnList;
        }
        throw new IllegalStateException(String.format("Property('%s') isn't a list.", this.cache));
    }

    /**
     * 得到 {@code Number} 类型的数据。
     *
     * 采用延迟转换方式，具体调用 <a href="https://docs.oracle.com/javase/7/docs/api/java/lang/Number.html">Number</a> 方法时注意格式转换错误。
     *
     * <strong>注意：只能在 {@link #isProperty()} 返回值为 {@code true} 是才能调用此方法，否则会出错。</strong>
     * @return {@code Number} 类型的数据。
     * @see #isProperty()
     */
    public Number getAsNumber() {
        checkPropertyOperation();

        if (this.value instanceof Number) {
            return (Number) this.value;
        }

        return new LazilyParsedNumber(String.valueOf(this.value));
    }

    private final Map<String, WrapGson> cache;

    /**
     * 初始化一个 WrapGson。
     */
    public WrapGson() {
        this.value = null;
        this.cache = new LinkedTreeMap<String, WrapGson>();
    }

    private WrapGson(WrapGson value) {
        if (value.isProperty()) {
            this.value = value.value;
            this.cache = null;
        } else {
            this.value = null;
            this.cache = new LinkedTreeMap<String, WrapGson>();
            this.cache.putAll(value.cache);
        }
    }

    /**
     * 将 {@code List} 类型的值及键值添加到 WrapGson 中。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * <strong>程序将会递归对 {@code List} 元素进行类型检查，类型只能为 {@code String Number Boolean List} 中一种，否则会抛出错误。</strong>
     * @param  key   将要添加到 WrapGson 中的键
     * @param  value 将要添加到 WrapGson 中的值
     * @return 以前与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @throws IllegalStateException {@code List} 元素只能为 {@code String Number Boolean List} 中一种
     * @see #isProperty()
     */
    public WrapGson add(String key, List value) {
        checkListNorm(value);

        String middle = GSON.toJson(value);
        return add(key, middle);
    }

    /**
     * 确保{@code List} 元素只能为 {@code String Number Boolean List} 中一种。
     *
     * 将碰到 {@code List} 嵌套 {@code List} 的将循环调用。
     *
     * @param  list 将要检查的参数。
     */
    private void checkListNorm(List list) {
        for (Object obj : list) {
            if (obj == null)
                continue;
            else if (obj instanceof List)
                checkListNorm((List) obj);
            else if (!(obj instanceof String || obj instanceof Number || obj instanceof Boolean))
                throw new IllegalStateException("Error : Your list element isn't instanceof String || Number || Boolean.");
        }
    }

    /**
     * 将 {@code WrapGson} 类型的值及键值添加到 WrapGson 中。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     * 将会对 {@code value} 进行保护性赋值，外部的更改不影响内部的内容。
     *
     * @param  key   将要添加到 WrapGson 中的键值
     * @param  value 将要添加到 WrapGson 中的值
     * @return 以前与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @see #isProperty()
     */
    public WrapGson add(String key, WrapGson value) {
        checkMapOperation();

        return this.cache.put(key, new WrapGson(value));
    }

    /**
     * 将 {@code String} 类型的值及键值添加到 WrapGson 中。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @param  key   将要添加到 WrapGson 中的键值
     * @param  value 将要添加到 WrapGson 中的值
     * @return 以前与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @see #isProperty()
     */
    public WrapGson add(String key, String value) {
        return add(key, (Object) value);
    }

    /**
     * 将 {@code Number} 类型的值及键值添加到 WrapGson 中。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @param  key   将要添加到 WrapGson 中的键值
     * @param  value 将要添加到 WrapGson 中的值
     * @return 以前与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @see #isProperty()
     */
    public WrapGson add(String key, Number value) {
        return add(key, (Object) value);
    }

    /**
     * 将 {@code boolean} 类型的值及键值添加到 WrapGson 中。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @param  key   将要添加到 WrapGson 中的键值
     * @param  value 将要添加到 WrapGson 中的值
     * @return 与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @see #isProperty()
     */
    public WrapGson add(String key, boolean value) {
        return add(key, (Object) value);
    }

    /**
     * 如果存在一个和 key 对应的映射关系，则移除。
     *
     * 返回和 key 对应的映射值，如果 key 不对应任何映射关系，则返回 null。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @param  key 将要移除的映射的键值
     * @return 与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @see #isProperty()
     */
    public WrapGson remove(String key) {
        checkMapOperation();

        return this.cache.remove(key);
    }

    /**
     * 获取与 key 对应映射关系的值。
     *
     * 返回和 key 对应的映射值，如果 key 不对应任何映射关系，则返回 null。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @param  key 键
     * @return 与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @see #isProperty()
     */
    public WrapGson get(String key) {
        checkMapOperation();

        return this.cache.get(key);
    }

    /**
     * 获取与 key 对应映射关系 {@code List} 类型的值。
     *
     * 如果 key 不对应任何映射关系，则返回 null。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @param  key 键
     * @return 与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @see #isProperty()
     * @see #getAsList()
     */
    public List<WrapGson> getAsList(String key) {
        return has(key) ? this.cache.get(key).getAsList() : null;
    }

    /**
     * 获取与 key 对应映射关系 {@code Boolean} 类型的值。
     *
     * 如果 key 不对应任何映射关系，则返回 null。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @param  key 键
     * @return 与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @see #isProperty()
     * @see #getAsBoolean()
     */
    public Boolean getAsBoolean(String key) {
        return has(key) ? this.cache.get(key).getAsBoolean() : null;
    }

    /**
     * 获取 {@code String} 类型的值。
     *
     * @return string
     * @see #toString()
     */
    public String getAsString() {
        return toString();
    }

    /**
     * 获取与 key 对应映射关系 {@code String} 类型的值。
     *
     * 如果 key 不对应任何映射关系，则返回 null。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @param  key 键
     * @return 与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @see #isProperty()
     * @see #getAsString()
     */
    public String getAsString(String key) {
        return has(key) ? this.cache.get(key).getAsString() : null;
    }

    /**
     * 获取与 key 对应映射关系 {@code Number} 类型的值。
     *
     * 如果 key 不对应任何映射关系，则返回 null。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @param  key 键
     * @return 与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     * @see #isProperty()
     * @see #getAsNumber()
     */
    public Number getAsNumber(String key) {
        return has(key) ? this.cache.get(key).getAsNumber() : null;
    }

    /**
     * 查询当前 {@code WrapGson} 中是否有此键对应的映射关系。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @param  key 键
     * @return 是否有与 key 关联的值
     * @see #isProperty()
     */
    public boolean has(String key) {
        checkMapOperation();

        return this.cache.containsKey(key);
    }

    /**
     * 获得当前 {@code WrapGson} 的 {@code Set} 集合。
     *
     * 我们将对数据进行安全性拷贝，对获取集合的任何操作不会影响到 {@code WrapGson} 本身。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     * @return {@code Set} 集合
     * @see #isProperty()
     */
    public Set<Map.Entry<String, WrapGson>> entrySet() {
        checkMapOperation();

        return new HashSet<Map.Entry<String, WrapGson>>(this.cache.entrySet());
    }

    /**
     * 获得当前 {@code WrapGson} 的一级子节点数量。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是 {@code true} 则不能进行 {@code add} 操作。
     *
     * @return 一级子节点数量
     */
    public int size() {
        checkMapOperation();

        return this.cache.size();
    }

    /**
     * 添加一条 key-value 数据到 WrapGson 中。
     *
     * 操作前请调用 {@link #isProperty()} 检查是否是一个属性值，如果是则不能进行 {@code add} 操作。
     *
     * <strong>注意：key / value 均不能为 {@code null}。</strong>
     * @param  key 将要添加到 WrapGson 中的键值
     * @param  value 将要添加到 WrapGson 中的值
     * @return 以前与 key 关联的值，如果没有针对 key 的映射关系，则返回 null。
     */
    private WrapGson add(String key, Object value) {
        checkMapOperation();

        WrapGson transform = fromJson(value);
        return this.cache.put(key, transform);
    }

    private String propertyToJson() {
        if (this.value instanceof Number || this.value instanceof Boolean) {
            return String.valueOf(this.value);
        } else if (this.value instanceof List){
            @SuppressWarnings("unchecked")
            List<WrapGson> list = (List<WrapGson>) this.value;
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (WrapGson listElement : list) {
                if (listElement == null)
                    sb.append(listElement).append(",");
                else
                    sb.append(listElement.toJson()).append(",");
            }
            sb.setCharAt(sb.length() - 1, ']');
            return sb.toString();
        }
        return String.format("\"%s\"", this.value);
    }

    private String toJson() {
        if (isProperty())
            return propertyToJson();

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Entry<String, WrapGson> entry : this.cache.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() == null)
                sb.append(entry.getValue()).append(",");
            else
                sb.append(entry.getValue().toJson()).append(",");
        }
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }

    @Override
    public String toString() {
        if (isProperty()) {
            return String.valueOf(this.value);
        }
        return toJson();
    }

    /**
     * 将 JsonElement 类型转为 WrapGson。
     *
     * 方便递归调用。
     *
     * @param  element Gson 类型数据
     * @return WrapGson 数据
     */
    private static WrapGson jsonElementToWrapGson(JsonElement element) {
        WrapGson json;
        if (element.isJsonNull()) {
            json = null;
        } else if (element.isJsonObject()) {
            json = jsonObjectToWrapGson(element);
        } else if (element.isJsonArray()) {
            json = jsonArrayToWrapGson(element);
        } else {
            json = jsonPrimitiveToWrapGson(element);
        }
        return json;
    }

    private static WrapGson jsonPrimitiveToWrapGson(JsonElement element) {
        JsonPrimitive primitive = element.getAsJsonPrimitive();

        if (primitive.isString()) {
            return new WrapGson(primitive.getAsString());
        } else if (primitive.isNumber()) {
            return new WrapGson(primitive.getAsNumber());
        } else if (primitive.isBoolean()) {
            return new WrapGson(primitive.getAsBoolean());
        } else {
            return new WrapGson(primitive);
        }
    }

    private static WrapGson jsonArrayToWrapGson(JsonElement element) {
        JsonArray array = element.getAsJsonArray();
        List<WrapGson> list = new ArrayList<WrapGson>(array.size());

        for (JsonElement arrayElement : array) {
            list.add(jsonElementToWrapGson(arrayElement));
        }
        return new WrapGson(list);
    }

    private static WrapGson jsonObjectToWrapGson(JsonElement element) {
        WrapGson json = new WrapGson();
        JsonObject object = element.getAsJsonObject();
        for (Entry<String, JsonElement> objectEntry : object.entrySet()) {
            json.add(objectEntry.getKey(), jsonElementToWrapGson(objectEntry.getValue()));
        }
        return json;
    }

    /**
     * 检查当前 {@code WrapGson} 是否是一个属性值。
     *
     * 属性值包括 {@code String Number Boolean List} 。
     *
     * {@code WrapGson} 类中大部分方法会根据此方法的返回值来确保正确的调用。
     *
     * @return 是不是一个属性值
     */
    public boolean isProperty() {
        return this.cache == null;
    }

    /**
     * 检查是否是属性值，如果是，则不能进行类似 Map 类型的操作。
     */
    private void checkMapOperation() {
        if (isProperty()) {
            throw new UnsupportedOperationException("This is a property.");
        }
    }

    /**
     * 检查是否是属性值，如果不是，则不能进行对属性的操作。
     */
    private void checkPropertyOperation() {
        if (!isProperty()) {
            throw new UnsupportedOperationException("This is not a property.");
        }
    }
}
