package com.leadmarkt.leadmarkt
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.recycler_view_row.*
import java.time.Month
import java.time.MonthDay
import java.time.Year
import java.time.YearMonth

class ProductActivity : AppCompatActivity() {

    // Logout işlemi için menü
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Logout işlemi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.favs){
            val intent = Intent(applicationContext,
                StoreProductActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (item.itemId == R.id.logout){
            auth.signOut()
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    var userName : ArrayList<String> = ArrayList()
    var userComment : ArrayList<String> = ArrayList()
    var userDate : ArrayList<String> = ArrayList()
    var adapter : ProductAdapter? = null
    var i = 0

    var tmp : Float = 0F
    var tmp2 : Float = 0F
    var tmp3 : Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        // actionbar
        val actionbar = supportActionBar
        actionbar!!.title = "LeadMarkt"
        actionbar.setDisplayHomeAsUpEnabled(true)
        ///////////////////////////////////////////////////////////////////////////////////////////Rating


        ///////////////////////////////////////////////////////////////////////////////////////////Rating

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        getDataFromFirestore()
        //val currentUser = auth.currentUser

        //get intent
        val intent2 = intent
        val textviewtext = intent2.getStringExtra("barcodenumbertext")
       //print(textviewtext)
        if(textviewtext != null) {
            barcodeTextView.text = textviewtext.toString()
        }

        //RecyclerView
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = ProductAdapter(userName,userComment,userDate)
        recyclerView.adapter = adapter


    }

//Back Press
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onBackPressed() {
        startActivity(Intent(this@ProductActivity, ScanActivity::class.java))
    }
    //Back Press


    //COMMENTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
    fun addComment (view: View){
     //   val comment = commentEditText.text.toString().trim()
        val usermail = auth.currentUser?.email.toString()

        db.collection("Users").addSnapshotListener { snapshot, exception ->
            if (exception != null){
                Toast.makeText(applicationContext,exception.localizedMessage?.toString(),Toast.LENGTH_LONG).show()
            }else{

                if (snapshot!=null){
                    if(!snapshot.isEmpty){
                        val documentsUser = snapshot.documents
                        val currentMail = auth.currentUser?.email.toString()

                        for(documentUser in documentsUser ){

                            val emailFB = documentUser.get("email") as? String

                            if (emailFB == currentMail) {
                                val commentMap = hashMapOf<String, Any>()
                                val name = documentUser.get("name") as? String
                                val surname = documentUser.get("surname") as? String
                                val comment = commentEditText.text.toString().trim()


                                if (commentEditText.text.toString() != "" ) {
                                    if (name != null) {
                                        commentMap.put("name", name)
                                    }
                                    if (surname != null) {
                                        commentMap.put("surname", surname)
                                    }
                                    if (comment != "") {
                                        commentMap.put("comment", comment)
                                    }
                                    commentMap.put("comment", comment)
                                    commentMap.put("barcode", barcodeTextView.text.toString())
                                    commentMap.put("date", Timestamp.now())


                                    db.collection("Comment").add(commentMap).addOnCompleteListener { task ->
                                        if (task.isComplete && task.isSuccessful) {
                                            //  println("auth current user $auth.currentUser!!.email")
                                            commentEditText.text = null
                                            val imm =
                                                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                            imm.hideSoftInputFromWindow(
                                                currentFocus!!.windowToken,
                                                0
                                            )
                                            getDataFromFirestore()
                                        }
                                    }.addOnFailureListener{exception ->
                                        Toast.makeText(applicationContext,exception.localizedMessage?.toString(),Toast.LENGTH_LONG).show()
                                    }
                                }}}}}}}}
    //COMMENTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT


    fun addRate(){

    }


    fun getDataFromFirestore() {

        db.collection("Barcode").addSnapshotListener { snapshot, exception ->
            if (exception != null){
                Toast.makeText(applicationContext,exception.localizedMessage?.toString(),Toast.LENGTH_LONG).show()
            }else{
                if (snapshot!=null){
                    if(!snapshot.isEmpty){
                        val documents = snapshot.documents
                     print(documents)

                        for(document in documents ){

                            val barcode = document.get("barcode") as? String
                            val price = document.get("price") as? String
                            val name = document.get("title") as? String
                            val img= document.get("image") as? String

                           if (barcode.toString() == barcodeTextView.text.toString()) {
                               print(barcode.toString())
                               Picasso.get().load(img).into(imageView)
                               titleTextView.text = name.toString()
                               priceTextView.text = price.toString()


                               //Rating
                               val rBar = findViewById<RatingBar>(R.id.rBar)
                               if (rBar != null) {
                                   val button = findViewById<Button>(R.id.button)
                                   button?.setOnClickListener {
                                       var ratePoint = rBar.rating

                                       val ProductActivity = intent
                                       finish()
                                        startActivity(ProductActivity)

                                       val RatingMap = hashMapOf<String, Any>()
                                       val rating = ratePoint
                                       val email = auth.currentUser!!.email.toString()
                                       val barcode = barcodeTextView.text.toString()

                                       RatingMap.put("rate", rating)
                                       RatingMap.put("email", email)
                                       RatingMap.put("barcode", barcode)

                                       db.collection("Rating").add(RatingMap).addOnCompleteListener { task ->
                                           //  println("auth current user $auth.currentUser!!.email")

                                       }.addOnFailureListener { exception ->
                                           Toast.makeText(
                                               applicationContext,
                                               exception.localizedMessage?.toString(),
                                               Toast.LENGTH_LONG
                                           ).show()
                                       }
                                   }}

                               db.collection("Rating").addSnapshotListener { snapshot, exception ->
                                   if (exception != null){
                                       Toast.makeText(applicationContext,exception.localizedMessage?.toString(),Toast.LENGTH_LONG).show()
                                   }else{
                                       if (snapshot!=null){
                                           if(!snapshot.isEmpty){
                                               val documents = snapshot.documents
                                               i = 0
                                               for(document in documents ) {
                                                   i ++
                                                   //val barcode = document.get("barcode") as? String
                                                   val email = document.get("email") as? String
                                                   val rate = document.get("rate") as Number


                                                   tmp2 = tmp3
                                                   tmp= rate.toFloat()
                                                   tmp3 = tmp + tmp2

                                               }

                                           }
                                           val sum = tmp3/i

                                           rBar.rating = sum
                                          // rateText.text = sum.toString()
                                       }}}


                               //Comment
                               db.collection("Comment").orderBy("date",
                                   Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
                                   if (exception != null){
                                       Toast.makeText(applicationContext,exception.localizedMessage?.toString(),Toast.LENGTH_LONG).show()
                                   }else {
                                       if (snapshot!=null)
                                           if(!snapshot.isEmpty){
                                              userName.clear()
                                              userComment.clear()
                                              userDate.clear()

                                               val documents = snapshot.documents
                                               for(document in documents ){


                                       val name = document.get("name") as? String
                                       val surname = document.get("surname") as? String
                                       val comment = document.get("comment") as? String
                                       val barcodeNo = document.get("barcode") as? String
                                       val timestamp = document.get("date") as Timestamp
                                       val date = timestamp.toDate()


                                                   if (barcodeNo.toString() == barcodeTextView.text.toString() ) {

                                                       if (name != null) {
                                                           userName.add(name +" "+ surname)
                                                       }

                                                       if (comment != null) {
                                                           userComment.add(comment)
                                                       }

                                                          userDate.add(date.toString())

                                                       adapter!!.notifyDataSetChanged()
                                                   }


                                               }}}}
                               // barcodeTextView.text = textviewtext.toString()
                         }


                        /*
                           if (nameTextView.text=="TextView"){
                               nameTextView.text = "Ürün Bulunamadı"
                               titleTextView.text = ""
                               imageTextView.text = ""
                               Toast.makeText(applicationContext,"Ürün Bulunamadı".toString(),Toast.LENGTH_LONG).show()

                               var intent = Intent(applicationContext,ScanActivity::class.java)
                               startActivity(intent)
                               finish() }
*/
                            //       else{}

                        }}}}}}


    //Adding current item to clients favorite list
    fun addFav(view: View) {
                            val authMail = auth.currentUser?.email.toString()

                                    val map = hashMapOf<String, String>()

        // Barcode verisi alma
        db.collection("Barcode").addSnapshotListener { snapshot, exception ->
            if (exception != null){
                Toast.makeText(applicationContext,exception.localizedMessage?.toString(),Toast.LENGTH_LONG).show()
            }else{
                if (snapshot!=null){
                    if(!snapshot.isEmpty){
                        val documents = snapshot.documents

                        for(document in documents ){

                            val barcode = document.get("barcode") as String
                            val img= document.get("image") as String

                            if (barcode== barcodeTextView.text.toString()) {
                                map.put("image", img.toString())}}
        // Barcode verisi alma//
                                map.put("email", authMail)
                                map.put("barcode", barcodeTextView.text.toString())
                                map.put("title", titleTextView.text.toString())
                                map.put("price", priceTextView.text.toString())



                                    db.collection("Favorites").add(map)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(this@ProductActivity, "This item added to your fav list", Toast.LENGTH_SHORT).show()
                                            }
                                        }.addOnFailureListener { exception ->
                                        Toast.makeText(
                                            applicationContext,
                                            exception.localizedMessage?.toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }


                        }
}}}}}