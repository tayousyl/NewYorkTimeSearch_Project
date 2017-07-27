package com.example.sonel.newyorktimessearch.Modele;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sonel on 7/26/2017.
 */

public class Legacy {


    @SerializedName("thumbnailheight")
    @Expose
    private String thumbnailheight;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("thumbnailwidth")
    @Expose
    private String thumbnailwidth;

    /**
     *
     * @return
     *     The thumbnailheight
     */
    public String getThumbnailheight() {
        return thumbnailheight;
    }

    /**
     *
     * @param thumbnailheight
     *     The thumbnailheight
     */
    public void setThumbnailheight(String thumbnailheight) {
        this.thumbnailheight = thumbnailheight;
    }

    /**
     *
     * @return
     *     The thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     *
     * @param thumbnail
     *     The thumbnail
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     *
     * @return
     *     The thumbnailwidth
     */
    public String getThumbnailwidth() {
        return thumbnailwidth;
    }

    /**
     *
     * @param thumbnailwidth
     *     The thumbnailwidth
     */
    public void setThumbnailwidth(String thumbnailwidth) {
        this.thumbnailwidth = thumbnailwidth;
    }


}
