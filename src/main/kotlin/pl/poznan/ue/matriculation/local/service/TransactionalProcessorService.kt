package pl.poznan.ue.matriculation.local.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionalProcessorService {

    @Transactional(propagation = Propagation.REQUIRES_NEW, transactionManager = "transactionManager")
    fun processInNewTransaction(work: () -> Unit) {
        work.invoke()
    }
}
