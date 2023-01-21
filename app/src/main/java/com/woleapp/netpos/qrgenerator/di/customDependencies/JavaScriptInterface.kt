package com.woleapp.netpos.qrgenerator.di.customDependencies

import android.webkit.JavascriptInterface
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.woleapp.netpos.qrgenerator.ui.dialog.ResponseModal
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

class JavaScriptInterface(
     private val fragmentManager: FragmentManager,
     private val termUrl: String,
     private val md: String,
     private val cReq: String,
     private val acsUrl: String,
     private val transId: String
) {
    @Inject
    lateinit var responseModal: ResponseModal
    private val webViewBaseUrl =
        BuildConfig.STRING_WEB_VIEW_BASE_URL + BuildConfig.STRING_CHECKOUT_MERCHANT_ID + "/"

    @JavascriptInterface
    fun sendValueToWebView() =
        "$termUrl<======>$md<======>$cReq<======>$acsUrl<======>$transId<======>$webViewBaseUrl"

    @JavascriptInterface
    fun webViewCallback(webViewResponse: String) {
        val responseFromWebView = Gson().fromJson(
            webViewResponse,
            QrTransactionResponseModel::class.java
        )
        val response = responseFromWebView.mapQrTransRespToQrRespFinalModel(
            accType = "",
            terminalId = Singletons.getCurrentlyLoggedInUser()?.terminal_id ?: "",
            merchantId = BuildConfig.STRING_MERCHANT_ID
        )
        fragmentManager.setFragmentResult(
            QR_TRANSACTION_RESULT_REQUEST_KEY,
            bundleOf(QR_TRANSACTION_RESULT_BUNDLE_KEY to response)
        )
        fragmentManager.popBackStack()
        responseModal.show(fragmentManager, STRING_QR_RESPONSE_MODAL_DIALOG_TAG)
    }
}
