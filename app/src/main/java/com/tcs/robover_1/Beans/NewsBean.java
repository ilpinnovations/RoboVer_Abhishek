package com.tcs.robover_1.Beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FAYUSULLA on 6/10/2016.
 */
public class NewsBean {

    private String _type;

   private List<Value> value = new ArrayList<>();

   // private ArrayList<Value> value;

    private Instrumentation instrumentation;

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }


    public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }

    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public void setInstrumentation(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public String toString() {
        return "ClassPojo [_type = " + _type + ", value = " + value + ", instrumentation = " + instrumentation + "]";
    }


public class Value {
        private String category;

        private String urlPingSuffix;

        private String description;

        private String datePublished;

        private String name;

        private Image image;

        private Provider[] provider;

        private String url;

        private ClusteredArticles[] clusteredArticles;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getUrlPingSuffix() {
            return urlPingSuffix;
        }

        public void setUrlPingSuffix(String urlPingSuffix) {
            this.urlPingSuffix = urlPingSuffix;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDatePublished() {
            return datePublished;
        }

        public void setDatePublished(String datePublished) {
            this.datePublished = datePublished;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public Provider[] getProvider() {
            return provider;
        }

        public void setProvider(Provider[] provider) {
            this.provider = provider;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public ClusteredArticles[] getClusteredArticles() {
            return clusteredArticles;
        }

        public void setClusteredArticles(ClusteredArticles[] clusteredArticles) {
            this.clusteredArticles = clusteredArticles;
        }

        @Override
        public String toString() {
            return "ClassPojo [category = " + category + ", urlPingSuffix = " + urlPingSuffix + ", description = " + description + ", datePublished = " + datePublished + ", name = " + name + ", image = " + image + ", provider = " + provider + ", url = " + url + ", clusteredArticles = " + clusteredArticles + "]";
        }
    }

    class ClusteredArticles {
        private String category;

        private String urlPingSuffix;

        private String description;

        private String datePublished;

        private String name;

        private Provider[] provider;

        private String url;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getUrlPingSuffix() {
            return urlPingSuffix;
        }

        public void setUrlPingSuffix(String urlPingSuffix) {
            this.urlPingSuffix = urlPingSuffix;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDatePublished() {
            return datePublished;
        }

        public void setDatePublished(String datePublished) {
            this.datePublished = datePublished;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Provider[] getProvider() {
            return provider;
        }

        public void setProvider(Provider[] provider) {
            this.provider = provider;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "ClassPojo [category = " + category + ", urlPingSuffix = " + urlPingSuffix + ", description = " + description + ", datePublished = " + datePublished + ", name = " + name + ", provider = " + provider + ", url = " + url + "]";
        }
    }
    class Provider
    {
        private String _type;

        private String name;

        public String get_type ()
        {
            return _type;
        }

        public void set_type (String _type)
        {
            this._type = _type;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [_type = "+_type+", name = "+name+"]";
        }
    }
    class Image {
        private Thumbnail thumbnail;

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
        }

        @Override
        public String toString() {
            return "ClassPojo [thumbnail = " + thumbnail + "]";
        }
    }
    class Thumbnail
    {
        private String height;

        private String contentUrl;

        private String width;

        public String getHeight ()
        {
            return height;
        }

        public void setHeight (String height)
        {
            this.height = height;
        }

        public String getContentUrl ()
        {
            return contentUrl;
        }

        public void setContentUrl (String contentUrl)
        {
            this.contentUrl = contentUrl;
        }

        public String getWidth ()
        {
            return width;
        }

        public void setWidth (String width)
        {
            this.width = width;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [height = "+height+", contentUrl = "+contentUrl+", width = "+width+"]";
        }
    }
    class Instrumentation
    {
        private String pageLoadPingUrl;

        private String pingUrlBase;

        public String getPageLoadPingUrl ()
        {
            return pageLoadPingUrl;
        }

        public void setPageLoadPingUrl (String pageLoadPingUrl)
        {
            this.pageLoadPingUrl = pageLoadPingUrl;
        }

        public String getPingUrlBase ()
        {
            return pingUrlBase;
        }

        public void setPingUrlBase (String pingUrlBase)
        {
            this.pingUrlBase = pingUrlBase;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [pageLoadPingUrl = "+pageLoadPingUrl+", pingUrlBase = "+pingUrlBase+"]";
        }
    }
}