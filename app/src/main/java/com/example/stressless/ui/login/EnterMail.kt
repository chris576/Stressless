package com.example.stressless.ui.login

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stressless.MainActivity
import com.example.stressless.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

val mail_key = "Mail"
val user_key = "User"

/**
 *  Adds an function to string that checks, if it is a valid mail.
 */
fun String.isValidMail(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
        .matches()
}

class EnterMail : AppCompatActivity(R.layout.enter_mail_activity) {

    /**
     * Is invoked when the "next" Button is clicked.
     */
    fun enterMail(view: View) {
        val text_edit = findViewById<EditText>(R.id.input_mail_ident)
        val mail = text_edit.text.toString()
        if (!mail.isValidMail()) {
            text_edit.setError("Mail nicht valide.")
            return
        }
        val intent = Intent(this, EnterPassword::class.java)
        intent.putExtra(mail_key, mail)
        startActivity(intent)

    }

    /**
     *
     */
    fun register(view: View) {
        startActivity(Intent(this, Register_User::class.java))
    }
}

class EnterPassword : AppCompatActivity(R.layout.enter_password_activity) {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onStart() {
        firebaseAuth = FirebaseAuth.getInstance()
        super.onStart()
    }

    fun checkPassword(view: View) {
        val pw_field = findViewById<EditText>(R.id.input_password_ident)
        if (pw_field.text.isEmpty()) {
            pw_field.setError("Geben Sie ein Passwort ein!")
            return
        }
        val password: String = pw_field.text.toString()
        val task =
            firebaseAuth.signInWithEmailAndPassword(intent.getStringExtra(mail_key), password)
        task.addOnSuccessListener(this) { result ->
            val user = result.user
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(user_key, user)
            startActivity(intent)
        }

        var failures_count = 0;

        task.addOnFailureListener(this) { result ->
            findViewById<EditText>(R.id.input_password_ident).setError("Falsches Passwort!")
            failures_count++
            if (failures_count >= 3)
                back()
        }
    }

    private fun back(view: View? = null) {
        onBackPressed()
    }
}

class Register_User : AppCompatActivity(R.layout.register_user_activity_layout) {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onStart() {
        firebaseAuth = FirebaseAuth.getInstance()
        super.onStart()
    }

    fun register(view: View) {
        val mail_view = findViewById<EditText>(R.id.mail_input)
        val name_view = findViewById<EditText>(R.id.name_input)
        val password_view = findViewById<EditText>(R.id.password_input)
        if (mail_view.text.isEmpty()
            && !mail_view.text.toString().isValidMail()
        ) {
            mail_view.setError("Geben Sie eine valide E-Mail ein!")
            return
        }
        if (name_view.text.isEmpty()) {
            name_view.setError("Geben Sie einen Namen ein!")
            return
        }
        if (password_view.text.isEmpty()) {
            password_view.setError("Geben Sie ein Passwort ein!")
            return
        }
        val mail = mail_view.text.toString()
        val name = name_view.text.toString()
        val password = password_view.text.toString()
        mail.trim()
        name.trim()
        val create_user_task = firebaseAuth.createUserWithEmailAndPassword(mail, password)
        // @Todo: What if updateProfile failes?
        create_user_task.addOnSuccessListener(this) { r ->
            val changeRequest = UserProfileChangeRequest.Builder().setDisplayName(name)
            r.user.updateProfile(changeRequest.build())
            val intent = Intent(this, Register_Or_Apply_To_Community::class.java)
            startActivity(intent)
        }
        create_user_task.addOnFailureListener(this) { r ->

            Toast.makeText(this, r.message, Toast.LENGTH_SHORT).show()
        }
    }
}

class Register_Or_Apply_To_Community :
    AppCompatActivity(R.layout.register_or_apply_to_community_activity) {

    fun found_new_community(view: View) {

    }

    fun applyForCommunity(view: View) {

    }

}

class Register_Community : AppCompatActivity() {

}