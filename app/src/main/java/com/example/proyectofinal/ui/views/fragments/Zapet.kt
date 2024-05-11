package com.example.proyectofinal.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.proyectofinal.R
import com.google.android.material.tabs.TabLayout


class Zapet : Fragment() {
    private lateinit var tabConsejos: TabLayout
    private lateinit var contendorConsejos: ViewPager

    private var portadasConsejos = arrayOf(R.drawable.calendar_icon, R.drawable.foto_perfil, R.drawable.papelera_icon)
    private var indicadores = arrayOf(R.drawable.tab_indicator_icon, R.drawable.tab_indicator_icon, R.drawable.tab_indicator_icon)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zapet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabConsejos = view.findViewById(R.id.tabConsejos)
        contendorConsejos = view.findViewById(R.id.contenedorConsejos)

        val adapter = ImagePagerAdapter()
        contendorConsejos.adapter = adapter

        tabConsejos.setupWithViewPager(contendorConsejos, true)
        contendorConsejos.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabConsejos))

        /* Configurar el TabLayout con imágenes personalizadas
        for (i in portadasConsejos.indices) {
            val tab = tabConsejos.newTab()
            tab.customView = createTabView(i)
            tabConsejos.addTab(tab)
        }*/
    }

    private fun createTabView(position: Int): View {
        val imageView = ImageView(requireContext())
        imageView.setImageResource(indicadores[position])
        return imageView
    }

    private inner class ImagePagerAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return portadasConsejos.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(requireContext())
            imageView.setImageResource(portadasConsejos[position])

            // Agregar el evento onClick para cada imagen
            imageView.setOnClickListener {
                // Lógica específica al hacer clic en cada imagen
                when (position) {
                    0 -> Toast.makeText(requireContext(), "Clic en Imagen 1", Toast.LENGTH_SHORT).show()
                    1 -> Toast.makeText(requireContext(), "Clic en Imagen 2", Toast.LENGTH_SHORT).show()
                    2 -> Toast.makeText(requireContext(), "Clic en Imagen 3", Toast.LENGTH_SHORT).show()
                }
            }

            container.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getPageTitle(position: Int): CharSequence? {
            // Devuelve el título de cada página, en este caso, la posición como texto
            return (position).toString()
        }
    }

}