package me.sankalpchauhan.positively.service.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity(tableName = "quotes")
public class Quotes implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id=0;
    @ColumnInfo(name = "quoteText")
    @SerializedName("text")
    @Expose
    private String text;
    @ColumnInfo(name = "author")
    @SerializedName("author")
    @Expose
    private String author;

    @ColumnInfo(name = "imagePath")
    private String imageUrl;


    //Empty Constructor
    @Ignore
    public Quotes() {
    }

    /**
     * Room Constructor
     */
    public Quotes(String text, String author, String imageUrl) {
        this.text = text;
        this.author = author;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}