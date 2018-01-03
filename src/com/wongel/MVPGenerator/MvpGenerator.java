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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by tseringwongelgurung on 12/27/17.
 */

class MvpGenerator {
    private OnFinishListner<String> listner;

    public MvpGenerator(OnFinishListner<String> listner) {
        this.listner = listner;
    }

    public void createPackage(PsiDirectory subDirectory, MvpModule mvpModule) {
        Properties defaultProperties = FileTemplateManager.getInstance(subDirectory.getProject()).getDefaultProperties();

        try {
            createLayout(subDirectory, defaultProperties, mvpModule.isFragment(), getName(mvpModule.isFragment(), mvpModule.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            listner.onFailed(e.getMessage());
        }

        try {
            createMVP(subDirectory, defaultProperties, mvpModule);
        } catch (Exception e) {
            e.printStackTrace();
            listner.onFailed(e.getMessage());
        }
    }

    public void createLayout(PsiDirectory subDirectory, Properties defaultProperties, boolean isFragment, String templateName) throws Exception {
        PsiDirectory srcDirectory;
        srcDirectory = ClassUtil.sourceRoot(subDirectory).getParent().findSubdirectory("res").findSubdirectory("layout");

        if (srcDirectory == null)
            srcDirectory = ClassUtil.sourceRoot(subDirectory).getParent().findSubdirectory("res").findSubdirectory("layouts");

        if (isFragment)
            createFileFromTemplate(templateName, "FragmentTemplate.xml", defaultProperties, srcDirectory);
        else
            createFileFromTemplate(templateName, "ActivityTemplate.xml", defaultProperties, srcDirectory);
    }

    public void createMVP(PsiDirectory directory, Properties defaultProperties, MvpModule mvpModule) throws Exception {
        defaultProperties.setProperty("PACKAGE", mvpModule.getName());
        defaultProperties.setProperty("LAYOUT_NAME", getName(mvpModule.isFragment(), mvpModule.getName()));
        defaultProperties.setProperty("R_PATH", "import " + getProjectPackge(directory) + ".R");

        createClass(getList(mvpModule), defaultProperties, directory);
        onFinish();
    }

    public void createClass(Map<String, String> map, Properties defaultProperties, PsiDirectory directory) throws Exception {
        Set<String> keys = map.keySet();
        for (String key : keys) {
            String val = map.get(key);

            if (val.equals("MVPActivity.java") || val.equals("Activity.java")) {
                PsiClassImpl file = (PsiClassImpl) createFileFromTemplate(key, val, defaultProperties, directory);
                registerActivity(getManifest(directory), getCreatedPacakge(file));
            } else if (val.equals("MVPActivity.kt") || val.equals("Activity.kt")) {
                PsiClassImpl file = (PsiClassImpl) createFileFromTemplate(key, val, defaultProperties, directory);
                registerActivity(getManifest(directory), getCreatedPacakge(file));
            } else
                createFileFromTemplate(key, val, defaultProperties, directory);
        }
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

    public Map<String, String> getList(MvpModule mvpModule) {
        Map<String, String> map = new HashMap<>();
        String subfix = ".java";
        if (mvpModule.isKotlin())
            subfix = ".kt";

        if (mvpModule.isFragment())
            map.put(mvpModule.getName() + "Fragment", "MVPFragment" + subfix);
        else
            map.put(mvpModule.getName() + "Activity", "MVPActivity" + subfix);

        map.put(mvpModule.getName() + "Presenter", "PresenterTemplate" + subfix);
        map.put(mvpModule.getName() + "View", "ViewTemplate" + subfix);

        if (mvpModule.hasInteractor())
            map.put(mvpModule.getName() + "Interactor", "InteractorTemplate" + subfix);
        return map;
    }

    private void onFinish(){
        listner.onFinished(null);
    }
}
