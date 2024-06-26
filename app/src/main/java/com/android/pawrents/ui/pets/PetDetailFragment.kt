package com.android.pawrents.ui.pets

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.pawrents.R
import com.android.pawrents.data.model.Pet
import com.android.pawrents.databinding.FragmentAllPetsBinding
import com.android.pawrents.databinding.FragmentPetDetailBinding
import com.android.pawrents.ui.LoadingDialog
import com.android.pawrents.ui.UserViewModel
import com.android.pawrents.ui.home.PetsViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class PetDetailFragment : Fragment() {

    private var _binding: FragmentPetDetailBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPetDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentPet = PetDetailFragmentArgs.fromBundle(arguments as Bundle).petsToBeShown
        initView(currentPet)
        initOwner(currentPet)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initOwner(currentPet: Pet) {
        val loadingDialog = LoadingDialog(requireContext())

        userViewModel.isLoading.observe(viewLifecycleOwner){
            if(it) loadingDialog.startLoadingDialog() else loadingDialog.dismissDialog()
        }

        userViewModel.errorMsg.observe(viewLifecycleOwner){
            makeToast(it)
        }

        userViewModel.fetchAllUsers(currentPet)
        userViewModel.petCreator.observe(viewLifecycleOwner){
            Log.d("PetViewModel", it.toString())
            if(it!=null){
                binding.tvUserName.text = it.username
                Glide.with(requireContext()).load(it.profilePic).override(150,150).diskCacheStrategy(
                    DiskCacheStrategy.NONE).placeholder(R.drawable.img_default_user).centerCrop().skipMemoryCache(true).into(binding.ivUser)
            }
        }
    }

    private fun initView(currentPet: Pet) {
        binding.apply {
            Glide.with(requireContext()).load(currentPet.photoLink).diskCacheStrategy(
                DiskCacheStrategy.NONE).override(600,600).into(ivPetDetail)
            tvPetName.text = currentPet.name
            tvPetCategory.text = "(${currentPet.category})"
            tvPetLocation.text = currentPet.location
            tvPetGender.text = currentPet.gender
            tvPetAge.text = "${currentPet.age} Month"
            tvPetWeight.text = "${currentPet.weight} Kg"
            tvPetBreed.text = currentPet.breed
            tvPetColor.text = currentPet.color
            tvPetVaccine.text = currentPet.vaccine
            tvOwner.text = "${currentPet.name} owner"
            tvPetDesc.text = currentPet.description

            if(currentPet.userId == FirebaseAuth.getInstance().currentUser?.uid ?:""){
                btnAdopt.visibility = View.INVISIBLE
                contactLayout.visibility = View.GONE
            }else{
                btnAdopt.visibility = View.VISIBLE
                contactLayout.visibility = View.VISIBLE
            }
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