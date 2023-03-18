
package com.perevod.perevodkassa.presentation.global.dialog

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.core.navigation.route.FragmentRoute
import com.perevod.perevodkassa.utils.withArguments
import kotlinx.parcelize.Parcelize

class CommonAlertScreen(
    private val params: CommonAlertParam
) : FragmentRoute() {

    companion object {

        private const val ARG_PARAMS = "arg_params"
        private const val ARG_RESULT = "arg_result"

        fun fetchArgParams(args: Bundle): CommonAlertParam =
            args.getParcelable(ARG_PARAMS)!!

        fun prepareResult(result: CommonAlertResult): Bundle = Bundle().apply {
            putParcelable(ARG_RESULT, result)
        }

        @JvmStatic
        fun fetchResult(bundle: Bundle): CommonAlertResult =
            bundle.getParcelable(ARG_RESULT) ?: CommonAlertResult.Cancel

        fun isConfirmed(bundle: Bundle): Boolean {
            val result = bundle.getParcelable<CommonAlertResult>(ARG_RESULT) ?: return false
            return result is CommonAlertResult.Ok
        }
    }

    override fun createFragment(factory: FragmentFactory): Fragment =
        CommonAlertDialog().withArguments {
            putParcelable(ARG_PARAMS, params)
        }
}

sealed class CommonAlertResult : Parcelable {

    @Parcelize
    object Ok : CommonAlertResult()

    @Parcelize
    object Cancel : CommonAlertResult()
}

@Parcelize
class CommonAlertParam(
    val requestKey: String,
    val title: String? = null,
    val text: String? = null,
    val ok: String? = null,
    val cancel: String? = null,
    val style: Int = R.style.RoundedDialogStyle
) : Parcelable