
package me.sankalpchauhan.positively.service.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Podcast implements Serializable {

    @SerializedName("audio")
    @Expose
    private String audio;
    @SerializedName("audio_length_sec")
    @Expose
    private Integer audioLengthSec;
    @SerializedName("rss")
    @Expose
    private String rss;
    @SerializedName("description_highlighted")
    @Expose
    private String descriptionHighlighted;
    @SerializedName("description_original")
    @Expose
    private String descriptionOriginal;
    @SerializedName("title_highlighted")
    @Expose
    private String titleHighlighted;
    @SerializedName("title_original")
    @Expose
    private String titleOriginal;
    @SerializedName("podcast_title_highlighted")
    @Expose
    private String podcastTitleHighlighted;
    @SerializedName("podcast_title_original")
    @Expose
    private String podcastTitleOriginal;
    @SerializedName("publisher_highlighted")
    @Expose
    private String publisherHighlighted;
    @SerializedName("publisher_original")
    @Expose
    private String publisherOriginal;
    @SerializedName("transcripts_highlighted")
    @Expose
    private List<Object> transcriptsHighlighted = null;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("itunes_id")
    @Expose
    private Integer itunesId;
    @SerializedName("pub_date_ms")
    @Expose
    private long pubDateMs;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("podcast_id")
    @Expose
    private String podcastId;
    @SerializedName("genre_ids")
    @Expose
    private List<Integer> genreIds = null;
    @SerializedName("listennotes_url")
    @Expose
    private String listennotesUrl;
    @SerializedName("podcast_listennotes_url")
    @Expose
    private String podcastListennotesUrl;
    @SerializedName("explicit_content")
    @Expose
    private Boolean explicitContent;
    @SerializedName("link")
    @Expose
    private String link;

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Integer getAudioLengthSec() {
        return audioLengthSec;
    }

    public void setAudioLengthSec(Integer audioLengthSec) {
        this.audioLengthSec = audioLengthSec;
    }

    public String getRss() {
        return rss;
    }

    public void setRss(String rss) {
        this.rss = rss;
    }

    public String getDescriptionHighlighted() {
        return descriptionHighlighted;
    }

    public void setDescriptionHighlighted(String descriptionHighlighted) {
        this.descriptionHighlighted = descriptionHighlighted;
    }

    public String getDescriptionOriginal() {
        return descriptionOriginal;
    }

    public void setDescriptionOriginal(String descriptionOriginal) {
        this.descriptionOriginal = descriptionOriginal;
    }

    public String getTitleHighlighted() {
        return titleHighlighted;
    }

    public void setTitleHighlighted(String titleHighlighted) {
        this.titleHighlighted = titleHighlighted;
    }

    public String getTitleOriginal() {
        return titleOriginal;
    }

    public void setTitleOriginal(String titleOriginal) {
        this.titleOriginal = titleOriginal;
    }

    public String getPodcastTitleHighlighted() {
        return podcastTitleHighlighted;
    }

    public void setPodcastTitleHighlighted(String podcastTitleHighlighted) {
        this.podcastTitleHighlighted = podcastTitleHighlighted;
    }

    public String getPodcastTitleOriginal() {
        return podcastTitleOriginal;
    }

    public void setPodcastTitleOriginal(String podcastTitleOriginal) {
        this.podcastTitleOriginal = podcastTitleOriginal;
    }

    public String getPublisherHighlighted() {
        return publisherHighlighted;
    }

    public void setPublisherHighlighted(String publisherHighlighted) {
        this.publisherHighlighted = publisherHighlighted;
    }

    public String getPublisherOriginal() {
        return publisherOriginal;
    }

    public void setPublisherOriginal(String publisherOriginal) {
        this.publisherOriginal = publisherOriginal;
    }

    public List<Object> getTranscriptsHighlighted() {
        return transcriptsHighlighted;
    }

    public void setTranscriptsHighlighted(List<Object> transcriptsHighlighted) {
        this.transcriptsHighlighted = transcriptsHighlighted;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getItunesId() {
        return itunesId;
    }

    public void setItunesId(Integer itunesId) {
        this.itunesId = itunesId;
    }

    public long getPubDateMs() {
        return pubDateMs;
    }

    public void setPubDateMs(Integer pubDateMs) {
        this.pubDateMs = pubDateMs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(String podcastId) {
        this.podcastId = podcastId;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getListennotesUrl() {
        return listennotesUrl;
    }

    public void setListennotesUrl(String listennotesUrl) {
        this.listennotesUrl = listennotesUrl;
    }

    public String getPodcastListennotesUrl() {
        return podcastListennotesUrl;
    }

    public void setPodcastListennotesUrl(String podcastListennotesUrl) {
        this.podcastListennotesUrl = podcastListennotesUrl;
    }

    public Boolean getExplicitContent() {
        return explicitContent;
    }

    public void setExplicitContent(Boolean explicitContent) {
        this.explicitContent = explicitContent;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
