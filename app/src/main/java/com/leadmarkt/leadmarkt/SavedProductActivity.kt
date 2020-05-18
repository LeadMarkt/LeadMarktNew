package com.leadmarkt.leadmarkt

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_saved_product.*

class SavedProductActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    var favTitle : ArrayList<String> = ArrayList()
    var favPrice : ArrayList<String> = ArrayList()
    var favImage : ArrayList<String> = ArrayList()
    var adapter : SavedProductAdapter? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_product)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        generateFavList()

        // actionbar
        val actionbar = supportActionBar
        actionbar!!.title = "LeadMarkt"
        actionbar.setDisplayHomeAsUpEnabled(true)

        var layoutManager = LinearLayoutManager(this)
        recycler_view.layoutManager = layoutManager

        adapter = SavedProductAdapter(favTitle,favPrice,favImage)
        recycler_view.adapter = adapter



    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        startActivity(Intent(this@SavedProductActivity, ScanActivity::class.java))
    }

    private fun generateFavList() {
        db.collection("Favorites").addSnapshotListener{ snapshot, exception ->
            if (exception != null){
                Toast.makeText(applicationContext,exception.localizedMessage?.toString(), Toast.LENGTH_LONG).show()
            }
            else{
                if (snapshot!=null){
                    if(!snapshot.isEmpty){
                        favTitle.clear()
                        favPrice.clear()
                        favImage.clear()
                        val documents = snapshot.documents
                        for(document in documents ){
                            val title = document.get("title") as String
                            val price = document.get("price") as String
                            val image = document.get("image") as? String
                            val email = document.get("email") as String
                            //val barcode = document.get("barcode") as String

                            if (email == auth.currentUser!!.email)
                            {
                                favTitle.add(title)
                                favPrice.add(price)
                                if (image != null) {
                                    favImage.add(image)
                                }




                                adapter!!.notifyDataSetChanged()

                            }}
                    }
                }

            }
        }
    }
}
