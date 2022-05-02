package com.example.ladm_u3_p1_sqlite_rojoamparo

import android.content.ContentValues
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u3_p1_sqlite_rojoamparo.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    var idActualiza = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val extra = intent.extras!!
        idActualiza = extra.getString("idactualiza")!!

        cargarDatos()

        binding.actualizar.setOnClickListener {
            val baseDatos = BDAutos(this)
            try {
                val tablaAutos = baseDatos.writableDatabase
                val datos = ContentValues()

                datos.put("MARCA",binding.marca.text.toString())
                datos.put("MODELO",binding.modelo.text.toString())
                datos.put("KILOMETRAJE",binding.km.text.toString().toInt())

                val resultado = tablaAutos.update("AUTOMOVIL",datos,"IDAUTO=?", arrayOf(idActualiza))
                if (resultado!=0){
                    Toast.makeText(this,"SE ACTUALIZO CON EXITO!", Toast.LENGTH_LONG)
                        .show()
                }else{
                    Toast.makeText(this,"ERROR! no se pudo actualizar", Toast.LENGTH_LONG)
                        .show()
                }

            }catch (err: SQLiteException){
                AlertDialog.Builder(this)
                    .setMessage(err.message)
                    .show()
            }finally {
                baseDatos.close()
            }
        }
        binding.regresar.setOnClickListener {
            finish()
        }

    }

    fun cargarDatos(){
        val baseDatos = BDAutos(this)
        try {
            val tablaAuto = baseDatos.readableDatabase

            var cursor = tablaAuto.query("AUTOMOVIL", arrayOf("*"),"IDAUTO=?", arrayOf(idActualiza),null,null,null)
            if(cursor.moveToFirst()){

                binding.marca.setText(cursor.getString(2))
                binding.modelo.setText(cursor.getString(1))
                binding.km.setText(cursor.getString(3))

            }else{
                AlertDialog.Builder(this)
                    .setMessage("ERROR NO SE ENCONTRO DATOS DEL ID ACTUALIZAR")
                    .show()
            }
        }catch (err: SQLiteException){
            AlertDialog.Builder(this)
                .setMessage(err.message)
                .show()
        }finally {
            baseDatos.close()
        }
    }

}