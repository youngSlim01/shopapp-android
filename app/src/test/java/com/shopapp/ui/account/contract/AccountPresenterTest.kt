package com.shopapp.ui.account.contract

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.verify
import com.shopapp.domain.interactor.account.GetCustomerUseCase
import com.shopapp.domain.interactor.account.SessionCheckUseCase
import com.shopapp.domain.interactor.account.ShopInfoUseCase
import com.shopapp.domain.interactor.account.SignOutUseCase
import com.shopapp.gateway.entity.Customer
import com.shopapp.gateway.entity.Error
import com.shopapp.gateway.entity.Shop
import com.shopapp.util.RxImmediateSchedulerRule
import com.shopapp.util.ext.mock
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AccountPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Mock
    private lateinit var view: AccountView

    @Mock
    private lateinit var sessionCheckUseCase: SessionCheckUseCase

    @Mock
    private lateinit var signOutUseCase: SignOutUseCase

    @Mock
    private lateinit var shopInfoUseCase: ShopInfoUseCase

    @Mock
    private lateinit var getCustomerUseCase: GetCustomerUseCase

    @Mock
    private lateinit var shop: Shop

    @Mock
    private lateinit var customer: Customer

    private lateinit var presenter: AccountPresenter

    @Before
    fun setUpTest() {
        MockitoAnnotations.initMocks(this)
        presenter = AccountPresenter(sessionCheckUseCase, signOutUseCase, shopInfoUseCase, getCustomerUseCase)
        presenter.attachView(view)
        sessionCheckUseCase.mock()
        signOutUseCase.mock()
        shopInfoUseCase.mock()
        getCustomerUseCase.mock()
    }

    //is authorize check

    @Test
    fun shouldCallUseCaseOnAuthorizationCheck() {
        given(sessionCheckUseCase.buildUseCaseSingle(any())).willReturn(Single.just(true))
        presenter.isAuthorized()
        verify(sessionCheckUseCase).execute(any(), any(), any())
    }

    @Test
    fun shouldNotifyViewOnAuthCheck() {
        given(sessionCheckUseCase.buildUseCaseSingle(any())).willReturn(Single.just(true))
        presenter.isAuthorized()
        verify(view).showContent(true)

        given(sessionCheckUseCase.buildUseCaseSingle(any())).willReturn(Single.just(false))
        presenter.isAuthorized()
        verify(view).showContent(false)
    }

    @Test
    fun shouldShowMessageOnAuthCheckNonCriticalError() {
        given(sessionCheckUseCase.buildUseCaseSingle(any())).willReturn(Single.error(Error.NonCritical("ErrorMessage")))
        presenter.isAuthorized()

        val inOrder = inOrder(view, sessionCheckUseCase)
        inOrder.verify(sessionCheckUseCase).execute(any(), any(), any())
        inOrder.verify(view).showMessage("ErrorMessage")
    }

    @Test
    fun shouldShowErrorOnAuthCheckContentError() {
        given(sessionCheckUseCase.buildUseCaseSingle(any())).willReturn(Single.error(Error.Content(false)))
        presenter.isAuthorized()

        val inOrder = inOrder(view, sessionCheckUseCase)
        inOrder.verify(sessionCheckUseCase).execute(any(), any(), any())
        inOrder.verify(view).showError(false)
    }

    //log out

    @Test
    fun shouldCallUseCaseOnSignOut() {
        given(signOutUseCase.buildUseCaseCompletable(any())).willReturn(Completable.complete())
        presenter.signOut()
        verify(signOutUseCase).execute(any(), any(), any())
    }

    @Test
    fun shouldNotifyViewOnSignOut() {
        given(signOutUseCase.buildUseCaseCompletable(any())).willReturn(Completable.complete())
        presenter.signOut()
        verify(view).signedOut()
    }

    @Test
    fun shouldShowMessageOnSignOutNonCriticalError() {
        given(signOutUseCase.buildUseCaseCompletable(any())).willReturn(Completable.error(Error.NonCritical("ErrorMessage")))
        presenter.signOut()

        val inOrder = inOrder(view, signOutUseCase)
        inOrder.verify(signOutUseCase).execute(any(), any(), any())
        inOrder.verify(view).showMessage("ErrorMessage")
    }

    @Test
    fun shouldShowErrorOnSignOutContentError() {
        given(signOutUseCase.buildUseCaseCompletable(any())).willReturn(Completable.error(Error.Content(false)))
        presenter.signOut()

        val inOrder = inOrder(view, signOutUseCase)
        inOrder.verify(signOutUseCase).execute(any(), any(), any())
        inOrder.verify(view).showError(false)
    }

    //get shop info

    @Test
    fun shouldCallUseCaseOnShopInfoRequest() {
        given(shopInfoUseCase.buildUseCaseSingle(any())).willReturn(Single.just(shop))
        presenter.getShopInfo()
        verify(shopInfoUseCase).execute(any(), any(), any())
    }

    @Test
    fun shouldNotifyViewOnShopInfoRequest() {
        given(shopInfoUseCase.buildUseCaseSingle(any())).willReturn(Single.just(shop))
        presenter.getShopInfo()
        verify(view).shopReceived(shop)
    }

    @Test
    fun shouldShowMessageOnShopInfoRequestNonCriticalError() {
        given(shopInfoUseCase.buildUseCaseSingle(any())).willReturn(Single.error(Error.NonCritical("ErrorMessage")))
        presenter.getShopInfo()

        val inOrder = inOrder(view, shopInfoUseCase)
        inOrder.verify(shopInfoUseCase).execute(any(), any(), any())
        inOrder.verify(view).showMessage("ErrorMessage")
    }

    @Test
    fun shouldShowErrorOnShopInfoRequestContentError() {
        given(shopInfoUseCase.buildUseCaseSingle(any())).willReturn(Single.error(Error.Content(false)))
        presenter.getShopInfo()

        val inOrder = inOrder(view, shopInfoUseCase)
        inOrder.verify(shopInfoUseCase).execute(any(), any(), any())
        inOrder.verify(view).showError(false)
    }

    // get customer

    @Test
    fun shouldCallUseCaseOnGetCustomer() {
        given(getCustomerUseCase.buildUseCaseSingle(any())).willReturn(Single.just(customer))
        presenter.getCustomer()
        verify(getCustomerUseCase).execute(any(), any(), any())
    }

    @Test
    fun shouldNotifyViewOnGetCustomer() {
        given(getCustomerUseCase.buildUseCaseSingle(any())).willReturn(Single.just(customer))
        presenter.getCustomer()
        verify(view).customerReceived(customer)
    }

    @Test
    fun shouldReturnNullCustomerOnGetCustomerNonCriticalError() {
        given(getCustomerUseCase.buildUseCaseSingle(any())).willReturn(Single.error(Error.NonCritical("ErrorMessage")))
        presenter.getCustomer()

        val inOrder = inOrder(view, getCustomerUseCase)
        inOrder.verify(getCustomerUseCase).execute(any(), any(), any())
        inOrder.verify(view).customerReceived(null)
    }

    @Test
    fun shouldReturnNullCustomerOnGetCustomerContentError() {
        given(getCustomerUseCase.buildUseCaseSingle(any())).willReturn(Single.error(Error.Content(false)))
        presenter.getCustomer()

        val inOrder = inOrder(view, getCustomerUseCase)
        inOrder.verify(getCustomerUseCase).execute(any(), any(), any())
        inOrder.verify(view).customerReceived(null)
    }

    @After
    fun tearDown() {
        presenter.detachView(false)
    }

}
