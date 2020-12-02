package com.mutool.javafx.core.javafx.helper;

import cn.hutool.core.util.ObjectUtil;
import com.mutool.javafx.core.util.EnumUtil;
import com.mutool.javafx.core.util.KeyValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

public class ChoiceBoxHelper {

    public static <T extends Enum<T>> void setContentDisplay(
        ChoiceBox<T> choiceBox, Class<T> enumType, Function<T, String> toString
    ) {
        List<KeyValue<String, T>> keyValues = EnumUtil.toKeyValueList(enumType, toString);
        setContentDisplay(choiceBox, keyValues);
    }

    public static <T> void setContentDisplay(ChoiceBox<T> choiceBox, KeyValue<String, T>... keyValues) {
        setContentDisplay(choiceBox, Arrays.asList(keyValues));
    }

    public static <T> void setContentDisplay(ChoiceBox<T> choiceBox, List<KeyValue<String, T>> keyValues) {
        List<T> values = keyValues.stream().map(KeyValue::getValue).collect(Collectors.toList());
        Map<String, T> map = new HashMap<>();
        keyValues.forEach(keyValue -> map.put(keyValue.getKey(), keyValue.getValue()));
        setContentDisplay(choiceBox, values, map);
    }

    public static <T> void setContentDisplay(ChoiceBox<T> choiceBox, List<T> items, Map<String, T> itemMappings) {
        choiceBox.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                AtomicReference<String> result = null;
                itemMappings.forEach((K, V) -> {
                    if(ObjectUtil.equal(V, object)){
                        result.set(K);
                    }
                });
                return result.get();
            }

            @Override
            public T fromString(String string) {
                return itemMappings.get(string);
            }
        });
        choiceBox.getItems().addAll(items);
    }
}
