package pt.isec.amov.a2018020733.aula9_firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import pt.isec.amov.a2018020733.aula9_firebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var binding : ActivityMainBinding

    private val strEmail
        get() = binding.edEmail.text.toString()

    private val strPass
        get() = binding.edPassword.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.bind(findViewById(R.id.scrollView))

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        intent.extras?.apply {
            for(k in keySet()) {
                Log.i(TAG, "Extras: $k -> ${get(k)}")
            }
        }

        Firebase.messaging.subscribeToTopic("amov")
            .addOnCompleteListener(this) {
                if (it.isSuccessful)
                    Log.i(TAG, "AMov topic subscribed")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        Firebase.messaging.unsubscribeFromTopic("amov")
    }

    override fun onStart() {
        super.onStart()
        showUser()
    }

    fun showUser(user : FirebaseUser? = auth.currentUser) {
        val str = when (user) {
            null -> "No authenticated user"
            else -> "User: ${user.email}"
        }
        binding.tvStatus.text = str
        Log.i(TAG,str)
    }

    fun onCreateAccountEmail(view: View) {
        if(strEmail.isBlank() || strPass.isBlank())
            return

        createUserWithEmail(strEmail, strPass)
    }

    fun createUserWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(this) { result ->
                Log.i(TAG, "createUser: success")
                showUser(auth.currentUser)
            }
            .addOnFailureListener(this) { e ->
                Log.i(TAG, "createUser: failure ${e.message}")
                showUser(null)
            }
    }

    fun onSignInEmail(view: View) {
        if(strEmail.isBlank() || strPass.isBlank())
            return

        signInWithEmail(strEmail, strPass)
    }

    fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(this) { result ->
                Log.d(TAG, "signInWithEmail: success")
                showUser(auth.currentUser)
            }
            .addOnFailureListener(this) { e->
                Log.d(TAG, "signInWithEmail: failure ${e.message}")
                showUser(null)
            }
    }

    fun onLogoutEmail(view: View) {
        signOut()
    }

    fun signOut() {
        if (auth.currentUser != null) {
            auth.signOut()
        }
        showUser(auth.currentUser)
    }

    fun onSignInGmail(view: View) {
//        startActivity(googleSignInClient.signInIntent, 1234)
        signInWithGoogle.launch(googleSignInClient.signInIntent)
    }

    val signInWithGoogle = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.i(TAG, "onActivityResult - Google authentication: failure")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener(this) { result ->
                Log.d(TAG, "signInWithCredential:success")
                showUser(auth.currentUser)
            }
            .addOnFailureListener(this) { e ->
                Log.d(TAG, "signInWithCredential:failure ${e.message}")
                showUser(auth.currentUser)
            }
    }

    fun onLogoutGmail(view: View) {
        signOutGoogle()
    }

    fun signOutGoogle() {
        if (auth.currentUser != null) {
            googleSignInClient.signOut()
                .addOnCompleteListener(this) {
                    showUser(null)
                }
        }
    }

    fun onAddNewData(view: View) {
        addDataToFirestore()
    }

    fun addDataToFirestore() {
        val db = Firebase.firestore
        val scores = hashMapOf(
            "nrgames" to 0,
            "topscore" to 0
        )
        db.collection("Scores").document("Level1").set(scores)
            .addOnSuccessListener {
                Log.i(TAG, "addDataToFirestore: Success")
            }
            .addOnFailureListener { e->
                Log.i(TAG, "addDataToFirestore: ${e.message}")
            }
    }

    fun onUpdateData(view: View) {
//        updateDataInFirestore()
        updateDataInFirestoreTrans()
    }

    fun updateDataInFirestore() {
        val db = Firebase.firestore
        val v = db.collection("Scores").document("Level1")
        v.get(Source.SERVER)
            .addOnSuccessListener {
                val exists = it.exists()
                Log.i(TAG, "updateDataInFirestore: Success? $exists")
                if (!exists)
                    return@addOnSuccessListener
                val value= it.getLong("nrgames") ?: 0
                v.update("nrgames",value+1)
            }
            .addOnFailureListener { e ->
                Log.i(TAG, "updateDataInFirestore: ${e.message}")
            }
    }

    fun updateDataInFirestoreTrans() {
        val db = Firebase.firestore
        val v = db.collection("Scores").document("Level1")
        db.runTransaction { transaction ->
            val doc = transaction.get(v)
            if (doc.exists()) {
                val newnrgames = (doc.getLong("nrgames") ?: 0) + 1
                val newtopscore = (doc.getLong("topscore") ?: 0) + 100
                transaction.update(v, "nrgames", newnrgames)
                transaction.update(v, "topscore", newtopscore)
                null
            } else
                throw FirebaseFirestoreException(
                    "Error",
                    FirebaseFirestoreException.Code.UNAVAILABLE
                )
        }.addOnSuccessListener {
            Log.i(TAG, "updateDataInFirestoreTrans: Success")
        }.addOnFailureListener { e ->
            Log.i(TAG, "updateDataInFirestoreTrans: ${e.message}")
        }
    }

    fun onRemoveData(view: View) {
        removeDataFromFirestore()
    }

    fun removeDataFromFirestore() {
        val db = Firebase.firestore
        val v = db.collection("Scores").document("Level1")
        v.delete()
            .addOnSuccessListener {
                Log.i(TAG, "removeDataFromFirestore: Success")
            }
            .addOnFailureListener { e ->
                Log.i(TAG, "removeDataFromFirestore: ${e.message}")
            }
    }

    fun onObserveData(view: View) {
        startObserver()
    }

    var listenerRegistration : ListenerRegistration? = null
    fun startObserver() {
        val db = Firebase.firestore
        db.collection("Scores").document("Level1")
            .addSnapshotListener { docSS, e ->
                if (e!=null) {
                    return@addSnapshotListener
                }
                if (docSS!=null && docSS.exists()) {
                    val nrgames = docSS.getLong("nrgames")
                    val topscore = docSS.getLong("topscore")
                    Log.i(TAG, "$nrgames : $topscore")
                    binding.tvLog.text = "$nrgames : $topscore"
                }
            }
    }

    override fun onPause() {
        super.onPause()
        stopObserver()
    }

    fun stopObserver() {
        listenerRegistration?.remove()
    }
}