package com.nuntteuniachim.sroomi.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.nuntteuniachim.sroomi.R
import com.nuntteuniachim.sroomi.base.SharedPreferenceManager
import com.nuntteuniachim.sroomi.view.main.FragmentActivity
import kotlinx.android.synthetic.main.activity_setting.*
import com.nuntteuniachim.sroomi.retrofit.IMyService
import com.nuntteuniachim.sroomi.retrofit.RetrofitClient
import com.soundcloud.android.crop.Crop
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*

//회원정보, 설정

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private var disposable: CompositeDisposable? = CompositeDisposable()  //retrofit 통신
    private var retrofitClient = RetrofitClient.getInstance()
    private var iMyService: IMyService? = (retrofitClient as Retrofit).create(IMyService::class.java)

    private var isCamera: Boolean? = false
    private var tempFile: File? = null
    private lateinit var mCurrentPhotoPath: String
    private var rotatedBitmap: Bitmap? = null

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

        tv_username.text = SharedPreferenceManager.getString(LoginActivity.mContext, "PREFNM").toString().substring(1, 4)
        tv_hakbun.text = SharedPreferenceManager.getString(LoginActivity.mContext, "PREFID").toString().substring(2, 4) + "학번"

        btn_logout.setOnClickListener(this)
        btn_editpicture.setOnClickListener(this)

        requestPermissions() //권한요청
        initSetImage() //껏다 켜도 이미지뷰 유지
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> {
                val intent = Intent(mContext, LoginActivity::class.java)
                startActivity(intent)
                SharedPreferenceManager.clear(FragmentActivity.mContext)
                finish()
            }
            R.id.btn_editpicture -> registerPictures()
        }
    }

    //카메라or앨범 선택
    private fun registerPictures() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("사진 등록/수정")

        builder.setItems(R.array.LAN) { dialog, pos ->
            when (pos) {
                0 -> takePhoto()
                1 -> goAlbum()
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
                val photoUri: Uri = data!!.data
                cropImage(photoUri) //crop->set
            }
            PICK_FROM_CAMERA -> {
                setImage()
            }
            Crop.REQUEST_CROP -> setImage()
        }
    }

    //권한요청
    private fun requestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("grant", "권한 설정 완료")
            } else {
                Log.d("grant", "권한 설정 요청")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }
    }

    // 권한
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("", "onRequestPermissionsResult")
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("", "Permission: " + permissions[0] + "was " + grantResults[0])
        }
    }

    //껏다 켜도 이미지뷰 유지
    private fun initSetImage(){
        if (SharedPreferenceManager.getString(mContext, "PREFBTM") != "") {
            val array : ByteArray = Base64.decode(SharedPreferenceManager.getString(mContext, "PREFBTM"), Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(array, 0, array.size)
            iv_userpicture.setImageBitmap(bmp)
        }
    }

    //앨범에서 이미지 가져오기
    private fun goAlbum() {
        isCamera = false

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    //카메라에서 이미지 가져오기
    private fun takePhoto() {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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

    //이미지 crop/////////////////////////////////////////////////////////////////////////////////////////////////////
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = SharedPreferenceManager.getString(LoginActivity.mContext, "PREFID").toString().substring(0, 10) + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val image = File.createTempFile(imageFileName, ".jpg", storageDir)

        mCurrentPhotoPath = image.absolutePath
        return image
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
        Crop.of(photoUri, savingUri).asSquare().start(this) //->setImage
    }

    private fun setImage() {
        val file = File(mCurrentPhotoPath)

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
        if (bitmap != null) {
            val ei = ExifInterface(mCurrentPhotoPath)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

            //화면회전
            rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
                else -> bitmap
            }

            iv_userpicture.setImageBitmap(rotatedBitmap) //이미지뷰에 적용

            val stream = ByteArrayOutputStream() //bitmap->byteArray->base64로 SharedPreferenceManager에 저장
            rotatedBitmap!!.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            val saveThis : String = Base64.encodeToString(image, Base64.DEFAULT)
            SharedPreferenceManager.setString(mContext, "PREFBTM", saveThis)

            if(isCamera == true){ //카메라 선택시
                saveOnAlbum() //앨범에 저장
                goAlbum() //앨범으로 이동
            }else{ //앨범 선택시
                postId(SharedPreferenceManager.getString(LoginActivity.mContext, "PREFID").toString()) //node.js서버에 파일명으로 사용될 학번보내기
                multipartImageUpload(rotatedBitmap!!)  //node.js서버에 업로드
            }
        }
        tempFile = null
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //사진회전
    private fun rotateImage(source: Bitmap, angle: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    //앨범에저장
    private fun saveOnAlbum(){
        try{
            val array : ByteArray = Base64.decode(SharedPreferenceManager.getString(mContext, "PREFBTM"), Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(array, 0, array.size)
            val imageSaveUri = MediaStore.Images.Media.insertImage(contentResolver, bmp, "사진저장", "저장완료")
            val uri : Uri = Uri.parse(imageSaveUri)
//            sendBroadcast( Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            sendBroadcast( Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory()))) //
        }catch(e: IOException){
        }
    }

    //node.js서버에 파일명으로 사용될 학번보내기
    private fun postId(studentId: String) {
        disposable!!.add(iMyService!!.postId(studentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { response ->

                }
        )
    }

    //node.js서버에 업로드
    private fun multipartImageUpload(originalBm: Bitmap?) {
        try {
            val filesDir = applicationContext.filesDir
            val file = File(filesDir, "image" + ".png")

            val bos = ByteArrayOutputStream()
            originalBm!!.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()

            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

            val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
            val body = MultipartBody.Part.createFormData("upload", file.name, reqFile)
            val name = RequestBody.create(MediaType.parse("text/plain"), "upload")

            val req = iMyService!!.postImage(body, name)
            req.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                    Toast.makeText(applicationContext, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, "Uploaded failed", Toast.LENGTH_SHORT).show()
                    t.printStackTrace()
                }
            })

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        disposable?.clear()
    }
}
