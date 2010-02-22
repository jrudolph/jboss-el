package org.jboss.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

public abstract class MethodResolver {
	public abstract Object invoke(ELContext context, Object base, String name, Object[] params) throws Exception;
}
