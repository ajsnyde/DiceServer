package dice;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dice.server.app.Utils;

@Entity
@Table(name = "diebatch")
public class DieBatch {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long id;
	@Lob
	public ArrayList<Long> dice;
	@JsonIgnore
	@Transient
	public ArrayList<Image> faces; // 6 compilations of each face of each die - each to be printed

	public DieBatch() {
		dice = new ArrayList<Long>();
	}

	public ResponseEntity<Resource> zip(String zipName) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			for (Image image : faces) {
				ZipEntry ze = new ZipEntry("image" + image.hashCode() + ".png");
				zos.putNextEntry(ze);
				zos.write(Utils.ImageToByteArray(image));
				zos.closeEntry();
			}
			zos.close();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipName + "\"")
					.body(new ByteArrayResource(imageInByte));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
