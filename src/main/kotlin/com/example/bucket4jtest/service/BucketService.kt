package com.example.bucket4jtest.service

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.BucketConfiguration
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

    init {
        val keyAttribute = AttributeDefinition().withAttributeName("key").withAttributeType("S")
        val keySchema = KeySchemaElement().withAttributeName("key").withKeyType(KeyType.HASH)
        val request = CreateTableRequest().withTableName("rateLimit")
            .withAttributeDefinitions(listOf(keyAttribute))
            .withKeySchema(listOf(keySchema))
            .withProvisionedThroughput(ProvisionedThroughput(3,3))
        try{
            DynamoDB(db).createTable(request).waitForActive()
            logger.info { "DynamoDB Table successfully created" }
        } catch (ex : ResourceInUseException){
            logger.info { "DynamoDB Table already exists" }
        }

    }

    fun tryConsume(bucketName: String, amount: Long): Boolean {
        val bucket = buckets[bucketName]
        logger.info { "Available Tokens : $bucketName - ${bucket!!.availableTokens}" }
        return bucket!!.tryConsume(amount)
    }


}