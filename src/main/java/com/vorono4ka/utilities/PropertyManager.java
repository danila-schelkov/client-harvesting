package com.vorono4ka.utilities;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;

import java.util.List;
import java.util.Optional;

public final class PropertyManager {
    public static Optional<IntProperty> getPropertyByName(BlockState blockState, String propertyName) {
        for (Property<?> property : blockState.getProperties()) {
            if (property.getName().equals(propertyName)) {
                if (property instanceof IntProperty) {
                    return Optional.of((IntProperty) property);
                }

                break;
            }
        }

        return Optional.empty();
    }

    public static Integer getMaxPropertyValue(IntProperty ageProperty) {
        List<Integer> values = List.copyOf(ageProperty.getValues());
        return values.get(values.size() - 1);
    }
}
