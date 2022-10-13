package pt.isec.amov.a2018020733.aula3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import pt.isec.amov.a2018020733.aula3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTexto.text = "Arq. Móveis"
        xpto()

        binding.btnOpcao1.setOnClickListener(this)
        binding.btnOpcao2.setOnClickListener(ProcBotao2(binding.tvTexto))
        binding.btnOpcao3.setOnClickListener(procBotao3)
        binding.btnOpcao4.setOnClickListener{
            binding.tvTexto.text = "Opção 4"
        }
    }

    val procBotao3 = object : View.OnClickListener {
        override fun onClick(p0: View?) {
            binding.tvTexto.text = "opção 3"
        }
    }

    val procBotao3b = View.OnClickListener { binding.tvTexto.text = "opção 3" }

    class ProcBotao2(val tv : TextView) : View.OnClickListener{
        override fun onClick(p0: View?) {
            tv.text = "Opcao 2"
        }
    }

    fun xpto() {
        binding.tvTexto.text = "DEIS-ISEC"
    }

    override fun onClick(p0: View?) {
        binding.tvTexto.text = (p0 as Button).text
    }

    fun onOpcao5(view: View) {
        binding.tvTexto.text = "55555"
    }
}