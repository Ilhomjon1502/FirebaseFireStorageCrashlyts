package com.example.firebasefilestorage

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebasefilestorage.Adapters.UserAdapter
import com.example.firebasefilestorage.Models.User
import com.example.firebasefilestorage.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.PermissionToken

import com.karumi.dexter.listener.PermissionDeniedResponse

import com.karumi.dexter.listener.PermissionGrantedResponse

import com.karumi.dexter.listener.single.PermissionListener

import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest
import java.lang.RuntimeException


class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    lateinit var firebaseFirestore: FirebaseFirestore
    var list = ArrayList<User>()
    lateinit var userAdapter: UserAdapter

    lateinit var firebaseStorage: FirebaseStorage
    lateinit var reference: StorageReference
    private val TAG = "MainActivity"

    var imageUrl:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()

        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        reference = firebaseStorage.getReference("images")

        binding.btnSave.setOnClickListener {
            val name = binding.edt1.text.toString()
            val age = binding.edt2.text.toString().toInt()

            val user = User(name, age, imageUrl)

            firebaseFirestore.collection("users")
                .add(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "${it.id} Muvaffaqiyatli yuklandi", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "${it.message} xatolik", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnError.setOnClickListener {
            throw RuntimeException("Salom crashlytics")
        }

        firebaseFirestore.collection("users")
            .get()
            .addOnCompleteListener{
                if (it.isSuccessful){
                    val result = it.result
                    result?.forEach {queryDocumentSnapshot ->
                        val user = queryDocumentSnapshot.toObject(User::class.java)
                        list.add(user)
                    }
                    userAdapter = UserAdapter(list)
                    binding.rv.adapter = userAdapter
                }
            }

        binding.imageAdd.setOnClickListener {
            getImageContent.launch("image/*")
        }
    }

    private var getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()){uri->
        binding.imageAdd.setImageURI(uri)

        val m = System.currentTimeMillis()

        val uploadTask = reference.child(m.toString()).putFile(uri)

        uploadTask.addOnSuccessListener {
            if (it.task.isSuccessful){
                val downloadUrl = it.metadata?.reference?.downloadUrl
                downloadUrl?.addOnSuccessListener {imageUri->
                    imageUrl = imageUri.toString()
                }
            }
        }.addOnFailureListener{
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}