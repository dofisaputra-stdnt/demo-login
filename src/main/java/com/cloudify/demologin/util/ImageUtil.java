package com.cloudify.demologin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageUtil {

    @Value("${image.max-size}")
    private float maxSizeInMegaByte;

    public byte[] compressImage(MultipartFile file) throws IOException {
        float maxSizeInBytes = maxSizeInMegaByte * 1024 * 1024;
        if (file.getSize() <= maxSizeInBytes) {
            return file.getBytes();
        }

        BufferedImage image = ImageIO.read(file.getInputStream());

        float quality = 0.9f;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while (quality > 0.1f) {
            byteArrayOutputStream.reset();
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null), param);
            }
            writer.dispose();

            if (byteArrayOutputStream.size() <= maxSizeInBytes) {
                break;
            }

            quality -= 0.1f;
        }

        if (byteArrayOutputStream.size() > maxSizeInBytes) {
            throw new IOException("Unable to compress image to under " + maxSizeInBytes + " bytes");
        }

        return byteArrayOutputStream.toByteArray();
    }

}
