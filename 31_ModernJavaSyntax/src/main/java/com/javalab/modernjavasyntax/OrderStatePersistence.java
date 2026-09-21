package com.javalab.modernjavasyntax;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * 注文一覧({@code Map<String, OrderState>})をJSONファイルへ保存・復元する。
 * {@link OrderState}に付与した{@code @JsonTypeInfo}/{@code @JsonSubTypes}により、
 * インターフェース型のまま4種類のrecordを型情報付きで往復できる。
 */
public final class OrderStatePersistence {

    // JavaTimeModuleのLocalDateSerializerはWRITE_DATES_AS_TIMESTAMPSが有効だと[年,月,日]の配列で
    // 出力してしまうため、無効化してISO-8601形式の文字列("2026-01-01")で出力させる。
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final TypeReference<Map<String, OrderState>> ORDERS_TYPE = new TypeReference<>() {
    };

    private OrderStatePersistence() {
    }

    public static void save(Map<String, OrderState> orders, Path path) throws IOException {
        // writeValue(File, Object)は値の実行時クラス(LinkedHashMap)しか見ないため、
        // Mapの値の宣言型がOrderStateであることを見失い、型情報(typeプロパティ)が出力されない。
        // writerFor(TypeReference)で明示的に静的型を与えることでポリモーフィックな型情報を出力させる。
        MAPPER.writerFor(ORDERS_TYPE).withDefaultPrettyPrinter().writeValue(path.toFile(), orders);
    }

    public static Map<String, OrderState> load(Path path) throws IOException {
        try {
            return MAPPER.readValue(path.toFile(), ORDERS_TYPE);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("ファイルが見つかりません: " + path);
        }
    }
}
