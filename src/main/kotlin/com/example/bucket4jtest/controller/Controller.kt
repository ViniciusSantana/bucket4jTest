package com.example.bucket4jtest.controller

import com.example.bucket4jtest.service.BucketService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    val service : BucketService
) {

    @GetMapping("/{bucketName}/{amount}")
    fun hello(@PathVariable amount: Long, @PathVariable bucketName : String) =
        service.tryConsume(bucketName, amount)
}