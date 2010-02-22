package org.jboss.el.lang;

import java.lang.reflect.Method;

import javax.el.FunctionMapper;

public abstract class ExtendedFunctionMapper extends FunctionMapper
{
   public abstract Method resolveFunction(String prefix, String localName, int paramCount);   
}
