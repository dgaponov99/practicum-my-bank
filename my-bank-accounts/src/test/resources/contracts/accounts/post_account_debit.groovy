package contracts.accounts

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description 'Debit account'
    name 'post_account_debit'

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
                amount: 100
        )
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(
                username: 'user1',
                name: 'Иванов Иван',
                birthDate: '2000-01-01'
        )
    }
}
