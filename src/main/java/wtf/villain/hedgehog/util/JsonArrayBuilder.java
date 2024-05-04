package wtf.villain.hedgehog.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class JsonArrayBuilder {

    private final JsonArray array = new JsonArray();

    protected JsonArrayBuilder() {
    }

    @Contract("_ -> this")
    @NotNull
    public JsonArrayBuilder use(@NotNull Consumer<JsonArrayBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public JsonArrayBuilder add(@NotNull String value) {
        array.add(value);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public JsonArrayBuilder add(int value) {
        array.add(value);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public JsonArrayBuilder add(boolean value) {
        array.add(value);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public JsonArrayBuilder add(double value) {
        array.add(value);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public JsonArrayBuilder add(long value) {
        array.add(value);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public JsonArrayBuilder add(@NotNull JsonElement value) {
        array.add(value);
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public JsonArrayBuilder add(JsonArrayBuilder value) {
        array.add(value.build());
        return this;
    }

    @NotNull
    public JsonArray build() {
        return array;
    }

}
