package com.example.app_vehiculos


import com.amazonaws.auth.BasicSessionCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient

object AwsConfig {

    private const val ACCESS_KEY = ""
    private const val SECRET_KEY = ""
    private const val SESSION_TOKEN = ""
    private val region: Region = Region.US_EAST_1

    private val credentials = AwsSessionCredentials.create(ACCESS_KEY, SECRET_KEY, SESSION_TOKEN)
    private val credentialsProvider = StaticCredentialsProvider.create(credentials)

    val dynamoDbClient: DynamoDbClient by lazy {
        DynamoDbClient.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .httpClient(UrlConnectionHttpClient.builder().build())
            .build()
    }

    val s3Client: AmazonS3Client
        get() {
            val sessionCredentials = BasicSessionCredentials(
                ACCESS_KEY,
                SECRET_KEY,
                SESSION_TOKEN
            )
            val s3 = AmazonS3Client(sessionCredentials)
            s3.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_EAST_1))
            return s3
        }
}