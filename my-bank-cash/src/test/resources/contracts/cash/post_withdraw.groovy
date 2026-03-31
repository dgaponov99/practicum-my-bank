package contracts.cash

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description 'Withdraw cash'
    name 'post_withdraw'

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
                amount: 100
        )
    }

    response {
        status OK()
    }
}
