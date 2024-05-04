package wtf.villain.hedgehog.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Json {

    @NotNull
    public static Json builder() {
        return new Json();
    }

    @NotNull
    public static JsonObject of(@NotNull Map<String, JsonElement> map) {
        var object = new JsonObject();
        map.forEach(object::add);
        return object;
    }

    @NotNull
    public static JsonArrayBuilder array() {
        return new JsonArrayBuilder();
    }

    private final JsonObject object = new JsonObject();

    @Contract("_ -> this")
    @NotNull
    public Json use(@NotNull Consumer<Json> consumer) {
        consumer.accept(this);
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public Json add(@NotNull String key, @NotNull String value) {
        object.addProperty(key, value);
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public Json add(@NotNull String key, int value) {
        object.addProperty(key, value);
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public Json add(@NotNull String key, boolean value) {
        object.addProperty(key, value);
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public Json add(@NotNull String key, @NotNull JsonElement value) {
        object.add(key, value);
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public Json add(@NotNull String key, @NotNull JsonArray value) {
        object.add(key, value);
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public Json add(@NotNull String key, @NotNull Json value) {
        object.add(key, value.build());
        return this;
    }

    @Contract("_, _ -> this")
    @NotNull
    public Json add(@NotNull String key, @NotNull JsonArrayBuilder value) {
        object.add(key, value.build());
        return this;
    }

    @NotNull
    public JsonObject build() {
        return object;
    }

}
