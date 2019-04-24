package com.naldana.ejemplo11

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import android.widget.Toast





class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // TODO (1) Obtener acceso a Shared Preference
        // TODO (2) Obtener Shared Preference desde la actividad
        // TODO (3) Especificar el nombre del archivo de preferencia (si se esta usando mas de uno)
        // TODO (3.1) Cuando se necesita solamente un archivo de preferencia en el app llamar a getPreferences con un paramentro
        // TODO (4) Asignar que la preferencias se leeran o escribiran en modo privado (Para mantender privado las configuración)
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)


        bt_save.setOnClickListener {
            // TODO (5) Para escribir valores en shared preference
            with(sharedPref.edit()) {
                putString(
                    getString(R.string.save_email_key),
                    et_option.text.toString()
                ) // TODO (6) Se guardan en formato clave valor
                commit() // TODO (7) Confirma que se guarden todos los elementos añadidos

            }

            tv_data.text = et_option.text.toString() // Solamente para mostrar el valor de inmediato
        }


        // TODO (8) Para leer basta con ejecutar el metodo getXXXX y definir el valor por defecto si no existe
        val email = sharedPref.getString(getString(R.string.save_email_key), "")

        tv_data.text = email


        bt_write_internal.setOnClickListener {
            // TODO (9) openFileOutput crea un archivo y un InputStream para escribir en el
            val filename = "email.txt"
            val fileContent = "email: $email"

            // TODO (10) Al usar use obtenemos el fileInput que devuelve FileOutputStream
            // TODO (11) use cierra el FileOutputStream y maneja la exception a nivel de bloque

            openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(fileContent.toByteArray())
            }

        }


        bt_read_internal.setOnClickListener {
            // TODO (12) Abrir un archivo existente
            val filename = "email.txt"
            openFileInput(filename).use {
                val text = it.bufferedReader().readText() // TODO (13) Se lee todo el contenido
                tv_data.text = text
            }
        }

        bt_write_external.setOnClickListener {
        

            val filename = "email_sd.txt"
            val fileContent = "email: $email"
            if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val textFile = File(Environment.getExternalStorageDirectory(), filename)
                try {
                    val fos = FileOutputStream(textFile)
                    fos.write(fileContent.toByteArray())
                    fos.close()
                    println(Environment.getExternalStorageDirectory())
                    Toast.makeText(this, "File Saved.", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                Toast.makeText(this, "Cannot Write to External Storage.", Toast.LENGTH_SHORT).show()
                setupPermissions(102)
            }

        }

        bt_read_external.setOnClickListener {
            val filename = "email_sd.txt"

            if (isExternalStorageReadable()) {
               
                try {
                    val textFile = File(Environment.getExternalStorageDirectory(), filename)
                    val fis = FileInputStream(textFile)

                    val isr = InputStreamReader(fis)
                    val buff = BufferedReader(isr)
                    var line: String?
                    line = buff.readLine()
                    Log.d("line",line.toString())

                    tv_data.text = line
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                Toast.makeText(this, "Cannot Read from External Storage.", Toast.LENGTH_SHORT).show()
            }

        }

    }

    //Permission
    private val TAG = "PermissionDemo"
    private val READ_REQUEST_CODE = 101
    private val WRITE_REQUEST_CODE = 102


    private fun setupPermissions(id: Int) {
        val permissionRead = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val permissionWrite = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        when (id) {
            101 -> if (permissionRead != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission to read denied")
                makeRequestToRead()
            }

            102 -> if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission to write denied")
                makeRequestToWrite()
            }
        }


    }

    private fun makeRequestToRead() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_REQUEST_CODE
        )
    }

    private fun makeRequestToWrite() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            READ_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission READ_EXTERNAL_STORAGE has been denied by user")
                } else {
                    Log.i(TAG, "Permission READ_EXTERNAL_STORAGE has been granted by user")
                }
            }

            WRITE_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission WRITE_EXTERNAL_STORAGE has been denied by user")
                } else {
                    Log.i(TAG, "Permission WRITE_EXTERNAL_STORAGE has been granted by user")
                }
            }
        }

    }

    private fun isExternalStorageWritable(): Boolean {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            Log.i("State", "Yes, it is writable!")
            return true
        } else {
            return false
        }
    }

    private fun isExternalStorageReadable(): Boolean {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()) {
            Log.i("State", "Yes, it is readable!")
            return true
        } else {
            return false
        }
    }

    fun checkPermission(permission: String): Boolean {
        val check = ContextCompat.checkSelfPermission(this, permission)
        return check == PackageManager.PERMISSION_GRANTED
    }
}
