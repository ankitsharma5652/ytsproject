package com.drag0nlab.dev.yts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class MovieList extends AppCompatActivity {

    ProgressDialog dialog;
    RecyclerView recyclerView ;
    MovieListAdapter adapter;
    boolean isLoading = false;
    LinearLayoutManager linearLayoutManager;
    int page=1;
    static String url = "https://yts.ag/api/v2/list_movies.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        adapter = new MovieListAdapter(this);

        dialog = ProgressDialog.show(this,"Loading","Fetching data...",true,false);
        recyclerView = (RecyclerView)findViewById(R.id.movielist);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
        makeRequest(url);




        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {


                linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisible = linearLayoutManager.findLastVisibleItemPosition();
                int totalVisible = linearLayoutManager.getItemCount();

                if((!isLoading))
                    if(totalVisible == (lastVisible+1)) {
                    if(!isLoading) {
                        isLoading = true;
                        adapter.progress();

                        String url = Home.url+ "?page="+ Integer.toString(++page)+"&limit="+MovieParameter.MOVIE_LIMIT;
                        System.out.println(url);
                        makeRequest(url);


                    }
                }
            }
        });

    }


    void makeRequest(String url){


         final MovieListAdapter adapter = (MovieListAdapter) recyclerView.getAdapter();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject obj = new JSONObject(response);

                    //  Checking Status ... if (true)-> Perform ele NO RESPONSE
                    if (obj.getString("status").equalsIgnoreCase("ok")) {

                        // Fetching Movie array forom response
                        JSONArray array = (obj.getJSONObject("data")).getJSONArray("movies");

                        if (array.length() > 0) {

                            ArrayList<MovieData> movieDatas= new ArrayList<>();
//                        if (adapter.getItemCount() == 0)
//                            Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
                            MovieData movieData;
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject data = (JSONObject) array.get(i);


                                //Genre array to string
                                StringBuilder builder = new StringBuilder();
                                JSONArray ar = data.getJSONArray("genres");
                                for (int m = 0; m < ar.length(); m++) {


                                    builder.append(ar.get(m).toString());
                                    if (m < (ar.length() - 1))
                                        builder.append("/");

                                }

                                //  END
                                movieData = new MovieData(data.getString("medium_cover_image"), data.getString("title"), builder.toString(), data.getDouble("rating"), data.getInt("year"));


                                // Other info for next page
                                movieData.setId(data.getInt("id"));
                                movieData.setIsitem(true);

                                movieDatas.add(movieData);
                            }
                            if(page > 1){


                                    adapter.addResponse(movieDatas);
                                //////Log.d("I am Here","yes");
                                isLoading=false;
                            }
                            else{
                                adapter.addAll(movieDatas);

                            }



                        } else
                            Toast.makeText(getApplicationContext(), "No Movie Found", Toast.LENGTH_SHORT).show();
                    } else {

                        ////Log.d("Status issue", "Check status");
                    }

                    if(page ==1)
                    dialog.dismiss();


                } catch (JSONException e) {
                    e.printStackTrace();

                    ////Log.d("JSON Exception","Exception of JSON request movie");

                    if(page ==1)
                    dialog.dismiss();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ////Log.d("Movie Request Error",error.toString());
                Toast.makeText(MovieList.this, "Network Issue", Toast.LENGTH_SHORT).show();

            }
        });


        if(page ==1)
        dialog.show();
        MyRequestQueue.getInstance(this).add(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case android.R.id.home:
                finish();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


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





}



class MovieData  {

    String image_url;
    String title;
    String genre;
    double rating;
    boolean isfooter = false;
    boolean isitem =true;

    public boolean isfooter() {
        return isfooter;
    }

    public void setIsfooter(boolean isfooter) {
        this.isfooter = isfooter;
    }


    public boolean isitem() {
        return isitem;
    }

