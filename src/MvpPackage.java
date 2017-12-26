import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.ide.projectWizard.ModuleTypeCategory;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;

import java.io.File;
import java.util.Properties;

/**
 * Created by tseringwongelgurung on 12/21/17.
 */
public class MvpPackage extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        MvpPackageDialog.create(new OnListner() {
            @Override
            public void OnSuccess(String name, boolean isKotlin, boolean isFragment, boolean createInteractor) {
                final PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
                if (element == null)
                    return;

                PsiDirectory directory = (PsiDirectory) element;
                PsiDirectory subDirectory = FileUtils.makeDir(directory, name);

                if (subDirectory == null)
                    return;

                if (isKotlin)
                    createMVPKotlin(subDirectory, name, isFragment, createInteractor);
                else
                    createMVPJava(subDirectory, name, isFragment, createInteractor);
            }
        });
    }

    private void createMVPKotlin(PsiDirectory directory, String name, boolean isFragment, boolean makeInteractor) {
        FileTemplate activityTemplate;
        FileTemplate presenterTemplate;
        FileTemplate viewTemplate;
        FileTemplate interactorTemplate = null;
        String _name = name;
        if (isFragment) {
            activityTemplate = FileTemplateManager.getDefaultInstance().getInternalTemplate("MVPFragment.kt");
            _name += "Fragment";
        } else {
            activityTemplate = FileTemplateManager.getDefaultInstance().getInternalTemplate("MVPActivity.kt");
            _name += "Activity";
        }
        presenterTemplate = FileTemplateManager.getDefaultInstance().getInternalTemplate("PresenterTemplate.kt");
        viewTemplate = FileTemplateManager.getDefaultInstance().getInternalTemplate("ViewTemplate.kt");
        if (makeInteractor)
            interactorTemplate = FileTemplateManager.getDefaultInstance().getInternalTemplate("InteractorTemplate.kt");

        Properties defaultProperties = FileTemplateManager.getInstance(directory.getProject()).getDefaultProperties();
        defaultProperties.setProperty("PACKAGE", name);
        try {
            FileTemplateUtil.createFromTemplate(activityTemplate, _name, defaultProperties, directory);
            FileTemplateUtil.createFromTemplate(presenterTemplate, name + "Presenter", defaultProperties, directory);
            FileTemplateUtil.createFromTemplate(viewTemplate, name + "View", defaultProperties, directory);
            if (interactorTemplate != null)
                FileTemplateUtil.createFromTemplate(interactorTemplate, name + "Interactor", defaultProperties, directory);
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("Ola" + e1.getMessage());
        }
    }

    private void createMVPJava(PsiDirectory directory, String name, boolean isFragment, boolean makeInteractor) {
        FileTemplate activityTemplate;
        FileTemplate presenterTemplate;
        FileTemplate viewTemplate;
        FileTemplate interactorTemplate = null;
        if (isFragment)
            activityTemplate = FileTemplateManager.getDefaultInstance().getInternalTemplate("MVPFragment.java");
        else
            activityTemplate = FileTemplateManager.getDefaultInstance().getInternalTemplate("MVPActivity.java");
        presenterTemplate = FileTemplateManager.getDefaultInstance().getInternalTemplate("PresenterTemplate.java");
        viewTemplate = FileTemplateManager.getDefaultInstance().getInternalTemplate("ViewTemplate.java");
        if (makeInteractor)
            interactorTemplate = FileTemplateManager.getDefaultInstance().getInternalTemplate("InteractorTemplate.java");


        Properties defaultProperties = FileTemplateManager.getInstance(directory.getProject()).getDefaultProperties();
        try {
            FileTemplateUtil.createFromTemplate(activityTemplate, name, defaultProperties, directory);
            FileTemplateUtil.createFromTemplate(presenterTemplate, name, defaultProperties, directory);
            FileTemplateUtil.createFromTemplate(viewTemplate, name, defaultProperties, directory);
            if (interactorTemplate != null)
                FileTemplateUtil.createFromTemplate(interactorTemplate, name, defaultProperties, directory);
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("Ola" + e1.getMessage());
        }
    }
}
