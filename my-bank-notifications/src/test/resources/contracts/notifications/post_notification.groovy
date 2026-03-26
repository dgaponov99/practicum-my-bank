package contracts.notifications

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'Notification'
    name 'post_notification'

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
                username: 'user1',
                message: 'Текст уведомления'
        )
    }

    response {
        status OK()
    }
}
