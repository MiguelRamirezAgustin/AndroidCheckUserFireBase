package com.ejemplo.projectuserfirebaseemplyes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.ejemplo.projectuserfirebase.Model.EmployesCheck
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_check_employes.*
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.database.DatabaseError

import androidx.annotation.NonNull

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.DatabaseReference

import com.google.firebase.database.FirebaseDatabase




class CheckEmployesActivity : AppCompatActivity() {
    private lateinit var referenceDatabase: DatabaseReference
    private lateinit var hoursCheck:String
    private lateinit var checkDay:String
    private lateinit var keyUser:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_employes)
        supportActionBar?.hide()
        referenceDatabase = FirebaseDatabase.getInstance().getReference("users")
        keyUser = intent.getStringExtra("key").toString()

        val formaterDate = SimpleDateFormat("dd/M/yyyy")
        val hoursFormater = SimpleDateFormat("hh:mm:ss a")
        checkDay = formaterDate.format(Date())
        hoursCheck = hoursFormater.format(Date())

        textDate.setText(checkDay)
        println("Hora--->" + hoursCheck)
        checkDayUser(keyUser,checkDay)
        // check Go
        btnChekGo.setOnClickListener {
            getUserCheck(keyUser,checkDay, true)
        }

        // check exit
        btnChekExit.setOnClickListener {
            getUserCheck(keyUser,checkDay, false)
        }
    }


    private fun getUserCheck(keyUser: String, checkDay: String, proccess:Boolean) {
        println("Consulta "+keyUser +" "+checkDay );
        referenceDatabase = FirebaseDatabase.getInstance().getReference("users")
        referenceDatabase.child("Hours").orderByChild("dateCheck").equalTo(checkDay)
        referenceDatabase.child("Hours").orderByChild("key").equalTo(keyUser)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        println("Datos consultados-->"+ dataSnapshot)
                        dataSnapshot.children.forEach {
                            if(proccess){
                                var id =  it.child("checkGo").getValue().toString()
                                if(id == ""){
                                    checkGo()
                                }else{
                                    messageToas("EL usuario ya realizo el check de Entrada")
                                }
                            }else{
                                var id =  it.child("checkExit").getValue().toString()
                                if(id == ""){
                                    checkExit(it.key.toString())
                                }else{
                                    messageToas("EL usuario ya realizo el check salida")
                                }
                            }
                        }
                    }else{
                        checkGo()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    messageToas("Error al reacilizar chek intente nuevamente")
                }
            })
    }

    private fun checkExit(keyCheck:String){
        referenceDatabase = FirebaseDatabase.getInstance().getReference("users")
        referenceDatabase.child("Hours").child(keyCheck).child("checkExit")
            .setValue(hoursCheck)
            .addOnSuccessListener {
                messageToas("Ha realizado el check de salida exitosamente")
            }
            .addOnFailureListener {
                messageToas("Error al realizar el check de salida")
            }
    }

    private fun checkGo(){
        referenceDatabase = FirebaseDatabase.getInstance().getReference("users")
        var uidd = referenceDatabase.push().key
        val user = EmployesCheck(keyUser, checkDay, hoursCheck, "", false)
        referenceDatabase.child("Hours").child(uidd!!).setValue(user)
            .addOnSuccessListener {
                messageToas("Ha realizado el check de entrada")
            }
            .addOnFailureListener {
                messageToas("Error al realizar check de entrada")
            }
    }

    //verifica si el usuario ya realizo el check del dia
    private fun checkDayUser(key:String, dateDay:String){
        val rootRef = FirebaseDatabase.getInstance().reference
        val doctorsRef = rootRef.child("users")
        val categoryQuery = doctorsRef.child("Hours").orderByChild("dateCheck").equalTo(dateDay)
        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val uidList: MutableList<String?> = ArrayList()
                messageToas("Error al realizar la operacion intente nuevamente"+dataSnapshot.children)
                for (ds in dataSnapshot.children) {
                    val uid = ds.key
                    uidList.add(uid)
                }
                println("Valor final "+ uidList)
                //Do what you need to do with your list
            }
            override fun onCancelled(databaseError: DatabaseError) {
                messageToas("Error al realizar la operacion intente nuevamente")
            }
        }
        categoryQuery.addListenerForSingleValueEvent(valueEventListener)

        println("Consulta de check del dia----> "+ key + " "+ dateDay)
        referenceDatabase.child("Hours").orderByChild("dateCheck").equalTo(dateDay)
        referenceDatabase.child("Hours").orderByChild("key").equalTo(key)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        println("Datos usuario para check del  dia"+ dataSnapshot)
                        dataSnapshot.children.forEach {
                        }
                    }else{
                        messageToas("El usuario no ha realizado el check del dia")
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    messageToas("Error al realizar la operacion intente nuevamente")
                }
            })
    }

    private fun messageToas(message:String){
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

}