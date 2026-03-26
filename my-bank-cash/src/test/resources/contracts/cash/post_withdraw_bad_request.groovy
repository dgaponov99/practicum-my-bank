package contracts.cash

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description 'Withdraw cash with bad request'
    name 'post_withdraw_bad_request'

    request {
        method POST()
        url '/withdraw'
        headers {
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),
                    producer('Bearer test-token')
            )
            contentType(applicationJson())
        }
        body(
                username: 'user1',
                amount: 10000
        )
    }

    response {
        status BAD_REQUEST()
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
