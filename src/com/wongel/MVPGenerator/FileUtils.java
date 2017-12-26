package com.wongel.MVPGenerator;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by tseringwongelgurung on 12/21/17.
 */
public class FileUtils {
    public static void makeDir(PsiDirectory directory, String dirName, OnFinishListner<PsiDirectory> listner) {
        try {
            Application application = ApplicationManager.getApplication();
            application.runWriteAction(() -> {
                PsiDirectory subDirectory = directory.createSubdirectory(dirName.toLowerCase());
                listner.onFinished(subDirectory);
            });
        } catch (IncorrectOperationException e) {
            listner.onFailed(e.getMessage());
        }
    }

    public static void createFile(PsiDirectory directory, String fileName, String text) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());

        FileType type = FileTypeRegistry.getInstance().getFileTypeByFileName(fileName);
        final PsiFile file = factory.createFileFromText(fileName, type, text);
        directory.add(file);
    }

}
