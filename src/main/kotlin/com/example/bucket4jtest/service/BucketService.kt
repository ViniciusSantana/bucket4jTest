package com.example.bucket4jtest.service

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.distributed.BucketProxy
import io.github.bucket4j.distributed.proxy.AbstractProxyManager
import io.github.bucket4j.distributed.proxy.ClientSideConfig
import io.github.bucket4j.dynamodb.v1.DynamoDBProxyManager
import mu.KLogging
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class BucketService {
    companion object : KLogging()

    val endpoint = AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2")
    val db: AmazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(endpoint).build()
    val proxy: AbstractProxyManager<String> =
        DynamoDBProxyManager.stringKey(db, "rateLimit", ClientSideConfig.getDefault())
    val config: BucketConfiguration =
        BucketConfiguration.builder().addLimit(Bandwidth.simple(10, Duration.ofMinutes(1))).build()
    val buckets = mapOf(
        "b1" to proxy.builder().build("b1", config),
        "b2" to proxy.builder().build("b2", config),
        "b3" to proxy.builder().build("b3", config),
    )
    fun tryConsume(bucketName: String, amount: Long): Boolean {
        val bucket = buckets[bucketName]
        logger.info { "Available Tokens : $bucketName - ${bucket!!.availableTokens}" }
        return bucket!!.tryConsume(amount)
    }


}