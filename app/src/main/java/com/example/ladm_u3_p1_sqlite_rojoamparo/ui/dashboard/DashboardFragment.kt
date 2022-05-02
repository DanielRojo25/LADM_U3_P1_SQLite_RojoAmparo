package com.example.ladm_u3_p1_sqlite_rojoamparo.ui.dashboard

import android.R
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ladm_u3_p1_sqlite_rojoamparo.BDAutos
import com.example.ladm_u3_p1_sqlite_rojoamparo.MainActivity2
import com.example.ladm_u3_p1_sqlite_rojoamparo.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    var listaIDs = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*mostrar()*/

        binding.insertar.setOnClickListener {
            val baseDatos = BDAutos(context)
            try {

                var datos = ContentValues()
                val tablaArrenda = baseDatos.writableDatabase

                datos.put("NOMBRE", binding.nombre.text.toString())
                datos.put("DOMICILIO", binding.domicilio.text.toString())
                datos.put("LICENCIACOND", binding.licencia.text.toString())
                datos.put("IDAUTO", binding.auto.text.toString().toInt())
                datos.put("FECHA", binding.fecha.text.toString())

                val resultado = tablaArrenda.insert("ARRENDAMIENTO", "IDARRENDA",datos)
                if (resultado == -1L){
                    Toast.makeText(context,"NO SE PUDO INSERTAR", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(context,"EXITO SE INSERTO", Toast.LENGTH_LONG)
                        .show()
                    /*mostrar()*/
                    binding.nombre.setText("")
                    binding.domicilio.setText("")
                    binding.licencia.setText("")
                    binding.auto.setText("")
                    binding.fecha.setText("")
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
            val tablaArrendamiento = baseDatos.readableDatabase

            var cursor = tablaArrendamiento.query("ARRENDAMIENTO", arrayOf("NOMBRE", "IDARRENDA"),null,null,null,null,null)

            if(cursor.moveToFirst()){
                do{
                    arreglo.add(cursor.getString(0))
                    listaIDs.add(cursor.getInt(1).toString())
                } while (cursor.moveToNext())
            }else{
                arreglo.add("NO HAY RESULTADOS")
            }
        }catch (err: SQLiteException){
            context?.let {
                AlertDialog.Builder(it)
                    .setMessage(err.message)
                    .show()
            }
        }finally {
            baseDatos.close()
        }

        binding.listaA.adapter = context?.let {
            ArrayAdapter<String>(
                it,
                R.layout.simple_list_item_1, arreglo
            )
        }

        fun mostrarUnNombre(idBuscado:String) : String {
            val baseDatos = BDAutos(context)
            var resultado = ""
            try {
                val tablaArrendamiento = baseDatos.readableDatabase

                var cursor = tablaArrendamiento.query("ARRENDAMIENTO", arrayOf("*"),"IDARRENDA=?", arrayOf(idBuscado),null,null,null)
                if(cursor.moveToFirst()){

                    resultado = "IDARRENDA: "+cursor.getInt(0).toString()+"\nNOMBRE: "+
                            cursor.getString(1)+"\nDOMICILIO: "+cursor.getString(2)+
                            "\nLICENCIA: "+cursor.getInt(3).toString()+"\nIDAUTO: "+
                            cursor.getString(4)+"\nFECHA:"+cursor.getString(5)
                }else{
                    resultado = "NO SE ENCONTRO DATOS DE LA CONSULTA"
                }
            }catch (err:SQLiteException){
                activity?.let {
                    AlertDialog.Builder(it)
                        .setMessage(err.message)
                        .show()
                }
            }finally {
                baseDatos.close()
            }
            return resultado
        }

        binding.listaA.setOnItemClickListener { adapterView, view, indice, l ->
            val idRecuperado = listaIDs.get(indice)

            val datosCliente = mostrarUnNombre(idRecuperado)
            activity?.let {
                AlertDialog.Builder(it)
                    .setTitle("INFORMACION")
                    .setMessage("DATOS DEL CLIENTE:\n${datosCliente}")
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
            val tablaArrendamiento = baseDatos.writableDatabase
            val resultado = tablaArrendamiento.delete("ARRENDAMIENTO","IDARRENDA=?", arrayOf(idEliminar))
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
                AlertDialog.Builder(it)
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