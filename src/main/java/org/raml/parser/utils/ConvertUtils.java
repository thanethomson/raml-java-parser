package org.raml.parser.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.ClassUtils;

public class ConvertUtils
{
    private static List<Converter> converters = new ArrayList<Converter>();
    
    static {
        initializeConverters();
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T convertTo(String value, Class<T> type)
    {
        if (type.isEnum())
        {
            return type.cast(Enum.valueOf((Class) type, value.toUpperCase()));
        }
        else
        {
            Class<T> wrapperClass = type;
            if (type.isPrimitive()) {
                wrapperClass = ClassUtils.primitiveToWrapper(type);
            }
            return wrapperClass.cast(org.apache.commons.beanutils.ConvertUtils.convert(value, type));
        }
    }

    public static boolean canBeConverted(String value, Class<?> type)
    {
        if (type.isEnum())
        {
            Object[] enumConstants = type.getEnumConstants();
            for (Object enumConstant : enumConstants)
            {
                if (enumConstant.toString().equals(value.toUpperCase())) {
                    return true;
                }
            }
            return false;
        }
        else if (type.isInstance(value)) {
            return true;
        } else {
            try {
                Class<?> wrapperClass = ClassUtils.primitiveToWrapper(type);
                convertTo(value, wrapperClass);
                return true;
            } catch (ClassCastException e) {
                return false;
            } catch (ConversionException e) {
                return false;
            }
        }
    }
    
    private static void initializeConverters()
    {
        BooleanConverter booleanConverter = new BooleanConverter();
        converters.add(booleanConverter);
        org.apache.commons.beanutils.ConvertUtils.register(booleanConverter, Boolean.class);
        org.apache.commons.beanutils.ConvertUtils.register(booleanConverter, Boolean.TYPE);
    }
}
