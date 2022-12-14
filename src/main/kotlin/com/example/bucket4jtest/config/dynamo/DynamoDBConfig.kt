package com.example.bucket4jtest.config.dynamo

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DynamoDBConfig {

    @Bean
    fun dynamoDB() : AmazonDynamoDB {
        val endpoint = AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2")
        return AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(endpoint).build()
    }
}