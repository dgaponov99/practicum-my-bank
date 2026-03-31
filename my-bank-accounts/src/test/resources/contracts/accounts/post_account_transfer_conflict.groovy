package contracts.accounts

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description 'Transfer accounts with conflict'
    name 'post_account_transfer_conflict'

    request {
        method POST()
        url '/transfer'
        headers {
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),
                    producer('Bearer test-token')
            )
            contentType(applicationJson())
        }
        body(
                fromUsername: 'user1',
                toUsername: 'user2',
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
