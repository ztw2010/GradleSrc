package com.ebest.frame.gradlesrc.aop

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

/**
 * 使用ajc编译java代码 ， 同 时 织 入 切 片 代 码
 * 使用 AspectJ 的编译器（ajc，一个java编译器的扩展）
 * 对所有受 aspect 影响的类进行织入。
 * 在 gradle 的编译 task 中增加额外配置，使之能正确编译运行。
 */
public class AspectjPlugin implements Plugin<Project> {

    void apply(Project project) {
        def hasApp = project.plugins.hasPlugin('com.android.application')
        def hasLib = project.plugins.hasPlugin('com.android.library')
        if (!hasApp && !hasLib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }
        project.dependencies {
            compile 'org.aspectj:aspectjrt:1.8.9'
        }
        final def log = project.logger
        log.error "========================";
        log.error "Aspectj切片开始编织Class!";
        log.error "========================";
        final def variants
        if (hasApp) {
            variants = project.android.applicationVariants
        } else {
            variants = project.android.libraryVariants
        }
        variants.all { variant ->
            JavaCompile javaCompile = variant.javaCompile
            javaCompile.doLast {
                String[] args = ["-showWeaveInfo",
                                 "-source", javaCompile.sourceCompatibility,
                                 "-target", javaCompile.targetCompatibility,
                                 "-inpath", javaCompile.destinationDir.toString(),
                                 "-aspectpath", javaCompile.classpath.asPath,
                                 "-d", javaCompile.destinationDir.toString(),
                                 "-classpath", javaCompile.classpath.asPath,
                                 "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
                log.error "ajc args: " + Arrays.toString(args)

                MessageHandler handler = new MessageHandler(true);
                new Main().run(args, handler);
                for (IMessage message : handler.getMessages(null, true)) {
                    switch (message.getKind()) {
                        case IMessage.ABORT:
                        case IMessage.ERROR:
                        case IMessage.FAIL:
                            log.error message.message, message.thrown
                            break;
                        case IMessage.WARNING:
                            log.warn message.message, message.thrown
                            break;
                        case IMessage.INFO:
                            log.info message.message, message.thrown
                            break;
                        case IMessage.DEBUG:
                            log.debug message.message, message.thrown
                            break;
                    }
                }
            }
        }
    }
}