/**
 * 
 */
package org.jboss.el.lang;

class MyNumber extends Number {
    @Override
    public double doubleValue() {
        return 3.562;
    }

    @Override
    public float floatValue() {
        return 3456.232f;
    }

    @Override
    public int intValue() {
        return 23;
    }

    @Override
    public long longValue() {
        return 2342342343l;
    }
}