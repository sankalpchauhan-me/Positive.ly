
package me.sankalpchauhan.positively.service.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerResult implements Serializable {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("next_offset")
    @Expose
    private Integer nextOffset;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("took")
    @Expose
    private Double took;
    @SerializedName("results")
    @Expose
    private List<Podcast> podcasts = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(Integer nextOffset) {
        this.nextOffset = nextOffset;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getTook() {
        return took;
    }

    public void setTook(Double took) {
        this.took = took;
    }

    public List<Podcast> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(List<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

}
