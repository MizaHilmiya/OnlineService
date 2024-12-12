package id.ac.polbeng.mizahilmiya.onlineservice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.ac.polbeng.mizahilmiya.onlineservice.viewmodel.ServiceViewModel
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.FragmentServiceBinding

class ServiceFragment : Fragment() {
    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val serviceViewModel = ViewModelProvider(this).get(ServiceViewModel::class.java)

        _binding = FragmentServiceBinding.inflate(inflater, container,
            false)
        val root: View = binding.root

        serviceViewModel.text.observe(viewLifecycleOwner) {
            binding.textService.text = it
        }

        binding.textService.text = "Ini Service Fragment"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}