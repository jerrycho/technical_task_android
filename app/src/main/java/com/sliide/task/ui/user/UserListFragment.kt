package com.sliide.task.ui.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

import dagger.hilt.android.AndroidEntryPoint
import com.sliide.task.R
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.sliide.task.base.ViewState
import com.sliide.task.databinding.FragmentUserListBinding
import com.sliide.task.model.User
import com.sliide.task.ui.user.adapter.UserAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.text.InputType

import android.widget.LinearLayout

import android.widget.EditText
import androidx.core.view.updatePadding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sliide.task.ui.user.listener.OnItemListener


@AndroidEntryPoint
class UserListFragment: Fragment(R.layout.fragment_user_list){

    private val viewModel by viewModels<UserListViewModel>()
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentUserListBinding.bind(view)

        binding.swipeToRefresh.apply {
            setOnRefreshListener {
                isRefreshing = false
                viewModel.refresh()
            }
        }

        //setup recycle view
        _binding?.userList?.apply {
            userAdapter = UserAdapter()
            setHasFixedSize(true)

            userAdapter.setListener(object : OnItemListener {
                override fun onItemLongClick(item : User){
                    showDeleteDialog(item)
                }
            })
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())

            //load more
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(!recyclerView.canScrollVertically(1)
                        && userAdapter.itemCount>0
                    ) {
                        viewModel.getNextPage()
                    }
                }
            })
        }

        binding.loadingCircularProgressIndicator.visibility = View.GONE

        //list
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.getUserListState.collect { viewState->
                    when (viewState) {
                        is ViewState.Success ->{
                            binding.loadingCircularProgressIndicator.visibility = View.GONE
                        }
                        is ViewState.Failure ->{
                            binding.loadingCircularProgressIndicator.visibility = View.GONE
                            displayDialog(viewState.errorAny)
                        }
                        is ViewState.Loading->{
                            binding.loadingCircularProgressIndicator.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        //Insert User
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createUserState.collect { viewState->
                    when (viewState) {
                        is ViewState.Success ->{
                            binding.loadingCircularProgressIndicator.visibility = View.GONE
                            binding.userList.layoutManager?.scrollToPosition(0)
                        }

                        is ViewState.Failure ->{
                            binding.loadingCircularProgressIndicator.visibility = View.GONE
                            var message = ""
                            if (viewState.errorAny  is Int)
                                message = getString(viewState.errorAny )
                            else if (viewState.errorAny  is String)
                                message = viewState.errorAny
                            displayDialogAndShowCreateDialog(message)
                        }
                        is ViewState.Loading->{
                            binding.loadingCircularProgressIndicator.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        //Delete User
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.deleteUserState.collect { viewState->
                    when (viewState) {
                        is ViewState.Success ->{
                            binding.loadingCircularProgressIndicator.visibility = View.GONE
                        }
                        is ViewState.Failure ->{
                            binding.loadingCircularProgressIndicator.visibility = View.GONE
                            displayDialog(viewState.errorAny)
                        }
                        is ViewState.Loading->{
                            binding.loadingCircularProgressIndicator.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        //control list
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.userListState.collect { viewState->
                    when (viewState) {
                        is ViewState.Success ->{
                            userAdapter.submitList(viewState.data)
                            binding.loadingCircularProgressIndicator.visibility = View.GONE
                        }
                        is ViewState.Clean ->{
                            userAdapter.submitList(null)
                        }
                        is ViewState.Loading->{
                            binding.loadingCircularProgressIndicator.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

//        viewModel.userList.observe(viewLifecycleOwner, {
//            it.let {
//                    list->  userAdapter?.let {
//                                it.submitList(list)
//                            }
//            }
//        })

        binding.fabAdd.setOnClickListener {
            showCreateUserDialog()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showCreateUserDialog(){
        _binding?.root?.context.let {
            val builder = MaterialAlertDialogBuilder(_binding?.root!!.context)

            builder.setTitle(R.string.create_user)

            val edtName = EditText(_binding?.root!!.context)
            edtName.setHint(R.string.hint_name)

            val edtEmail = EditText(_binding?.root!!.context)
            edtEmail.setHint(R.string.hint_email)
            edtEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)

            viewModel?.user?.value?.let {
                edtName.setText(viewModel?.user?.value!!.name)
                edtEmail.setText(viewModel?.user?.value!!.email)
            }

            val lay = LinearLayout(_binding?.root!!.context)
            lay.orientation = LinearLayout.VERTICAL
            lay.updatePadding(
                left = 50,
                right = 50
            )
            lay.addView(edtName)
            lay.addView(edtEmail)
            builder.setView(lay)

            builder.setPositiveButton(
                android.R.string.ok
            ) { dialog, whichButton -> //get the two inputs
                val name = edtName.text.toString()
                val email = edtEmail.text.toString()
                viewModel.createUser(name, email)
            }

            builder.setNegativeButton(
                android.R.string.cancel
            ) { dialog, whichButton ->
                dialog.cancel()
                viewModel.clearUser()
            }
            builder.show()
        }
    }

    fun showDeleteDialog(user:User){
        user?.let {
            _binding?.root?.let {

                MaterialAlertDialogBuilder(_binding?.root!!.context)
                    .setMessage(getString(R.string.confirm_remove_user))
                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                        dialog.dismiss()
                        viewModel.deleteUser(user)
                    }
                    .setNegativeButton(android.R.string.no) { dialogInterface, which ->

                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }

    private fun displayDialog(mess: Any){
        var message = ""
        if (mess is Int)
            message = getString(mess)
        else if (mess is String)
            message = mess

        _binding?.root?.let {
            MaterialAlertDialogBuilder(_binding?.root!!.context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun displayDialogAndShowCreateDialog(message: String){
        _binding?.root?.let {
            MaterialAlertDialogBuilder(_binding?.root!!.context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    showCreateUserDialog()
                }
                .setCancelable(false)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}