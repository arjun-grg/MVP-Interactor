package com.wongel.MVPGenerator;

import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by tseringwongelgurung on 12/21/17.
 */
public class FileUtils {
    public static PsiDirectory makeDir(PsiDirectory directory, String dirName) {
        try {
            return directory.createSubdirectory(dirName);
        } catch (IncorrectOperationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void createFile(PsiDirectory directory, String fileName, String text) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());

        FileType type = FileTypeRegistry.getInstance().getFileTypeByFileName(fileName);
        final PsiFile file = factory.createFileFromText(fileName, type, text);
        directory.add(file);
    }

}
