package contracts.transfer

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description 'Transfer accounts'
    name 'post_transfer'

    request {
        method POST()
        url '/'
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
                amount: 100
        )
    }

    response {
        status OK()
    }
}
