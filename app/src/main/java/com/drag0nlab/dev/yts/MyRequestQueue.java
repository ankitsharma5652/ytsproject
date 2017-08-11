package com.drag0nlab.dev.yts;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by DEV on 9/30/2016.
 */
public class MyRequestQueue {

    private static MyRequestQueue myRequestQueue ;
    private static Context context;
    private RequestQueue requestQueue ;
    private ImageLoader mImageLoader;

    private MyRequestQueue(Context context){

        this.context = context;
        requestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });

    }



    private RequestQueue getRequestQueue(){

        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);
        return requestQueue;
        }

    public void add(Request request){ requestQueue.add(request);}


    public void stop(Request request){requestQueue.stop();}

    public static synchronized MyRequestQueue  getInstance(Context context){

        if(myRequestQueue == null)
            myRequestQueue = new MyRequestQueue(context);
        return myRequestQueue;

    }


    public  void destroy(){requestQueue.getCache().clear();}
    public void stop(){requestQueue.stop();}

    public ImageLoader getmImageLoader(){return  mImageLoader;}

}
