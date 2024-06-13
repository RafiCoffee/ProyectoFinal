package com.example.proyectofinal.ui.views

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Usuario
import com.example.proyectofinal.data.repositories.FriendRepository
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.databinding.AddAmigoBinding
import com.example.proyectofinal.ui.modelView.FriendViewModel
import com.example.proyectofinal.ui.modelView.FriendViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class AddAmigoActivity: AppCompatActivity() {
    private lateinit var usuarioLogueado: Usuario
    private lateinit var recyclerView: RecyclerView

    private lateinit var atrasBt: ImageView
    private lateinit var searchText: EditText
    private lateinit var loadBar: ProgressBar

    @Inject  lateinit var generalService: GeneralService
    @Inject  lateinit var userService: UserService
    @Inject  lateinit var firebaseService: FirebaseService

    private lateinit var addAmigoBinding: AddAmigoBinding

    private val viewModel: FriendViewModel by viewModels {
        val repositorio = FriendRepository(userService)
        FriendViewModelFactory(repositorio, userService, firebaseService)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addAmigoBinding = AddAmigoBinding.inflate(layoutInflater)
        setContentView(addAmigoBinding.root)

        usuarioLogueado = intent.getParcelableExtra("usuario")!!
        userService.setFriendUserObject(usuarioLogueado)

        asociarElementos()
        viewModel.loadFriends()
        iniciarEventos()
        registerLiveData()
    }

    private fun asociarElementos(){
        searchText = findViewById(R.id.findFriend)
        atrasBt = findViewById(R.id.atrasBt)
        loadBar = addAmigoBinding.loadBar
        recyclerView = addAmigoBinding.friendRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.setAdapter(recyclerView, 0)
    }

    private fun iniciarEventos(){
        atrasBt.setOnClickListener {
            finish()
        }

        searchText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length!! > 2 && s.startsWith("#FR")){
                    viewModel.loadFriendsByCode(s.toString())
                }else{
                    viewModel.loadFriendsByName(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun registerLiveData(){
        viewModel.loading.observe(this){
            if(it){
                loadBar.visibility = LinearLayout.VISIBLE
            }else{
                loadBar.visibility = LinearLayout.GONE
            }
        }

        viewModel.friendList.observe(this){
            viewModel.setAdapter(recyclerView, 0)
            recyclerView.adapter?.notifyDataSetChanged()
        }

        viewModel.friendFiltredList.observe(this){
            viewModel.setAdapter(recyclerView, 1)
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }
}