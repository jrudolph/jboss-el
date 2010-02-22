package org.jboss.el.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;

import org.junit.Test;

public class TestELArithmetic {
    private <T> void assertCoerceToBigDecimalSame(Object val){
        Object coerced = ELArithmetic.BIGDECIMAL.coerce(val);
        assertEquals(val, coerced);
        assertSame(val, coerced);
    }
    private <T> void assertCoerceToBigDecimalSameNumber(Number val){
        Object coerced = ELArithmetic.BIGDECIMAL.coerce(val);
        assertEquals(val, coerced);
        assertSame(val, coerced);
    }

    @Test
    public void testCoerceObjectBigDecimal(){
        assertCoerceToBigDecimalSame(new BigDecimal("3434"));
    }
    @Test
    public void testCoerceObjectMyBigDecimal(){
        assertCoerceToBigDecimalSame(new MyBigDecimal("3434"));
    }
    @Test
    public void testCoerceNumberBigDecimal(){
        assertCoerceToBigDecimalSameNumber(new BigDecimal("3434"));
    }
    @Test
    public void testCoerceNumberMyBigDecimal(){
        assertCoerceToBigDecimalSameNumber(new MyBigDecimal("3434"));
    }
}
