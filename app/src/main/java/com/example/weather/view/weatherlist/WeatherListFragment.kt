package com.example.weather.view.weatherlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.FragmentWeatherListBinding
import com.example.weather.view.ditails.OnItemClick
import com.example.weather.domain.Weather
import com.example.weather.view.ditails.DetailsFragment
import com.example.weather.viewmodel.AppState
import com.example.weather.viewmodel.WeatherListViewModel
import com.google.android.material.snackbar.Snackbar

class WeatherListFragment : Fragment(), OnItemClick {

    companion object {
        fun getInstance() = WeatherListFragment()
    }

    private var isRussian = true

    private var _binding: FragmentWeatherListBinding? = null
    private val binding: FragmentWeatherListBinding
        get() {
            return _binding!!
        }

    lateinit var viewModel: WeatherListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherListBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeatherListViewModel::class.java)
        viewModel.getLiveData().observe(viewLifecycleOwner) { t -> renderData(t) }

        binding.floatingButton.setOnClickListener {
            isRussian = !isRussian
            if (isRussian) {
                viewModel.getWeatherForRussia()
                binding.floatingButton.setImageResource(R.drawable.ic_earth)
            } else {
                viewModel.getWeatherForWorld()
                binding.floatingButton.setImageResource(R.drawable.ic_russia)
            }
        }
        viewModel.getWeatherForRussia()
        binding.floatingButton.setImageResource(R.drawable.ic_earth)
    }

    @SuppressLint("SetTextI18n")
    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Error -> {
                binding.success()
                snackBar(
                    binding.root,
                    resources.getString(R.string.bar_message),
                    Snackbar.LENGTH_LONG,
                    resources.getString(R.string.action_string)
                ) {
                    isRussian = !isRussian
                    if (isRussian) {
                        viewModel.getWeatherForRussia()
                        binding.floatingButton.setImageResource(R.drawable.ic_earth)
                    } else {
                        viewModel.getWeatherForWorld()
                        binding.floatingButton.setImageResource(R.drawable.ic_russia)
                    }
                }
            }
            AppState.Loading -> {
                binding.loading()
            }
            is AppState.SuccessForOneLocation -> {
                binding.success()
            }
            is AppState.SuccessForManyLocations -> {
                binding.success()
                binding.recyclerView.adapter = WeatherListAdapter(appState.weatherList, this)
            }
        }
    }

    override fun onItemClick(weather: Weather) {
        requireActivity().supportFragmentManager.beginTransaction().hide(this).add(
            R.id.container, DetailsFragment.getInstance(weather)
        ).addToBackStack("").commit()
    }

    private fun FragmentWeatherListBinding.loading() {
        fragmentLoadingLayout.visibility = View.VISIBLE
        floatingButton.visibility = View.GONE
    }

    private fun FragmentWeatherListBinding.success() {
        fragmentLoadingLayout.visibility = View.GONE
        floatingButton.visibility = View.VISIBLE
    }

    private fun snackBar(view: View,
        barMessage: String, duration: Int, actionString: String, lambda: (view: View) -> Unit) {
        Snackbar.make(view, barMessage, duration).setAction(actionString, lambda).show()
    }
}