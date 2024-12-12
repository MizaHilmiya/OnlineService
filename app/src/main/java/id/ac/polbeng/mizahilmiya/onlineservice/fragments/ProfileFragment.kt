package id.ac.polbeng.mizahilmiya.onlineservice.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.ac.polbeng.mizahilmiya.onlineservice.R
import id.ac.polbeng.mizahilmiya.onlineservice.activities.EditProfileActivity
import id.ac.polbeng.mizahilmiya.onlineservice.activities.LoginActivity
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.FragmentProfileBinding
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.Config
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.SessionHandler
import id.ac.polbeng.mizahilmiya.onlineservice.models.DefaultResponse
import id.ac.polbeng.mizahilmiya.onlineservice.models.User
import id.ac.polbeng.mizahilmiya.onlineservice.services.ServiceBuilder
import id.ac.polbeng.mizahilmiya.onlineservice.services.UserService
import id.ac.polbeng.mizahilmiya.onlineservice.viewmodel.ProfileViewModel
import retrofit2.Call
import retrofit2.Response


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container,
            false)
        val root: View = binding.root

        profileViewModel.text.observe(viewLifecycleOwner) {
            //binding.textProfile.text = it
        }

        val session = SessionHandler(requireContext())
        val user: User? = session.getUser()
        val titikDua = ": "

        if(user != null) {
            val url = Config.PROFILE_IMAGE_URL + user.gambar
            Glide.with(requireContext())
                .load(url)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user))
                .into(binding.imgLogo)
            binding.tvNama.text = titikDua + user.nama
            binding.tvTanggalLahir.text = titikDua + user.tanggalLahir
            binding.tvJenisKelamin.text = titikDua + user.jenisKelamin
            binding.tvNomorHP.text = titikDua + user.nomorHP
            binding.tvAlamat.text = titikDua + user.alamat
            binding.tvEmail.text = titikDua + user.email
            binding.tvWaktuSesi.text = titikDua + session.getExpiredTime()
        }
        binding.btnEditProfil.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
        }
        binding.btnHapusUser.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Hapus Akun")
            builder.setMessage("Apakah anda yakin menghapus akun? Anda tidak akan bisa lagi login ke akun.")

                    builder.setIcon(R.drawable.ic_baseline_delete_forever_black_24)
                    builder.setPositiveButton("Ya"){ dialog, _ ->
                val userService: UserService =
                    ServiceBuilder.buildService(UserService::class.java)
                val requestCall: Call<DefaultResponse> =
                    userService.deleteUser(user?.id!!)
                showLoading(true)
                requestCall.enqueue(object:
                    retrofit2.Callback<DefaultResponse>{
                    override fun onFailure(call: Call<DefaultResponse>,
                                           t: Throwable) {
                        showLoading(false)
                        Toast.makeText(context, "Error terjadi ketika sedang menghapus user: " + t.toString(), Toast.LENGTH_LONG).show()
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
                                session.removeUser()
                                Toast.makeText(context,
                                    defaultResponse.message, Toast.LENGTH_LONG).show()
                                val intent = Intent(context,
                                    LoginActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        }else{
                            Toast.makeText(context, "Gagal menghapus user: " + response.body()?.message, Toast.LENGTH_LONG).show()
                        }
                    }
                })
                dialog.dismiss()
            }

                    builder.setNegativeButton("Tidak"){ dialog, _ ->
                dialog.dismiss()
            }
                val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE
        else View.GONE
    }
}