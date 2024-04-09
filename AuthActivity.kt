/*TRIDENTT*/

import android.content.Intent
import android.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import android.view.View
import android.widget.Button
import android.widget.ImageView
import kotlin.random.Random




/*MODULODEAUTENTICACION*/

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configurar inicio de sesión de Google
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Configurar inicio de sesión de Facebook
        callbackManager = CallbackManager.Factory.create()

        // Botón de inicio de sesión con Google
        val googleSignInButton: Button = findViewById(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        // Botón de inicio de sesión con Facebook
        val facebookSignInButton: Button = findViewById(R.id.facebookSignInButton)
        facebookSignInButton.setOnClickListener {
            signInWithFacebook()
        }
    }

    private fun signInWithGoogle() {
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    private fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "Facebook login success.")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "Facebook login canceled.")
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "Facebook login error: ${error.message}")
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    // Manejar el inicio de sesión exitoso con Facebook aquí
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // Manejar el inicio de sesión fallido con Facebook aquí
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Resultado del inicio de sesión de Google
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed: ${e.statusCode}")
            }
        }

        // Resultado del inicio de sesión de Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    // Manejar el inicio de sesión exitoso con Google aquí
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // Manejar el inicio de sesión fallido con Google aquí
                }
            }
    }

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_GOOGLE_SIGN_IN = 9001
    }
}


/*MODULODEAUTENTICACION*/ 

