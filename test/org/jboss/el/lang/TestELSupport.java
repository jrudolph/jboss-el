package org.jboss.el.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class TestELSupport {

    // Tests for public final static Number coerceToNumber(final Object obj)
    @Test
    public void testCoerceToNumberObjectNull() {
        Number numberNull = ELSupport.coerceToNumber(null);
        assertEquals(0l, numberNull);
        assertEquals(Long.class, numberNull.getClass());
    }

    @Test
    public void testCoerceToNumberObjectNumber() {
        Long l = 25l;

        Number number = ELSupport.coerceToNumber(l);
        assertSame(l, number);
        assertEquals(Long.class, number.getClass());
        assertEquals(25l, number.longValue());
    }
    @Test
    public void testCoerceToNumberObjectMyNumber() {
        Number myNumber = new MyNumber();

        Number number = ELSupport.coerceToNumber(myNumber);
        assertSame(myNumber, number);
        assertEquals(3.562, number.doubleValue());
        assertEquals(3456.232f, number.floatValue());
        assertEquals(23, number.intValue());
        assertEquals(2342342343l, number.longValue());
    }
    @Test
    public void testCoerceToNumberObjectString() {
        String intStr = "25";

        Number number = ELSupport.coerceToNumber(intStr);
        assertEquals(Integer.class, number.getClass());
        assertEquals(25, number.intValue());

        number = ELSupport.coerceToNumber("34543534534");
        assertEquals(Long.class, number.getClass());
        assertEquals(34543534534l, number.longValue());

        number = ELSupport.coerceToNumber("4565465464565434534534534");
        assertEquals(BigInteger.class, number.getClass());
        assertEquals("4565465464565434534534534", number.toString());
    }

    // Tests for public final static Object coerceToType(final Object obj, final Class type)
    @Test
    public void coerceToTypeNull(){
        String test = "test";
        assertSame(test, ELSupport.coerceToType(test, null));

        Object o = new Object(){};
        assertSame(o, ELSupport.coerceToType(o, null));
    }

    private <T> void assertCoerceToSuperType(T val, Class<? super T> type){
        Object coerced = ELSupport.coerceToType(val, type);
        assertEquals(val, coerced);
        assertSame(val, coerced);
    }
    @Test
    public void coerceToTypeSuperTypeAnonymousObject(){
        assertCoerceToSuperType(new Object(){}, Object.class);
    }
    @Test
    public void coerceToTypeSuperTypeCharSequence(){
        assertCoerceToSuperType("test", CharSequence.class);
    }
    @Test
    public void coerceToTypeSuperTypeString(){
        assertCoerceToSuperType("test", String.class);
    }
    @Test
    public void coerceToTypeSuperTypeLong(){
        assertCoerceToSuperType(5L, Long.class);
    }
    @Test
    public void coerceToTypeSuperTypeLongToNumber(){
        assertCoerceToSuperType(5L, Number.class);
    }
    @Test
    public void coerceToTypeSuperTypeMyNumberToMyNumber(){
        assertCoerceToSuperType(new MyNumber(), MyNumber.class);
    }
    @Test
    public void coerceToTypeSuperTypeMyNumberToNumber(){
        assertCoerceToSuperType(new MyNumber(), Number.class);
    }
    @Test
    public void coerceToTypeSuperTypeMyBigDecimalToNumber(){
        assertCoerceToSuperType(new MyBigDecimal("34.223"), Number.class);
    }
    @Test
    public void coerceToTypeSuperTypeMyBigDecimalToBigDecimal(){
        assertCoerceToSuperType(new MyBigDecimal("34.223"), BigDecimal.class);
    }
}
