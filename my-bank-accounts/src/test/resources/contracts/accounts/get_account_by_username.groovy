package contracts.accounts

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description 'Get account info by username'
    name 'get_account_by_username'

    request {
        method GET()
        url '/user1'
        headers {
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),
                    producer('Bearer test-token')
            )
        }
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
