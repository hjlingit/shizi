package com.example.shiziapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * 本地进度持久化（DataStore）。
 * 记录：金币、已解锁阶段、兔子解锁的装扮、已学会的字。
 */
private val Context.dataStore by preferencesDataStore(name = "shizi_progress")

class ProgressRepository(private val context: Context) {

    companion object {
        private val KEY_COINS = intPreferencesKey("coins")
        private val KEY_UNLOCKED_STAGE = intPreferencesKey("unlocked_stage")
        private val KEY_ITEMS = stringPreferencesKey("owned_items")
        private val KEY_LEARNED = stringPreferencesKey("learned_chars")
        private val KEY_EQUIPPED = stringPreferencesKey("equipped_items")
    }

    val coins: Flow<Int> = context.dataStore.data.map { it[KEY_COINS] ?: 0 }

    val unlockedStage: Flow<Int> = context.dataStore.data.map { it[KEY_UNLOCKED_STAGE] ?: 1 }

    val ownedItems: Flow<List<String>> = context.dataStore.data.map {
        splitList(it[KEY_ITEMS])
    }

    val learnedChars: Flow<List<String>> = context.dataStore.data.map {
        splitList(it[KEY_LEARNED])
    }

    /** 当前穿戴的装扮ID列表（每类一个） */
    val equippedItems: Flow<List<String>> = context.dataStore.data.map {
        splitList(it[KEY_EQUIPPED])
    }

    private fun splitList(raw: String?): List<String> =
        (raw ?: "").split("|").filter { s -> s.isNotBlank() }

    suspend fun addCoins(amount: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_COINS] ?: 0
            prefs[KEY_COINS] = current + amount
        }
    }

    /** 花费金币，余额不足返回 false */
    suspend fun spendCoins(amount: Int): Boolean {
        var ok = false
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_COINS] ?: 0
            if (current >= amount) {
                prefs[KEY_COINS] = current - amount
                ok = true
            }
        }
        return ok
    }

    suspend fun unlockStage(stage: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_UNLOCKED_STAGE] ?: 1
            if (stage > current) prefs[KEY_UNLOCKED_STAGE] = stage
        }
    }

    suspend fun addOwnedItem(item: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_ITEMS] ?: ""
            val list = splitList(current)
            if (item !in list) {
                prefs[KEY_ITEMS] = (list + item).joinToString("|")
            }
        }
    }

    suspend fun markLearned(char: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_LEARNED] ?: ""
            val list = splitList(current)
            if (char !in list) {
                prefs[KEY_LEARNED] = (list + char).joinToString("|")
            }
        }
    }

    /** 立即读取已学字数（一次性） */
    suspend fun learnedCountNow(): Int = learnedChars.first().size

    /** 装备一件装扮（同类的先替换） */
    suspend fun equip(itemId: String) {
        val item = RabbitItems.itemById(itemId) ?: return
        context.dataStore.edit { prefs ->
            val current = splitList(prefs[KEY_EQUIPPED])
            // 移除同类别旧装备
            val filtered = current.filter { eqId ->
                RabbitItems.itemById(eqId)?.category != item.category
            }
            val newList = filtered + itemId
            prefs[KEY_EQUIPPED] = newList.joinToString("|")
        }
    }

    /** 卸下某装扮 */
    suspend fun unequip(itemId: String) {
        context.dataStore.edit { prefs ->
            val current = splitList(prefs[KEY_EQUIPPED])
            prefs[KEY_EQUIPPED] = current.filter { it != itemId }.joinToString("|")
        }
    }
}
