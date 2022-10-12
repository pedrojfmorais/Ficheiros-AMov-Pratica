package pt.isec.amov.a2018020733.aula2

import android.app.Activity
import android.os.Bundle
import android.util.Log

const val TAG = "AMOVP3"

class MyActivity : Activity() {

    var conta = 0

    val app : MyApp by lazy { application as MyApp }

    fun Logi(tag : String, msg : String){
        Log.i(tag, "$msg ${app.my_value} ${MyObject.my_value}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.my_activity)

        conta++

        Logi(TAG, "onCreate: $conta")
    }

    override fun onRestart() {
        super.onRestart()
        Logi(TAG, "onRestart: ")
    }

    override fun onStart() {
        super.onStart()
        Logi(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        conta++
        Logi(TAG, "onResume: $conta")
    }

    override fun onPause() {
        super.onPause()
        Logi(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Logi(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logi(TAG, "onDestroy: ")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        conta++

        Logi(TAG, "onSaveInstanceState: $conta")

        outState.putInt("conta", conta)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        conta = savedInstanceState.getInt("conta")

        Logi(TAG, "onRestoreInstanceState: $conta")
    }
}