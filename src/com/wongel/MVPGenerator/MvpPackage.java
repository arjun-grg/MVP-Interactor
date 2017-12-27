package com.wongel.MVPGenerator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;

/**
 * Created by tseringwongelgurung on 12/21/17.
 */
public class MvpPackage extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        OnDialogListner listner = (MvpPackageDialog dialog, MvpModule mvpModule) -> {

            final PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);

            if (element == null)
                return;

            PsiDirectory directory = (PsiDirectory) element;

            FileUtils.makeDir(directory, mvpModule.getName(), new OnFinishListner<PsiDirectory>() {
                @Override
                public void onFinished(PsiDirectory result) {
                    new MvpGenerator(new OnFinishListner<String>() {

                        @Override
                        public void onFinished(String result) {
                            dialog.dispose();
                        }

                        @Override
                        public void onFailed(String msg) {
                            dialog.setError(msg);
                        }
                    }).createPackage(result, mvpModule);
                }

                @Override
                public void onFailed(String msg) {
                    dialog.setError(msg);
                }
            });
        };
        MvpPackageDialog.create(listner);
    }
}
