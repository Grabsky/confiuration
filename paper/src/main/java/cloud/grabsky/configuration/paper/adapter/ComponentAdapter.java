/*
 * MIT License
 *
 * Copyright (c) 2023 Grabsky <44530932+Grabsky@users.noreply.github.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * HORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cloud.grabsky.configuration.paper.adapter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonReader.Token;
import com.squareup.moshi.JsonWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Converts {@link String} or {@link String String[]} to {@link Component} using provided function.
 */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class ComponentAdapter extends JsonAdapter<Component> {
    /* DEFAULT */ public static final ComponentAdapter INSTANCE = new ComponentAdapter(MiniMessage.miniMessage());

    private final MiniMessage miniMessage;

    @Override
    public Component fromJson(final @NotNull JsonReader in) throws IOException {
        return switch (in.peek()) {
            case STRING -> {
                final String text = in.nextString();
                // Returning empty component if value is null or blank
                if ("".equals(text) == true)
                    yield Component.empty();
                // Parsing and returning
                yield miniMessage.deserialize(text).compact();
            }
            case BEGIN_ARRAY -> {
                final StringBuilder builder = new StringBuilder();
                // ...
                in.beginArray();
                // ...
                while (in.hasNext() == true && in.peek() == Token.STRING) {
                    builder.append(in.nextString());
                    // ...
                    if (in.hasNext() == true) {
                        builder.append("<newline><reset>");
                    }
                }
                // ...
                in.endArray();
                // ...
                yield miniMessage.deserialize(builder.toString()).compact();
            }
            case BEGIN_OBJECT -> {
                final String json = in.nextSource().readUtf8();
                // ...
                yield GsonComponentSerializer.gson().deserialize(json);
            }
            case NULL -> null;
            default -> throw new JsonDataException("Expected STRING or BEGIN_ARRAY at " + in.getPath() + " but found: " + in.peek());
        };
    }

    @Override
    public void toJson(final @NotNull JsonWriter out, final Component value) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }
}
