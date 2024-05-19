package com.niobe.can_i.provider.services.firebase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

    fun actualizarArticulo(articuloId: String, articulo: Articulo, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firestore.collection("articulos").document(articuloId).set(articulo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    fun uploadImageToFirebaseStorage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getArticulo(articuloId: String, onSuccess: (Articulo?) -> Unit, onFailure: (String) -> Unit) {
        val articuloRef = firestore.collection("articulos").document(articuloId)
        articuloRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val articulo = document.toObject(Articulo::class.java)
                    onSuccess(articulo)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error al obtener el artículo")
            }
    }


    fun guardarArticulo(articuloId: String, articulo: Articulo, callback: (Boolean) -> Unit) {
        firestore.collection("articulos").document(articuloId).set(articulo)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
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

    fun obtenerArticuloPorId(documentId: String, onSuccess: (Articulo?) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("articulos")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val articulo = document.toObject(Articulo::class.java)
                    onSuccess(articulo)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun getArticulosComandaByComanda(idComanda: String, callback: (List<ArticulosComanda>) -> Unit) {
        firestore.collection("articulos_comanda")
            .whereEqualTo("idComanda.idComanda", idComanda)
            .get()
            .addOnSuccessListener { result ->
                val articulosComandaList = mutableListOf<ArticulosComanda>()
                for (document in result) {
                    val articuloComanda = document.toObject(ArticulosComanda::class.java)
                    articulosComandaList.add(articuloComanda)
                }
                callback(articulosComandaList)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(emptyList())
            }
    }

    fun getArticuloById(articuloId: String, callback: (Articulo?) -> Unit) {
        firestore.collection("articulos").document(articuloId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val articulo = document.toObject(Articulo::class.java)
                    callback(articulo)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null)
            }
    }

    fun getArticuloComandaByComandaAndArticulo(idComanda: String, idArticulo: String, callback: (ArticulosComanda?) -> Unit) {
        firestore.collection("articulos_comanda")
            .whereEqualTo("idComanda.idComanda", idComanda)
            .whereEqualTo("idArticulo.articuloId", idArticulo)
            .get()
            .addOnSuccessListener { result ->
                val articuloComanda = result.documents.firstOrNull()?.toObject(ArticulosComanda::class.java)
                callback(articuloComanda)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null)
            }
    }

    fun updateArticuloComanda(idComanda: String, idArticulo: String, nuevaCantidad: Int, callback: (Boolean) -> Unit) {
        firestore.collection("articulos_comanda")
            .whereEqualTo("idComanda.idComanda", idComanda)
            .whereEqualTo("idArticulo.articuloId", idArticulo)
            .get()
            .addOnSuccessListener { result ->
                val document = result.documents.firstOrNull()
                if (document != null) {
                    document.reference.update("cantidad", nuevaCantidad)
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { callback(false) }
    }

    fun deleteArticuloComanda(idComanda: String, idArticulo: String, callback: (Boolean) -> Unit) {
        firestore.collection("articulos_comanda")
            .whereEqualTo("idComanda.idComanda", idComanda)
            .whereEqualTo("idArticulo.articuloId", idArticulo)
            .get()
            .addOnSuccessListener { result ->
                val document = result.documents.firstOrNull()
                if (document != null) {
                    document.reference.delete()
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { callback(false) }
    }


}