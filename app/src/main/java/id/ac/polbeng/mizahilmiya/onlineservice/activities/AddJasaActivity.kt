package id.ac.polbeng.mizahilmiya.onlineservice.activities


import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.ActivityAddJasaBinding
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.SessionHandler
import id.ac.polbeng.mizahilmiya.onlineservice.models.DefaultResponse
import id.ac.polbeng.mizahilmiya.onlineservice.models.User
import id.ac.polbeng.mizahilmiya.onlineservice.services.JasaService
import id.ac.polbeng.mizahilmiya.onlineservice.services.ServiceBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddJasaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddJasaBinding
    private val pickImage = 100
    // global variable untuk imageFile.
    private lateinit var imageFile: MultipartBody.Part

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJasaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionHandler(applicationContext)
        val user: User? = session.getUser()

        binding.btnCari.setOnClickListener {
            // check permission untuk android M dan ke atas.
            // saat permission disetujui oleh user maka jalan script untuk intent ke pick image.

            if(EasyPermissions.hasPermissions(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                ImagePicker.with(this)
                    .compress(1024)  //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }else{
                // tampilkan permission request saat belum mendapat permission dari user
                EasyPermissions.requestPermissions(this,"This application need your permission to access photo gallery.", pickImage,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        binding.btnAddJasa.setOnClickListener {
            val namaJasa = binding.etNamaJasa.text.toString()
            val deskripsiSingkat = binding.etDeskripsiSingkat.text.toString()
            val uraianDeskripsi = binding.etUraianDeskripsi.text.toString()
            val rating = binding.tvRating.text.toString()
            val gambar = binding.tvImage.text.toString()
            if(TextUtils.isEmpty(namaJasa)){
                binding.etNamaJasa.setError("Nama jasa tidak boleh kosong!")
                        binding.etNamaJasa.requestFocus()
                    return@setOnClickListener
            }
            if(TextUtils.isEmpty(deskripsiSingkat)){
                binding.etDeskripsiSingkat.setError("Deskripsi singkat tidak boleh kosong!")
                        binding.etDeskripsiSingkat.requestFocus()
                    return@setOnClickListener
            }
            if(TextUtils.isEmpty(uraianDeskripsi)){
                binding.etUraianDeskripsi.setError("Uraian deskripsi tidak boleh kosong!")
                        binding.etUraianDeskripsi.requestFocus()
                    return@setOnClickListener
            }
            if(gambar.equals("Pilih sebuah gambar!")){
                Toast.makeText(applicationContext, "Silahkan pilih satu gambar, klik button Cari disamping!", Toast.LENGTH_SHORT).show()
                        binding.btnCari.requestFocus()
                    return@setOnClickListener
            }

            // add another part within the multipart request
            val reqIdUser = user?.id.toString().toRequestBody("multipart/form data".toMediaTypeOrNull())
            val reqNamaJasa = namaJasa.toRequestBody("multipart/form data".toMediaTypeOrNull())
            val reqDeskripsiSingkat = deskripsiSingkat.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val reqUraianDeskripsi = uraianDeskripsi.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val reqRating = rating.toRequestBody("multipart/form data".toMediaTypeOrNull())
            val reqGambar = gambar.toRequestBody("multipart/form data".toMediaTypeOrNull())

            val jasaService = ServiceBuilder.buildService(JasaService::class.java)
            val requestCall: Call<DefaultResponse> = jasaService.addJasa(
                imageFile, reqIdUser, reqNamaJasa, reqDeskripsiSingkat,
                reqUraianDeskripsi, reqRating, reqGambar
            )
            showLoading(true)

            requestCall.enqueue(object :
                Callback<DefaultResponse>{
                override fun onFailure(call: Call<DefaultResponse>, t:
                Throwable) {
                    showLoading(false)
                    Toast.makeText(this@AddJasaActivity, "Error terjadi ketika sedang menambahkan jasa: " + t.toString(),
                    Toast.LENGTH_LONG).show()
                }
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    showLoading(false)
                    if(!response.body()?.error!!) {
                        finish()
                        Toast.makeText(this@AddJasaActivity,
                            response.body()?.message, Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this@AddJasaActivity, "Gagal menambahkan jasa: " + response.body()?.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) {
            val resultCode = it.resultCode
            val data = it.data
            if(resultCode == RESULT_OK && data != null){
                val imageUri: Uri? = data.data
                // mempilkan image yang akan diupload dengan glide ke imgUpload.
                Glide.with(this)
                    .load(imageUri)
                    .into(binding.imgJasa)
                binding.tvImage.text = imageUri?.lastPathSegment

                // membuat variable file yang menampung image.
                val file = File(imageUri?.path)

                // membuat request body yang berisi file dari picked image.
                val requestBody = file.asRequestBody("multipart/form data".toMediaTypeOrNull())

                        // mengoper value dari requestbody sekaligus membuat form data untuk upload. dan juga mengambil nama dari picked image
                        imageFile = MultipartBody.Part.createFormData("file", file.name, requestBody)
            } else if (it.resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data),
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}