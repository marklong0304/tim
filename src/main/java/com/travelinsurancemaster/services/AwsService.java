package com.travelinsurancemaster.services;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.travelinsurancemaster.web.controllers.VendorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yaroslav on 15.02.2019.
 */
@Service
public class AwsService {

   private static final Logger log = LoggerFactory.getLogger(VendorController.class);

   @Value("${aws.bucketName}")
   private String bucketName;

   @Value("${aws.vendorLogoFolderPath}")
   private String vendorLogoFolderPath;

   @Value("${aws.distributionID}")
   private String distributionID;

   public final static Regions region = Regions.US_EAST_1;

   private final static Long logoSizeLimitInMB = 10L;

   private AWSCredentialsProvider awsCredentialsProvider;

   public AwsService(AWSCredentialsProvider awsCredentialsProvider) {
      this.awsCredentialsProvider = awsCredentialsProvider;
   }

   /**
    * Method invalidates cache on the cloudfront for specified paths
    *
    * @param paths list of paths to files that should be invalidated on cloudfront
    * @return id of invalidation process
    */
   private String invalidateCloudFront(List<String> paths) {
      log.debug("Invalidating cloudfront for paths: " + paths.toString());

      AmazonCloudFront client = AmazonCloudFrontClientBuilder
            .standard()
            .withRegion(region)
            .withCredentials(awsCredentialsProvider)
            .build();

      Paths invalidation_paths = new Paths().withItems(paths).withQuantity(paths.size());
      InvalidationBatch invalidation_batch = new InvalidationBatch(invalidation_paths, "" + System.currentTimeMillis());
      CreateInvalidationRequest invalidation = new CreateInvalidationRequest(distributionID, invalidation_batch);
      CreateInvalidationResult ret = client.createInvalidation(invalidation);

      log.debug("Invalidation created successfully. Invalidation id: " + ret.getInvalidation().getId());
      return ret.getInvalidation().getId();
   }

   /**
    * Method updates vendor logo on s3 and creates invalidation for this logo on cloudfront
    *
    * @param bytes    vendor logo object represented as byte array
    * @param logoName name of vendor logo
    * @return id of invalidation process on cloudfront
    */
   public String syncWithS3VendorLogo(byte[] bytes, String logoName) {
      log.debug("Updating vendors's logo on s3 bucket. Logo name: " + logoName);
      try {
         long contentLengthBytes = bytes.length;
         long contentLengthMB = contentLengthBytes / (1024 * 1024);
         if(contentLengthMB > logoSizeLimitInMB) {
            throw new RuntimeException("Vendor logo size is exceeded");
         }


         AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
               .withRegion(region)
               .withCredentials(awsCredentialsProvider)
               .build();

         ObjectMetadata metadata = new ObjectMetadata();
         metadata.setContentType("image/png");
         metadata.setCacheControl("max-age=2592000");
         metadata.setContentLength(contentLengthBytes);

         // Upload a file as a new object with ContentType and title specified.
         String path = vendorLogoFolderPath + logoName;
         PutObjectRequest request = new PutObjectRequest(bucketName, path, new ByteArrayInputStream(bytes), metadata)
               .withCannedAcl(CannedAccessControlList.PublicRead);

         s3Client.putObject(request);

         return invalidateCloudFront(Collections.singletonList("/" + path));
      } catch (Exception e) {
         String msg = String.format("Unable to update logo with name: %s", logoName);
         log.error(msg, e);
         throw new RuntimeException(e);
      }
   }
}