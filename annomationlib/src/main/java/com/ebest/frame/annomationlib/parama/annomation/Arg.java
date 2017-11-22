package com.ebest.frame.annomationlib.parama.annomation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解作用于任意实体类中的成员变量。用以标记此成员变量应该被数据注入器ParcelInjector读取并进行注入操作。
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Arg {
    /**
     * 重命名key值。当为空时，表示将使用成员变量的名字作为从Bundle进行存取时所使用的key值。
     */
    String value() default "";
}
