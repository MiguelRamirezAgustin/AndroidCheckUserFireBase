package com.ejemplo.projectuserfirebaseemplyes

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.ejemplo.projectuserfirebaseemplyes.Utilities.Utils
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var checkTextEmail:Boolean = false
    private var checkTextPass:Boolean = false
    private lateinit var referenceDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        btnLoginEmployes.isEnabled = false

        inputEmailEmployes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if (inputEmailEmployes.text.toString().length > 0) {
                    checkTextEmail = true
                    if(checkTextPass){
                        btnLoginEmployes.setBackgroundResource(R.drawable.iconbtn)
                        linearButton.setBackgroundColor(Color.parseColor("#FF5722"))
                        btnLoginEmployes.isEnabled = true
                    }else{
                        btnLoginEmployes.setBackgroundResource(R.drawable.iconbtnnot)
                        linearButton.setBackgroundColor(Color.parseColor("#BFBDBD"))
                        btnLoginEmployes.isEnabled = false
                    }
                }else{
                    checkTextEmail = false
                    btnLoginEmployes.setBackgroundResource(R.drawable.iconbtnnot)
                    linearButton.setBackgroundColor(Color.parseColor("#BFBDBD"))
                    btnLoginEmployes.isClickable = false
                }
            }
        })

        inputPasswordEmployes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if (inputPasswordEmployes.text.toString().length > 0) {
                    checkTextPass = true
                    if(checkTextEmail){
                        btnLoginEmployes.setBackgroundResource(R.drawable.iconbtn)
                        linearButton.setBackgroundColor(Color.parseColor("#FF5722"))
                        btnLoginEmployes.isEnabled = true
                    }else{
                        btnLoginEmployes.setBackgroundResource(R.drawable.iconbtnnot)
                        linearButton.setBackgroundColor(Color.parseColor("#BFBDBD"))
                        btnLoginEmployes.isEnabled = false
                    }
                }else{
                    checkTextPass = false
                    btnLoginEmployes.setBackgroundResource(R.drawable.iconbtnnot)
                    linearButton.setBackgroundColor(Color.parseColor("#BFBDBD"))
                    btnLoginEmployes.isEnabled = false
                }
            }
        })


        btnLoginEmployes.setOnClickListener {
            if(inputEmailEmployes.text!!.isNotEmpty()   && inputPasswordEmployes.text!!.isNotEmpty()){
                var email = inputEmailEmployes.text.toString().trim()
                println("valida correo  "+ email )
                if(Utils.validateEmal((email))){
                    println("Datos para consulta login Correo: "+ email +" Password: "+inputPasswordEmployes.text.toString())
                    referenceDatabase = FirebaseDatabase.getInstance().getReference("users")
                    referenceDatabase.orderByChild("email").equalTo(email)
                    referenceDatabase.orderByChild("password").equalTo(inputPasswordEmployes.text.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if(dataSnapshot.exists()){
                                    println("Datos consultados Login "+ dataSnapshot)
                                    var key = ""
                                    dataSnapshot!!.children.forEach {
                                        if(it.child("email").getValue().toString().trim() == email){
                                            key = it.key.toString()
                                            println("Datos consultados-->"+ key)
                                            val _intent = Intent(applicationContext, CheckEmployesActivity::class.java)
                                            _intent.putExtra("key", key)
                                            startActivity(_intent)
                                            finish()
                                            return
                                        }
                                    }
                                }else{
                                    showToas("Error al ingresar el usuario no existe")
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                showToas("Error al ingresar intente mas tarde")
                            }
                        })
                }else{
                    showToas("El correo es invalido")
                }
            }else{
                showToas("Los campos son requeridos")
            }
        }
    }


    // fun reusable toas
    private fun showToas(msg:String){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

}