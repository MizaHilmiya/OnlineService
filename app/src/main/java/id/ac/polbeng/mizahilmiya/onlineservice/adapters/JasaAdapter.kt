package id.ac.polbeng.mizahilmiya.onlineservice.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.ac.polbeng.mizahilmiya.onlineservice.R
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.JasaListBinding
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.Config
import id.ac.polbeng.mizahilmiya.onlineservice.models.Jasa

class JasaAdapter: RecyclerView.Adapter<JasaAdapter.ServiceHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    var jasaList: ArrayList<Jasa> = arrayListOf()

    fun setData(items: ArrayList<Jasa>) {
        jasaList.clear()
        jasaList.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Jasa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ServiceHolder {
        val binding =
            JasaListBinding.inflate(LayoutInflater.from(parent.context), parent,
                false)
        return ServiceHolder(binding)
    }

    override fun getItemCount(): Int {
        return jasaList.size
    }

    override fun onBindViewHolder(holder: ServiceHolder, position: Int) {
        val service = jasaList[position]
        holder.binding.tvNamaJasa.text = service.namaJasa
        holder.binding.tvDeskripsiSingkat.text = service.deskripsiSingkat
        holder.binding.tvRating.text = service.rating.toString()
        // tampilkan gambar dengan library glide pada view imgList
        Glide.with(holder.binding.imageView.context)
            .load(Config.IMAGE_URL + service.gambar)
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(holder.binding.imageView)

        holder.itemView.setOnClickListener {

            onItemClickCallback.onItemClicked(jasaList[holder.adapterPosition])
        }
    }

    inner class ServiceHolder(val binding: JasaListBinding) :
        RecyclerView.ViewHolder(binding.root)
}