package com.vorono4ka.config;

import com.mojang.datafixers.util.Pair;
import com.vorono4ka.utilities.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModConfigProvider implements SimpleConfig.DefaultConfig {
    private static final String LIST_DELIMITER = ",";

    private final List<Pair<String, ?>> values = new ArrayList<>();
    private boolean isDirty = false;
    private String content;

    public void add(String key, Object value) {
        values.add(new Pair<>(key, value));
        isDirty = true;
    }

    @Override
    public String get(String namespace) {
        if (isDirty) {
            StringBuilder builder = new StringBuilder();

            for (Pair<String, ?> pair : values) {
                Object value = pair.getSecond();

                String parameterName = pair.getFirst();
                String parameterValue = value.toString();

                if (value instanceof Collection<?>) {
                    parameterValue = ArrayUtils.join(LIST_DELIMITER, ((Collection<?>) value));
                }

                builder.append(parameterName).append("=").append(parameterValue);
                builder.append("\n");
            }

            content = builder.toString();
            isDirty = false;
        }
        return content;
    }
}
