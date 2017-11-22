package com.ebest.frame.annomationlib.parama.annomation;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;

/**
 * 默认提供的FastJson数据转换器。使用此转换器前。请确定你的项目依赖了fastjson。
 */
public final class FastJsonConverter implements BundleConverter {

    static {
        try {
            String ignore = JSON.class.getCanonicalName();
        } catch (Throwable t) {
            throw new RuntimeException("You should add fastjson to your dependencies list first", t);
        }
    }

    @Override
    public Object convertToEntity(Object data, Type type) {
        String json;
        if (data instanceof String) {
            json = (String) data;
        } else if (data instanceof StringBuilder || data instanceof StringBuffer) {
            json = data.toString();
        } else {
            throw new RuntimeException(String.format("Unsupported type %s to parse with fastjson", data.getClass()));
        }

        return JSON.parseObject(json, type);
    }

    @Override
    public Object convertToBundle(Object data) {
        return JSON.toJSONString(data);
    }

}
