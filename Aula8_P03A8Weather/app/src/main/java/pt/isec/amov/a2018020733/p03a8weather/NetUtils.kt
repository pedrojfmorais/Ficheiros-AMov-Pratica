package pt.isec.amov.a2018020733.p03a8weather

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class NetUtils {
    companion object {
        private const val LIMIT = 4096

        fun getDataAsync(strURL: String, result: MutableLiveData<String?>) {
//            Thread(Runnable{ getData(strURL) }).start()
            thread {
                val strContent = getData(strURL)
                result.postValue(strContent)
            }
        }

        private fun getData
                    (strUrl: String?): String? {
            val resp = StringBuilder()
            var conn: HttpURLConnection? = null
            try {
                val url = URL(strUrl
                )
                conn = url.openConnection() as HttpURLConnection
                conn.readTimeout = 10000
                conn.connectTimeout = 15000
                conn.requestMethod = "GET"
                conn.doInput = true
                conn.connect()
                val code: Int = conn.responseCode
                if (code == HttpURLConnection.HTTP_OK /*200*/) {
                    var count = 0
                    conn.inputStream.bufferedReader().forEachLine{
                        count++
                        if (count >= LIMIT)
                            return@forEachLine
                        resp.append(it + "\n")
                    }
                } else {
                    resp.append("Error: $code")
                }
            } catch (_: Exception) {
                return null
            } finally {
                conn?.inputStream?.close()
                conn?.disconnect()
            }
            return resp.toString()
        }

        fun verifyNetworkState(context: Context): Boolean {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val networkInfo = connMgr?.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected)
                return true
            return false
        }

        fun verifyNetworkStateV2(context: Context):Boolean {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connMgr.allNetworks.forEach { network ->
                connMgr.getNetworkCapabilities(network).apply {
                    if (this?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                        this?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true)
                        return true
                }
            }
            return false
        }

        fun verifyNetworkStateV3(context: Context):Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                with(connMgr.activeNetwork) {
                    with(connMgr.getNetworkCapabilities(this)) {
                        if (this?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                            this?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true)
                            return true
                    }
                }
            }
            return false
        }
    }
}