package com.example.proyectofinal.ui.views

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.repositories.FriendRepository
import com.example.proyectofinal.data.repositories.NotificationRepository
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.NotificationService
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.databinding.AddAmigoBinding
import com.example.proyectofinal.databinding.InfoActivityBinding
import com.example.proyectofinal.ui.modelView.FriendViewModel
import com.example.proyectofinal.ui.modelView.FriendViewModelFactory
import com.example.proyectofinal.ui.modelView.InfoViewModel
import com.example.proyectofinal.ui.modelView.InfoViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.Q)
class InfoActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    private lateinit var atrasBt: ImageView
    private lateinit var loadBar: ProgressBar

    @Inject lateinit var generalService: GeneralService
    @Inject lateinit var userService: UserService
    @Inject lateinit var notificationService: NotificationService
    @Inject lateinit var tareasService: TareasService
    @Inject lateinit var firebaseService: FirebaseService

    private lateinit var infoBinding: InfoActivityBinding

    private val viewModel: InfoViewModel by viewModels {
        val repositorioAmigos = FriendRepository(userService)
        val repositorioNotificaciones = NotificationRepository(notificationService)
        InfoViewModelFactory(repositorioAmigos, repositorioNotificaciones, userService, firebaseService, tareasService)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        infoBinding = InfoActivityBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)

        asociarElementos()
        iniciarEventos()
        registerLiveData()

        when (intent.getIntExtra("infoMode", -1)) {
            1 -> {
                viewModel.loadNotifications()
            }
            2 -> {
                viewModel.loadActualFriends()
            }
            else -> {
                Toast.makeText(this, "Error al acceder a la pantalla", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun asociarElementos(){
        atrasBt = findViewById(R.id.atrasBt)
        loadBar = infoBinding.loadBar
        recyclerView = infoBinding.infoRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun iniciarEventos(){
        atrasBt.setOnClickListener {
            finish()
        }
    }

    private fun registerLiveData(){
        viewModel.loading.observe(this){
            if(it){
                loadBar.visibility = LinearLayout.VISIBLE
            }else{
                loadBar.visibility = LinearLayout.GONE
            }
        }

        viewModel.notificationList.observe(this){
            viewModel.setNotificationAdapter(recyclerView)
            recyclerView.adapter?.notifyDataSetChanged()
        }

        viewModel.actualFriendList.observe(this){
            viewModel.setActualFriendAdapter(recyclerView)
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }
}