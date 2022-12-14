package com.example.bucket4jtest.config.dynamo

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException
import com.example.bucket4jtest.service.BucketService
import mu.KLogging
import org.springframework.context.annotation.Configuration

@Configuration
class DynamoTableSetup(private val dynamoDb: AmazonDynamoDB) {

    companion object : KLogging()

    init {
        createRateLimitTable()
    }

    private fun createRateLimitTable() {
        try {
            DynamoDB(dynamoDb).createTable(buildCreateTableRequest()).waitForActive()
            BucketService.logger.info { "DynamoDB Table successfully created" }
        } catch (ex: ResourceInUseException) {
            BucketService.logger.info { "DynamoDB Table already exists" }
        }
    }

    private fun buildCreateTableRequest(): CreateTableRequest? {
        val keyAttribute = AttributeDefinition().withAttributeName("key").withAttributeType("S")
        val keySchema = KeySchemaElement().withAttributeName("key").withKeyType(KeyType.HASH)
        return CreateTableRequest().withTableName("rateLimit")
            .withAttributeDefinitions(listOf(keyAttribute))
            .withKeySchema(listOf(keySchema))
            .withProvisionedThroughput(ProvisionedThroughput(3, 3))
    }
}