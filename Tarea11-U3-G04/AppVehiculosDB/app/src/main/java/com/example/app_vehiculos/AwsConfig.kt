package com.example.app_vehiculos

import software.amazon.awssdk.auth.credentials.AwsSessionCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

object AwsConfig {

    private const val ACCESS_KEY = ""
    private const val SECRET_KEY = ""
    private const val SESSION_TOKEN = ""
    val region: Region = Region.US_EAST_1


    val dynamoDbClient: DynamoDbClient by lazy {
        val credentials = AwsSessionCredentials.create(ACCESS_KEY, SECRET_KEY, SESSION_TOKEN)

        DynamoDbClient.builder()
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .httpClient(UrlConnectionHttpClient.builder().build())
            .build()
    }
}