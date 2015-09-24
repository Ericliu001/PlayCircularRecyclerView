package com.example.ericliu.playcircularrecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ericliu.playcircularrecyclerview.ui.CircularList;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Employee> mList;

    private CircularList<Employee> circularList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initList();

        intCircularList();
    }

    private void initList() {
        mList = RawData.getRawDataList();
    }

    private void intCircularList() {
        circularList = (CircularList) findViewById(R.id.circularList);
        circularList.setPresenter(new CustomListPresenter(this, mList));
    }


    private static class CustomListPresenter implements CircularList.ListPresenter {
        private List dataList;
        private Activity activity;

        public CustomListPresenter(Activity activity, List list){
            this.activity = activity;
            dataList = list;
        }

        @Override
        public void onPostViewCreated() {

        }

        @Override
        public CircularList.CustomViewHolder getCustomViewHolder(ViewGroup parent) {
            View itemView = activity.getLayoutInflater().inflate(R.layout.circular_list_normal_row, parent, false);
            return new PersonViewHolder(itemView);
        }

        @Override
        public CircularList.CustomViewHolder getMiddleViewHolder(ViewGroup parent) {
            View itemView = activity.getLayoutInflater().inflate(R.layout.circular_list_normal_row, parent, false);
            return new PersonViewHolder(itemView);
        }


        @Override
        public Object getItemAtPosition(int position) {
            return dataList.get(position);
        }

        @Override
        public int getListLength() {
            return dataList.size();
        }

        public class PersonViewHolder extends CircularList.CustomViewHolder<Employee>{
            public TextView title;

            public PersonViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.tvRow);
            }

            @Override
            protected void refreshView() {
                title.setText(t.getFirstName() + " " + t.getLastName());
            }


        }
    }
}
