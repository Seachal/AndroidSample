package com.pinger.android.utils.inject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.pinger.android.utils.inject.annotation.BindView;
import com.pinger.android.utils.inject.annotation.ClickEvent;
import com.pinger.android.utils.inject.annotation.ContentView;
import com.pinger.android.utils.inject.annotation.ListenerInvocationHandler;
import com.pinger.android.utils.inject.annotation.ViewFinder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pinger
 * @since 2017/6/18 21:28
 * 注解工具类，支持注入布局，控件和事件，事件支持点击，长按。
 */

public class InjectUtils {


    private static final String TAG = InjectUtils.class.getSimpleName();

    /**
     * 注入到Activity里
     */
    public static void inject(Activity activity) {
        injectLayout(activity);
        inject(new ViewFinder(activity), activity);
    }

    /**
     * 注入到Fragment等使用View填充的对象里
     */
    public static void inject(View view, Object object) {
        inject(new ViewFinder(view), object);
    }


    /**
     * 注入到View里
     */
    public static void inject(View view) {
        inject(new ViewFinder(view), view);
    }


    /**
     * 注入
     */
    private static void inject(ViewFinder finder, Object object) {
        injectView(finder, object);
        injectClick(finder, object);
    }


    /**
     * 事件注入
     */
    private static void injectClick(ViewFinder finder, Object object) {
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<?> annotationType = annotation.annotationType();
                performClickEvent(finder, object, method, annotation, annotationType);
            }
        }
    }

    /**
     * 执行事件点击
     */
    private static void performClickEvent(ViewFinder finder, Object object, Method method, Annotation annotation, Class<?> annotationType) {
        ClickEvent clickEvent = annotationType.getAnnotation(ClickEvent.class);
        if (clickEvent != null) {

            // 获取点击事件的各个属性
            String setter = clickEvent.setter();
            Class<?> type = clickEvent.type();
            String callBackMethod = clickEvent.callBackMethod();

            // 使用动态代理调用设置监听的方法
            Map<String, Method> methodMap = new HashMap<>();
            // 将点击回调的方法作为键，注解的方法作为值，存入代理的方法，
            // 就是使用注解的方法来代理点击回调的方法
            methodMap.put(callBackMethod, method);


            try {
                // 获取注解内部的方法
                Method valueMethod = annotationType.getDeclaredMethod("value");
                // 存入的ids
                int[] viewIds = (int[]) valueMethod.invoke(annotation);

                for (int id : viewIds) {
                    if (id != View.NO_ID) {
                        View view = finder.findViewById(id);

                        // 使用反射进行方法的调用
                        Method setListener = view.getClass().getMethod(setter, type);

                        ListenerInvocationHandler handler = new ListenerInvocationHandler(object, methodMap);
                        // 生成代理对象
                        Object proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, handler);

                        // 激活方法
                        setListener.invoke(view, proxy);
                    }

                }

            } catch (NoSuchMethodException e) {
                Log.e(TAG, "注入事件失败，激活方法失败");
            } catch (InvocationTargetException e) {
                Log.e(TAG, "注入事件失败，方法调用失败");
            } catch (IllegalAccessException e) {
                Log.e(TAG, "注入事件失败，非法访问");
            }
        }

    }

    /**
     * 控件注入
     */
    private static void injectView(ViewFinder finder, Object object) {
        Class<?> clazz = object.getClass();
        // 获取所有的字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 获取每个字段的注解
            BindView bindView = field.getAnnotation(BindView.class);
            // 如果字段上没有注解则跳过
            if (bindView != null) {

                // 获取控件的Id
                int viewId = bindView.value();
                if (viewId != View.NO_ID) {
                    try {
                        field.setAccessible(true);
                        field.set(object, finder.findViewById(viewId));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        Log.e(TAG, "注入控件失败，非法访问异常");
                    }
                }
            }
        }
    }

    /**
     * 布局注入
     */
    private static void injectLayout(Context context) {
        Class<?> clazz = context.getClass();
        // 获取类上的布局注解对象
        ContentView contentView = clazz.getAnnotation(ContentView.class);

        // 如果类上没有注解，则直接返回（有的类可能并不需要页面）
        if (contentView == null) return;

        // 获取注入的布局控件Id
        int layoutId = contentView.value();

        // 反射调用setContentView方法
        try {
            Method method = clazz.getMethod("setContentView", int.class);
            method.invoke(context, layoutId);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "注入布局失败，没有setContentView这个方法");
        } catch (InvocationTargetException e) {
            Log.e(TAG, "注入布局失败，setContentView方法调用失败");
        } catch (IllegalAccessException e) {
            Log.e(TAG, "注入布局失败，setContentView非法访问");
        }
    }


}