    public void setIsitem(boolean isitem) {
        this.isitem = isitem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;

    int year;

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setYear(int year) {
        this.year = year;
    }



    public MovieData(){}




    public MovieData(String image_url, String title, String genre, double rating, int year) {
        this.image_url = image_url;
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.year = year;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getRating() { return rating;}

    public void setRating(byte rating) {
        this.rating = rating;
    }

    public int getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

}

class MovieListAdapter extends RecyclerView.Adapter{

    private ArrayList<MovieData> list;
    private Context context;
    private boolean isLoading = false;
    MovieListAdapter(Context context){
        this.context = context;
        list = new ArrayList();}


    public void empty(){this.list.clear();}
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView card;
        TextView title;
        TextView genre;
        TextView rating;
        TextView year;
        ImageView imageView;


        public MyViewHolder(View itemView) {
            super(itemView);


            year = (TextView) itemView.findViewById(R.id.year);
            title = (TextView) itemView.findViewById(R.id.movie_title);
            rating = (TextView) itemView.findViewById(R.id.rating);
            genre = (TextView) itemView.findViewById(R.id.genre);
            imageView = (ImageView) itemView.findViewById(R.id.imageView2);
            card = (CardView) itemView.findViewById(R.id.moviecard);
        }

        @Override
        public void onClick(View v) {

            Toast.makeText(context, title.getText().toString(), Toast.LENGTH_SHORT).show();

        }


    }






    static class ViewTpe{

        public static final int HEADER =0;
        public static final int ITEM = 1;
        public static final int FOOTER =2;
    }



    public void addAll(ArrayList<MovieData> list){this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void progress(){
        if(!isLoading) {
            MovieData d = new MovieData();
            d.setIsfooter(true);
            list.add(d);
            notifyDataSetChanged();
            isLoading = true;
//            ////Log.d("Progress last index", String.valueOf(list.size()));
        }
    }

    public void addResponse(ArrayList<MovieData> newdata){

        if(isLoading) {

            this.list.remove(list.size()-1);
            notifyItemRemoved(list.size());
            list.addAll(newdata);
//            ////Log.d("Response last index", String.valueOf(list.size()));
            notifyDataSetChanged();
            isLoading = false;
        }
    }



    @Override
    public int getItemViewType(int position) {



        if(list.get(position).isfooter) {
            ////Log.d("Footer","Yes");
            return ViewTpe.FOOTER;

        }
        else{
            ////Log.d("Item","Yes");
            return ViewTpe.ITEM;
        }


    }

    public void add(MovieData obj){ list.add(obj);}


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View myview ;
        RecyclerView.ViewHolder holder ;

        if(viewType == ViewTpe.ITEM)
        {
            myview= LayoutInflater.from(context).inflate(R.layout.listmovierow,parent,false);
            holder = new MyViewHolder(myview);
        }
        else if(viewType == ViewTpe.FOOTER){

            myview= LayoutInflater.from(context).inflate(R.layout.progress,parent,false);
            holder = new AnotherHolder(myview);

        }
        else{

            myview= LayoutInflater.from(context).inflate(R.layout.listmovierow,parent,false);
            holder = new MyViewHolder(myview);
        }


        return holder;
    }



    class AnotherHolder extends  RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        private AnotherHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }



        @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


                if(holder instanceof  MyViewHolder) {

                    try {

                        final MyViewHolder row = (MyViewHolder) holder;
                        final MovieData data = list.get(position);


                        row.title.setText(data.getTitle());
                        row.genre.setText(data.getGenre());
                        row.rating.setText(Double.toString(data.getRating()));
                        row.year.setText(Integer.toString(data.getYear()));
                        Glide.with(context).load(data.getImage_url()).diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop().into(row.imageView);

                        row.card.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                ArrayList list = new ArrayList<>(1);
                                list.add(data);
                                Intent action = new Intent(context, MovieDetail.class);
                                action.putExtra("id", data.getId());
                                context.startActivity(action);
                            }
                        });
                    }catch (IndexOutOfBoundsException e){
                        System.out.println("fuck");
                    }
                }
                else if(holder instanceof  AnotherHolder){


                }


            }
            @Override
            public int getItemCount() {
        return list.size();
    }
}