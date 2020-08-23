package main.emfk.com.emfklatest.Helper;

/**
 * Created by anim8r on 16/04/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PostsDBAdapter {

    public static final String KEY_ROWID = "rowid";
    public static final String KEY_POSTID = "post_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TAG = "tag";
    public static final String KEY_LINK_URL = "linkurl";
    public static final String KEY_SLUG = "slug";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_CONTENT_SANITIZED = "content_sanitized";
    public static final String KEY_INSERT_DATE = "insert_date";
    public static final String KEY_POST_DATE = "post_date";
    public static final String KEY_IMG_URL_WEB = "img_url_web";
    public static final String KEY_READ_URL = "read_more_url";
    public static final String KEY_IMG_URL_LOCAL = "img_url_local";
    public static final String KEY_SEARCH = "searchData";
    private static final String TAG_ATTACH_TAG = "attachments";
    public static final String KEY_SAVED = "postSaved"; //updated to Y when post is saved locally

    private static final String TAG = "PostsDBAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;

    private static final String DATABASE_NAME = "PostsData";
    private static final String FTS_VIRTUAL_TABLE = "PostsInfo";
    private static final String FTS_EMS_TABLE = "EmsPostsInfo";
    private static final String FTS_VIDEOS_TABLE = "EmsVideosInfo";

    private static final int DATABASE_VERSION = 1;

    //Create a FTS3 Virtual Table for fast searches
    private static final String DATABASE_CREATE =
            "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE + " USING fts3(" +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_INSERT_DATE + "," +
                    KEY_POST_DATE + "," +
                    KEY_IMG_URL_WEB + "," +
                    KEY_IMG_URL_LOCAL + "," +
                    KEY_READ_URL + "," +
                    KEY_SEARCH + "," +
                    KEY_SAVED + "," +
                    TAG_ATTACH_TAG + "," +
                    " UNIQUE (" + KEY_POSTID + "));" +
                    " CREATE VIRTUAL TABLE " + FTS_VIDEOS_TABLE + " USING fts3(" +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_INSERT_DATE + "," +
                    KEY_POST_DATE + "," +
                    KEY_IMG_URL_WEB + "," +
                    KEY_IMG_URL_LOCAL + "," +
                    KEY_READ_URL + "," +
                    KEY_SEARCH + "," +
                    KEY_SAVED + "," +
                    TAG_ATTACH_TAG + "," +
                    " UNIQUE (" + KEY_POSTID + "));" +

                    " CREATE VIRTUAL TABLE " + FTS_EMS_TABLE + " USING fts3(" +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_INSERT_DATE + "," +
                    KEY_POST_DATE + "," +
                    KEY_IMG_URL_WEB + "," +
                    KEY_IMG_URL_LOCAL + "," +
                    KEY_READ_URL + "," +
                    KEY_SEARCH + "," +
                    KEY_SAVED + "," +
                    TAG_ATTACH_TAG + "," +
                    " UNIQUE (" + KEY_POSTID + "));";

    private static final String CREATE_EMS_TABLE =
            "CREATE VIRTUAL TABLE " + FTS_EMS_TABLE + " USING fts3(" +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_INSERT_DATE + "," +
                    KEY_POST_DATE + "," +
                    KEY_IMG_URL_WEB + "," +
                    KEY_IMG_URL_LOCAL + "," +
                    KEY_READ_URL + "," +
                    KEY_SEARCH + "," +
                    KEY_SAVED + "," +
                    TAG_ATTACH_TAG + "," + " UNIQUE (" + KEY_POSTID + "));";

    private static final String CREATE_VIDEOS_TABLE =
            "CREATE VIRTUAL TABLE " + FTS_VIDEOS_TABLE + " USING fts3(" +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_INSERT_DATE + "," +
                    KEY_POST_DATE + "," +
                    KEY_IMG_URL_WEB + "," +
                    KEY_IMG_URL_LOCAL + "," +
                    KEY_READ_URL + "," +
                    KEY_SEARCH + "," +
                    KEY_SAVED + "," +
                    TAG_ATTACH_TAG + "," +
                    " UNIQUE (" + KEY_POSTID + "));";



    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
            db.execSQL(CREATE_EMS_TABLE);
            db.execSQL(CREATE_VIDEOS_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + FTS_EMS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIDEOS_TABLE);
            onCreate(db);
        }
    }

    public PostsDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public PostsDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long createPost(String postid, String pTitle, String pTag, String pLinkUrl, String pSlug, String pContent,
                           String pContentSanitized, String postDate, String imgUrlWeb, String attachStr,
    String readUrl) {

        ContentValues initialValues = new ContentValues();
        String searchValue = postid + " " +
                pTitle + " " +
                pTag + " " +
                pSlug + " " +
                pContentSanitized + " " +
                postDate;
        initialValues.put(KEY_POSTID, postid);
        initialValues.put(KEY_TITLE, pTitle);
        initialValues.put(KEY_TAG, pTag);
        initialValues.put(KEY_LINK_URL, pLinkUrl);
        initialValues.put(KEY_SLUG, pSlug);
        initialValues.put(KEY_CONTENT, pContent);
        initialValues.put(KEY_CONTENT_SANITIZED, pContentSanitized);
        initialValues.put(KEY_INSERT_DATE, "");
        initialValues.put(KEY_POST_DATE, postDate);
        initialValues.put(KEY_IMG_URL_WEB, imgUrlWeb);
        initialValues.put(KEY_IMG_URL_LOCAL, "");
        initialValues.put(KEY_READ_URL, readUrl);
        initialValues.put(KEY_SEARCH, searchValue);
        initialValues.put(TAG_ATTACH_TAG, attachStr);
        initialValues.put(KEY_SAVED, "N");
        //KEY_SAVED + "," +

        return mDb.insert(FTS_VIRTUAL_TABLE, null, initialValues);
    }

    //create EMSFrag PostsFrag Only Table

    public long createEMSPost(String postid, String pTitle, String pTag, String pLinkUrl, String pSlug, String pContent,
                           String pContentSanitized, String postDate, String imgUrlWeb, String attachStr,
                              String readUrl) {


        ContentValues initialValues = new ContentValues();
        String searchValue =     postid + " " +
                pTitle + " " +
                pTag + " " +
                pSlug + " " +
                pContentSanitized + " " +
                postDate;
        initialValues.put(KEY_POSTID, postid);
        initialValues.put(KEY_TITLE, pTitle);
        initialValues.put(KEY_TAG, pTag);
        initialValues.put(KEY_LINK_URL, pLinkUrl);
        initialValues.put(KEY_SLUG, pSlug);
        initialValues.put(KEY_CONTENT, pContent);
        initialValues.put(KEY_CONTENT_SANITIZED, pContentSanitized);
        initialValues.put(KEY_INSERT_DATE, "");
        initialValues.put(KEY_POST_DATE, postDate);
        initialValues.put(KEY_IMG_URL_WEB, imgUrlWeb);
        initialValues.put(KEY_IMG_URL_LOCAL, "");
        initialValues.put(KEY_READ_URL, readUrl);
        initialValues.put(KEY_SEARCH, searchValue);
        initialValues.put(TAG_ATTACH_TAG, attachStr);
        initialValues.put(KEY_SAVED, "N");

        return mDb.insert(FTS_EMS_TABLE, null, initialValues);
    }

    public Cursor searchAllPosts() throws SQLException {
        Cursor mCursor = null;
        try {
            String query = "SELECT docid as _id," +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_POST_DATE + "," +
                    KEY_SEARCH + "," +
                    KEY_SAVED + "," +
                    KEY_READ_URL + "," +
                    KEY_IMG_URL_WEB +
                    " from " + FTS_VIRTUAL_TABLE + " order by " + KEY_POST_DATE + " desc";
            Log.w(TAG, query);
            mCursor = mDb.rawQuery(query, null);
        } catch (SQLiteException e){
            addColumn();

        } catch (Exception e){
            addColumn();
        }

        return mCursor;
    }

    public Cursor getAllPostsByCategory(String category) throws SQLException {
        Cursor mCursor = null;
        try {
            String query = "SELECT docid as _id," +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_POST_DATE + "," +
                    KEY_SEARCH + "," +
                    KEY_SAVED + "," +
                    KEY_READ_URL + "," +
                    KEY_IMG_URL_WEB +
                    " from " + FTS_VIRTUAL_TABLE + " order by " + KEY_POST_DATE + " desc";
            Log.w(TAG, query);
            mCursor = mDb.rawQuery(query, null);
        } catch (SQLiteException e){
            addColumn();

        } catch (Exception e){
            addColumn();
        }

        return mCursor;
    }

    public Cursor searchPost(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        String query = "SELECT " +
                KEY_POSTID + "," +
                KEY_TITLE + "," +
                KEY_TAG + "," +
                KEY_LINK_URL + "," +
                KEY_SLUG + "," +
                KEY_CONTENT + "," +
                KEY_CONTENT_SANITIZED + "," +
                KEY_POST_DATE + "," +
                KEY_SEARCH + "," +
                KEY_READ_URL + "," +
                KEY_IMG_URL_WEB +
                " from " + FTS_VIRTUAL_TABLE +
                " where " +  KEY_SEARCH + " MATCH '" + inputText + "'";
            Log.w(TAG, query);
        Cursor mCursor = mDb.rawQuery(query,null);

        return mCursor;

    }

    public Cursor searchEMSPosts() throws SQLException {

        String query = "SELECT docid as _id," +
                KEY_POSTID + "," +
                KEY_TITLE + "," +
                KEY_TAG + "," +
                KEY_LINK_URL + "," +
                KEY_SLUG + "," +
                KEY_CONTENT + "," +
                KEY_CONTENT_SANITIZED + "," +
                KEY_SEARCH + "," +
                KEY_SAVED + "," +
                KEY_READ_URL + "," +
                KEY_POST_DATE + "," +
                KEY_IMG_URL_WEB +
                " from " + FTS_EMS_TABLE  + " order by " + KEY_POST_DATE + " desc";
        Log.w(TAG, query);
        Cursor mCursor = mDb.rawQuery(query,null);

        return mCursor;

    }

    public int getPostCount(){
        int count = 0;
        String query = "SELECT * FROM " + FTS_VIRTUAL_TABLE;
        Cursor mCursor = mDb.rawQuery(query,null);
        count = mCursor.getCount();
        return count;
    }

    public int checkPostExistsCount(String postid){
        int count = 0;
        String query = "SELECT * FROM " + FTS_VIRTUAL_TABLE + " WHERE " + KEY_POSTID + " MATCH '" + postid + "'";
        Cursor mCursor = mDb.rawQuery(query,null);
        count = mCursor.getCount();
        return count;
    }

    public int checkEMSPostExistsCount(String postid){
        int count = 0;
        String query = "SELECT * FROM " + FTS_EMS_TABLE + " WHERE " + KEY_POSTID + " MATCH '" + postid + "'";
        Cursor mCursor = mDb.rawQuery(query,null);
        count = mCursor.getCount();
        return count;
    }

    public int getEmsPostCount(){
        int count = 0;
        String query = "SELECT * FROM " + FTS_EMS_TABLE;
        Cursor mCursor = mDb.rawQuery(query,null);
        count = mCursor.getCount();
        return count;
    }

    public int checkVideoPostExistsCount(String postid){
        int count = 0;
        String query = "SELECT * FROM " + FTS_VIDEOS_TABLE + " WHERE " + KEY_POSTID + " MATCH '" + postid + "'";
        Cursor mCursor = mDb.rawQuery(query,null);
        count = mCursor.getCount();
        return count;
    }

    public int getVideoPostCount(){
        int count = 0;
        String query = "SELECT * FROM " + FTS_VIDEOS_TABLE;
        Cursor mCursor = mDb.rawQuery(query,null);
        count = mCursor.getCount();
        return count;
    }
    public boolean deleteAllPosts() {

        int doneDelete = 0;
        doneDelete = mDb.delete(FTS_VIRTUAL_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }


    public int createTable(){
        try{
            mDb.execSQL(CREATE_EMS_TABLE);
            return 1;
        } catch (SQLiteException sq) {
            return 0;
        }
    }
    //alter table t1 add column status varchar default 'N';
    public int addColumn(){
        try{
            mDb.execSQL("alter table " + FTS_VIRTUAL_TABLE + " add column " + KEY_SAVED + " varchar default 'N'");
            mDb.execSQL("alter table " + FTS_EMS_TABLE + " add column " + KEY_SAVED + " varchar default 'N'");
            return 1;
        } catch (SQLiteException sq) {
            return 0;
        }
    }

    public String toggleSavePost(String postid, String table){
        String updateVal = null;
        try{
            if(getCurrentSaveState(postid,table) != null){
                if(getCurrentSaveState(postid,table).equalsIgnoreCase("N")){
                    updateVal = "Y";
                } else {
                    updateVal = "N";
                }

                mDb.execSQL("UPDATE " + table + " SET " + KEY_SAVED + " = '" + updateVal + "' WHERE " + KEY_POSTID +
                        " = '" + postid + "'");
            } else {
                updateVal = "N";
            }

        } catch (SQLiteException sq) {
            updateVal = "N";
            Log.d("SQLErr",sq.getMessage());
        } catch (Exception e){
            updateVal = "N";
            Log.d("EXCErr",e.getMessage());
        }
        return updateVal;
    }

    public String getCurrentSaveState(String postid, String table){
        String res = null;
        try {
            String query = "SELECT " + KEY_SAVED + " FROM " + table + " WHERE "
                    + KEY_POSTID + " MATCH '" + postid + "'";
            Log.d("QKEY", "The q is " + query);
            Cursor mCursor = mDb.rawQuery(query, null);
            if (mCursor.moveToFirst()) {
                while (!mCursor.isAfterLast()) {
                    res = mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_SAVED));
                    mCursor.moveToNext();
                }
            }
            if (res == null) {
                return "";
            } else {
                return res;
            }
        } catch (Exception e){
            //handle ems main table unsave
            /*private static final String FTS_VIRTUAL_TABLE = "PostsInfo";
            private static final String FTS_EMS_TABLE = "EmsPostsInfo";
            private static final String FTS_VIDEOS_TABLE = "EmsVideosInfo";*/

            try {
                String query = "SELECT " + KEY_SAVED + " FROM " + FTS_VIRTUAL_TABLE + " WHERE "
                        + KEY_POSTID + " MATCH '" + postid + "'";
                Log.d("QKEY", "The q is " + query);
                Cursor mCursor = mDb.rawQuery(query, null);
                if (mCursor.moveToFirst()) {
                    while (!mCursor.isAfterLast()) {
                        res = mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_SAVED));
                        mCursor.moveToNext();
                    }
                }
                if (res == null) {
                    return "";
                } else {
                    return res;
                }
            } catch (Exception e1){
                try {
                    String query = "SELECT " + KEY_SAVED + " FROM " + FTS_EMS_TABLE + " WHERE "
                            + KEY_POSTID + " MATCH '" + postid + "'";
                    Log.d("QKEY", "The q is " + query);
                    Cursor mCursor = mDb.rawQuery(query, null);
                    if (mCursor.moveToFirst()) {
                        while (!mCursor.isAfterLast()) {
                            res = mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_SAVED));
                            mCursor.moveToNext();
                        }
                    }
                    if (res == null) {
                        return "";
                    } else {
                        return res;
                    }
                } catch (Exception e2){
                    try {
                        String query = "SELECT " + KEY_SAVED + " FROM " + FTS_VIDEOS_TABLE + " WHERE "
                                + KEY_POSTID + " MATCH '" + postid + "'";
                        Log.d("QKEY", "The q is " + query);
                        Cursor mCursor = mDb.rawQuery(query, null);
                        if (mCursor.moveToFirst()) {
                            while (!mCursor.isAfterLast()) {
                                res = mCursor.getString(mCursor.getColumnIndexOrThrow(KEY_SAVED));
                                mCursor.moveToNext();
                            }
                        }
                        if (res == null) {
                            return "";
                        } else {
                            return res;
                        }
                    } catch (Exception e3){
                        Log.d("MErr","Main error: " + e3.getMessage());
                        return res;
                    }
                }
            }
        }
    }

    public Cursor searchAllSavedPosts() throws SQLException {
        Cursor mCursor = null;
        try {
            String query = "SELECT docid as _id," +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_POST_DATE + "," +
                    KEY_SEARCH + "," +
                    KEY_READ_URL + "," +
                    KEY_SAVED + "," +
                    KEY_IMG_URL_WEB +
                    ", '" + FTS_VIRTUAL_TABLE + "' as tableName" +
                    " from " + FTS_VIRTUAL_TABLE + " WHERE " + KEY_SAVED + " MATCH 'Y' "

                    + " union " +

                    "SELECT docid as _id," +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_POST_DATE + "," +
                    KEY_SEARCH + "," +
                    KEY_READ_URL + "," +
                    KEY_SAVED + "," +
                    KEY_IMG_URL_WEB +
                    ", '" + FTS_VIDEOS_TABLE + "' as tableName" +
                    " from " + FTS_VIDEOS_TABLE + " WHERE " + KEY_SAVED + " MATCH 'Y' "

                    + " union " +

                    "SELECT docid as _id," +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_POST_DATE + "," +
                    KEY_SEARCH + "," +
                    KEY_READ_URL + "," +
                    KEY_SAVED + "," +
                    KEY_IMG_URL_WEB +
                    ", '" + FTS_EMS_TABLE + "' as tableName" +
                    " from " + FTS_EMS_TABLE + " WHERE " + KEY_SAVED + " MATCH 'Y' "
                    + " order by " + KEY_POST_DATE + " desc";
            Log.w(TAG, query);
            mCursor = mDb.rawQuery(query, null);
        } catch (SQLiteException e){
            e.printStackTrace();
            Log.d("TBERR","Error " + e.getMessage());
            //addColumn();

        } catch (Exception e){
            Log.d("TBERR2","Error " +  e.getMessage());
            //addColumn();
        }
        return mCursor;
    }

    public Cursor searchAllVideoPosts() throws SQLException {
        Cursor mCursor = null;
        try {
            String query = "SELECT docid as _id," +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_POST_DATE + "," +
                    KEY_SEARCH + "," +
                    KEY_READ_URL + "," +
                    KEY_SAVED + "," +
                    KEY_IMG_URL_WEB +
                    " from " + FTS_VIDEOS_TABLE + " order by " + KEY_POST_DATE + " desc";
            Log.w(TAG, query);
            mCursor = mDb.rawQuery(query, null);
        } catch (SQLiteException e){
            addColumn();

        } catch (Exception e){
            addColumn();
        }
        return mCursor;
    }

    public long createVideosPost(String postid, String pTitle, String pTag, String pLinkUrl, String pSlug, String pContent,
                              String pContentSanitized, String postDate, String imgUrlWeb, String attachStr,
                              String readUrl) {


        ContentValues initialValues = new ContentValues();
        String searchValue =     postid + " " +
                pTitle + " " +
                pTag + " " +
                pSlug + " " +
                pContentSanitized + " " +
                postDate;
        initialValues.put(KEY_POSTID, postid);
        initialValues.put(KEY_TITLE, pTitle);
        initialValues.put(KEY_TAG, pTag);
        initialValues.put(KEY_LINK_URL, pLinkUrl);
        initialValues.put(KEY_SLUG, pSlug);
        initialValues.put(KEY_CONTENT, pContent);
        initialValues.put(KEY_CONTENT_SANITIZED, pContentSanitized);
        initialValues.put(KEY_INSERT_DATE, "");
        initialValues.put(KEY_POST_DATE, postDate);
        initialValues.put(KEY_IMG_URL_WEB, imgUrlWeb);
        initialValues.put(KEY_IMG_URL_LOCAL, "");
        initialValues.put(KEY_READ_URL, readUrl);
        initialValues.put(KEY_SEARCH, searchValue);
        initialValues.put(TAG_ATTACH_TAG, attachStr);
        initialValues.put(KEY_SAVED, "N");

        return mDb.insert(FTS_VIDEOS_TABLE, null, initialValues);
    }

    //search posts all categories
    public Cursor searchAllCategPosts() throws SQLException {
        Cursor mCursor = null;
        try {
            String query = "SELECT docid as _id," +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_POST_DATE + "," +
                    KEY_SEARCH + "," +
                    KEY_READ_URL + "," +
                    KEY_SAVED + "," +
                    KEY_IMG_URL_WEB +
                    ", '" + FTS_VIRTUAL_TABLE + "' as tableName" +
                    " from " + FTS_VIRTUAL_TABLE

                    + " union " +

                    "SELECT docid as _id," +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_POST_DATE + "," +
                    KEY_SEARCH + "," +
                    KEY_READ_URL + "," +
                    KEY_SAVED + "," +
                    KEY_IMG_URL_WEB +
                    ", '" + FTS_VIDEOS_TABLE + "' as tableName" +
                    " from " + FTS_VIDEOS_TABLE

                    + " union " +

                    "SELECT docid as _id," +
                    KEY_POSTID + "," +
                    KEY_TITLE + "," +
                    KEY_TAG + "," +
                    KEY_LINK_URL + "," +
                    KEY_SLUG + "," +
                    KEY_CONTENT + "," +
                    KEY_CONTENT_SANITIZED + "," +
                    KEY_POST_DATE + "," +
                    KEY_SEARCH + "," +
                    KEY_READ_URL + "," +
                    KEY_SAVED + "," +
                    KEY_IMG_URL_WEB +
                    ", '" + FTS_EMS_TABLE + "' as tableName" +
                    " from " + FTS_EMS_TABLE + " order by " + KEY_POST_DATE + " desc";
            Log.w(TAG, query);
            mCursor = mDb.rawQuery(query, null);
        } catch (SQLiteException e){
            e.printStackTrace();
            Log.d("TBERR","Error " + e.getMessage());
            //addColumn();

        } catch (Exception e){
            Log.d("TBERR2","Error " +  e.getMessage());
            //addColumn();
        }
        return mCursor;
    }

    public String updatePostPicUrl(String postid, String picUrl, String table){
        String updateVal = null;
        try{
            if(picUrl != null){
                mDb.execSQL("UPDATE " + table + " SET " + KEY_IMG_URL_WEB + " = '" + picUrl + "' WHERE " + KEY_POSTID +
                        " = '" + postid + "'");
                updateVal = "Y";
            }

        } catch (SQLiteException sq) {
            updateVal = "N";
            Log.d("SQLErr2",sq.getMessage());
        } catch (Exception e){
            updateVal = "N";
            Log.d("EXCErr2",e.getMessage());
        }
        return updateVal;
    }

  }