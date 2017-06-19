package com.pinger.android.utils.inject;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.pinger.android.utils.inject.annotation.BindView;
import com.pinger.android.utils.inject.annotation.ClickEvent;
import com.pinger.android.utils.inject.annotation.ContentView;
import com.pinger.android.utils.inject.annotation.ListenerInvocationHandler;

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

    public static void inject(Context context) {
        injectLayout(context);
        injectView(context);
        injectClick(context);

    }

    /**
     * 事件注入
     */
    private static void injectClick(Context context) {

        Class<?> clazz = context.getClass();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<?> annotationType = annotation.annotationType();
                performClickEvent(context, clazz, method, annotation, annotationType);
            }
        }
    }
    
    /**
     * 单击事件
     */
    private static void performClickEvent(Context context, Class<?> clazz, Method method, Annotation annotation, Class<?> annotationType) {
        ClickEvent clickEvent = annotationType.getAnnotation(ClickEvent.class);
        if (clickEvent == null) return;

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
                Method findViewById = clazz.getMethod("findViewById", int.class);
                View view = (View) findViewById.invoke(context, id);

                if (view == null) continue;

                // 使用反射进行方法的调用
                Method setListener = view.getClass().getMethod(setter, type);

                ListenerInvocationHandler handler = new ListenerInvocationHandler(context, methodMap);
                // 生成代理对象
                Object proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, handler);

                // 激活方法
                setListener.invoke(view, proxy);
            }

        } catch (NoSuchMethodException e) {
            Log.e(TAG, "注入事件失败，激活方法失败");
        } catch (InvocationTargetException e) {
            Log.e(TAG, "注入事件失败，方法调用失败");
        } catch (IllegalAccessException e) {
            Log.e(TAG, "注入事件失败，非法访问");
        }
    }

    /**
     * 控件注入
     */
    private static void injectView(Context context) {
        Class<?> clazz = context.getClass();
        // 获取所有的字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 获取每个字段的注解
            BindView bindView = field.getAnnotation(BindView.class);
            // 如果字段上没有注解则跳过
            if (bindView == null) continue;

            // 获取控件的Id
            int id = bindView.value();

            // 反射调用findViewById方法
            try {
                Method method = clazz.getMethod("findViewById", int.class);
                // 调用方法返回获取的View对象
                View view = (View) method.invoke(context, id);
                // 赋值给字段
                // 暴力访问
                field.setAccessible(true);
                field.set(context, view);

            } catch (NoSuchMethodException e) {
                Log.e(TAG, "注入控件失败，没有findViewById这个方法");
            } catch (InvocationTargetException e) {
                Log.e(TAG, "注入控件失败，findViewById方法调用失败");
            } catch (IllegalAccessException e) {
                Log.e(TAG, "注入控件失败，findViewById非法访问");
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
