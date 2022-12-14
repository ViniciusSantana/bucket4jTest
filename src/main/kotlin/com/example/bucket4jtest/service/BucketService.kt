package com.example.bucket4jtest.service

import io.github.bucket4j.Bucket
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class BucketService(
    private val buckets: Map<String, Bucket>
) {
    companion object : KLogging()

    fun tryConsume(bucketName: String, amount: Long): Boolean {
        val bucket = buckets[bucketName]
        logger.info { "Available Tokens : $bucketName - ${bucket!!.availableTokens}" }
        return bucket!!.tryConsume(amount)
    }


}