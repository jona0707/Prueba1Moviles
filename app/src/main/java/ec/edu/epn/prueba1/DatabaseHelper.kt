package ec.edu.epn.nanEC

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "Actividades.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "actividades"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "nombre"
        const val COLUMN_PLACE = "lugar"
        const val COLUMN_DATE = "fecha"
        const val COLUMN_ATTENDEES = "asistentes"
    }

    // creamos una tabla
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
         CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_PLACE TEXT NOT NULL,
            $COLUMN_DATE TEXT NOT NULL,
            $COLUMN_ATTENDEES TEXT NOT NULL
        )  
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.let { onCreate(it) }
    }

    // método para insertar datos en la base de datos
    fun insertActividad(nombre: String, lugar: String, fecha: String, asistentes: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, nombre)
            put(COLUMN_PLACE, lugar)
            put(COLUMN_DATE, fecha)
            put(COLUMN_ATTENDEES, asistentes)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // función para leer los datos -- que retorna todos los valores de actividades
    fun getAllActividades(): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_NAME, null, null, null, null, null, null)
    }
}