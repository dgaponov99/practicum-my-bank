package com.github.dgaponov99.practicum.mybank.accounts.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@RefreshScope
@ConfigurationProperties(prefix = "outbox")
public class OutboxProperties {

    private long baseDelay = 5;
    private long maxDelay = 3600;

    private int batchSize = 50;

}
