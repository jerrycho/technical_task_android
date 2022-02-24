package com.sliide.task.ui.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.sliide.task.databinding.UserListItemBinding
import com.sliide.task.model.User
import com.sliide.task.ui.user.listener.OnItemListener


class UserAdapter()
    : ListAdapter<User, UserAdapter.UserViewHolder>(DIFF_CALLBACK) {

    private lateinit var listener: OnItemListener

    open fun setListener(listener: OnItemListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.UserViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position),listener)
    }

    class UserViewHolder(private val binding: UserListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var myUser: User? = null

        fun bind(user: User?, listener: OnItemListener) {
            this.myUser = user

            user?.let {
                binding.tvName.text = myUser!!.name
                binding.tvEmail.text = myUser!!.email
                binding.tvGender.text = myUser!!.gender
                binding.tvStatus.text = myUser!!.status
            }
            listener?.let {
                binding.root.setOnLongClickListener{
                    listener.onItemLongClick(myUser!!)
                    false
                }
            }
        }
    }

    companion object {

        fun from(parent: ViewGroup): UserViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = UserListItemBinding.inflate(layoutInflater, parent, false)
            return UserViewHolder(binding)
        }

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id && oldItem.status == newItem.status
            }
        }
    }

}