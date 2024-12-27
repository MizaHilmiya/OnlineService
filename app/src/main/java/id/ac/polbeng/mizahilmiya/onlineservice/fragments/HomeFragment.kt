package id.ac.polbeng.mizahilmiya.onlineservice.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.ac.polbeng.mizahilmiya.onlineservice.activities.DetailJasaActivity
import id.ac.polbeng.mizahilmiya.onlineservice.adapters.JasaAdapter
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.FragmentHomeBinding
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.Config
import id.ac.polbeng.mizahilmiya.onlineservice.models.Jasa
import id.ac.polbeng.mizahilmiya.onlineservice.models.JasaResponse
import id.ac.polbeng.mizahilmiya.onlineservice.services.JasaService
import id.ac.polbeng.mizahilmiya.onlineservice.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var jasaAdapter: JasaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        jasaAdapter = JasaAdapter()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.rvData?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = jasaAdapter
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
        val requestCall: Call<JasaResponse> = jasaService.getJasa()

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
                    val jasaResponse: JasaResponse? = response.body()
                    jasaResponse?.let {
                        val daftarJasa: ArrayList<Jasa> = jasaResponse.data
                        jasaAdapter.setData(daftarJasa)
                        jasaAdapter.setOnItemClickCallback(object :
                            JasaAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: Jasa) {
                                Toast.makeText(context, "Service clicked {data.namaJasa}", Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, DetailJasaActivity::class.java)
                                intent.putExtra(Config.EXTRA_JASA, data)
                                startActivity(intent)
                            }
                        })
                    }
                }else{
                    Toast.makeText(context, "Gagal menampilkan data jasa:"
                            + response.body()?.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        _binding?.progressBar?.visibility = if (isLoading) View.VISIBLE
        else View.GONE
    }
}