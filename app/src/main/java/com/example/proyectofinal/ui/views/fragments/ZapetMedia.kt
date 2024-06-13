package com.example.proyectofinal.ui.views.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.data.models.Tarea
import com.example.proyectofinal.data.repositories.FriendRepository
import com.example.proyectofinal.data.repositories.NotificationRepository
import com.example.proyectofinal.data.repositories.TareasRepository
import com.example.proyectofinal.data.services.FirebaseService
import com.example.proyectofinal.data.services.GeneralService
import com.example.proyectofinal.data.services.NotificationService
import com.example.proyectofinal.data.services.TareasService
import com.example.proyectofinal.data.services.UserService
import com.example.proyectofinal.databinding.FragmentTareasBinding
import com.example.proyectofinal.databinding.FragmentZapetMediaBinding
import com.example.proyectofinal.databinding.InfoActivityBinding
import com.example.proyectofinal.ui.adapters.AdapterTareaMedia
import com.example.proyectofinal.ui.modelView.InfoViewModel
import com.example.proyectofinal.ui.modelView.InfoViewModelFactory
import com.example.proyectofinal.ui.modelView.MediaViewModel
import com.example.proyectofinal.ui.modelView.MediaViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class ZapetMedia : Fragment() {
    private lateinit var mainRecyclerView: RecyclerView

    private lateinit var loadBar: ProgressBar
    private lateinit var sinAmigosMsg: TextView

    @Inject lateinit var userService: UserService
    @Inject lateinit var tareasService: TareasService
    @Inject lateinit var firebaseService: FirebaseService

    private lateinit var zapetMediaBinding: FragmentZapetMediaBinding

    private val viewModel: MediaViewModel by viewModels {
        val repositorioAmigos = FriendRepository(userService)
        val repositorioTareas = TareasRepository(tareasService)
        MediaViewModelFactory(repositorioAmigos, repositorioTareas, firebaseService)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        zapetMediaBinding = FragmentZapetMediaBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return zapetMediaBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        asociarElementos(view)
        registerLiveData(view)
        viewModel.loadAmigosMedia()
    }

    fun asociarElementos(view: View){
        sinAmigosMsg = view.findViewById(R.id.mensaje_sin_amigos)
        loadBar = zapetMediaBinding.loadBar
        mainRecyclerView = zapetMediaBinding.mainMediaRecyclerView
        mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun updateTareasRecyclerViews(tareasPorAmigo: Map<String, List<Tarea>>, view: View) {
        for ((amigoId, tareas) in tareasPorAmigo) {
            val recyclerView = view.findViewById<RecyclerView>(amigoId.hashCode())
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            val friendTaskList = tareas.toMutableList()

            recyclerView.adapter = AdapterTareaMedia(
                friendTaskList
            )

            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    fun registerLiveData(view: View){
        viewModel.loading.observe(viewLifecycleOwner){
            if(it){
                loadBar.visibility = LinearLayout.VISIBLE
            }else{
                loadBar.visibility = LinearLayout.GONE
            }
        }

        viewModel.actualMediaFriendList.observe(viewLifecycleOwner){
            viewModel.setMediaFriendAdapter(mainRecyclerView)
            mainRecyclerView.adapter?.notifyDataSetChanged()
            if (it.isEmpty()){
                sinAmigosMsg.visibility = LinearLayout.VISIBLE
            }else{
                sinAmigosMsg.visibility = LinearLayout.GONE
            }
        }

        viewModel.tareasMediaList.observe(viewLifecycleOwner){
            updateTareasRecyclerViews(it, view)
        }
    }
}