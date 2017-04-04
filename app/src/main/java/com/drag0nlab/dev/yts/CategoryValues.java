package com.drag0nlab.dev.yts;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryValues.GenreMovie} interface
 * to handle interaction events.
 */
public class CategoryValues extends Fragment {


    private ListView listView;
    private ArrayAdapter<String> adapterr;
    private GenreMovie mListener;
    ArrayList<ArrayAdapter<String>> adapter_list;
    int nlist;
    int []selected_filter;


    public CategoryValues() {
        // Required empty public constructor

        selected_filter = new int[3];
        adapter_list = new ArrayList<>(3);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_category_values, container, false);
        listView = (ListView) view.findViewById(R.id.category_list);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GenreMovie) {
            mListener = (GenreMovie) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        adapter_list.add(0,new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_single_choice,getResources().getStringArray(R.array.quality)));
        adapter_list.add(1,new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_single_choice,getResources().getStringArray(R.array.genre)));
        adapter_list.add(2,new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_single_choice,getResources().getStringArray(R.array.rating)));
//        adapter_list.add(3,new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_single_choice,getResources().getStringArray(R.array.order_by)));

        showValues(0);


        listView.setSelection(selected_filter[nlist]);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selected_filter[nlist]  = position;

            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public ArrayList<Object> getSelected(){

        ArrayList<Object> filter = new ArrayList<>(4);
        for(int i =0 ; i< 3; i++) {
            adapterr = adapter_list.get(i);
            filter.add(i,adapterr.getItem(selected_filter[i]));
        }

        return filter;
    }
    public interface GenreMovie{

        void genremovie(int position,String genre);
    }

    public void showValues(int position) {
        nlist = position;
        switch (position) {

            case 0:
            case 1:
            case 2:
                adapterr = adapter_list.get(position);
                break;
            default:
                adapterr = adapter_list.get(0);

        }


        listView.setAdapter(adapterr);
        listView.setItemChecked(selected_filter[nlist], true);


    }
}
