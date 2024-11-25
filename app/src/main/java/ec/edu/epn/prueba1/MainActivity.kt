package ec.edu.epn.prueba1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ec.edu.epn.nanEC.DatabaseHelper
import ec.edu.epn.prueba1.ui.theme.Prueba1Theme

class MainActivity : ComponentActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        databaseHelper = DatabaseHelper(this);
        insertarDatosIniciales();
        setContent {
            ListaDeActividades(
                databaseHelper = databaseHelper,
                onMapClick = { direccion ->
                    val uri = Uri.parse("geo:0,0?q=$direccion")
                    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(mapIntent)
                }
            )
        }
    }

    private fun insertarDatosIniciales() {
        val cursor = databaseHelper.getAllActividades()
        if (cursor.count == 0) {
            databaseHelper.insertActividad("Trail en el Mirador de la Perdiz", "Mirador de la Perdiz", "31/10/2024","30 asistentes")
            databaseHelper.insertActividad("Concurso de la Mejor colada morada", "Estadio de San José", "31/10/2024","100 asistentes")
            databaseHelper.insertActividad("Conmemoración día de los difuntos", "Cementerio de San José", "02/11/2024","30 asistentes")
            databaseHelper.insertActividad("Concurso de guagua de pan", "Estadio de San José", "03/11/2024"," 100 asistentes")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ListaDeActividades(onMapClick = {})
}

@Composable
fun ListaDeActividades(
    databaseHelper: DatabaseHelper? = null,
    onMapClick: (String) -> Unit
) {
    val actividades = remember { mutableListOf<Actividad>() }
    val cursor = databaseHelper?.getAllActividades()
    cursor?.use {
        while (it.moveToNext()) {
            val nombreIndex = it.getColumnIndex(DatabaseHelper.COLUMN_NAME)
            val lugarIndex = it.getColumnIndex(DatabaseHelper.COLUMN_PLACE)
            val fechaIndex = it.getColumnIndex(DatabaseHelper.COLUMN_DATE)
            val asistentesIndex = it.getColumnIndex(DatabaseHelper.COLUMN_ATTENDEES)

            if (nombreIndex != -1 && lugarIndex != -1 && fechaIndex != -1 && asistentesIndex != -1) {
                val nombre = it.getString(nombreIndex)
                val lugar = it.getString(lugarIndex)
                val fecha = it.getString(fechaIndex)
                val asistentes = it.getString(asistentesIndex)
                actividades.add(Actividad(nombre, lugar, fecha, asistentes))
            } else {
                Log.e("DatabaseError", "Una o más columnas no fueron encontradas en el cursor.")
            }
        }
    }
    Column {
    Text(text="Eventos en San José por el feriado de noviembre",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
            .padding(horizontal = 10.dp),
        textAlign = TextAlign.Center,
    )
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(actividades) { index, actividad ->
            val imagen = when (index) {
                0 -> R.drawable.img0
                1 -> R.drawable.img1
                2 -> R.drawable.img2
                else -> R.drawable.img3
            }
            ActividadCard(actividad, imagen, onMapClick)
        }
    }
    }
}

@Composable
fun ActividadCard(
    actividad: Actividad,
    imagen: Int,
    onMapClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .clickable{
                    onMapClick(actividad.lugar)
                }
        ) {
            Image(
                painter = painterResource(id = imagen),
                contentDescription = "Imagen de la actividad",
                modifier = Modifier.fillMaxSize().height(150.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = actividad.nombre,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                )
                Text(text = actividad.lugar, fontStyle = FontStyle.Italic, modifier = Modifier.padding(top = 10.dp))
                Text(text = actividad.fecha, modifier = Modifier.padding(top = 10.dp))
            }
            Text(text = actividad.asistentes, modifier = Modifier.padding(
                PaddingValues(
                    start = 10.dp,
                    top = 0.dp,
                    end = 10.dp,
                    bottom = 20.dp
                )
            ))
        }
    }
}

data class Actividad(
    val nombre: String,
    val lugar: String,
    val fecha: String,
    val asistentes: String
)
