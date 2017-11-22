package com.ebest.frame.compilelib.route.model;

import com.ebest.frame.annomationlib.parama.annomation.Arg;
import com.ebest.frame.annomationlib.router.annomation.RouteExecutor;
import com.ebest.frame.compilelib.UtilMgr;
import com.ebest.frame.compilelib.route.Constants;
import com.ebest.frame.compilelib.route.exception.RouterException;
import com.ebest.frame.compilelib.route.util.Utils;
import com.squareup.javapoet.ClassName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public class Parser {
    private static Map<String,TypeElement> parsed = new HashMap<>();
    private String[] routers;
    private TypeElement type;
    private RouteRuleConfig configurations;
    private Map<String,TypeMirror> map = new HashMap<>();
    private ClassName executorClass;

    public static Parser create (TypeElement element, RouteRuleConfig configurations) {
        Parser parser = new Parser();
        parser.type = element;
        parser.configurations = configurations;
        parser.executorClass = parser.obtainExecutor();
        return parser;
    }

    private ClassName obtainExecutor() {
        RouteExecutor annotation = type.getAnnotation(RouteExecutor.class);
        if (annotation == null) {
            return ClassName.bestGuess(Constants.CLASSNAME_MAINTHREADEXECUTOR);
        }
        try {
            return ClassName.get(annotation.value());
        } catch (MirroredTypeException mirrored) {
            TypeMirror typeMirror = mirrored.getTypeMirror();
            return ClassName.get((TypeElement) UtilMgr.getMgr().getTypeUtils().asElement(typeMirror));
        }
    }

    public void parse () {
        parseEffectField (type);
        routers = configurations.getRoute();
        for (int i = 0; i < routers.length; i++) {
            String route = routers[i];
            checkIsDuplicate(route);
        }
    }

    private void checkIsDuplicate(String route) {
        if (route.endsWith("/")) {
            route = route.substring(0,route.lastIndexOf("/"));
        }

        if (parsed.containsKey(route)) {
            throw new RouterException(String.format("A same scheme was double defined on another class %s", parsed.get(route)),type);
        }
        parsed.put(route,type);
    }

    private void parseEffectField(TypeElement type) {
        List<? extends Element> elements = type.getEnclosedElements();
        for (Element ele : elements) {
            if (ele.getKind() != ElementKind.FIELD) continue;
            // get field annotated by Arg
            if (!Utils.isEffectType((VariableElement) ele)) continue;

            Arg arg = ele.getAnnotation(Arg.class);
            if (arg == null) continue;

            String key = Utils.getKeyFromArg(arg,ele.getSimpleName().toString());
            map.put(key,ele.asType());
        }
    }

    public Map<String, TypeMirror> getMap() {
        return map;
    }

    public String[] getScheme() {
        return routers;
    }

    public TypeElement getType() {
        return type;
    }

    public ClassName getExecutorClass() {
        return executorClass;
    }

    public RouteRuleConfig getConfigurations() {
        return configurations;
    }
}

