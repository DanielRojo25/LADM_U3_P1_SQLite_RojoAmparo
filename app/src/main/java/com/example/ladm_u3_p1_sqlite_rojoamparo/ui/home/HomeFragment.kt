package com.example.ladm_u3_p1_sqlite_rojoamparo.ui.home

import android.R
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ladm_u3_p1_sqlite_rojoamparo.BDAutos
import com.example.ladm_u3_p1_sqlite_rojoamparo.MainActivity2
import com.example.ladm_u3_p1_sqlite_rojoamparo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var listaIDs = ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mostrar()

        binding.insertar.setOnClickListener {
            val baseDatos = BDAutos(context)
            try {

                var datos = ContentValues()
                val tablaAutos = baseDatos.writableDatabase

                datos.put("Marca", binding.marca.text.toString())
                datos.put("Modelo", binding.modelo.text.toString())
                datos.put("Kilometraje", binding.km.text.toString().toInt())

                val resultado = tablaAutos.insert("AUTOMOVIL", "IDAUTO",datos)
                if (resultado == -1L){
                    Toast.makeText(context,"NO SE PUDO INSERTAR", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(context,"EXITO SE INSERTO", Toast.LENGTH_LONG)
                        .show()
                    mostrar()
                    binding.marca.setText("")
                    binding.modelo.setText("")
                    binding.km.setText("")
                }

            }catch (err: SQLiteException){
                Toast.makeText(null,"SQL Exception", Toast.LENGTH_LONG)
                    .show()
            } finally {
                baseDatos.close()
            }
        }

        return root

    }

    fun mostrar (){
        val baseDatos = BDAutos(context)
        var arreglo = ArrayList<String> ()

        listaIDs.clear()

        try{
            val tablaAuto = baseDatos.readableDatabase

            var cursor = tablaAuto.query("AUTOMOVIL", arrayOf("MARCA", "IDAUTO"),null,null,null,null,null)

            if(cursor.moveToFirst()){
                do{
                    arreglo.add(cursor.getString(0))
                    listaIDs.add(cursor.getInt(1).toString())
                } while (cursor.moveToNext())
            }else{
                arreglo.add("NO HAY RESULTADOS")
            }
        }catch (err: SQLiteException){
            Toast.makeText(context,err.message,Toast.LENGTH_LONG)
        }finally {
            baseDatos.close()
        }

        binding.lista.adapter = context?.let {
            ArrayAdapter<String>(
                it,
                R.layout.simple_list_item_1, arreglo
            )
        }

        fun mostrarUnAuto(idBuscado:String) : String {
            val baseDatos = BDAutos(context)
            var resultado = ""
            try {
                val tablaAuto = baseDatos.readableDatabase

                var cursor = tablaAuto.query("AUTOMOVIL", arrayOf("*"),"IDAUTO=?", arrayOf(idBuscado),null,null,null)
                if(cursor.moveToFirst()){

                    resultado = "IDAUTO: "+cursor.getInt(0).toString()+"\nMARCA: "+
                            cursor.getString(2)+"\nMODELO: "+cursor.getString(1)+
                            "\nKILOMETRAJE: "+cursor.getInt(3).toString()
                }else{
                    resultado = "NO SE ENCONTRO DATOS DE LA CONSULTA"
                }
            }catch (err:SQLiteException){
                activity?.let {
                    androidx.appcompat.app.AlertDialog.Builder(it)
                        .setMessage(err.message)
                        .show()
                }
            }finally {
                baseDatos.close()
            }
            return resultado
        }

        binding.lista.setOnItemClickListener { adapterView, view, indice, l ->
            val idRecuperado = listaIDs.get(indice)

            val datosAuto = mostrarUnAuto(idRecuperado)
            activity?.let {
                androidx.appcompat.app.AlertDialog.Builder(it)
                    .setTitle("INFORMACION")
                    .setMessage("DATOS DEL AUTOMOVIL:\n${datosAuto}")
                    .setPositiveButton("Aceptar"){d,i->}
                    .setNeutralButton("ELIMINAR"){d,i->confirmarEliminar(idRecuperado)}
                    .setNegativeButton("ACTUALIZAR"){d,i->
                        var otraVentana = Intent(context, MainActivity2::class.java)

                        otraVentana.putExtra("idactualiza", idRecuperado)
                        startActivity(otraVentana)
                    }
                    .show()
            }
        }
    }

    fun eliminar(idEliminar: String){
        val baseDatos = BDAutos(context)
        try {
            val tablaPersona = baseDatos.writableDatabase
            val resultado = tablaPersona.delete("AUTOMOVIL","IDAUTO=?", arrayOf(idEliminar))
            if(resultado != 0){
                Toast.makeText(activity,"SE ELIMINO CORRECTAMENTE",Toast.LENGTH_LONG)
                    .show()
                mostrar()
            }else{
                activity?.let {
                    AlertDialog.Builder(it)
                        .setTitle("ATENCION")
                        .setMessage("NO SE PUDO ELIMINAR EL ID ${idEliminar}")
                        .setPositiveButton("Aceptar"){d,i->}
                        .show()
                }
            }
        }catch (err:SQLiteException){
            activity?.let {
                androidx.appcompat.app.AlertDialog.Builder(it)
                    .setMessage(err.message)
                    .show()
            }
        }finally {
            baseDatos.close()
        }
    }

    fun confirmarEliminar(idEliminar:String){
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("CONFIRMA ELIMINACION")
                .setMessage("¿ESTAS SEGURO QUE DESEAS\nELIMINAR A ${idEliminar}?")
                .setPositiveButton("SI"){d,i-> eliminar(idEliminar)}
                .setNegativeButton("NO"){d,i->}
                .show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}