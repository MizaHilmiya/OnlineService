package id.ac.polbeng.mizahilmiya.onlineservice.activities

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import id.ac.polbeng.mizahilmiya.onlineservice.R
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.ActivityEditJasaBinding
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.Config
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.Config.Companion.EXTRA_JASA
import id.ac.polbeng.mizahilmiya.onlineservice.models.DefaultResponse
import id.ac.polbeng.mizahilmiya.onlineservice.models.Jasa
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

class EditJasaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditJasaBinding
    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var imageFile: MultipartBody.Part
    private var receiveJasa: Jasa? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditJasaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        receiveJasa = intent.getSerializableExtra(EXTRA_JASA) as? Jasa
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        receiveJasa?.let { jasa ->
            with(binding) {
                etNamaJasa.setText(jasa.namaJasa)
                etDeskripsiSingkat.setText(jasa.deskripsiSingkat)
                etUraianDeskripsi.setText(jasa.uraianDeskripsi)
                tvRating.text = jasa.rating.toString()
                tvImage.text = jasa.gambar

                Glide.with(this@EditJasaActivity)
                    .load(Config.IMAGE_URL + jasa.gambar)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(imgJasa)
            }
        }
    }


    private fun setupListeners() {
        with(binding) {
            btnCari.setOnClickListener { pickImage() }
            btnHapusJasa.setOnClickListener { showDeleteConfirmation() }
            btnEditJasa.setOnClickListener { validateAndUpdateJasa() }
        }
    }

    private fun pickImage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ImagePicker.with(this)
                .compress(512)
                .maxResultSize(540, 540)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This application needs your permission to access photo gallery.",
                pickImage,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this).apply {
            setTitle("Hapus Jasa")
            setMessage("Apakah anda yakin menghapus jasa?\n${receiveJasa?.namaJasa}")
            setIcon(R.drawable.ic_baseline_delete_forever_black_24)
            setPositiveButton("Ya") { dialog, _ ->
                deleteJasa()
                dialog.dismiss()
            }
            setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun deleteJasa() {
        val jasaService: JasaService = ServiceBuilder.buildService(JasaService::class.java)
        receiveJasa?.idJasa?.let { id ->
            jasaService.deleteJasa(id).enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(
                        this@EditJasaActivity,
                        "Error terjadi ketika sedang menghapus jasa: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    response.body()?.let { resp ->
                        if (!resp.error) {
                            Toast.makeText(
                                this@EditJasaActivity,
                                resp.message,
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@EditJasaActivity,
                                "Gagal menghapus jasa: ${resp.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })
        }
    }

    private fun validateAndUpdateJasa() {
        with(binding) {
            when {
                TextUtils.isEmpty(etNamaJasa.text) -> {
                    etNamaJasa.error = "Nama jasa tidak boleh kosong!"
                    etNamaJasa.requestFocus()
                    return
                }
                TextUtils.isEmpty(etDeskripsiSingkat.text) -> {
                    etDeskripsiSingkat.error = "Deskripsi singkat tidak boleh kosong!"
                    etDeskripsiSingkat.requestFocus()
                    return
                }
                TextUtils.isEmpty(etUraianDeskripsi.text) -> {
                    etUraianDeskripsi.error = "Uraian deskripsi tidak boleh kosong!"
                    etUraianDeskripsi.requestFocus()
                    return
                }
            }

            val isUpdateImage = tvImage.text.toString() != receiveJasa?.gambar
            if (isUpdateImage) {
                updateJasaWithImage()
            } else {
                updateJasaWithoutImage()
            }
        }
    }

    private fun updateJasaWithImage() {
        with(binding) {
            val reqIdJasa = receiveJasa?.idJasa.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val reqNamaJasa = etNamaJasa.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val reqDeskripsiSingkat = etDeskripsiSingkat.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val reqUraianDeskripsi = etUraianDeskripsi.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val reqGambar = tvImage.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

            val jasaService: JasaService = ServiceBuilder.buildService(JasaService::class.java)
            showLoading(true)

            jasaService.editJasaReplaceImage(
                imageFile, reqIdJasa, reqNamaJasa,
                reqDeskripsiSingkat, reqUraianDeskripsi, reqGambar
            ).enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(
                        this@EditJasaActivity,
                        "Error terjadi ketika sedang mengedit data jasa: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    showLoading(false)
                    response.body()?.let { resp ->
                        if (!resp.error) {
                            finish()
                            Toast.makeText(
                                this@EditJasaActivity,
                                resp.message,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@EditJasaActivity,
                                "Gagal mengedit data jasa: ${resp.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })
        }
    }

    private fun updateJasaWithoutImage() {
        with(binding) {
            val jasaService: JasaService = ServiceBuilder.buildService(JasaService::class.java)
            showLoading(true)

            jasaService.editJasa(
                receiveJasa?.idJasa ?: 0,
                etNamaJasa.text.toString(),
                etDeskripsiSingkat.text.toString(),
                etUraianDeskripsi.text.toString()
            ).enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(
                        this@EditJasaActivity,
                        "Error terjadi ketika sedang mengubah data jasa: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    showLoading(false)
                    response.body()?.let { resp ->
                        if (!resp.error) {
                            finish()
                            Toast.makeText(
                                this@EditJasaActivity,
                                resp.message,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@EditJasaActivity,
                                "Gagal mengubah data jasa: ${resp.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })
        }
    }

    private val startForProfileImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultCode = result.resultCode
        val data = result.data

        when {
            resultCode == RESULT_OK && data != null -> {
                imageUri = data.data
                imageUri?.let { uri ->
                    Glide.with(this)
                        .load(uri)
                        .into(binding.imgJasa)
                    binding.tvImage.text = uri.lastPathSegment

                    val file = File(uri.path ?: "")
                    val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    imageFile = MultipartBody.Part.createFormData("file", file.name, requestBody)
                }
            }
            resultCode == ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
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