package com.shubham.test.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@RestController
@RequestMapping("api")
public class HelperController {

    private final S3Client s3Client;

    public HelperController(@Autowired S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @GetMapping
    public void test() {
        String bucketName = "bucketName";
        listAllObjectsInBucket(bucketName);
    }

    public void listAllObjectsInBucket(String bucketName) {
        String nextContinuationToken = null;
        long totalObjects = 0;
        String folderName = "folderName";

        do {
            ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(folderName)
                    .continuationToken(nextContinuationToken);

            ListObjectsV2Response response = s3Client.listObjectsV2(requestBuilder.build());
            nextContinuationToken = response.nextContinuationToken();

            totalObjects += response.contents().stream()
                    .map(S3Object::key)
                    .peek(System.out::println)
                    .reduce(0, (subtotal, element) -> subtotal + 1, Integer::sum);
        } while (nextContinuationToken != null);
        System.out.println("Number of objects in the bucket: " + totalObjects);
    }
}
