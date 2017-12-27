package com.wongel.MVPGenerator;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.util.ClassUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

/**
 * Created by tseringwongelgurung on 12/21/17.
 */
public class MvpPackage extends AnAction {

    public static final void prettyPrint(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));
        System.out.println(out.toString());
    }

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

    private void createLayout(PsiDirectory srcDirectory, Properties defaultProperties, boolean isFragment, String templateName) throws Exception {
        if (isFragment)
            createFileFromTemplate(templateName, "FragmentTemplate.xml", defaultProperties, srcDirectory);
        else
            createFileFromTemplate(templateName, "ActivityTemplate.xml", defaultProperties, srcDirectory);
    }

    private void createPackage(PsiDirectory subDirectory, boolean isFragment, String name,
                               boolean createInteractor, boolean isKotlin) {
        Properties defaultProperties = FileTemplateManager.getInstance(subDirectory.getProject()).getDefaultProperties();

        PsiDirectory srcDirectory = ClassUtil.sourceRoot(subDirectory).getParent().findSubdirectory("res").findSubdirectory("layout");

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

    private void createMVPJava(PsiDirectory directory, Properties defaultProperties, String name, boolean isFragment,
                               boolean makeInteractor) throws Exception {
        defaultProperties.setProperty("LAYOUT_NAME", getName(isFragment, name));
        defaultProperties.setProperty("R_PATH", getProjectPackge(directory));
        if (isFragment) {
            createFileFromTemplate(name, "MVPFragment.java", defaultProperties, directory);
        } else {
            PsiClassImpl file = (PsiClassImpl) createFileFromTemplate(name, "MVPActivity.java", defaultProperties, directory);
            registerActivity(getManifest(directory), getCreatedPacakge(file));
        }
        createFileFromTemplate(name, "PresenterTemplate.java", defaultProperties, directory);
        createFileFromTemplate(name, "ViewTemplate.java", defaultProperties, directory);

        if (makeInteractor)
            createFileFromTemplate(name, "InteractorTemplate.java", defaultProperties, directory);
    }

    private void createMVPKotlin(PsiDirectory directory, Properties defaultProperties, String name, boolean isFragment,
                                 boolean makeInteractor) throws Exception {
        defaultProperties.setProperty("PACKAGE", name);
        defaultProperties.setProperty("LAYOUT_NAME", getName(isFragment, name));
        defaultProperties.setProperty("R_PATH", getProjectPackge(directory));
        if (isFragment)
            createFileFromTemplate(name + "Fragment", "MVPFragment.kt", defaultProperties, directory);
        else {
            PsiClassImpl file = (PsiClassImpl) createFileFromTemplate(name + "Activity", "MVPActivity.kt", defaultProperties, directory);
            registerActivity(getManifest(directory), getCreatedPacakge(file));
        }
        createFileFromTemplate(name + "Presenter", "PresenterTemplate.kt", defaultProperties, directory);
        createFileFromTemplate(name + "View", "ViewTemplate.kt", defaultProperties, directory);

        if (makeInteractor)
            createFileFromTemplate(name + "Interactor", "InteractorTemplate.kt", defaultProperties, directory);
    }

    private void registerActivity(PsiFile manifest, String activityPath) {
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

    private String getName(boolean isFragment, String name) {
        return isFragment ? "fragment_" + name.toLowerCase() : "activity_" + name.toLowerCase();
    }

    private PsiElement createFileFromTemplate(String name, String templateName, Properties defaultProperties, PsiDirectory directory) throws Exception {
        FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);
        PsiElement element = FileTemplateUtil.createFromTemplate(template, name, defaultProperties, directory);
        return element;
    }

    private String getCreatedPacakge(PsiClassImpl file) {
        String packageName = ((PsiJavaFile) file.getParent()).getPackageName();
        return packageName + "." + file.getName();
    }

    private PsiFile getManifest(PsiDirectory directory) {
        PsiFile manifest = ClassUtil.sourceRoot(directory).getParent().findFile("AndroidManifest.xml");
        return manifest;
    }

    private String getProjectPackge(PsiDirectory directory) {
        PsiFile manifest=getManifest(directory);

        XmlFile tag = (XmlFile) manifest;
        String projectPath = tag.getRootTag().getAttribute("package").getValue();
        return projectPath;
    }
}
