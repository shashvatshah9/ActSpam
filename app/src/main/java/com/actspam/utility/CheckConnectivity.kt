package utility

import android.os.AsyncTask
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

internal class CheckConnectivity(private val mConsumer : Consumer): AsyncTask<Void, Void, Boolean>(){

    interface Consumer{
        fun accept(internet : Boolean?)
    }

    init{
        execute()
    }

    override fun doInBackground(vararg params: Void?): Boolean {
        try{
            val sock = Socket()
            sock.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            sock.close()
        }
        catch (e: IOException){
            return false
        }
        return true
    }

    override fun onPostExecute(internet: Boolean?) {
        mConsumer.accept(internet)
    }
}