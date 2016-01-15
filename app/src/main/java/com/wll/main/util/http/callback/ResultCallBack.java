package com.wll.main.util.http.callback;

import com.squareup.okhttp.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * 网络请求回调类
 * Created by wll on 2015/11/17.
 */
public abstract class ResultCallBack<T> {

    public Type mType;

    public ResultCallBack() {
        mType = getSuperclassTypeParameter(getClass());
    }

    private Type getSuperclassTypeParameter(Class<?> subclass) {
        //getGenericSuperclass()获得带有泛型的父类
        //Type是 Java 编程语言中所有类型的公共高级接口。它们包括原始类型、参数化类型、数组类型、类型变量和基本类型。
        Type superclassType = subclass.getGenericSuperclass();
        if (superclassType instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        //ParameterizedType参数化类型，即泛型
        ParameterizedType parameterizedType = (ParameterizedType) superclassType;
        //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
        //返回规范的泛型类型
        return parameterizedType.getActualTypeArguments()[0];

    }

    /**
     * 网络请求之前，可以重写此方法显示加载动画
     */
    public void onBefore(Request request) {
    }

    /**
     * 网络请求之后，可以重写此方法隐藏加载动画
     */
    public void onAfter() {
    }

    /**
     * 网络请求，可以重写此方法进行进度条的更新
     */
    public void inProgress(float progress) {
    }

    /**
     * 网络请求失败
     */
    public abstract void onError(Request request, Exception e);

    /**
     * 网路请求成功
     */
    public abstract void onResponse(T result);

    /**
     * 默认网络请求回调函数实现
     */
    public static final ResultCallBack<String> DEFAULT_RESULT_CALLBACK = new ResultCallBack<String>() {
        @Override
        public void onError(Request request, Exception e) {

        }

        @Override
        public void onResponse(String result) {

        }
    };


}
