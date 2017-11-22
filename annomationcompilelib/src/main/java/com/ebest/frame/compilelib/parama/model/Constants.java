package com.ebest.frame.compilelib.parama.model;

import com.squareup.javapoet.ClassName;

public class Constants {

    public static final String INJECTOR_SUFFIX = "$$Injector";

    public static final ClassName CLASS_BUNDLE = ClassName.bestGuess("android.os.Bundle");

    public static ClassName CLASS_INJECTOR = ClassName.bestGuess("com.ebest.frame.annomationapilib.parama.ParcelInjector");

    public static ClassName CLASS_PARCELER = ClassName.bestGuess("com.ebest.frame.annomationapilib.parama.Parceler");

    public static ClassName CLASS_FACTORY = ClassName.bestGuess("com.ebest.frame.annomationapilib.parama.BundleFactory");

    public static ClassName CLASS_MANAGER = ClassName.bestGuess("com.ebest.frame.annomationapilib.parama.CacheManager");

    public static ClassName CLASS_UTILS = ClassName.bestGuess("com.ebest.frame.annomationapilib.parama.Utils");

    public static final String METHOD_TO_ENTITY = "toEntity";

    public static final String METHOD_TO_BUNDLE = "toBundle";
}
