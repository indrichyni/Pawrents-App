package com.android.pawrents.ui.home

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.pawrents.R
import com.android.pawrents.data.model.Knowledge
import com.android.pawrents.databinding.FragmentAddBinding
import com.android.pawrents.databinding.FragmentHomeBinding
import com.android.pawrents.ui.LoadingDialog
import com.google.android.material.chip.Chip


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val petsViewModel: PetsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadingDialog = LoadingDialog(requireContext())

        val knowledgeList = ArrayList<Knowledge>()
        knowledgeList.add(Knowledge(1, "Pet Nutrition 101", R.drawable.img_knowledge_one))
        knowledgeList.add(Knowledge(2, "Regular Vet Check-Ups", R.drawable.img_knowledge_two))
        knowledgeList.add(Knowledge(3, "Recognizing Pet Illness", R.drawable.img_knowledge_three))
        knowledgeList.add(Knowledge(4, "Essential Pet Vaccinations", R.drawable.img_knowledge_four))

      
        petsViewModel.isLoading.observe(viewLifecycleOwner){
            if(it){
                loadingDialog.startLoadingDialog()
            } else{
                loadingDialog.dismissDialog()
            }
        }

        val timePetAdapter = HomePetsAdapter{ pet ->
            val go = HomeFragmentDirections.actionHomeFragmentToPetDetailFragment(pet)
            findNavController().navigate(go)
        }
        val randomPetAdapter = HomePetsAdapter{pet ->
            val go = HomeFragmentDirections.actionHomeFragmentToPetDetailFragment(pet)
            findNavController().navigate(go)
        }
        binding.rvNewPets.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = timePetAdapter
            isNestedScrollingEnabled = false
        }

        binding.rvRecommendedPets.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = randomPetAdapter
            isNestedScrollingEnabled = false
        }


        val knowledgeAdapter = HomeKnowledgeAdapter()
        binding.rvKnowledge.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = knowledgeAdapter
            isNestedScrollingEnabled = false
        }

        knowledgeAdapter.submitList(knowledgeList)

        petsViewModel.petsSortedByTimestamp.observe(viewLifecycleOwner){
            if(it.size >= 2){
                val onlyTwo = it.take(2)
                timePetAdapter.submitList(onlyTwo)
            }else{
                timePetAdapter.submitList(it)
            }

        }

        petsViewModel.errorMsg.observe(viewLifecycleOwner){
            makeToast(it)
        }

        petsViewModel.petsRandomOrder.observe(viewLifecycleOwner){
            if(it.size >= 2){
                val onlyTwo = it.take(2)
                randomPetAdapter.submitList(onlyTwo)
            }else{
                randomPetAdapter.submitList(it)
            }
        }

        binding.btnViewAllNew.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_allPetsFragment)
        }

        binding.btnViewAllRecom.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_allPetsFragment)
        }

        binding.catCategory.isEnabled = false
        binding.dogCategory.isEnabled = false
        binding.birdCategory.isEnabled = false
        binding.rabbitCategory.isEnabled = false
        binding.hamsterCategory.isEnabled = false
        binding.allCategory.isEnabled = false

        binding.chipScroll.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_allPetsFragment)
        }

        binding.svToClick.setOnClickListener {
            val toAllPets = HomeFragmentDirections.actionHomeFragmentToAllPetsFragment()
            toAllPets.isSearchMode = true
            findNavController().navigate(toAllPets)

        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.svButton.setOnClickListener{
            val toAllPets = HomeFragmentDirections.actionHomeFragmentToAllPetsFragment()
            toAllPets.isSearchMode = true
            findNavController().navigate(toAllPets)
        }
    }

    private fun makeToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}