package id.ac.polbeng.mizahilmiya.onlineservice.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.SessionHandler
import id.ac.polbeng.mizahilmiya.onlineservice.models.DefaultResponse
import id.ac.polbeng.mizahilmiya.onlineservice.models.User
import id.ac.polbeng.mizahilmiya.onlineservice.services.ServiceBuilder
import id.ac.polbeng.mizahilmiya.onlineservice.services.UserService
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.ActivityEditProfileBinding
import retrofit2.Call
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionHandler(applicationContext)
        val user = session.getUser()

        if(user != null) {
            binding.etNama.setText(user.nama)
            binding.etTanggalLahir.setText(user.tanggalLahir)
            if(user.jenisKelamin.equals("Pria"))
                binding.spJenisKelamin.setSelection(1)
            else
                binding.spJenisKelamin.setSelection(2)
            binding.etNomorHP.setText(user.nomorHP)
            binding.etAlamat.setText(user.alamat)
            binding.etEmail.setText(user.email)
        }

        binding.btnSubmit.setOnClickListener {
            val id = user?.id!!
            val nama = binding.etNama.text.toString()
            val tanggalLahir = binding.etTanggalLahir.text.toString()
            val jenisKelamin =
                binding.spJenisKelamin.selectedItem.toString()
            val nomorHP = binding.etNomorHP.text.toString()
            val alamat = binding.etAlamat.text.toString()
            val email = binding.etEmail.text.toString()
            var password = binding.etPassword.text.toString()
            val konfirmasiPassword =
                binding.etKonfirmasiPassword.text.toString()

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

            if (TextUtils.isEmpty(password) &&
                TextUtils.isEmpty(konfirmasiPassword)) {
                password = ""
            } else {
                if (TextUtils.isEmpty(password)) {
                    binding.etPassword.error = "Password tidak boleh kosong!"
                    binding.etPassword.requestFocus()
                    return@setOnClickListener
                }
                if (TextUtils.isEmpty(konfirmasiPassword)) {
                    binding.etKonfirmasiPassword.setError("Konfirmasi password tidak boleh kosong!")
                            binding.etKonfirmasiPassword.requestFocus()
                        return@setOnClickListener
                }
                if (!password.equals(konfirmasiPassword)) {
                    binding.etKonfirmasiPassword.setError("Password dan konfirmasi password tidak sama!")
                            binding.etKonfirmasiPassword.requestFocus()
                        return@setOnClickListener
                }
            }

            val updatedUser = User(id, nama, tanggalLahir, jenisKelamin,
                nomorHP, alamat, email, password);
            val userService: UserService =
                ServiceBuilder.buildService(UserService::class.java)
            val requestCall: Call<DefaultResponse> =
                userService.updateUser(updatedUser)

            showLoading(true)

            requestCall.enqueue(object :
                retrofit2.Callback<DefaultResponse>{
                override fun onFailure(call: Call<DefaultResponse>, t:
                Throwable) {
                    showLoading(false)
                    Toast.makeText(this@EditProfileActivity, "Error terjadi ketika sedang mengubah data user: " + t.toString(),
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
                            session.updateUser(updatedUser)
                            Toast.makeText(this@EditProfileActivity,
                                defaultResponse.message, Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }else{
                        Toast.makeText(this@EditProfileActivity, "Gagal mengubah user: "
                                + response.body()?.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else
            View.GONE
    }
}