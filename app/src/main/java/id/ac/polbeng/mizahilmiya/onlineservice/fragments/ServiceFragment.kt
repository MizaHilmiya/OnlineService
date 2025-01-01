package id.ac.polbeng.mizahilmiya.onlineservice.fragments

import android.content.Intent
import id.ac.polbeng.mizahilmiya.onlineservice.activities.AddJasaActivity
import id.ac.polbeng.mizahilmiya.onlineservice.activities.EditJasaActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.ac.polbeng.mizahilmiya.onlineservice.adapters.JasaAdapter
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.FragmentServiceBinding
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.SessionHandler
import id.ac.polbeng.mizahilmiya.onlineservice.models.Jasa
import id.ac.polbeng.mizahilmiya.onlineservice.models.JasaResponse
import id.ac.polbeng.mizahilmiya.onlineservice.services.JasaService
import id.ac.polbeng.mizahilmiya.onlineservice.services.ServiceBuilder
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.Config
import retrofit2.Call
import retrofit2.Response

class ServiceFragment : Fragment() {
    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var session : SessionHandler
    private lateinit var jasaAdapter: JasaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        session = SessionHandler(requireContext())
        jasaAdapter = JasaAdapter()
        _binding = FragmentServiceBinding.inflate(inflater, container,
            false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.rvData?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = jasaAdapter
        }
        _binding?.fabAddJasa?.setOnClickListener {
            val intent = Intent(context, AddJasaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        loadService()
    }

    private fun loadService() {
        val jasaService: JasaService =
            ServiceBuilder.buildService(JasaService::class.java)
        val requestCall: Call<JasaResponse> =
            jasaService.getJasaUser(session.getUserId())

        showLoading(true)

        requestCall.enqueue(object : retrofit2.Callback<JasaResponse>{
            override fun onFailure(call: Call<JasaResponse>, t: Throwable)
            {
                showLoading(false)
                Toast.makeText(context, "Error terjadi ketika sedang mengambil data jasa: " + t.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<JasaResponse>,
                response: Response<JasaResponse>
            ) {
                showLoading(false)
                if(!response.body()?.error!!) {
                    val servicesResponse: JasaResponse? = response.body()
                    servicesResponse?.let {
                        val daftarJasa: ArrayList<Jasa> =
                            servicesResponse.data
                        jasaAdapter.setData(daftarJasa)
                        jasaAdapter.setOnItemClickCallback(object :
                            JasaAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: Jasa) {
                                val intent = Intent(context,
                                    EditJasaActivity::class.java)
                                intent.putExtra(Config.EXTRA_JASA, data)
                                startActivity(intent)
                            }
                        })
                    }
                }else{
                    Toast.makeText(context, "Gagal menampilkan data jasa:" + response.body()?.message, Toast.LENGTH_LONG).show()
                }
            }
        });
    }

    private fun showLoading(isLoading: Boolean) {
        _binding?.progressBar?.visibility = if (isLoading) View.VISIBLE
        else View.GONE
    }
}