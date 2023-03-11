package com.perevod.perevodkassa.presentation.global.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CommonAlertDialog : DialogFragment() {

    private val params: CommonAlertParam by lazy(LazyThreadSafetyMode.NONE) {
        CommonAlertScreen.fetchArgParams(requireArguments())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext(), params.style).also {
            if (params.title != null) {
                it.setTitle(params.title)
            }
            if (params.text != null) {
                it.setMessage(params.text)
            }
            if (params.ok != null) {
                it.setPositiveButton(
                    params.ok
                ) { _, _ ->
                    deliverResult(CommonAlertResult.Ok)
                }
            }
            if (params.cancel != null) {
                it.setNegativeButton(
                    params.cancel
                ) { _, _ ->
                    deliverResult(CommonAlertResult.Cancel)
                }
            }
        }
        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface) {
        deliverResult(CommonAlertResult.Cancel)
        super.onCancel(dialog)
    }

    private fun deliverResult(result: CommonAlertResult) {
        parentFragmentManager.setFragmentResult(
            params.requestKey,
            CommonAlertScreen.prepareResult(result)
        )
    }
}