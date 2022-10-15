package pt.isec.amov.a2018020733.aula3ex1a

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import pt.isec.amov.a2018020733.aula3ex1a.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    lateinit var binding : ActivityMainBinding

    var num1 : Double = 0.0
    var operator : String = "+"
    var num2 : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.ac.setOnClickListener{
            binding.display.text = "0"
        }

        binding.maisMenos.setOnClickListener{

            when(binding.display.text.toString()[0]){
                '0' -> {}
                '-' -> binding.display.text = binding.display.text.toString().drop(1)
                else -> binding.display.text = "-" + binding.display.text.toString()
            }
        }

        binding.zero.setOnClickListener(procZero)
        binding.um.setOnClickListener(procNumeros)
        binding.dois.setOnClickListener(procNumeros)
        binding.tres.setOnClickListener(procNumeros)
        binding.quatro.setOnClickListener(procNumeros)
        binding.cinco.setOnClickListener(procNumeros)
        binding.seis.setOnClickListener(procNumeros)
        binding.sete.setOnClickListener(procNumeros)
        binding.oito.setOnClickListener(procNumeros)
        binding.nove.setOnClickListener(procNumeros)

        binding.add.setOnClickListener(procOperation)
        binding.sub.setOnClickListener(procOperation)
        binding.mul.setOnClickListener(procOperation)
        binding.div.setOnClickListener(procOperation)

        binding.equals.setOnClickListener{

            num2 = binding.display.text.toString().toDouble()

            var res : Double = 0.0

            when(operator){
                "+" -> res = num1 + num2
                "-" -> res = num1 - num2
                "*" -> res = num1 * num2
                "/" -> res = num1 / num2
            }

            binding.display.text = res.toString()

            num1 = 0.0
            operator = "+"
            num2 = 0.0
        }

        TODO(% .)
    }

    val procNumeros = object : View.OnClickListener {
        override fun onClick(p0: View?) {
            when(binding.display.text.toString()){
                "0" -> binding.display.text = (p0 as Button).text
                else -> binding.display.text = binding.display.text.toString() + (p0 as Button).text
            }
        }
    }

    val procZero = object : View.OnClickListener {
        override fun onClick(p0: View?) {
            if(binding.display.text.toString() != "0")
                binding.display.text = binding.display.text.toString() + (p0 as Button).text
        }
    }

    val procOperation = object : View.OnClickListener {
        override fun onClick(p0: View?) {
            if(binding.display.text.toString() != "0") {
                num1 = binding.display.text.toString().toDouble()
                operator = (p0 as Button).text.toString()
                binding.display.text = "0"
            }
        }
    }
}