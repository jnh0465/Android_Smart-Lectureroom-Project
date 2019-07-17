package com.nuntteuniachim.sroomi.view.setting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.nuntteuniachim.sroomi.R
import com.nuntteuniachim.sroomi.base.SharedPreferenceManager
import com.nuntteuniachim.sroomi.view.login.LoginActivity
import com.nuntteuniachim.sroomi.view.main.FragmentActivity
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.soundcloud.android.crop.Crop
import java.io.File
import java.io.IOException

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private var isPermission: Boolean? = true
    private var isCamera: Boolean? = false
    private var tempFile: File? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
        private const val PICK_FROM_ALBUM = 1
        private const val PICK_FROM_CAMERA = 2
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        mContext = this

        tedPermission()

        tv_username.text = SharedPreferenceManager.getString(LoginActivity.mContext, "PREFNM").toString().substring(1, 4)
        tv_hakbun.text = SharedPreferenceManager.getString(LoginActivity.mContext, "PREFID").toString().substring(2, 4) + "학번"

        btn_logout.setOnClickListener(this)
        btn_editpicture.setOnClickListener(this)

        var temp = SharedPreferenceManager.getString(mContext, "PREFBIT")
        if (temp != "") { //값이 존재하면
            val options = BitmapFactory.Options()
            val originalBm = BitmapFactory.decodeFile(temp, options)
            iv_userpicture.setImageBitmap(originalBm)  //imageVIew에 그리기
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> {
                val intent = Intent(mContext, LoginActivity::class.java)
                startActivity(intent)
                SharedPreferenceManager.clear(FragmentActivity.mContext)
            }
            R.id.btn_editpicture -> registerPictures()
        }
    }

    private fun registerPictures() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("사진 등록/수정")

        builder.setItems(R.array.LAN) { dialog, pos ->
            when(pos){
                0 -> takePhoto()
                1 -> goToAlbum()
            }
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) { //중간에 취소시
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show()
            if (tempFile != null) {
                if (tempFile!!.exists()) {
                    if (tempFile!!.delete()) {
                        tempFile = null
                    }
                }
            }
            return
        }

        when (requestCode) {
            PICK_FROM_ALBUM -> {
                val photoUri = data!!.data
                cropImage(photoUri)
            }
            PICK_FROM_CAMERA -> {
                val photoUri = Uri.fromFile(tempFile)
                cropImage(photoUri)
            }
            Crop.REQUEST_CROP -> setImage()
        }
    }

    private fun tedPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                isPermission = true
            } // 권한요청 성공

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                isPermission = false
            } // 권한 요청 실패
        }

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(resources.getString(R.string.permission_2))
                .setDeniedMessage(resources.getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check()
    }

    private fun goToAlbum() { //앨범에서 이미지 가져오기
        isCamera = false

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    private fun takePhoto() { //카메라에서 이미지 가져오기
        isCamera = true
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            tempFile = createImageFile()
        } catch (e: IOException) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            finish()
            e.printStackTrace()
        }

        if (tempFile != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                val photoUri = FileProvider.getUriForFile(this, "com.nuntteuniachim.sroomi.fileprovider", tempFile!!)/////
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, PICK_FROM_CAMERA)
            } else {
                val photoUri = Uri.fromFile(tempFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, PICK_FROM_CAMERA)
            }
        }
    }

    private fun cropImage(photoUri: Uri?) {
        if (tempFile == null) {
            try {
                tempFile = createImageFile()
            } catch (e: IOException) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                finish()
                e.printStackTrace()
            }
        }

        val savingUri = Uri.fromFile(tempFile)
        Crop.of(photoUri, savingUri).asSquare().start(this)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName: String = SharedPreferenceManager.getString(LoginActivity.mContext, "PREFID").toString()
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun setImage() {
        ImageResizeUtils.resizeFile(tempFile!!, tempFile!!, 1280, isCamera!!)

        val options = BitmapFactory.Options()
        val originalBm = BitmapFactory.decodeFile(tempFile!!.absolutePath, options)
        SharedPreferenceManager.setString(mContext, "PREFBIT", tempFile!!.absolutePath) //SharedPreferenceManager에 저장

        iv_userpicture.setImageBitmap(originalBm) //imageVIew에 그리기

        tempFile = null
    }
}
