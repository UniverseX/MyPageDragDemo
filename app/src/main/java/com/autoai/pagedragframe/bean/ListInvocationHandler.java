package com.autoai.pagedragframe.bean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ListInvocationHandler<E> implements InvocationHandler {
    private static final String TAG = "ListInvocationHandler";
    private List<DataSetObserver<E>> observers;
    private List<E> list;
    private Class<?> valueClazz;
    private Class<?> dataSetObserverValueClazz;

    public ListInvocationHandler(List<E> object, List<DataSetObserver<E>> observers) {
        this.list = object;
        if (object.size() > 0) {
            valueClazz = object.get(0).getClass();
        }
        if (observers.size() > 0) {
            dataSetObserverValueClazz = observers.get(0).getClass();
        }
        this.observers = observers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke = null;
        try {
            method.setAccessible(true);
            invoke = method.invoke(list, args);
        }catch (Exception e){
        }


        if (valueClazz == null) {
            if (list.size() > 0) {
                valueClazz = list.get(0).getClass();
            } else {
                return invoke;
            }
        }

        if (dataSetObserverValueClazz == null) {
            if (observers.size() > 0) {
                dataSetObserverValueClazz = observers.get(0).getClass();
            } else {
                dataSetObserverValueClazz = DataSetObserver.class;
            }
        }

        Method observerMethod;

        String listMethodName = method.getName();
        String char_0 = String.valueOf(listMethodName.charAt(0));
        String observerMethodName = "on" + listMethodName.replace(char_0, char_0.toUpperCase());

        try {
            Class clazzType = objectToClass(invoke, dataSetObserverValueClazz == DataSetObserver.class);
            if (clazzType == null) {
                observerMethod = dataSetObserverValueClazz.getDeclaredMethod(observerMethodName);
            } else {
                observerMethod = dataSetObserverValueClazz.getDeclaredMethod(observerMethodName, clazzType);
            }

            for (DataSetObserver observer : observers) {
                if (clazzType == null) {
                    observerMethod.invoke(observer);
                } else {
                    observerMethod.invoke(observer, invoke);
                }
            }
        } catch (NoSuchMethodException e) {
//            Log.w(TAG, "invoke: NoSuchMethodException1 " + e.getMessage());
//            Log.w(TAG, " try to find by another way");
            try {
                if(args == null || args.length == 0){
                    observerMethod = dataSetObserverValueClazz.getDeclaredMethod(observerMethodName);
                    for (DataSetObserver observer : observers) {
                        observerMethod.invoke(observer);
                    }
                }else {
                    Class[] classes = new Class[args.length];
                    for (int i = 0; i < args.length; i++) {
                        classes[i] = objectToClass(args[i], dataSetObserverValueClazz == DataSetObserver.class);
                    }
                    observerMethod = dataSetObserverValueClazz.getDeclaredMethod(observerMethodName, classes);
                    for (DataSetObserver observer : observers) {
                        observerMethod.invoke(observer, args);
                    }
                }
            } catch (NoSuchMethodException e2) {
//                Log.w(TAG, "invoke: NoSuchMethodException2 " + e.getMessage());
            }
        }

        return invoke;
    }

    /**
     * 泛型与基本类型检查
     */
    private Class objectToClass(Object arg, boolean isInterface) {
        Class clazzType = null;
        if (arg != null) {
            if (valueClazz.isAssignableFrom(arg.getClass())) {
                clazzType = isInterface ? Object.class : valueClazz;
            } else {
                clazzType = transClassType(arg.getClass());
            }
        }
        return clazzType;
    }

    /**
     * 防止自动装箱导致类型不对
     */
    private static Class transClassType(Class<?> clazz) {
        if (clazz == Boolean.class) {
            return boolean.class;
        } else if (clazz == Byte.class) {
            return byte.class;
        } else if (clazz == Character.class) {
            return char.class;
        } else if (clazz == Short.class) {
            return short.class;
        } else if (clazz == Integer.class) {
            return int.class;
        } else if (clazz == Long.class) {
            return long.class;
        } else if (clazz == Float.class) {
            return float.class;
        } else if (clazz == Double.class) {
            return double.class;
        } else if (clazz == Void.class) {
            return void.class;
        } else {
            return clazz;
        }
    }
}
