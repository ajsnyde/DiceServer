package dice.server.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.h2.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service
public class AWSFileSystemStorageService {

	final AmazonS3 s3;
	final String bucketName;
	static final int MAX_FILE_SIZE_READ_BYTES = 1024 * 1024 * 10;

	@Autowired
	public AWSFileSystemStorageService() {
		s3 = AmazonS3ClientBuilder.standard().withRegion(System.getenv("AWS_REGION")).build();
		this.bucketName = System.getenv("AWS_BUCKET_NAME");
		init();
	}

	public AWSFileSystemStorageService(String region, String bucketName) {
		s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
		this.bucketName = bucketName;
		init();
	}

	// key is essentially a filepath (ex. 'foo/bar/test.foo')
	public void put(String key, byte[] bytes) {
		try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(bytes.length);
			s3.putObject(bucketName, key, is, metadata);
		} catch (AmazonServiceException | IOException e) {
			throw new StorageException("Failed to store object in AWS S3 Bucket: " + e.getLocalizedMessage());
		}
	}

	public List<String> getKeys() {
		try {
			ObjectListing listing = s3.listObjects(bucketName);
			List<S3ObjectSummary> summaries = listing.getObjectSummaries();
			return summaries.stream().map(o -> o.getKey()).collect(Collectors.toList());
		} catch (AmazonServiceException e) {
			throw new StorageException("Failed to get file listings: " + e.getErrorMessage());
		}
	}

	public byte[] get(String key) {
		if (!s3.doesObjectExist(bucketName, key))
			throw new StorageFileNotFoundException("File does not exist: " + key);

		try (InputStream is = s3.getObject(bucketName, key).getObjectContent()) {
			return IOUtils.readBytesAndClose(is, MAX_FILE_SIZE_READ_BYTES);
		} catch (SdkClientException | IOException e) {
			throw new StorageException("Failed to get file: " + e.getLocalizedMessage());
		}
	}

	public void delete(String key) {
		try {
			s3.deleteObject(bucketName, key);
		} catch (SdkClientException e) {
			throw new StorageException("Failed to delete file: " + e.getLocalizedMessage());
		}
	}

	public void deleteAll() {
		ObjectListing objectListing = s3.listObjects(bucketName);
		while (true) {
			Iterator<S3ObjectSummary> objIter = objectListing.getObjectSummaries().iterator();
			while (objIter.hasNext()) {
				s3.deleteObject(bucketName, objIter.next().getKey());
			}

			// If the bucket contains many objects, the listObjects() call
			// might not return all of the objects in the first listing. Check to
			// see whether the listing was truncated. If so, retrieve the next page of
			// objects
			// and delete them.
			if (objectListing.isTruncated()) {
				objectListing = s3.listNextBatchOfObjects(objectListing);
			} else {
				break;
			}
		}
	}

	public void init() {
		if (!s3.doesBucketExistV2(bucketName)) {
			s3.createBucket(bucketName);
			// Verify that the bucket was created by retrieving it and checking its
			// location.
			String bucketLocation = s3.getBucketLocation(new GetBucketLocationRequest(bucketName));
			System.out.println("Bucket location: " + bucketLocation);
		}
	}
}
