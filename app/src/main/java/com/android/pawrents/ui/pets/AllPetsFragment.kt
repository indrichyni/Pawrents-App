package com.android.pawrents.ui.pets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.pawrents.R
import com.android.pawrents.databinding.FragmentAllPetsBinding
import com.android.pawrents.databinding.FragmentHomeBinding
import com.android.pawrents.ui.LoadingDialog
import com.android.pawrents.ui.home.HomePetsAdapter
import com.android.pawrents.ui.home.PetsViewModel


class AllPetsFragment : Fragment() {

    private var _binding: FragmentAllPetsBinding? = null
    private val binding get() = _binding!!
    private val petsViewModel: PetsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllPetsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadingDialog = LoadingDialog(requireContext())

        val isSearchMode = AllPetsFragmentArgs.fromBundle(arguments as Bundle).isSearchMode

        val timePetAdapter = HomePetsAdapter{
            val go = AllPetsFragmentDirections.actionAllPetsFragmentToPetDetailFragment(it)
            findNavController().navigate(go)
        }
        binding.rvAllPets.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = timePetAdapter
            isNestedScrollingEnabled = false
        }

//        petsViewModel.petsSortedByTimestamp.observe(viewLifecycleOwner){
//            timePetAdapter.submitList(it)
//        }

        petsViewModel.petsFiltered.observe(viewLifecycleOwner){
            timePetAdapter.submitList(it)
        }

        petsViewModel.isLoading.observe(viewLifecycleOwner){
            if(it) loadingDialog.startLoadingDialog() else loadingDialog.dismissDialog()
        }

        petsViewModel.errorMsg.observe(viewLifecycleOwner){
            makeToast(it)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if(isSearchMode){
            binding.svPets.isIconified = false
            binding.svPets.requestFocus()
        }

        binding.chipGroup.setOnCheckedStateChangeListener { chipGroup, ints ->
            when {
                binding.allCategory.isChecked -> {
                    petsViewModel.filterPetsByCategory("all")
                }
                binding.catCategory.isChecked -> {
                    petsViewModel.filterPetsByCategory("Cat")
                }
                binding.dogCategory.isChecked -> {
                    petsViewModel.filterPetsByCategory("Dog")
                }
                binding.birdCategory.isChecked -> {
                    petsViewModel.filterPetsByCategory("Bird")
                }
                binding.rabbitCategory.isChecked -> {
                    petsViewModel.filterPetsByCategory("Rabbit")
                }
                binding.hamsterCategory.isChecked -> {
                    petsViewModel.filterPetsByCategory("Hamster")
                }
            }
        }

        binding.svPets.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { petsViewModel.searchPets(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { petsViewModel.searchPets(it) }
                return true
            }
        })

    }

    private fun makeToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}