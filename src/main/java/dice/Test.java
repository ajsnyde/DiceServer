package dice;

import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

public class Test {
	public static void main(String[] args) {
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		List<Bucket> buckets = s3.listBuckets();
		System.out.println("Your Amazon S3 buckets are:");
		for (Bucket b : buckets) {
			System.out.println("* " + b.getName());
		}
		try {
			s3.putObject(buckets.get(0).getName(), "DIRECTORY/KEY", "CONTENTS OF THE FILE2");
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
	}
}
