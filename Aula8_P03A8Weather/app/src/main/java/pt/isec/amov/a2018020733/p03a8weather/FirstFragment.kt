package pt.isec.amov.a2018020733.p03a8weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import pt.isec.amov.a2018020733.p03a8weather.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    val viewModel : MyViewModel2 by viewModels()

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.location.observe(requireActivity()) {
            binding.tvCity.text = it ?: "???"
        }

        viewModel.currentTemp.observe(requireActivity()) {
            binding.tvTemp.text = "$it"
        }

        viewModel.icon.observe(requireActivity()) {
            Glide.with(this)
                .load(it)
                .into(binding.iconWeather)
        }

        binding.btnRefresh.setOnClickListener {
            viewModel.getContent("Coimbra")
        }
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.location.removeObservers(requireActivity())
        viewModel.currentTemp.removeObservers(requireActivity())
        viewModel.icon.removeObservers(requireActivity())
        _binding = null
    }
}