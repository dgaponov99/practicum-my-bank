package contracts.transfer

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description 'Get accounts for transfer'
    name 'get_accounts'

    request {
        method GET()
        url '/accounts?username=user1'
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
        body([
                [
                        username : 'user2',
                        name     : 'Петров Петр',
                ]
        ])
    }
}
