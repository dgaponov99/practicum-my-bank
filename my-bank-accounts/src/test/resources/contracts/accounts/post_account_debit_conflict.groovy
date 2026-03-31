package contracts.accounts

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description 'Debit account conflict'
    name 'post_account_debit_conflict'

    request {
        method POST()
        url '/user1/debit'
        headers {
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),
                    producer('Bearer test-token')
            )
            contentType(applicationJson())
        }
        body(
                amount: 10000
        )
    }

    response {
        status CONFLICT()
        headers {
            contentType(applicationJson())
        }
        body(
                errors: ['На счету не достаточно средств для списания']
        )
        bodyMatchers {
            jsonPath('$.errors', byType {
                minOccurrence(1)
            })
        }
    }
}
