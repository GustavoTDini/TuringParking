package com.example.turingparking.user_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.turingparking.R
import com.example.turingparking.adapters.AvatarSelectViewAdapter
import com.example.turingparking.helpers.AvatarClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class SelectAvatarFragment : Fragment(), AvatarClickListener {

    private lateinit var avatarRecyclerView: RecyclerView
    private lateinit var avatarAdapter: AvatarSelectViewAdapter
    private var currentAvatar by Delegates.notNull<Int>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        auth = Firebase.auth
        currentAvatar = arguments?.getInt("currentAvatar") as Int

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val fragmentView =  inflater.inflate(R.layout.fragment_select_avatar, container, false)
        val confirmFab = fragmentView.findViewById<FloatingActionButton>(R.id.confirm_avatar_fab)
        avatarRecyclerView = fragmentView.findViewById(R.id.avatar_recycler_view)
        avatarRecyclerView.layoutManager = GridLayoutManager(context, 2)
        avatarAdapter = AvatarSelectViewAdapter(this, currentAvatar)
        avatarRecyclerView.adapter = avatarAdapter
        confirmFab.setOnClickListener{
            val currentUser = auth.currentUser
            if (currentUser!=null){
                db.collection("users").document(currentUser.uid).update("avatar", currentAvatar).addOnSuccessListener {
                    fragmentView.findNavController().popBackStack()
                }
            }
        }
        return fragmentView
    }

    companion object {
        private const val TAG = "SelectAvatarFragment"
    }

    override fun onAvatarListItemClick(view: View, newAvatar: Int) {
        val previousAvatar = currentAvatar
        currentAvatar = newAvatar
        avatarRecyclerView.adapter?.notifyItemChanged(newAvatar)
        avatarRecyclerView.adapter?.notifyItemChanged(previousAvatar)

    }
}