package com.marvelousportal.fragments.characters


import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.text.Html
import android.view.*
import android.widget.Toast
import com.marvelousportal.R
import com.marvelousportal.ViewModelFactory
import com.marvelousportal.app.AppController
import com.marvelousportal.base.BaseFragment
import com.marvelousportal.models.Result
import com.marvelousportal.utils.Constant
import com.marvelousportal.viewmodels.CharactersViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_characters.*


/**
 * A simple [Fragment] subclass.
 */
class CharactersFragment : BaseFragment() {

    private var charactersViewModel: CharactersViewModel? = null
    public var mAdapter: CharactersAdapter? = null
    private var characterList: MutableList<Result>? = null
    private var searchCharacterList: MutableList<Result>? = null
    private lateinit var viewModel: CharactersViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    /**
     * This method is used to instantiate the fragment.
     *
     * @return the instance of this fragment.
     */
    fun newInstance(): CharactersFragment {
        return CharactersFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_characters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setupRecyclerView()
        search_character.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchCharacter(query)
                search_character.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun init() {
        /*charactersViewModel = CharactersViewModel(mContext)*/
        characterList = ArrayList()
        searchCharacterList = ArrayList()
        mAdapter = CharactersAdapter(mContext, characterList!!)
        search_character.clearFocus()
        //inisalize view model factory
        /*viewModelFactory = Injection.provideViewModelFactory()
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CharactersViewModel::class.java)*/
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(mContext, 2)
        rv_characters.layoutManager = layoutManager
        rv_characters.adapter = mAdapter
        //Divider
        /*val dividerItemDecoration = DividerItemDecoration(rv_characters.context, layoutManager.orientation)
        rv_characters.addItemDecoration(dividerItemDecoration)*/
        fetchCharactersList()
    }

    private fun fetchCharactersList() {
        characters_view_flipper.displayedChild = 0
        val appController = AppController.create(mContext)
        val usersService = appController.apiService
        val timeStamp = getTimestamp()
        val disposable = usersService?.fetchCharacters(timeStamp, Constant.PUBLIC_KEY, getHash(timeStamp))?.subscribeOn(appController.subscribeScheduler())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ userResponse ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tv_character_attribution_html.text = Html.fromHtml(userResponse.attributionHTML, Html.FROM_HTML_MODE_LEGACY)
            } else {
                tv_character_attribution_html.text = Html.fromHtml(userResponse.attributionHTML)
            }
            characterList?.clear()
            characterList?.addAll(userResponse.data.results)
            mAdapter?.setUserList(characterList)
            characters_view_flipper.displayedChild = 1
        }, {
            characters_view_flipper.displayedChild = 1
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        })
        addSubscription(disposable)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main, menu)
    }

    private fun searchCharacter(query: String) {
        characters_view_flipper.displayedChild = 0
        val appController = AppController.create(mContext)
        val usersService = appController.apiService
        val timeStamp = getTimestamp()
        val disposable = usersService?.searchCharacters(timeStamp, Constant.PUBLIC_KEY, getHash(timeStamp), query)?.subscribeOn(appController.subscribeScheduler())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ userResponse ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tv_character_attribution_html.text = Html.fromHtml(userResponse.attributionHTML, Html.FROM_HTML_MODE_LEGACY)
            } else {
                tv_character_attribution_html.text = Html.fromHtml(userResponse.attributionHTML)
            }
            characterList?.clear()
            characterList?.addAll(userResponse.data.results)
            mAdapter?.setUserList(searchCharacterList)
            characters_view_flipper.displayedChild = 1
        }, {
            characters_view_flipper.displayedChild = 1
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        })
        addSubscription(disposable)
    }
}
