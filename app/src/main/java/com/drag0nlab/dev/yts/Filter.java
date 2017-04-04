package com.drag0nlab.dev.yts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class Filter extends AppCompatActivity implements CategoryList.OnListItemSelected,CategoryValues.GenreMovie {


    CategoryList c_list;
    CategoryValues c_values;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        c_list = (CategoryList) getSupportFragmentManager().findFragmentById(R.id.category_list);
        c_values = (CategoryValues) getSupportFragmentManager().findFragmentById(R.id.value_category);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.filter,menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){


            case R.id.applyfilter:

                ArrayList list = c_values.getSelected();

                if(list.get(1).toString().equalsIgnoreCase("All"))
                    list.set(1,"");
                if(((String)list.get(2)).equalsIgnoreCase("All"))
                 list.set(2,0);
                else
                    list.set(2, Integer.parseInt(((String) list.get(2)).charAt(0) + ""));

                Intent intent = new Intent();
                intent.putExtra("obj",list);
                Log.d("List  =",list.toString());
                setResult(13,intent);
                finish();


                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickUpdate(int position) {


        c_values.showValues(position);

    }

    @Override
    public void genremovie(int position,String genre) {



    }
}
