package io.helikon.subvt.data.repository

import androidx.lifecycle.LiveData
import io.helikon.subvt.data.dao.NetworkDAO
import io.helikon.subvt.data.model.Network

class NetworkRepository(private val dao: NetworkDAO) {
    val allNetworks: LiveData<List<Network>> = dao.getAll()

    suspend fun add(network: Network) {
        dao.insert(network)
    }

    suspend fun addAll(networks: List<Network>) {
        dao.insertAll(networks)
    }

    fun findById(id: Long): Network? = dao.findById(id)
}
