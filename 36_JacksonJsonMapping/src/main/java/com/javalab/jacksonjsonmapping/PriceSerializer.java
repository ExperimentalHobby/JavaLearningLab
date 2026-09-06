package com.javalab.jacksonjsonmapping;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;

/** 価格(BigDecimal)を{@code "1500円"}のような日本語表記の文字列として書き出すカスタムシリアライザ。 */
public class PriceSerializer extends StdSerializer<BigDecimal> {

    public PriceSerializer() {
        super(BigDecimal.class);
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.stripTrailingZeros().toPlainString() + "円");
    }
}
