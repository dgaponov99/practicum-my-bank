package contracts.accounts

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description 'Get account balance by username'
    name 'get_account_balance_by_username'

    request {
        method GET()
        url '/user1/balance'
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
                balance: 100
        )
    }
}
