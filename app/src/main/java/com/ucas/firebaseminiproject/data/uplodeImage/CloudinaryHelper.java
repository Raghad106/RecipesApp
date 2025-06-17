package com.ucas.firebaseminiproject.data.uplodeImage;

import android.content.Context;
import android.net.Uri;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryHelper {

    public static Cloudinary getCloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "daw9ulmhh");
        config.put("api_key", "436493325298865");
        config.put("api_secret", "SoSwFQOn8ZURynINOWKzYL1USBg");
        config.put("secure", "true");

        return new Cloudinary(config);
    }

    public interface OnUploadCompleteListener {
        void onSuccess(String imageUrl);
        void onFailure(String error);
    }

    public static void uploadImageToCloudinary(Context context, Uri imageUri, OnUploadCompleteListener listener) {
        Cloudinary cloudinary = CloudinaryHelper.getCloudinary();

        new Thread(() -> {
            try {
                File file = new File(FileUtils.getPath(context, imageUri)); // Ensure this path util is working
                Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");

                listener.onSuccess(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailure(e.getMessage());
            }
        }).start();
    }
}

