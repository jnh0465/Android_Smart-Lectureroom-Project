package com.jiwoolee.android_smartlectureroom.base

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.jiwoolee.android_smartlectureroom.R
import com.jiwoolee.android_smartlectureroom.view.login.LoginActivity

@Suppress("DEPRECATION")
open class BaseActivity : AppCompatActivity() {
    private var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() { //프로그래스바
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this, R.style.myDialog)
            mProgressDialog!!.addContentView(ProgressBar(this),
                    WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            mProgressDialog!!.isIndeterminate = true
        }
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

}