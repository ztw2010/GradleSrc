package com.ebest.frame.annomationlib.parama.annomation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解作用于 <b>已被{@link Arg}所标记</b> 的成员变量之上。用于在生成注入器类中。对此成员变量进行的注入操作。使用此处指定的数据转换器。
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Converter {

    Class<? extends BundleConverter> value();
}
