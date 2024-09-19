package org.sample.main.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class S3Operations {

    @Autowired
    private S3AsyncClient s3AsyncClient;

    @Scheduled(fixedDelay = 8, timeUnit = TimeUnit.SECONDS)
    public void sendMessage() throws Exception {
        getAllBuckets();
        checkBucketExists("dkr-s3-volume");
        getAllObjectsFromBucket("dkr-s3-volume");
        //getObjectFromBucket("dkr-s3-volume", "pdfs/93A-1.pdf");
        //addObjectToBucket("dkr-s3-volume");
    }

    private void getAllBuckets() throws Exception {
        ListBucketsResponse listBucketsResponse = s3AsyncClient.listBuckets().get();
        System.out.println("Buckets details");
        listBucketsResponse.buckets().stream().forEach(b ->
                System.out.println("\t" + ">> Name : " + b.name() +", Created : " + b.creationDate()));
    }

    private void checkBucketExists(String bucket) throws Exception {
        ListBucketsResponse listBucketsResponse = s3AsyncClient.listBuckets().get();
        System.out.println("Check for bucket : " + bucket + " returned : " +
        listBucketsResponse.buckets().stream().anyMatch( b -> b.name().equalsIgnoreCase(bucket)));
    }

    private void getAllObjectsFromBucket(String bucket) throws Exception {
        //https://dkr-s3-volume.s3.amazonaws.com/sample.txt.txt
        // s3://dkr-s3-volume/sample.txt.txt
        /*String bucket = "https://dkr-s3-volume.s3.amazonaws.com";*/
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(bucket).build();
        ListObjectsResponse listObjectsResponse = s3AsyncClient.listObjects(listObjectsRequest).get();
        System.out.println("List of Objects in bucket : " + bucket);
        listObjectsResponse.contents().stream().forEach(o ->
                System.out.println("\t" + ">> Key : " + o.key() + " ETag : " + o.eTag() + " Key : " + o.key() + " Owner : " + o.owner()
                + " Size : " + o.size()));
    }

    private void getObjectFromBucket(String bucket, String key) throws Exception {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();
        byte [] fileBytes = s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
                .thenApply(ResponseBytes::asByteArray).join();
        FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\Admin\\Downloads\\objectName.pdf"));
        fos.write(fileBytes);
        fos.close();
    }

    private void addObjectToBucket(String bucket) throws Exception {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key("pdfs/93A-1.pdf").build();
        File f = new File("C:\\Users\\Admin\\Downloads\\93A-1.pdf");
        PutObjectResponse putObjectResponse = s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromFile(f)).get();
        System.out.println("ETag : " + putObjectResponse.eTag() + " Version : " + putObjectResponse.versionId());
    }
}
