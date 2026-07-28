package com.trackapp.smsexpensetracker.smsingestion.data.repository

import android.content.Context
import android.provider.Telephony
import com.trackapp.smsexpensetracker.smsingestion.domain.model.RawSmsMessage
import com.trackapp.smsexpensetracker.smsingestion.domain.model.SmsSource
import com.trackapp.smsexpensetracker.smsingestion.domain.repository.SmsProviderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidSmsProviderRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : SmsProviderRepository {

    override fun queryInbox(since: Instant): Flow<RawSmsMessage> = flow {
        val projection = arrayOf(
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
        )
        val selection = "${Telephony.Sms.DATE} >= ?"
        val selectionArgs = arrayOf(since.toEpochMilli().toString())

        context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${Telephony.Sms.DATE} ASC",
        )?.use { cursor ->
            val addressIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val bodyIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val dateIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)

            while (cursor.moveToNext()) {
                val sender = cursor.getString(addressIndex) ?: continue
                val body = cursor.getString(bodyIndex) ?: continue
                val date = cursor.getLong(dateIndex)

                emit(
                    RawSmsMessage(
                        sender = sender,
                        body = body,
                        timestamp = Instant.ofEpochMilli(date),
                        source = SmsSource.Import,
                    ),
                )
            }
        }
    }.flowOn(Dispatchers.IO)
}
