package main.emfk.com.emfklatest;

import java.util.Date;

/**
 * Created by anim8r on 16/04/2016.
 */
public class Posts {

    String postid = null;
    String title = null;
    String tag = null;
    String slug = null;
    String content = null;
    String content_sanitized = null;
    String search_params = null;
    String insert_date = null;
    String img_url_web = null;
    String img_url_local = null;
    String post_saved = null;
    String read_more_link = null;
    String post_url;
    String post_date;

    public String getPostTable() {
        return postTable;
    }

    public void setPostTable(String postTable) {
        this.postTable = postTable;
    }

    String postTable;

    public String getPost_saved() {
        return post_saved;
    }

    public void setPost_saved(String post_saved) {
        this.post_saved = post_saved;
    }

    public String getRead_more_link() {
        return read_more_link;
    }

    public void setRead_more_link(String read_more_link) {
        this.read_more_link = read_more_link;
    }

    public String getPost_url() {
        return post_url;
    }

    public void setPost_url(String post_url) {
        this.post_url = post_url;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getImg_url_local() {
        return img_url_local;
    }

    public void setImg_url_local(String img_url_local) {
        this.img_url_local = img_url_local;
    }

    public String getImg_url_web() {
        return img_url_web;
    }

    public void setImg_url_web(String img_url_web) {
        this.img_url_web = img_url_web;
    }

    public String getInsert_date() {
        return insert_date;
    }

    public void setInsert_date(String insert_date) {
        this.insert_date = insert_date;
    }

    public String getSearch_params() {
        return search_params;
    }

    public void setSearch_params(String search_params) {
        this.search_params = search_params;
    }

    public String getContent_sanitized() {
        return content_sanitized;
    }

    public void setContent_sanitized(String content_sanitized) {
        this.content_sanitized = content_sanitized;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

}
