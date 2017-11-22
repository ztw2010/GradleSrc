package com.ebest.frame.gradlesrc

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.*
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

public class ComCodeTransform extends Transform {

    private Project project

    ClassPool classPool

    String applicationName

    ComCodeTransform(Project project) {
        this.project = project
    }

    /**
     * 具体的处理
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        getRealApplicationName(transformInvocation.getInputs());
        classPool = new ClassPool()
        project.android.bootClasspath.each {
            classPool.appendClassPath((String) it.absolutePath)
        }
        def box = ConvertUtils.toCtClasses(transformInvocation.getInputs(), classPool)

        //要收集的application，一般情况下只有一个
        List<CtClass> applications = new ArrayList<>();
        //要收集的applicationlikes，一般情况下有几个组件就有几个applicationlike
        List<CtClass> activators = new ArrayList<>();

        for (CtClass ctClass : box) {
            if (isApplication(ctClass)) {
                applications.add(ctClass)
                continue;
            }
            if (isActivator(ctClass)) {
                activators.add(ctClass)
            }
        }
        for (CtClass ctClass : applications) {
            System.out.println("application is   " + ctClass.getName());
        }
        for (CtClass ctClass : activators) {
            System.out.println("applicationlike is   " + ctClass.getName());
        }

        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        transformInvocation.inputs.each { TransformInput input ->
            //对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput ->
                //jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)

            }
            //对类型为“文件夹”的input进行遍历,文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
            input.directoryInputs.each { DirectoryInput directoryInput ->
                boolean isRegisterCompoAuto = project.extensions.combuild.isRegisterCompoAuto
                if (isRegisterCompoAuto) {
                    String fileName = directoryInput.file.absolutePath
                    File dir = new File(fileName)
                    dir.eachFileRecurse { File file ->
                        String filePath = file.absolutePath
                        String classNameTemp = filePath.replace(fileName, "").replace("\\", ".").replace("/", ".")
                        if (classNameTemp.endsWith(".class")) {
                            String className = classNameTemp.substring(1, classNameTemp.length() - 6)
                            if (className.equals(applicationName)) {
                                System.out.println("traget file path=" + fileName);
                                injectApplicationCode(applications.get(0), activators, fileName);
                            }
                        }
                    }
                }
                // 获取output目录
                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                System.out.println("srcDir path=" + directoryInput.file.absolutePath + ",destDir path=" + dest.absolutePath);
                // 将input的目录复制到output指定目录
                try {
                    FileUtils.copyDirectory(directoryInput.file, dest)
                    System.out.println("copy comeplete");
                } catch (IOException) {
                    System.out.println("copy file has IOException");
                }
            }
        }
    }


    private void getRealApplicationName(Collection<TransformInput> inputs) {
        applicationName = project.extensions.combuild.applicatonName
        if (applicationName == null || applicationName.isEmpty()) {
            throw new RuntimeException("you should set applicationName in combuild")
        }
    }


    private void injectApplicationCode(CtClass ctClassApplication, List<CtClass> activators, String patch) {
        System.out.println("injectApplicationCode begin");
        ctClassApplication.defrost();
        try {
            CtMethod attachBaseContextMethod = ctClassApplication.getDeclaredMethod("onCreate", null)
            attachBaseContextMethod.insertAfter(getAutoLoadComCode(activators))
        } catch (CannotCompileException | NotFoundException e) {
            System.out.println("injectApplicationCode has CannotCompileException or NotFoundException");
            StringBuilder methodBody = new StringBuilder();
            methodBody.append("protected void onCreate() {");
            methodBody.append("super.onCreate();");
            methodBody.
                    append(getAutoLoadComCode(activators));
            methodBody.append("}");
            ctClassApplication.addMethod(CtMethod.make(methodBody.toString(), ctClassApplication));
        } catch (Exception e) {
            System.out.println("injectApplicationCode has Exception");
        }
        ctClassApplication.writeFile(patch)
        ctClassApplication.detach()

        System.out.println("injectApplicationCode success ");
    }

    private String getAutoLoadComCode(List<CtClass> activators) {
        StringBuilder autoLoadComCode = new StringBuilder();
        for (CtClass ctClass : activators) {
            autoLoadComCode.append("new " + ctClass.getName() + "()" + ".onCreate();")
        }
        System.out.println("inject code="+autoLoadComCode.toString());
        return autoLoadComCode.toString()
    }


    private boolean isApplication(CtClass ctClass) {
        try {
            if (applicationName != null && applicationName.equals(ctClass.getName())) {
                return true;
            }
        } catch (Exception e) {
            println "class not found exception class name:  " + ctClass.getName()
        }
        return false;
    }

    private boolean isActivator(CtClass ctClass) {
        try {
            for (CtClass ctClassInter : ctClass.getInterfaces()) {
                if ("com.ebest.frame.commnetservicve.applike.IApplicationLike".equals(ctClassInter.name)) {
                    return true;
                }
            }
        } catch (Exception e) {
            println "class not found exception class name:  " + ctClass.getName()
        }

        return false;
    }

    /**
     * 设置自定义的Transform对应的Task名称
     * @return
     */
    @Override
    String getName() {
        return "ComponentCode"
    }

    /**
     * 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型,这样确保其他类型的文件不会传入
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指定Transform的作用范围
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

}