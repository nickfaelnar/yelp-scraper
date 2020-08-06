package com.crescendocollective.yelpscraper.service.impl;

import com.crescendocollective.yelpscraper.dto.User;
import com.crescendocollective.yelpscraper.service.GoogleVisionService;
import com.google.cloud.vision.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for using Google Vision API.
 */
@Service
public class GoogleVisionServiceImpl implements GoogleVisionService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleVisionServiceImpl.class);

    @Autowired
    private CloudVisionTemplate cloudVisionTemplate;

    /**
     * Retrieves face annotations based on the user img.
     * @param user target user
     * @throws IOException
     */
    @Override
    public void getUserImgAnnotation(User user) {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ImageSource imgSource = ImageSource.newBuilder().setImageUri(user.getImg()).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            AnnotateImageResponse res = response.getResponsesList().get(0);
            // Assume that we have only at most 1 detected face in the image
            if (!res.hasError()  && !CollectionUtils.isEmpty(res.getFaceAnnotationsList())) {
                FaceAnnotation annotation = res.getFaceAnnotationsList().get(0);
                user.setAngerLikelihood(annotation.getAngerLikelihood());
                user.setJoyLikelihood(annotation.getJoyLikelihood());
                user.setSurpriseLikelihood(annotation.getSurpriseLikelihood());
            }

        } catch (IOException e) {
            logger.error("Failed to call Vision API", e);
            throw new RuntimeException(e);
        }

    }

}
