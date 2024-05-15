package com.niobe.can_i.provider.services.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.ArticulosComanda
import com.niobe.can_i.model.Barra
import com.niobe.can_i.model.Camarero
import com.niobe.can_i.model.Comanda
import com.niobe.can_i.model.Usuario
import com.niobe.can_i.usecases.login.LogInActivity
import com.niobe.can_i.util.Util
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUtil {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun cerrarSesion(context: Context) {
        auth.signOut()
        // Redirigir al usuario de vuelta a la pantalla de inicio de sesión
        val intent = Intent(context, LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    fun leerArticulos(tipoArticulo: String, callback: (List<Articulo>) -> Unit) {
        firestore.collection("articulos")
            .whereEqualTo("tipo", tipoArticulo)
            .get()
            .addOnSuccessListener { result ->
                val articulos: MutableList<Articulo> = mutableListOf()
                for (document in result) {
                    val articulo = document.toObject(Articulo::class.java)
                    articulos.add(articulo)
                }
                callback(articulos)
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error al leer datos de Firestore: $exception")
                callback(emptyList())
            }
    }

    fun eliminarArticulo(articuloId: String, callback: (Boolean) -> Unit) {
        firestore.collection("articulos")
            .whereEqualTo("articuloId", articuloId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("SUCCESS", "Documento eliminado correctamente")
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("ERROR", "Error al eliminar documento", e)
                            callback(false)
                        }
                } else {
                    Log.e("ERROR", "No se encontró ningún artículo con el ID $articuloId")
                    callback(false)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error al obtener artículo: $exception")
                callback(false)
            }
    }

    fun actualizarArticulo(
        articuloId: String,
        nuevosDatos: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("articulos")
            .whereEqualTo("articuloId", articuloId) // Consultar documentos con el articuloId proporcionado
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documento = querySnapshot.documents[0] // Tomar el primer documento de la consulta
                    documento.reference.update(nuevosDatos) // Actualizar el documento con los nuevos datos
                        .addOnSuccessListener {
                            Log.d("SUCCESS", "Documento actualizado correctamente")
                            onSuccess() // Llamar a onSuccess cuando la operación tenga éxito
                        }
                        .addOnFailureListener { e ->
                            Log.e("ERROR", "Error al actualizar documento", e)
                            onFailure("Error al actualizar el artículo: ${e.message}") // Llamar a onFailure con el mensaje de error
                        }
                } else {
                    onFailure("No se encontró ningún artículo con el ID $articuloId") // Llamar a onFailure si no se encuentra ningún documento
                }
            }
            .addOnFailureListener { e ->
                Log.e("ERROR", "Error al obtener el artículo para actualizar", e)
                onFailure("Error al obtener el artículo para actualizar: ${e.message}") // Llamar a onFailure con el mensaje de error
            }
    }



    fun getArticulo(articuloId: String, onSuccess: (Articulo?) -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("articulos")
            .whereEqualTo("articuloId", articuloId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val articulo = document.toObject(Articulo::class.java)
                    onSuccess(articulo)
                } else {
                    onFailure("No se encontró ningún artículo con el ID $articuloId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error al obtener artículo: $exception")
                onFailure("Error al obtener el artículo: $exception")
            }
    }

    fun guardarArticulo(articuloId: String, articulo: Articulo, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("articulos")
            .document(articuloId) // Usar articuloId como documentId
            .set(articulo)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseUtil", "Error al guardar el artículo: ", e)
                callback(false)
            }
    }

    fun getBarra(idBarra: String, onSuccess: (Barra?) -> Unit, onFailure: (String) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("barras")
            .document(idBarra)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val barra = documentSnapshot.toObject(Barra::class.java)
                    onSuccess(barra)
                } else {
                    onFailure("No se encontró ninguna barra con el ID $idBarra")
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Error al obtener la barra: ${exception.message}")
            }
    }

    fun borrarBarra(idBarra: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val barrasCollection = firestore.collection("barras")

        barrasCollection.document(idBarra)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure("Error al borrar la barra: ${exception.message}")
            }
    }

    fun leerArticulosPorIdBarra(idBarra: String, callback: (List<Articulo>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val articulosCollection = firestore.collection("articulos_barra")

        articulosCollection.whereEqualTo("idBarra", idBarra)
            .get()
            .addOnSuccessListener { result ->
                val articulos = mutableListOf<Articulo>()
                for (document in result) {
                    val articulo = document.toObject(Articulo::class.java)
                    articulos.add(articulo)
                }
                callback(articulos)
            }
            .addOnFailureListener { exception ->
                // Manejar errores de base de datos aquí
                Log.e("FirebaseUtil", "Error al leer artículos: $exception")
            }
    }

    fun leerUsuariosPorRol(rol: String, callback: (List<Usuario>) -> Unit) {
        firestore.collection("usuarios")
            .whereEqualTo("rol", rol)
            .get()
            .addOnSuccessListener { result ->
                val usuarios: MutableList<Usuario> = mutableListOf()
                for (document in result) {
                    val usuario = document.toObject(Usuario::class.java)
                    usuarios.add(usuario)
                }
                callback(usuarios)
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error al leer datos de Firestore: $exception")
                callback(emptyList())
            }
    }
    fun eliminarUsuario(documentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").document(documentId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun obtenerUsuarioPorId(documentId: String, onSuccess: (Usuario?) -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)
                    onSuccess(usuario)
                } else {
                    // El documento no existe, devolvemos null
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun crearComanda(idCamarero: Camarero, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        // Genera un ID aleatorio para la comanda
        val idComanda = UUID.randomUUID().toString()

        // Obtén la fecha y hora actual
        val fechaHoraActual = Util.obtenerFechaHoraActual()

        // Crea una instancia de Comanda con los datos proporcionados
        val comanda = Comanda(idComanda, idCamarero, fechaHoraActual)

        // Agrega la comanda a Firestore con el ID generado
        firestore.collection("comandas")
            .document(idComanda)
            .set(comanda)
            .addOnSuccessListener {
                println("Comanda creada correctamente en Firestore")
                onSuccess(idComanda) // Devuelve el ID de la comanda creada
            }
            .addOnFailureListener { e ->
                println("Error al crear la comanda: $e")
                onFailure(e)
            }
    }

    fun obtenerCamareroPorId(documentId: String, onSuccess: (Camarero?) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("camareros")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val camarero = document.toObject(Camarero::class.java)
                    onSuccess(camarero)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    suspend fun obtenerComandaPorId(documentId: String, camarero: Camarero): Comanda {
        return suspendCoroutine { continuation ->
            firestore.collection("comandas")
                .document(documentId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val idComanda = document.getString("idComanda") ?: ""
                        val fechaHora = document.getString("fechaHora") ?: ""
                        val comanda = Comanda(idComanda, camarero, fechaHora)
                        continuation.resume(comanda)
                    } else {
                        continuation.resumeWithException(NoSuchElementException("No se encontró ninguna comanda con ese DocumentID"))
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }


    fun crearArticuloComanda(articulosComanda: ArticulosComanda, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Generar un ID aleatorio para idArticulosComanda
        val idArticulosComanda = UUID.randomUUID().toString()

        // Guardar el artículo de comanda en Firestore con el mismo ID que el documentID
        firestore.collection("articulos_comanda")
            .document(idArticulosComanda)
            .set(articulosComanda)
            .addOnSuccessListener {
                // Llamar a onSuccess si la operación tiene éxito
                onSuccess()
            }
            .addOnFailureListener { e ->
                // Llamar a onFailure si hay un error
                onFailure(e)
            }
    }

}