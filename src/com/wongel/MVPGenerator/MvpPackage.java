package com.wongel.MVPGenerator;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.ClassUtil;

import java.util.Properties;

/**
 * Created by tseringwongelgurung on 12/21/17.
 */
public class MvpPackage extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        MvpPackageDialog.create((name, isKotlin, isFragment, createInteractor) -> {
            final PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
            if (element == null)
                return;

            PsiDirectory directory = (PsiDirectory) element;

            FileUtils.makeDir(directory, name, new OnFinishListner<PsiDirectory>() {
                @Override
                public void onFinished(PsiDirectory result) {
                    createPackage(result, isFragment, name, createInteractor, isKotlin);
                }

                @Override
                public void onFailed(String msg) {
                    System.out.println(msg);
                }
            });
        });
    }

    private void createPackage(PsiDirectory subDirectory, boolean isFragment, String name,
                               boolean createInteractor, boolean isKotlin) {
        PsiDirectory javaDirectory = ClassUtil.sourceRoot(subDirectory);
        PsiDirectory srcDirectory = javaDirectory.getParent().findSubdirectory("res").findSubdirectory("layout");

        Properties defaultProperties = FileTemplateManager.getInstance(subDirectory.getProject()).getDefaultProperties();

        try {
            createLayout(srcDirectory, defaultProperties, isFragment, getName(isFragment, name));
            if (isKotlin)
                createMVPKotlin(subDirectory, defaultProperties, name, isFragment, createInteractor);
            else
                createMVPJava(subDirectory, defaultProperties, name, isFragment, createInteractor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLayout(PsiDirectory srcDirectory, Properties defaultProperties, boolean isFragment, String templateName) throws Exception {
        if (isFragment)
            createFileFromTemplate(templateName, "FragmentTemplate.xml", defaultProperties, srcDirectory);
        else
            createFileFromTemplate(templateName, "ActivityTemplate.xml", defaultProperties, srcDirectory);
    }

    private void createMVPKotlin(PsiDirectory directory, Properties defaultProperties, String name, boolean isFragment,
                                 boolean makeInteractor) throws Exception {
        defaultProperties.setProperty("PACKAGE", name);
        defaultProperties.setProperty("LAYOUT_NAME", getName(isFragment, name));
        if (isFragment)
            createFileFromTemplate(name + "Fragment", "MVPFragment.kt", defaultProperties, directory);
        else
            createFileFromTemplate(name + "Activity", "MVPActivity.kt", defaultProperties, directory);
        createFileFromTemplate(name + "Presenter", "PresenterTemplate.kt", defaultProperties, directory);
        createFileFromTemplate(name + "View", "ViewTemplate.kt", defaultProperties, directory);

        if (makeInteractor)
            createFileFromTemplate(name + "Interactor", "InteractorTemplate.kt", defaultProperties, directory);
    }

    private void createMVPJava(PsiDirectory directory, Properties defaultProperties, String name, boolean isFragment,
                               boolean makeInteractor) throws Exception {
        defaultProperties.setProperty("LAYOUT_NAME", getName(isFragment, name));
        if (isFragment) {
            createFileFromTemplate(name, "MVPFragment.java", defaultProperties, directory);
        } else {
            createFileFromTemplate(name, "MVPActivity.java", defaultProperties, directory);
        }
        createFileFromTemplate(name, "PresenterTemplate.java", defaultProperties, directory);
        createFileFromTemplate(name, "ViewTemplate.java", defaultProperties, directory);

        if (makeInteractor)
            createFileFromTemplate(name, "InteractorTemplate.java", defaultProperties, directory);
    }

    private String getName(boolean isFragment, String name) {
        return isFragment ? "fragment_" + name.toLowerCase() : "activity_" + name.toLowerCase();
    }

    private void createFileFromTemplate(String name, String templateName, Properties defaultProperties, PsiDirectory directory) throws Exception {
        FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);
        FileTemplateUtil.createFromTemplate(template, name, defaultProperties, directory);
    }
}
