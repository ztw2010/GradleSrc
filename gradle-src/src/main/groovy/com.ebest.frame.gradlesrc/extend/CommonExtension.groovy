package com.ebest.frame.gradlesrc.extend

public class CommonExtension {

    /**
     * 是否自动注册组件，true则会使用字节码插入的方式自动注册代码
     * false的话，需要手动使用反射的方式来注册
     */
    boolean isRegisterCompoAuto = false;

    /**
     * 当前组件的applicatonName，用于字节码插入。
     * 当isRegisterCompoAuto==true的时候是必须的
     */
    String applicatonName
}