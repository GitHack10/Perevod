package com.perevod.perevodkassa.presentation.screens.result_message

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.perevod.perevodkassa.R
import com.perevod.perevodkassa.databinding.ScreenSuccessMessageBinding
import com.perevod.perevodkassa.core.arch.BaseFragment
import com.perevod.perevodkassa.core.navigation.AppRouter
import com.perevod.perevodkassa.core.navigation.Screens
import com.perevod.perevodkassa.presentation.global.extensions.onDelayedClick
import com.perevod.perevodkassa.utils.createCircleDrawable
import com.perevod.perevodkassa.utils.createRoundedRippleDrawable
import com.perevod.perevodkassa.utils.dpToPx
import com.perevod.perevodkassa.utils.hideSystemUI
import com.perevod.perevodkassa.utils.resColor
import org.koin.android.ext.android.inject
import ru.atol.drivers10.service.IFptrService
import timber.log.Timber

class PaymentSuccessFragment : BaseFragment(R.layout.screen_success_message) {

    companion object {
        const val EXTRA_MESSAGE_SUCCESS = "EXTRA_MESSAGE_SUCCESS"
        const val EXTRA_PAPER_PRINT = "EXTRA_PAPER_PRINT"
    }

    private var fptrServiceBinder: IFptrService? = null

    private val viewBinding: ScreenSuccessMessageBinding by viewBinding()
    private val router: AppRouter by inject()

    private val serviceIntent = Intent().apply {
        action = "ru.atol.drivers10.service.SERVICE"
        setPackage("ru.atol.drivers10.service")
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            fptrServiceBinder = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            fptrServiceBinder = IFptrService.Stub.asInterface(service)
        }
    }

    private val messageSuccess: String by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString(EXTRA_MESSAGE_SUCCESS) ?: getString(R.string.payment_success_screen_message)
    }

    private val paperPrint: String? by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString(EXTRA_PAPER_PRINT)
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.hideSystemUI()
    }

    override fun prepareUi() {
        initService()
        printSlip(paperPrint)
        with(viewBinding) {
            ivSuccessAnimation.background = createCircleDrawable(resColor(R.color.white_10))
            ivSuccessAnimation.setAnimation(R.raw.payment_success_anim)
            ivSuccessAnimation.playAnimation()
            tvSuccess.text = messageSuccess
            btnGoBack.background = createRoundedRippleDrawable(
                resColor(R.color.ripple_primary),
                24.dpToPx.toFloat(),
                resColor(R.color.grey_4D4D4D)
            )
            btnGoBack.onDelayedClick {
                onBackPressed()
            }
        }
    }

    override fun onDestroyView() {
        fptrServiceBinder = null
        unbindService()
        super.onDestroyView()
    }

    override fun onBackPressed() {
        router.replaceScreen(Screens.homeScreen())
    }

    private fun initService() {
        context?.bindService(serviceIntent, serviceConnection, AppCompatActivity.BIND_AUTO_CREATE)
    }

    private fun printSlip(paperPrint: String?) {
        lifecycleScope.launchWhenStarted {
            try {
                val resultOpen = fptrServiceBinder?.processJson(paperPrint)
                Timber.tag("PRINT_RESULT").e(resultOpen)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun unbindService() {
        context?.unbindService(serviceConnection)
    }
}