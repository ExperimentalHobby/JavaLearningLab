package com.javalab.jacksonjsonmapping;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
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
        try {
            return new BigDecimal(text.replace("円", ""));
        } catch (NumberFormatException e) {
            // reportInputMismatchはMismatchedInputExceptionを送出する。ProductJsonMapper/ProductXmlMapper側で
            // JsonProcessingExceptionとしてまとめて捕捉し、IllegalArgumentExceptionへ変換する。
            return ctxt.reportInputMismatch(this, "price must be a valid number: %s", text);
        }
    }

    @Override
    public BigDecimal getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        // JSONのnullはdeserialize()を経由せずここに来る。text.replace呼び出しでのNPEを避けるため、
        // 明示的にエラーとして扱う。
        return ctxt.reportInputMismatch(this, "price must not be null");
    }
}
