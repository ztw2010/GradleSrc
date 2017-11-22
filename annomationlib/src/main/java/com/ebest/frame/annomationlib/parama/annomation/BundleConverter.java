package com.ebest.frame.annomationlib.parama.annomation;

import java.lang.reflect.Type;

/**
 * Bundle数据转换器。默认提供：{@link FastJsonConverter}数据解析器
 */
public interface BundleConverter {

    /**
     * 将指定数据data转换为对应type的数据类型并返回。
     *
     * <p>
     *     <i><b>请注意：被转换后的数据。应与参数指定的转换数据类型type一致。</b></i>
     * </p>
     *
     * @return 转换后的数据
     */
    Object convertToEntity(Object data, Type type);

    /**
     * 将指定数据data。转换为可被放入Bundle中的数据类型。并返回。
     *
     * <p>
     *     <i><b>请注意：被转换后的数据类型。应该为可被直接放入Bundle类中的数据类型, 如json串</b></i>
     * </p>
     *
     * @return 被转换后的数据。
     */
    Object convertToBundle(Object data);
}
