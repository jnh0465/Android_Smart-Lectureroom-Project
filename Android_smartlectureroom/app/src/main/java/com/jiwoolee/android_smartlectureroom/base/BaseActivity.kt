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

open class BaseActivity : AppCompatActivity() {
    private var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
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

//    fun materialDialog(presenter: LoginPresenter) {
//        val registerLayout = LayoutInflater.from(LoginActivity.mContext).inflate(R.layout.findpw_layout, null)
//        MaterialStyledDialog.Builder(LoginActivity.mContext)
//                .setTitle("FIND PASSWORD")
//                .setCustomView(registerLayout)
//                .setNegativeText("CANSEL")
//                .onNegative { dialog, which -> dialog.dismiss() }
//                .setPositiveText("SEND")
//                .onPositive { dialog, which ->
//                    val registerId = registerLayout.findViewById<TextView>(R.id.edit_id)
//                    val registerPw = registerLayout.findViewById<TextView>(R.id.edit_password)
//                    val registerPw2 = registerLayout.findViewById<TextView>(R.id.edit_password2)
//
//                    val test = presenter.validateForm(registerId, registerPw) //폼 채움 여부 확인
//                    if (test && registerPw === registerPw2) {
//                        presenter.findPassword(registerId.text.toString(), registerPw.text.toString())
//                    }
//                }.show()
//    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

}