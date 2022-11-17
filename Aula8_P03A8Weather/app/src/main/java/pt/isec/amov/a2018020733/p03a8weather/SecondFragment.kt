package pt.isec.amov.a2018020733.p03a8weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import pt.isec.amov.a2018020733.p03a8weather.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private val viewModel : MyViewModel by activityViewModels()

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.webContent.observe(requireActivity()) {
            it?.run { showInfo(it) }
        }
    }

    // implementation 'com.github.bumptech.glide:glide:4.14.2'
    private fun showInfo(content : String) {
        val json = JSONObject(content)
        //binding.tvContent.text = json.toString(4)

        val sb = StringBuilder("Weather information:\n")
        json.getJSONObject("location").let {
            sb.append("City: ${it["name"]},${it["country"]}\n")
        }

        json.getJSONObject("current").let {
            sb.append("Current temp: ${it["temp_c"]}\n")
            it.getJSONObject("condition").let {
                val icon = it["icon"]
                Glide.with(this)
                    .load("https:$icon")
                    .into(binding.imageView)
            }
        }

        json.getJSONObject("forecast").let {
            sb.append("\nForecast:\n")
            val forecastday=it["forecastday"] as JSONArray
            repeat(forecastday.length()) {
                val dayInfo = forecastday[it] as JSONObject
                val dayInfo2 = dayInfo["day"] as JSONObject
                sb.append("  - ${dayInfo["date"]}: ${dayInfo2["mintemp_c"]} <-t-> ${dayInfo2["maxtemp_c"]}\n")
            }
        }
        sb.append("\n\nFull info:\n"+json.toString(4))
        binding.tvContent.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.webContent.removeObservers(requireActivity())
        _binding = null
    }
}