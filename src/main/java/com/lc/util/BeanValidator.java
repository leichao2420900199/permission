package com.lc.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lc.exception.ParamException;
import org.apache.commons.collections.MapUtils;
import org.omg.CORBA.OBJ_ADAPTER;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

public class BeanValidator {

    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    public static  <T>Map<String,String> validate(T t,Class ...args){
        Validator validator = validatorFactory.getValidator();
        Set validateResult = validator.validate(t, args);
        if(validateResult.isEmpty()){
            return Collections.emptyMap();
        }else {
            LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
            Iterator iterable = validateResult.iterator();
            while (iterable.hasNext()){
                ConstraintViolation violation = (ConstraintViolation)iterable.next();
                linkedHashMap.put(violation.getPropertyPath(),violation.getMessage());
            }
            return linkedHashMap;
        }
    }

    public static Map<String,String> validateList(Collection<?> collection){
        Preconditions.checkNotNull(collection);
        Iterator iterator = collection.iterator();
        Map errors;
        do{
            if(!iterator.hasNext()){
                return Collections.emptyMap();
            }
            Object object = iterator.next();
            errors = validate(object,new Class[0]);
        }while (errors.isEmpty());

        return errors;
    }

    public static Map<String,String> validateObject(Object object,Object...objects){
        if(objects!=null && objects.length>0){
            return validateList(Lists.asList(object,objects));
        }else {
            return validate(object,new Class[0]);
        }
    }

    public static void check(Object param) throws ParamException{
        Map<String,String> map = validateObject(param);
        if(MapUtils.isNotEmpty(map)){
            throw new ParamException(map.toString());
        }

    }

}
