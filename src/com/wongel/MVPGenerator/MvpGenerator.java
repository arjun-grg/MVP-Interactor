package com.wongel.MVPGenerator;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.util.ClassUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.Properties;

/**
 * Created by tseringwongelgurung on 12/27/17.
 */

class MvpGenerator {
    private OnFinishListner<String> listner;

    public MvpGenerator(OnFinishListner<String> listner) {
        this.listner = listner;
    }

    public void createLayout(PsiDirectory srcDirectory, Properties defaultProperties, boolean isFragment, String templateName) throws Exception {
        if (isFragment)
            createFileFromTemplate(templateName, "FragmentTemplate.xml", defaultProperties, srcDirectory);
        else
            createFileFromTemplate(templateName, "ActivityTemplate.xml", defaultProperties, srcDirectory);
    }

    public void createPackage(PsiDirectory subDirectory,MvpModule mvpModule) {
        Properties defaultProperties = FileTemplateManager.getInstance(subDirectory.getProject()).getDefaultProperties();

        PsiDirectory srcDirectory;
        srcDirectory = ClassUtil.sourceRoot(subDirectory).getParent().findSubdirectory("res").findSubdirectory("layout");

        if (srcDirectory == null)
            srcDirectory = ClassUtil.sourceRoot(subDirectory).getParent().findSubdirectory("res").findSubdirectory("layouts");

        try {
            createLayout(srcDirectory, defaultProperties, mvpModule.isFragment(), getName(mvpModule.isFragment(), mvpModule.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            listner.onFailed(e.getMessage());
        }

        try {
            if (mvpModule.isKotlin())
                createMVPKotlin(subDirectory, defaultProperties, mvpModule);
            else
                createMVPJava(subDirectory, defaultProperties, mvpModule);
        } catch (Exception e) {
            e.printStackTrace();
            listner.onFailed(e.getMessage());
        }
    }

    public void createMVPJava(PsiDirectory directory, Properties defaultProperties,MvpModule mvpModule) throws Exception {
        defaultProperties.setProperty("LAYOUT_NAME", getName(mvpModule.isKotlin(), mvpModule.getName()));
        defaultProperties.setProperty("R_PATH", getProjectPackge(directory));
        if (mvpModule.isFragment()) {
            createFileFromTemplate(mvpModule.getName(), "MVPFragment.java", defaultProperties, directory);
        } else {
            PsiClassImpl file = (PsiClassImpl) createFileFromTemplate(mvpModule.getName(), "MVPActivity.java", defaultProperties, directory);
            registerActivity(getManifest(directory), getCreatedPacakge(file));
        }
        createFileFromTemplate(mvpModule.getName(), "PresenterTemplate.java", defaultProperties, directory);
        createFileFromTemplate(mvpModule.getName(), "ViewTemplate.java", defaultProperties, directory);

        if (mvpModule.hasInteractor())
            createFileFromTemplate(mvpModule.getName(), "InteractorTemplate.java", defaultProperties, directory);

        onFinish();
    }

    public void createMVPKotlin(PsiDirectory directory, Properties defaultProperties,MvpModule mvpModule) throws Exception {
        defaultProperties.setProperty("PACKAGE", mvpModule.getName());
        defaultProperties.setProperty("LAYOUT_NAME", getName(mvpModule.isFragment(), mvpModule.getName()));
        defaultProperties.setProperty("R_PATH", getProjectPackge(directory));
        if (mvpModule.isFragment())
            createFileFromTemplate(mvpModule.getName() + "Fragment", "MVPFragment.kt", defaultProperties, directory);
        else {
            PsiClassImpl file = (PsiClassImpl) createFileFromTemplate(mvpModule.getName() + "Activity", "MVPActivity.kt", defaultProperties, directory);
            registerActivity(getManifest(directory), getCreatedPacakge(file));
        }
        createFileFromTemplate(mvpModule.getName() + "Presenter", "PresenterTemplate.kt", defaultProperties, directory);
        createFileFromTemplate(mvpModule.getName() + "View", "ViewTemplate.kt", defaultProperties, directory);

        if (mvpModule.hasInteractor())
            createFileFromTemplate(mvpModule.getName() + "Interactor", "InteractorTemplate.kt", defaultProperties, directory);
        onFinish();
    }

    public void registerActivity(PsiFile manifest, String activityPath) {
        XmlFile tag = (XmlFile) manifest;
        XmlTag applicationTag = tag.getRootTag().findFirstSubTag("application");
        String projectPath = tag.getRootTag().getAttribute("package").getValue();

        String path = activityPath.replace(projectPath, "");

        CommandProcessor.getInstance().executeCommand(manifest.getProject(), new Runnable() {
            @Override
            public void run() {
                XmlTag xmlTag = applicationTag.createChildTag("activity", applicationTag.getNamespace(), "", true);
                xmlTag.setAttribute("android:name", path);
                applicationTag.addSubTag(xmlTag, false);
            }
        }, "createActivity", "activity");
    }

    public String getName(boolean isFragment, String name) {
        return isFragment ? "fragment_" + name.toLowerCase() : "activity_" + name.toLowerCase();
    }

    public PsiElement createFileFromTemplate(String name, String templateName, Properties defaultProperties, PsiDirectory directory) throws Exception {
        FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);
        PsiElement element = FileTemplateUtil.createFromTemplate(template, name, defaultProperties, directory);
        return element;
    }

    public String getCreatedPacakge(PsiClassImpl file) {
        String packageName = ((PsiJavaFile) file.getParent()).getPackageName();
        return packageName + "." + file.getName();
    }

    public PsiFile getManifest(PsiDirectory directory) {
        PsiFile manifest = ClassUtil.sourceRoot(directory).getParent().findFile("AndroidManifest.xml");
        return manifest;
    }

    public String getProjectPackge(PsiDirectory directory) {
        PsiFile manifest=getManifest(directory);

        XmlFile tag = (XmlFile) manifest;
        String projectPath = tag.getRootTag().getAttribute("package").getValue();
        return projectPath;
    }

    private void onFinish(){
        listner.onFinished(null);
    }
}
