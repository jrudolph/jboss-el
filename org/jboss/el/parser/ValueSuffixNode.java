package org.jboss.el.parser;

import javax.el.ELException;
import javax.el.MethodInfo;

import org.jboss.el.lang.EvaluationContext;

public abstract class ValueSuffixNode extends SimpleNode {

	public ValueSuffixNode(int i) {
		super(i);
	}
	
	public abstract Object getTarget(Object base, EvaluationContext ctx) throws ELException;

	public abstract Object getValue(Object base, EvaluationContext ctx)
			throws ELException;

	public abstract Class getType(Object base, EvaluationContext ctx)
			throws ELException;

	public abstract boolean isReadOnly(Object base, EvaluationContext ctx)
			throws ELException;

	public abstract void setValue(Object base, EvaluationContext ctx,
			Object value) throws ELException;

	public abstract MethodInfo getMethodInfo(Object base,
			EvaluationContext ctx, Class[] paramTypes) throws ELException;

	public abstract Object invoke(Object base, EvaluationContext ctx,
			Class[] paramTypes, Object[] paramValues) throws ELException;
}
