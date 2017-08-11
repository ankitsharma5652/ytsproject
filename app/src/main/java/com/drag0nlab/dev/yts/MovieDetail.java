package com.drag0nlab.dev.yts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetail extends AppCompatActivity {
    TextView title,summary,year,lang,rating,duration,genre;
    ImageView medium_image,cover_image;
    ProgressDialog dialog;
    int page =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = ProgressDialog.show(this,"Loading","Fetching Data");

        // Image View
        medium_image = (ImageView) findViewById(R.id.detail_medium_image);
        cover_image = (ImageView) findViewById(R.id.detail_cover_image);

        // Text View
        title = (TextView) findViewById(R.id.detail_title);
        summary = (TextView) findViewById(R.id.detail_summary);
        year = (TextView) findViewById(R.id.detail_year);
        lang = (TextView) findViewById(R.id.detail_lang);
        rating = (TextView) findViewById(R.id.detail_rating);
        duration = (TextView) findViewById(R.id.detail_duration);
        genre = (TextView) findViewById(R.id.detail_genre);

        Intent trans = getIntent();
        if(trans != null){

            int id = trans.getIntExtra("id",-1);
            if(id == -1)
                finish();

            getMovieInfo(id);


//
//            Log.d("Object",data.toString());
//            if(data == null)
//                Toast.makeText(this, "dfsad", Toast.LENGTH_SHORT).show();
//            // Glide Request
//            Glide.with(this).load(data.getImage_url()).centerCrop().into(medium_image);
//            Glide.with(this).load(data.getBackground_image_url()).centerCrop().into(cover_image);
//            medium_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//            // TextView
//            title.setText(data.getTitle());
//            year.setText(Integer.toString(data.getYear()));
//            genre.setText(data.getGenre());
//            lang.setText(data.getLang());
//            duration.setText(data.getDuration());
//            rating.setText(Double.toString(data.getRating()));
//            summary.setText(data.getSummary());




        }
        else{
            Toast.makeText(this, "Illegal Movie", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){


            case android.R.id.home:
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void getMovieInfo(final int id){

        String url = MovieParameter.MOVIE_DETAIL_URL+"?movie_id="+ Integer.toString(id) +"&with_images=false"+""+"&with_cast=true";

        System.out.println(url);
        final MOvieDetailData data = new MOvieDetailData();

//        if(data.getTitle() == null)
//            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();

        final StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if(obj.getString("status").equals("ok")){

                        JSONObject fetch =  obj.getJSONObject("data").getJSONObject("movie");

                        if(fetch.getInt("id") == id){


                            data.setId(id);
                            data.setTitle(fetch.getString("title_english"));

                            data.setBgimg_blur_url(fetch.getString("background_image"));
                            data.setBgimg_url(fetch.getString("background_image_original"));
                            data.setMed_img_url(fetch.getString("medium_cover_image"));

                            data.setDownload(fetch.getInt("download_count"));
                            data.setLike(fetch.getInt("like_count"));

                            data.setLanguage(fetch.getString("language"));
                            data.setDuration(fetch.getInt("runtime"));
                            data.setRating(fetch.getDouble("rating"));
                            data.setYear(fetch.getInt("year"));
                            data.setDescription(fetch.getString("description_full"));

                            //Genre array to string
                            StringBuffer builder = new StringBuffer();
                            JSONArray ar = fetch.getJSONArray("genres");
                            for (int m = 0; m < ar.length(); m++)
                            {
                                builder.append(ar.get(m).toString());
                                if (m < (ar.length() - 1))
                                    builder.append("/");
                            }

                            data.setGenre(builder.toString());

                            // Fetching Movie Cast
                            ar = fetch.getJSONArray("cast");
                            MovieCast cast ;
                            for(int c = 0 ;  c < ar.length(); c++){

                                cast  = new MovieCast();
                                JSONObject temp = ar.getJSONObject(c);

                                if(temp.has("imdb_code"))
                                cast.setImdb_id(temp.getInt("imdb_code"));

                                if(temp.has("url_small_image"))
                                    cast.setImg_url(temp.getString("url_small_image"));

                                if(temp.has("name"))
                                    cast.setName(temp.getString("name"));

                                if(temp.has("character_name"))
                                cast.setCharacter(temp.getString("character_name"));

                                data.setCast(cast);
                            }


                            // Fetching Torrent
                            ar = fetch.getJSONArray("torrents");

                            Torrent torrent;
                            for(int t= 0 ;t < ar.length(); t++){

                                torrent = new Torrent();

                                JSONObject temp = ar.getJSONObject(t);

                                if(temp.has("url"))
                                    torrent.setUrl(temp.getString("url"));

                                if(temp.has("hash"))
                                    torrent.setHash(temp.getString("hash"));

                                if(temp.has("quality"))
                                    torrent.setQuality(temp.getString("quality"));

                                if(temp.has("seeds"))
                                    torrent.setSeeds(temp.getInt("seeds"));

                                if(temp.has("peers"))
                                    torrent.setPeers(temp.getInt("peers"));

                                if(temp.has("size"))
                                    torrent.setSize(temp.getString("size"));

                                data.setTorrent(torrent);


                            }



                            assignData(data);

                            dialog.dismiss();



                        }
                        else{

                            Toast.makeText(MovieDetail.this, "ID Mismatch", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }
                    else{

                        Log.d("Status error","Status is not OK");
                        dialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JSON EXception","Error in onResponse Method");
                    dialog.dismiss();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Movie Detail ERROR!!","MOVIE VOLLEY ERROR");
                dialog.dismiss();
            }
        });


        dialog.show();
        MyRequestQueue.getInstance(this).add(request);


    }


  final  void assignData(MOvieDetailData data){

      title.setText(data.getTitle());
      year.setText(Integer.toString(data.getYear()));
      rating.setText(Double.toString(data.getRating()));
      cover_image.setScaleType(ImageView.ScaleType.FIT_XY);
      medium_image.setScaleType(ImageView.ScaleType.FIT_XY);
      Glide.with(this).load(data.getBgimg_blur_url()).error(android.R.drawable.alert_dark_frame).into(cover_image);
      Glide.with(this).load(data.getMed_img_url()).error(android.R.drawable.alert_dark_frame).into(medium_image);
      summary.setText(data.getDescription());
      lang.setText(data.getLanguage());
      genre.setText(data.getGenre());
      duration.setText(data.getDuration());

      GridLayout cast_gridview= (GridLayout) findViewById(R.id.cast_gridview);
      ArrayList movied= data.getCast();
      for(int v =0 ; v <  movied.size(); v++)
      cast_gridview.addView(setCastView((MovieCast) movied.get(v)));

      // Quality
      cast_gridview = (GridLayout) findViewById(R.id.quality_gridview);
      movied = data.getTorrent();
      for(int v =0 ; v <  movied.size(); v++)
          cast_gridview.addView(setTorrentButton((Torrent) movied.get(v)));


      dialog.dismiss();
    }


    public View setTorrentButton(final Torrent torrent){

        Button btn=new Button(this);
        btn.setText(torrent.getQuality());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MovieDetail.this, "Download using Torrent", Toast.LENGTH_SHORT).show();
                Intent download = new Intent(Intent.ACTION_VIEW);
                download.setData(Uri.parse(torrent.getUrl()));
                startActivity(download);


//                Toast.makeText(MovieDetail.this,torrent.getUrl(),Toast.LENGTH_SHORT).show();
            }
        });

        btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        return btn;
    }



    public  View setCastView(MovieCast cast){
        TextView name;
        TextView character;
        ImageView imageView;
        View row = LayoutInflater.from(this).inflate(R.layout.moviecast_row,null);

        name = (TextView) row.findViewById(R.id.cast_name);
        character = (TextView) row.findViewById(R.id.cast_char);
        imageView = (ImageView) row.findViewById(R.id.cast_img);

        name.setText(cast.getName());
        character.setText(cast.getCharacter());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        Glide.with(this).load(cast.getImg_url()).error(android.R.drawable.dark_header).placeholder(android.R.drawable.spinner_background).into(imageView);
        return row;
    }


}


