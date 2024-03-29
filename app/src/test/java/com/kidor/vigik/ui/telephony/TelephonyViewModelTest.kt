package com.kidor.vigik.ui.telephony

import android.provider.ContactsContract
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.data.telephony.Contact
import com.kidor.vigik.data.telephony.PhoneCall
import com.kidor.vigik.data.telephony.PhoneCallStatus
import com.kidor.vigik.data.telephony.Sms
import com.kidor.vigik.data.telephony.SmsType
import com.kidor.vigik.data.telephony.TelephonyRepository
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [TelephonyViewModel].
 */
class TelephonyViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TelephonyViewModel

    @MockK
    private lateinit var telephonyRepository: TelephonyRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        viewModel = TelephonyViewModel(
            ioDispatcher = mainCoroutineRule.testDispatcher,
            telephonyRepository = telephonyRepository
        )
    }

    @Test
    fun `test emitted state at start`() {
        logTestName()

        val initialState = viewModel.viewState.value

        assertTrue(initialState is TelephonyViewState.CheckPermission, "Initial state")
    }

    @Test
    fun `test all data refreshed when permissions are granted`() {
        logTestName()

        val contactList = listOf(
            Contact(1, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(1337, ContactsContract.CommonDataKinds.Phone.TYPE_HOME),
            Contact(42, ContactsContract.CommonDataKinds.Phone.TYPE_CAR),
            Contact(72, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(987654321, ContactsContract.CommonDataKinds.Phone.TYPE_WORK),
            Contact(0xDEADBEEF, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(4444, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE),
            Contact(30121988, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        )
        val mobileContact = contactList.filter { it.type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE }
        val smsList = listOf(
            Sms("test_content", SmsType.RECEIVED),
            Sms("test_content", SmsType.RECEIVED),
            Sms("test_content", SmsType.SENT)
        )
        val phoneCalls = listOf(
            PhoneCall("0123456789", PhoneCallStatus.MISSED),
            PhoneCall("0123456789", PhoneCallStatus.EMITTED),
            PhoneCall("0123456789", PhoneCallStatus.RECEIVED),
            PhoneCall("0123456789", PhoneCallStatus.REJECTED),
            PhoneCall("0123456789", PhoneCallStatus.RECEIVED),
            PhoneCall("0123456789", PhoneCallStatus.RECEIVED),
            PhoneCall("0123456789", PhoneCallStatus.EMITTED),
        )
        coEvery { telephonyRepository.getAllContact() } returns contactList
        coEvery { telephonyRepository.getAllMobileContact() } returns mobileContact
        coEvery { telephonyRepository.getAllSms() } returns smsList
        coEvery { telephonyRepository.getAllPhoneCalls() } returns phoneCalls

        runTest {
            viewModel.handleAction(TelephonyViewAction.PermissionsGranted)

            coVerify { telephonyRepository.getAllContact() }
            coVerify { telephonyRepository.getAllMobileContact() }
            coVerify { telephonyRepository.getAllSms() }
            coVerify { telephonyRepository.getAllPhoneCalls() }

            viewModel.viewState.value.let { viewState ->
                assertTrue(viewState is TelephonyViewState.ShowData, "View state")
                if (viewState is TelephonyViewState.ShowData) {
                    assertEquals(contactList.size, viewState.totalContactNumber, "All contact number")
                    assertEquals(mobileContact.size, viewState.mobileContactNumber, "Mobile contact number")
                    assertEquals(smsList, viewState.sms, "SMS list")
                    assertEquals(phoneCalls, viewState.phoneCalls, "Phone call list")
                } else {
                    fail()
                }
            }
        }
    }

    @Test
    fun `test sending blank SMS`() {
        logTestName()

        val phoneNumber = "0123456789"
        viewModel.handleAction(TelephonyViewAction.SendSms(phoneNumber, " "))

        // We should not send any SMS if the message is blank
        coVerify(inverse = true) { telephonyRepository.sendSms(phoneNumber, any()) }
    }

    @Test
    fun `test sending valid SMS`() {
        logTestName()

        val phoneNumber = "0123456789"
        val content = "Test message"
        viewModel.handleAction(TelephonyViewAction.SendSms(phoneNumber, content))

        coVerify { telephonyRepository.sendSms(phoneNumber, content) }
    }

    @Test
    fun `test SMS data refreshed after sending valid SMS`() {
        logTestName()

        val phoneNumber = "0123456789"
        val content = "Test message"
        val smsList = listOf(
            Sms("test_content", SmsType.RECEIVED),
            Sms("test_content", SmsType.RECEIVED),
            Sms("test_content", SmsType.SENT)
        )
        coEvery { telephonyRepository.getAllSms() } returns smsList

        runTest {
            viewModel.handleAction(TelephonyViewAction.SendSms(phoneNumber, content))
            delay(1_000)

            coVerify { telephonyRepository.sendSms(phoneNumber, content) }
            coVerify { telephonyRepository.getAllSms() }

            viewModel.viewState.value.let { viewState ->
                assertTrue(viewState is TelephonyViewState.ShowData, "View state")
                if (viewState is TelephonyViewState.ShowData) {
                    assertEquals(smsList, viewState.sms, "SMS list")
                } else {
                    fail()
                }
            }
        }
    }
}
