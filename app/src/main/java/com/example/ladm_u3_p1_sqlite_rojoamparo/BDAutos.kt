package com.example.ladm_u3_p1_sqlite_rojoamparo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.fragment.app.FragmentActivity
import com.example.ladm_u3_p1_sqlite_rojoamparo.ui.home.HomeFragment

class BDAutos(context: Context?) : SQLiteOpenHelper(context, "AUTO", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("CREATE TABLE AUTOMOVIL ( IDAUTO INTEGER PRIMARY KEY AUTOINCREMENT, MODELO VARCHAR(50), MARCA VARCHAR(50), KILOMETRAJE INTEGER)")
        db.execSQL("CREATE TABLE ARRENDAMIENTO ( IDARRENDA INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE VARCHAR(20), DOMICILIO VARCHAR(50), LICENCIACOND VARCHAR(50), IDAUTO INTEGER CONSTRAINT fk_id_auto REFERENCES AUTOMOVIL(IDAUTO), FECHA VARCHAR(30) )")


    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}