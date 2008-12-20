package test;

import java.lang.reflect.*;
import java.util.*;

/**
 * Manually run class used for figuring out exactly how JDK/generics
 * expose type information.
 */
public class TestReflection
    extends ArrayList<Integer>
{
    public static void main(String[] args)
        throws Exception
    {
        TestReflection test = new TestReflection();
        test.test();
    }

    static class Wrapper extends ArrayList<HashMap<String,Integer>> {
    }

    private void test() throws Exception
    {
        testMethod(getClass().getMethod("methodPlain"));
        testMethod(getClass().getMethod("methodParam"));
        testMethod(getClass().getMethod("methodArrayPlain"));
        testMethod(getClass().getMethod("methodArrayParam"));
        testMethod(getClass().getMethod("methodWildcard"));
    }

    public LinkedHashMap<Class<Integer>, Boolean[]> methodParam() { return null; }

    public Integer methodPlain() { return null; }

    public Boolean[] methodArrayPlain() { return null; }
    public LinkedList<String>[] methodArrayParam() { return null; }

    public Set<? extends List> methodWildcard() { return null; }

    private void testMethod(Method m) throws Exception
    {
        System.out.println("Return type for ["+m+"]:");
        int ix = 0;
        Type type = m.getGenericReturnType();

        printType(1, type);
    }

    private void indent(int amount)
    {
        for (int i = 0; i < amount; ++i) {
            System.out.print("  ");
        }
    }

    private void printType(int indent, Type type)
    {
        indent(indent);
        if (type == null) {
            System.out.println("[null]");
        }
        else if (type instanceof Class) {
            System.out.println("simple class: "+((Class) type).getName());
        }
        else if (type instanceof GenericArrayType) {
            System.out.println("array type, component:");
            GenericArrayType at = (GenericArrayType) type;
            printType(indent+1, at.getGenericComponentType());
        }
        else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            System.out.println("parameterized type:");
            ++indent;
            indent(indent);
            System.out.println("owner -> ");
            printType(indent+1, pt.getOwnerType());
            indent(indent);
            System.out.println("raw -> ");
            printType(indent+1, pt.getRawType());

            int ix = 0;
            for (Type t2 : pt.getActualTypeArguments()) {
                ++ix;
                indent(indent);
                System.out.println("actual #"+ix+" -> ");
                printType(indent+1, t2);
            }
        }
        else if (type instanceof TypeVariable) {
            TypeVariable v = (TypeVariable) type;
            System.out.println("type variable '"+v.getName()+"'");
        }
        else if (type instanceof WildcardType) {
            WildcardType w = (WildcardType) type;
            System.out.println("wildcard type:");
            ++indent;
            indent(indent);
            System.out.println("lower bounds[] -> ");
            for (Type t2 : w.getLowerBounds()) {
                printType(indent+1, t2);
            }
            indent(indent);
            System.out.println("upper bounds[] -> ");
            for (Type t2 : w.getUpperBounds()) {
                printType(indent+1, t2);
            }
        } else {
            throw new IllegalArgumentException("Weird type! "+type.getClass());
        }
    }
}