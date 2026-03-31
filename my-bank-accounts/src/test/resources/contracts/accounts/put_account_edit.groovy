package contracts.accounts

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description 'Edit account'
    name 'put_account_edit'

    request {
        method PUT()
        url '/user1'
        headers {
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),
                    producer('Bearer test-token')
            )
            contentType(applicationJson())
        }
        body(
                name: 'Иванов Иван',
                birthDate: '2000-01-01'
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
