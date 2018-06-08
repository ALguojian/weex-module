package com.shuwei.weex.wxmodule;

import com.alguojian.aldialog.dialog.FailDialog;
import com.alguojian.aldialog.dialog.InfoDialog;
import com.alguojian.aldialog.dialog.LoadingDialog;
import com.alguojian.aldialog.dialog.SuccessDialog;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

/**
 * 加载动画
 * 2018/5/8
 *
 * @author alguojian
 */

public class ProgressHud extends BaseWXModule {

    private LoadingDialog mLoadingDialog;

    /**
     * 获得缓存
     */
    @JSMethod(uiThread = true)
    public void show(JSCallback jsCallback) {

        if (!checkContext(jsCallback)) {
            return;
        }

        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mWXSDKInstance.getContext(), 2);
            mLoadingDialog.setDissmissByOutside(false);
            mLoadingDialog.setDissmissByBack(true);
        }
        mLoadingDialog.show();
        jsCallBack(jsCallback, MSG_SUCCESS);

    }


    @JSMethod(uiThread = true)
    public void dismiss(JSCallback jsCallback) {
        if (!checkContext(jsCallback)) {
            return;
        }
        dismissLoading();
        jsCallBack(jsCallback, MSG_SUCCESS);
    }

    private void dismissLoading() {

        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }


    @JSMethod(uiThread = true)
    public void showInfo(String string, JSCallback jsCallback) {
        if (!checkContext(jsCallback)) {
            return;
        }
        dismissLoading();
        InfoDialog infoDialog = new InfoDialog(mWXSDKInstance.getContext(), string);
        infoDialog.sssssssss(false, false);
        infoDialog.show();
        jsCallBack(jsCallback, MSG_SUCCESS);
    }

    @JSMethod(uiThread = true)
    public void showSuccess(String string, JSCallback jsCallback) {
        if (!checkContext(jsCallback)) {
            return;
        }
        dismissLoading();
        SuccessDialog successDialog = new SuccessDialog(mWXSDKInstance.getContext(), string);
        successDialog.sssssssss(false, false);
        successDialog.show();
        jsCallBack(jsCallback, MSG_SUCCESS);
    }

    @JSMethod(uiThread = true)
    public void showError(String string, JSCallback jsCallback) {
        if (!checkContext(jsCallback)) {
            return;
        }
        dismissLoading();
        FailDialog failDialog = new FailDialog(mWXSDKInstance.getContext(), string);
        failDialog.sssssssss(false, false);
        failDialog.show();
        jsCallBack(jsCallback, MSG_SUCCESS);
    }
}
