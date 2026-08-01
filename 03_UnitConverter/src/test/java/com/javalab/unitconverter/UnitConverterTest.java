package com.javalab.unitconverter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnitConverterTest {

    private final UnitConverter converter = new UnitConverter();

    @Test
    void convertsKilometersToMeters() {
        assertEquals(5000, converter.convert(5, "km", "m"));
    }

    @Test
    void convertsCentimetersToMeters() {
        assertEquals(1, converter.convert(100, "cm", "m"));
    }

    @Test
    void convertsKilogramsToGrams() {
        assertEquals(1000, converter.convert(1, "kg", "g"));
    }

    @Test
    void convertsMilligramsToGrams() {
        assertEquals(1, converter.convert(1000, "mg", "g"));
    }

    @Test
    void throwsExceptionForUnknownUnit() {
        assertThrows(UnitConverterException.class,
                () -> converter.convert(1, "xyz", "m"));
    }

    @Test
    void throwsExceptionForCrossCategoryConversion() {
        assertThrows(UnitConverterException.class,
                () -> converter.convert(1, "km", "g"));
    }
}
