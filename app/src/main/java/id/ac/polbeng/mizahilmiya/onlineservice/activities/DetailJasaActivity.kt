package id.ac.polbeng.mizahilmiya.onlineservice.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.ActivityDetailJasaBinding
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.Config
import id.ac.polbeng.mizahilmiya.onlineservice.models.Jasa

class DetailJasaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailJasaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJasaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receiveJasa = intent.getSerializableExtra(Config.EXTRA_JASA)
                as? Jasa
        if(receiveJasa != null){
            binding.tvNamaJasa.setText(receiveJasa.namaJasa)
            binding.tvPenyedia.setText(receiveJasa.namaPenyedia)
            binding.tvKontak.setText(receiveJasa.nomorHP)
            binding.tvDeskripsiSingkat.setText(receiveJasa.deskripsiSingkat)
            binding.tvUraianDeskripsi.setText(receiveJasa.uraianDeskripsi)
            binding.tvRating.text = receiveJasa.rating.toString()
            Glide.with(this)
                .load(Config.IMAGE_URL + receiveJasa.gambar)
                .into(binding.imgJasa)
        }
    }
}