package com.ucas.firebaseminiprojectcomplete.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class Recipe implements Parcelable{
    private String recipeId;
    private String imageUrl;
    private String videoUrl;
    private String publisherId;
    private String publisherName;
    private String publisherImage;
    private long likesCount;
    private List<String> categories;
    private String title;
    private String steps;
    private String Ingredients;
    private Date createAt;


    public Recipe() {
        // Required empty constructor for Firebase
    }

    public Recipe(String imageUrl, String videoUrl, String publisherName, String publisherImage, int likesCount, List<String> categories, String description) {
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.publisherName = publisherName;
        this.publisherImage = publisherImage;

        this.likesCount = likesCount;
        this.categories = categories;
        this.title = description;
    }

    public Recipe(String videoUrl, String publisherName, int likesCount, List<String> categories, String description) {
        this.videoUrl = videoUrl;
        this.publisherName = publisherName;
        this.likesCount = likesCount;
        this.categories = categories;
        this.title = description;
    }

    protected Recipe(Parcel in) {
        recipeId = in.readString();
        imageUrl = in.readString();
        videoUrl = in.readString();
        publisherId = in.readString();
        publisherName = in.readString();
        publisherImage = in.readString();
        likesCount = in.readLong();
        categories = in.createStringArrayList();
        title = in.readString();
        steps = in.readString();
        Ingredients = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipeId);
        dest.writeString(imageUrl);
        dest.writeString(videoUrl);
        dest.writeString(publisherId);
        dest.writeString(publisherName);
        dest.writeString(publisherImage);
        dest.writeLong(likesCount);
        dest.writeStringList(categories);
        dest.writeString(title);
        dest.writeString(steps);
        dest.writeString(Ingredients);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherImage() {
        return publisherImage;
    }

    public void setPublisherImage(String publisherImage) {
        this.publisherImage = publisherImage;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getIngredients() {
        return Ingredients;
    }

    public void setIngredients(String ingredients) {
        Ingredients = ingredients;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
