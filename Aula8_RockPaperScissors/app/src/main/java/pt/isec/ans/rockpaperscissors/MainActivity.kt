package pt.isec.ans.rockpaperscissors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //to do: should verify if the network is available
        findViewById<Button>(R.id.btnServer).setOnClickListener {
            startActivity(GameActivity.getServerModeIntent(this))
        }
        findViewById<Button>(R.id.btnClient).setOnClickListener {
            startActivity(GameActivity.getClientModeIntent(this))
        }
    }
}