package com.luseen.ribble.presentation.screen.home

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import com.luseen.ribble.di.scope.PerActivity
import com.luseen.ribble.domain.entity.User
import com.luseen.ribble.domain.interactor.UserInteractor
import com.luseen.ribble.presentation.base_mvp.api.ApiPresenter
import com.luseen.ribble.presentation.navigation.NavigationState
import com.luseen.ribble.presentation.widget.navigation_view.NavigationId
import com.luseen.ribble.utils.emptyString
import javax.inject.Inject

/**
 * Created by Chatikyan on 31.07.2017.
 */
@PerActivity
class HomePresenter @Inject constructor(private val userInteractor: UserInteractor)
    : ApiPresenter<User, HomeContract.View>(), HomeContract.Presenter {

    private var state: NavigationState? = null
    private var isDrawerLocked = false
    private var activeTitle = emptyString()
    private var user: User? = null
    private var currentNavigationSelectedItem = 0

    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (isDrawerLocked) {
            view?.lockDrawer()
        } else {
            view?.unlockDrawer()
        }
        view?.setToolBarTitle(activeTitle)
        user?.let {
            view?.updateDrawerInfo(it)
        }
    }

    override fun onPresenterCreate() {
        super.onPresenterCreate()
        this fetch userInteractor.getAuthenticatedUser()
        view?.openShotFragment()
    }

    override fun onRequestStart() {
    }

    override fun onRequestSuccess(data: User) {
        user = data
        view?.updateDrawerInfo(data)
    }

    override fun onRequestError(errorMessage: String?) {
    }

    override fun handleFragmentChanges(tag: String) {
        view?.setToolBarTitle(tag)
        activeTitle = tag
        if (tag == NavigationId.SHOT_DETAIL.name) {
            isDrawerLocked = true
            view?.lockDrawer()
        } else if (isDrawerLocked) {
            isDrawerLocked = false
            view?.unlockDrawer()
        }

        val checkPosition = when (tag) {
            NavigationId.SHOT.name -> 0
            NavigationId.USER_LIKES.name -> 1
            NavigationId.FOLLOWING.name -> 2
            NavigationId.ABOUT.name -> 3
            else -> currentNavigationSelectedItem
        }

        if (currentNavigationSelectedItem != checkPosition) {
            currentNavigationSelectedItem = checkPosition
            view?.checkNavigationItem(currentNavigationSelectedItem)
        }
    }

    override fun logOut() {
        userInteractor.logOut()
        view?.openLoginActivity()
    }

    override fun saveNavigatorState(state: NavigationState?) {
        this.state = state
    }

    override fun getNavigatorState(): NavigationState? {
        return state
    }
}