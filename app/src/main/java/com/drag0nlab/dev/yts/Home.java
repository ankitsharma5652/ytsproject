package com.drag0nlab.dev.yts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView ;
    RecyclerView recyclerView;
    ProgressDialog dialog;
    int page = 1;
    boolean another = true;
    String temp_url;
    boolean isLoading = false;
    Handler handler ;
    HomeMovieListAdapter adapter ;
    GridLayoutManager gridLayoutManager;
   static String url ="https://yts.ag/api/v2/list_movies.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handler = new Handler(Looper.getMainLooper());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fab.hide();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        adapter = new HomeMovieListAdapter(this);

        dialog = ProgressDialog.show(this,"Loading","Fetching Data...",true,false);
        recyclerView= (RecyclerView) findViewById(R.id.home_recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);





        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(THREAD_PRIORITY_BACKGROUND);
                        makeRequest(url);
            }
        }).start();


recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        int lastVisible = gridLayoutManager.findLastCompletelyVisibleItemPosition();
        int firstVisible = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
        int totalVisible = gridLayoutManager.getItemCount();
        int visible = recyclerView.getChildCount();
        if((!isLoading))
            if(totalVisible <= (lastVisible+firstVisible- visible))
            {
                if(!isLoading){

                   new Thread(new Runnable() {
                       @Override
                       public void run() {

                           Thread.currentThread().setPriority(THREAD_PRIORITY_BACKGROUND);

                           isLoading = true;
                           if(page > 1)
                               handler.postAtFrontOfQueue(new Runnable() {
                                   @Override
                                   public void run() {
                                       adapter.progress();
                                   }
                               }) ;
                           String urll;
                           if (another)
                               temp_url = urll = Home.url + "?page=" + Integer.toString(++page) + "&limit=" + MovieParameter.MOVIE_LIMIT;
                           else {

                               try {
                                   int loc = temp_url.indexOf("page=")+4;
                                   // loc point to =
                                   if (Character.isDigit(temp_url.charAt(loc + 1)) && Character.isDigit(loc + 2)) {
                                       int var = Integer.parseInt(temp_url.charAt(loc + 1) + "" + temp_url.charAt(loc + 2));
                                       System.out.println("var " + var);
                                       temp_url= temp_url.replace(Integer.toString(var),Integer.toString(++page));
                                       urll = temp_url;
                                       System.out.println(url);
                                   } else {

                                       int t = Integer.parseInt(temp_url.charAt(loc+1)+"");
                                       System.out.println("t =>"+ t);

                                       page = t;
                                       temp_url = temp_url.replace("?page="+Integer.toString(t), "?page=" +Integer.toString(++page));
                                       urll = temp_url;
//                                System.out.println(urll+" here  " + page);
                                   }
                               } catch (IndexOutOfBoundsException e) {
//                            System.out.println(temp_url);

                                   urll = temp_url;

                               }
                           }

                           makeRequest(urll);


                       }
                   }).start();
                }
            }
    }
});


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(adapter != null)
        outState.putInt ("adapter", ((GridLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition());


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState.containsKey("adapter"))
        {

            ((GridLayoutManager)recyclerView.getLayoutManager()).scrollToPosition(savedInstanceState.getInt("adapter"));



            Toast.makeText(this, String.valueOf(savedInstanceState.getInt("adapter")), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.refresh) {

            System.out.println(Home.url);
            if (page == 1) {
                makeRequest(Home.url);
                dialog.show();

            } else if (page > 1) {


                makeRequest(temp_url);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent ;
        int id = item.getItemId();

        if (id == R.id.nav_filter) {
            startActivityForResult(new Intent(this,Filter.class),12);
            // Handle the camera action
        } else if (id == R.id.latest) {

            intent = new Intent(this,MovieList.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {

            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT,"Subject");
            intent.putExtra(Intent.EXTRA_TEXT,"Try this awesome app..");
            startActivity(Intent.createChooser(intent,"Share via"));


        }
        else if (id == R.id.aboutus){

            intent = new Intent(this,Aboutus.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    void makeRequest(String url){



        final HomeMovieListAdapter adapter = (HomeMovieListAdapter) recyclerView.getAdapter();

        StringRequest request = new StringRequest(com.android.volley.Request.Method.GET ,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    //  Checking Status ... if (true)-> Perform ele NO RESPONSE
                    if(obj.getString("status").equalsIgnoreCase("ok")){

                        // Fetching Movie array forom response
                        JSONArray array = (obj.getJSONObject("data")).getJSONArray("movies");

                        if(array.length() > 0){


//                            if(adapter.getItemCount() == 0)
//                                Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
                            HomeMovieData movieData;
                            final ArrayList<HomeMovieData> movieDatas= new ArrayList<>();
                            for(int i=0 ; i< array.length() ;i++){

                                JSONObject data = (JSONObject) array.get(i);


                                //Genre array to string
                                StringBuilder builder = new StringBuilder();
                                JSONArray ar = data.getJSONArray("genres");
                                for(int m =0 ; m< ar.length(); m++){


                                    builder.append(ar.get(m).toString());
                                    if(m < (ar.length()-1)  && ar.length() > 1)
                                        builder.append("/");

                                }

                                //  END
                                movieData = new HomeMovieData(data.getInt("id"),data.getString("medium_cover_image"),data.getString("title"),data.getDouble("rating"),data.getInt("year"));


                                movieDatas.add(movieData);
                            }

                            if(page == 1) {

                                handler.postAtFrontOfQueue(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.addAll(movieDatas);
                                        dialog.dismiss();
                                    }
                                });

                            }
                            else if(page > 1){

                                handler.postAtFrontOfQueue(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.addResponse(movieDatas);
                                    }
                                });
                                isLoading=false;
                            }



                        }else
                            Toast.makeText( getApplicationContext(),"No Movie Found", Toast.LENGTH_SHORT).show();
                        if(page == 1)
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    dialog.dismiss();
                                }
                            });
                    }
                    else{

                        //Log.d("Status issue","Check status");
                        if(page ==1)
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    dialog.dismiss();
                                }
                            });
                        else if(page > 1)
                            page--;
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.d("JSON EXCEPTION HOME",e.toString());
                    if(page >1)
                        page--;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            dialog.dismiss();
                        }
                    });
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Error",error.toString());
        if (page ==1)
            handler.post(new Runnable() {
                @Override
                public void run() {

                    dialog.dismiss();
                }
            });
            }
        });


        if(page ==1)
            handler.post(new Runnable() {
            @Override
            public void run() {

                dialog.dismiss();
            }
        });



                MyRequestQueue.getInstance(this).add(request);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 12 && resultCode ==13){
            ArrayList list = data.getCharSequenceArrayListExtra("obj");
            page = 1;
            String url = Home.url +"?page="+Integer.toString(page)+"&limit=15&quality="+list.get(0).toString()+"&genre="+list.get(1).toString()+"&minimum_rating="+list.get(2).toString();

            another = false;
            temp_url= url;

            // CHecking url
            System.out.println(url);
            adapter.empty();
            makeRequest(url);


        }
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        MyRequestQueue.getInstance(this).destroy();
    }
}




