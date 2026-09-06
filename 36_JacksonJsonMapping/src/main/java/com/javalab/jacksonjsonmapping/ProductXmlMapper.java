package com.javalab.jacksonjsonmapping;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/** {@link Product}のXMLシリアライズ・デシリアライズを提供する。 */
public final class ProductXmlMapper {

    private static final XmlMapper MAPPER = (XmlMapper) new XmlMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ProductXmlMapper() {
    }

    public static String toXml(Product product) {
        try {
            return MAPPER.writeValueAsString(product);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String toXml(List<Product> products) {
        try {
            return MAPPER.writeValueAsString(new ProductList(products));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Product fromXml(String xml) {
        try {
            return MAPPER.readValue(xml, Product.class);
        } catch (MismatchedInputException e) {
            throw new IllegalArgumentException("不正なXML形式です: " + xml, e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
