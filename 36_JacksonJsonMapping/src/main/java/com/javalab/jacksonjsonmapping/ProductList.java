package com.javalab.jacksonjsonmapping;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * XML変換用のルート要素ラッパー。{@code List<Product>}をそのままXMLのルートにはできないため、
 * {@code <products><product>...</product>...</products>}という構造にするために介在させる。
 */
@JacksonXmlRootElement(localName = "products")
public record ProductList(
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "product")
        List<Product> products
) {
}
