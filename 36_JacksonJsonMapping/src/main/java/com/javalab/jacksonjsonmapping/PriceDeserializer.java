package com.javalab.jacksonjsonmapping;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

/** {@link PriceSerializer}が出力した{@code "1500円"}形式の文字列を{@link BigDecimal}へ読み戻すカスタムデシリアライザ。 */
public class PriceDeserializer extends StdDeserializer<BigDecimal> {

    public PriceDeserializer() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        return new BigDecimal(text.replace("円", ""));
    }
}