class HomeMovieListAdapter extends RecyclerView.Adapter implements Serializable{

    private ArrayList<HomeMovieData> list;
    private Context context;
    private boolean isLoading = false;


    HomeMovieListAdapter(Context context){
        this.context = context;
        list = new ArrayList();}



    public void empty(){this.list.clear();}
    class MyViewHolder extends RecyclerView.ViewHolder{

        CardView card;
        TextView title;
        TextView year;
        ImageView imageView;
        TextView rating;


        public MyViewHolder(View itemView) {
            super(itemView);


            year = (TextView) itemView.findViewById(R.id.home_year);
            title = (TextView) itemView.findViewById(R.id.home_name);
            imageView = (ImageView) itemView.findViewById(R.id.home_image);
            rating = (TextView) itemView.findViewById(R.id.home_raitng);
            card = (CardView) itemView.findViewById(R.id.home_card);
        }

    }



    static class ViewTpe{

        public static final int HEADER =0;
        public static final int ITEM = 1;
        public static final int FOOTER =2;
    }



    public void addAll(ArrayList<HomeMovieData> list){this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void progress(){
        if(!isLoading) {
            HomeMovieData d = new HomeMovieData();
            d.setIsfooter(true);
            list.add(d);
            notifyDataSetChanged();
            isLoading = true;
//            ////Log.d("Progress last index", String.valueOf(list.size()));
        }
    }

    public void addResponse(ArrayList<HomeMovieData> newdata){

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

        super.getItemViewType(position);

        if(list.get(position).isfooter) {
            ////Log.d("Footer","Yes");
            return MovieListAdapter.ViewTpe.FOOTER;

        }
        else{
            ////Log.d("Item","Yes");
            return MovieListAdapter.ViewTpe.ITEM;
        }


    }



    public void add(HomeMovieData obj){ list.add(obj);}


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View myview ;
        RecyclerView.ViewHolder holder ;

        if(viewType == ViewTpe.ITEM)
        {
            myview= LayoutInflater.from(context).inflate(R.layout.home_row,parent,false);
            holder = new MyViewHolder(myview);
        }
        else if(viewType == ViewTpe.FOOTER){

            myview= LayoutInflater.from(context).inflate(R.layout.progress,parent,false);
            holder = new AnotherHolder(myview);

        }
        else{

            myview= LayoutInflater.from(context).inflate(R.layout.home_row,parent,false);
            holder = new MyViewHolder(myview);
        }


        return holder;    }



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
            final MyViewHolder row = (MyViewHolder) holder;
            final HomeMovieData data =  list.get(position);
            row.rating.setText(Double.toString(data.getRating()) + "/10");
            row.title.setText(data.getTitle());
            row.year.setText(Integer.toString(data.getYear()));
            row.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(context).load(data.getImage_url()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(row.imageView);


            row.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent action = new Intent(context, MovieDetail.class);
                    action.putExtra("id", data.getId());

                    context.startActivity(action);
                }
            });

        }
        else if(holder instanceof AnotherHolder){

            ((AnotherHolder) holder).progressBar.post(new Runnable() {
                @Override
                public void run() {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}



class HomeMovieData{

    String image_url;
    String title;
    double rating;
    int id;
    boolean isfooter= false;
    boolean isnormal = false;

    public boolean isfooter() {
        return isfooter;
    }

    public void setIsfooter(boolean isfooter) {
        this.isfooter = isfooter;
    }

    public boolean isnormal() {
        return isnormal;
    }

    public void setIsnormal(boolean isnormal) {
        this.isnormal = isnormal;
    }

    public int getId() {
        return id;
    }

    int year;
    ArrayList torrent;



    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList getTorrent() {
        return torrent;
    }

    public void setTorrent(ArrayList torrent) {
        this.torrent = torrent;
    }


    public HomeMovieData(){}


    public HomeMovieData(int id,String image_url, String title, double rating, int year) {
        this.id = id;
        this.image_url = image_url;
        this.title = title;
        this.rating = rating;
        this.year = year;
    }

    public String getImage_url() {
        return image_url;
    }


    public String getTitle() {
        return title;
    }


    public double getRating() { return rating;}

    public int getYear() {
        return year;
    }

}


