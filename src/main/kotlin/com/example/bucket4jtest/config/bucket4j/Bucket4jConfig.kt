package com.example.bucket4jtest.config.bucket4j

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.distributed.proxy.ClientSideConfig
import io.github.bucket4j.distributed.proxy.ProxyManager
import io.github.bucket4j.dynamodb.v1.DynamoDBProxyManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class Bucket4jConfig(private val dynamoDB: AmazonDynamoDB) {

    @Bean
    fun buckets(): Map<String, Bucket> {
        val proxy = proxy()
        val config = config()
        return mapOf(
            "b1" to proxy.builder().build("b1", config),
            "b2" to proxy.builder().build("b2", config),
            "b3" to proxy.builder().build("b3", config),
        )
    }

    private fun proxy(): ProxyManager<String> =
        DynamoDBProxyManager.stringKey(dynamoDB, "rateLimit", ClientSideConfig.getDefault())

    private fun config() =
        BucketConfiguration.builder().addLimit(Bandwidth.simple(10, Duration.ofMinutes(1))).build()
}