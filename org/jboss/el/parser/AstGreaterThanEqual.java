/* Generated By:JJTree: Do not edit this line. AstGreaterThanEqual.java */

package org.jboss.el.parser;

import javax.el.ELException;

import org.jboss.el.lang.EvaluationContext;


/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: markt $
 */
public final class AstGreaterThanEqual extends BooleanNode {
    public AstGreaterThanEqual(int id) {
        super(id);
    }

    public Object getValue(EvaluationContext ctx)
            throws ELException {
        Object obj0 = this.children[0].getValue(ctx);
        Object obj1 = this.children[1].getValue(ctx);
        if (obj0 == obj1) {
            return Boolean.TRUE;
        }
        if (obj0 == null || obj1 == null) {
            return Boolean.FALSE;
        }
        return (compare(obj0, obj1) >= 0) ? Boolean.TRUE : Boolean.FALSE;
    }
}
