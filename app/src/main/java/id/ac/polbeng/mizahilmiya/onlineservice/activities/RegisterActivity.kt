package id.ac.polbeng.mizahilmiya.onlineservice.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.ActivityRegisterBinding
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import id.ac.polbeng.mizahilmiya.onlineservice.models.DefaultResponse
import id.ac.polbeng.mizahilmiya.onlineservice.models.User
import id.ac.polbeng.mizahilmiya.onlineservice.services.ServiceBuilder
import id.ac.polbeng.mizahilmiya.onlineservice.services.UserService
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val nama = binding.etNama.text.toString()
            val tanggalLahir = binding.etTanggalLahir.text.toString()
            val jenisKelamin = binding.spJenisKelamin.selectedItem.toString()
            val nomorHP = binding.etNomorHP.text.toString()
            val alamat = binding.etAlamat.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val konfirmasiPassword = binding.etKonfirmasiPassword.text.toString()

            if(TextUtils.isEmpty(nama)){
                binding.etNama.error = "Nama tidak boleh kosong!"
                binding.etNama.requestFocus()
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(tanggalLahir)){
                binding.etTanggalLahir.error = "Tanggal lahir tidak boleh kosong!"
                binding.etTanggalLahir.requestFocus()
                return@setOnClickListener
            }

            if(jenisKelamin.equals("Jenis Kelamin")){
                Toast.makeText(applicationContext, "Silahkan pilih jenis kelamin!", Toast.LENGTH_SHORT).show()
                        binding.spJenisKelamin.requestFocus()
                    return@setOnClickListener
            }

            if(TextUtils.isEmpty(nomorHP)){
                binding.etNomorHP.error = "Nomor HP tidak boleh kosong!"
                binding.etNomorHP.requestFocus()
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(alamat)){
                binding.etAlamat.error = "Alamat tidak boleh kosong!"
                binding.etAlamat.requestFocus()
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(email)){
                binding.etEmail.error = "Email tidak boleh kosong!"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password)){
                binding.etPassword.error = "Password tidak boleh kosong!"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(konfirmasiPassword)){
                binding.etKonfirmasiPassword.error = "Konfirmasi password tidak boleh kosong!"
                binding.etKonfirmasiPassword.requestFocus()
                return@setOnClickListener
            }

            if(!password.equals(konfirmasiPassword)){
                binding.etKonfirmasiPassword.error = "Password dan konfirmasi password tidak sama!"
                binding.etKonfirmasiPassword.requestFocus()
                return@setOnClickListener
            }

            val newUser = User(0, nama, tanggalLahir, jenisKelamin,
                nomorHP, alamat, email, password)

            val userService: UserService =
                ServiceBuilder.buildService(UserService::class.java)
            val requestCall: Call<DefaultResponse> =
                userService.registerUser(newUser)

            showLoading(true)

            requestCall.enqueue(object :
                retrofit2.Callback<DefaultResponse>{
                override fun onFailure(call: Call<DefaultResponse>, t:
                Throwable) {
                    showLoading(false)
                    Toast.makeText(this@RegisterActivity, "Error terjadi ketika sedang mendaftarkan user:$t",
                    Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    showLoading(false)
                    if(!response.body()?.error!!) {
                        val defaultResponse: DefaultResponse =
                            response.body()!!
                        defaultResponse.let {
                            Toast.makeText(this@RegisterActivity,
                                defaultResponse.message, Toast.LENGTH_LONG).show()
                            val intent = Intent(applicationContext, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                        }
                    }else{
                        Toast.makeText(this@RegisterActivity, "Gagal mendaftarkan user: " + response.body()?.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else
            View.GONE
    }
}