class MOvieDetailData{
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBgimg_blur_url() {
        return bgimg_blur_url;
    }

    public void setBgimg_blur_url(String bgimg_blur_url) {
        this.bgimg_blur_url = bgimg_blur_url;
    }

    public String getBgimg_url() {
        return bgimg_url;
    }

    public void setBgimg_url(String bgimg_url) {
        this.bgimg_url = bgimg_url;
    }

    public String getMed_img_url() {
        return med_img_url;
    }

    public void setMed_img_url(String med_img_url) {
        this.med_img_url = med_img_url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int download) {
        this.download = download;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public ArrayList<MovieCast> getCast() {
        return cast;
    }

    public void setCast(MovieCast cast) {

        this.cast.add(cast);
    }

    public ArrayList<Torrent> getTorrent() {
        return torrent;
    }

    public void setTorrent(Torrent torrent) {
        this.torrent.add(torrent);
    }



    public String getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = getTime(duration);
    }


    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }



    String getTime(int time){

        int hr = time /60 ;
        int min = time % 60;

        while(min >= 60){

            min= min -60;
            hr++;
        }
        return new String(Integer.toString(hr)+"h"+Integer.toString(min)+"min");
    }

    private String title;
    private String description;
    private String bgimg_blur_url;
    private String bgimg_url;
    private String med_img_url;
    private String language;
    private String duration;
    private double rating;
    private int download;
    private int like;
    private int id;
    private int year;
    private String genre;

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images.add(images);
    }

    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<MovieCast> cast = new ArrayList<>();
    private ArrayList<Torrent> torrent = new ArrayList<>();
}



class MovieCast{
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(int imdb_id) {
        this.imdb_id = imdb_id;
    }

    private String name;
    private String character;
    private String img_url;
    private int imdb_id;


}

class Torrent{

    private int seeds;
    private int peers;
    private String url;
    private String hash;
    private String size;
    private String quality;

    public int getSeeds() {
        return seeds;
    }

    public void setSeeds(int seeds) {
        this.seeds = seeds;
    }

    public int getPeers() {
        return peers;
    }

    public void setPeers(int peers) {
        this.peers = peers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}


