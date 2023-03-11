package com.perevod.perevodkassa.core.arch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import org.koin.core.scope.Scope
import org.koin.java.KoinJavaComponent
import com.perevod.perevodkassa.presentation.global.extensions.createClassScope

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
abstract class BaseFragment(resourceLayout: Int) : Fragment(resourceLayout) {

    private val scopesToCloseOnDestroy = mutableSetOf<Scope>()

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareUi()
        setupViewModel()
    }

    fun clearScopes() {

        scopesToCloseOnDestroy.run {
            forEach { it.close() }
            clear()
        }
    }

    protected inline fun <reified T : BaseFragment> T.createBindedScope(): Scope {
        return KoinJavaComponent.getKoin().createClassScope<T>().untilDestroy()
    }

    protected fun Scope.untilDestroy(): Scope {
        if (!scopesToCloseOnDestroy.contains(this)) scopesToCloseOnDestroy.add(this)
        return this
    }

    protected fun getBaseFragment() = (parentFragment as BaseFragment)

    protected fun Fragment.share(message: String) {

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    protected fun Fragment.hideKeyboard() = activity?.hideKeyboard()
    protected fun Fragment.showKeyboard() = activity?.showKeyboard()
    protected fun Fragment.setSoftInputMode(mode: Int) = activity?.window?.setSoftInputMode(mode)

    private fun Activity.showKeyboard() = showKeyboard(currentFocus ?: View(this))
    private fun Activity.hideKeyboard() = hideKeyboard(currentFocus ?: View(this))

    private fun Context.hideKeyboard(view: View) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun Context.showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    open fun prepareUi() {}
    open fun setupViewModel() {}
    open fun onBackPressed() {}
}