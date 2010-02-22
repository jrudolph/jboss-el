package org.jboss.el;

import javax.el.ELContext;

public abstract class TypeResolver {
	public abstract Object convertToType(ELContext context, Object in, Class type);
}
