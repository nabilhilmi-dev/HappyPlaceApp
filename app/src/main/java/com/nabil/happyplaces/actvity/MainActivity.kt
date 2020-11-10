package com.nabil.happyplaces.actvity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nabil.happyplaces.R
import com.nabil.happyplaces.adapter.HappyPlacesAdapter
import com.nabil.happyplaces.database.DatabaseHandler
import com.nabil.happyplaces.model.HappyPlaceModel
import com.nabil.happyplaces.util.SwipeToDeleteCallBack
import com.nabil.happyplaces.util.SwipeToEditCallBack
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        var ADD_HAPPY_ACTIVITY_REQUEST_CODE = 1
        var HAPPY_PLACES_DETAILS = "extra_happy_details"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_add_happy_place.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_HAPPY_ACTIVITY_REQUEST_CODE)
        }

        getHappyPlacesFromLocalDB()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_HAPPY_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getHappyPlacesFromLocalDB()
            } else {
                Log.e("Activity", "called or back pressed")
            }
        }
    }

   private fun getHappyPlacesFromLocalDB() {
       //variable supaya databasenya bisa kita gunakan di MainActicity
       val dbHandler = DatabaseHandler(this)
       val getHappyPlacesList = dbHandler.getHappyPlaceList()

       //sebuah kondisi ketika kondisi itu ada
       if (getHappyPlacesList.size > 0 ) {
           rv_happy_places_list.visibility = View.VISIBLE
           tv_no_records.visibility = View.GONE
           setupHappyPlacesRecyclerView(getHappyPlacesList)

           //kondisi ketika data itu kosong
      } else {
           rv_happy_places_list.visibility = View.GONE
           tv_no_records.visibility = View.VISIBLE
      }
  }

    //function ini digunakan untuk create recyclerview di dalam MainActivity
    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>) {
        //untuk mendeteksi data ketika ada perubahan seperti ada data baru yang masuk kedalam recyclerview
        rv_happy_places_list.layoutManager = LinearLayoutManager(this)
        //buat trigger ketika ada data baru
        rv_happy_places_list.setHasFixedSize(true)

        //untuk menjalankan adapter kita di dalam mainactivity sehingga recyclerview bisa berjalan dengan seharusnya
        val placeAdapter = HappyPlacesAdapter(this, happyPlacesList)
        rv_happy_places_list.adapter = placeAdapter

        placeAdapter.setOnClickListener(object : HappyPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyDetailActivity::class.java)
                intent.putExtra(HAPPY_PLACES_DETAILS, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallBack(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_happy_places_list.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_HAPPY_ACTIVITY_REQUEST_CODE

                )
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_happy_places_list)

        val deleteSwipeHandler = object : SwipeToDeleteCallBack(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_happy_places_list.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                getHappyPlacesFromLocalDB()
            }

        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_happy_places_list)
    }


}