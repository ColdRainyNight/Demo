package com.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.demo.base.Data;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private List<Data.DataBean> list;
    private int selectnum = 0;
private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);
        initData();
        adapter = new MyAdapter();
        lv.setAdapter(adapter);
    }

    private void showPop(View v, final int i) {
        final String[] strs = {"乒乓球", "刘国梁", "梁靖崑", "国兵", "来源", "重复,旧闻", "内容质量差"};
        boolean[] selects = new boolean[strs.length];
        final List<PopBean> listBean = new ArrayList<>();
        for (String str : strs) {
            PopBean bean = new PopBean();
            bean.name = str;
            listBean.add(bean);
        }

        View view = View.inflate(this, R.layout.pop, null);
        final TextView num = (TextView) view.findViewById(R.id.select_num);
        final TextView submit = (TextView) view.findViewById(R.id.submit);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);




        gridview.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return listBean.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                final PopBean b = listBean.get(i);
                TextView tv = new TextView(MainActivity.this);
                tv.setText(b.name);
                tv.setGravity(Gravity.CENTER);
                tv.setBackgroundResource(R.drawable.pop_tv_bg);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (b.isSelect) {
                            b.isSelect = false;
                            selectnum--;
                        } else {
                            b.isSelect = true;
                            selectnum++;
                        }
                        if(selectnum>0){
                            num.setText("选择理由，优化推荐");
                            submit.setText("不想看");
                        }
                        notifyDataSetChanged();
                    }
                });

                if (b.isSelect) {
                    tv.setTextColor(Color.RED);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return tv;
            }
        });


       final PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.cricle_bg));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectnum>0){
                    pop.dismiss();
                    list.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        pop.showAsDropDown(v);
    }

    class ViewHolder {
        ImageView image;
        TextView title;
        ImageView delete;
    }

    private void initData() {
        try {
            InputStream in = getAssets().open("data.json");
            byte[] buff = new byte[1024];
            int line = -1;
            StringBuffer sb = new StringBuffer();
            while ((line = in.read(buff)) != -1) {
                sb.append(new String(buff, 0, line));
            }
            Gson gson = new Gson();
            Data data = gson.fromJson(sb.toString(), Data.class);
            list = data.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


   class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                view = View.inflate(MainActivity.this, R.layout.item, null);
                holder = new ViewHolder();
                holder.image = (ImageView) view.findViewById(R.id.image);
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.delete = (ImageView) view.findViewById(R.id.delete);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Data.DataBean bean = list.get(i);

            holder.title.setText(bean.getNews_title());
            Glide.with(MainActivity.this).load(bean.getPic_url()).into(holder.image);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, i + "", Toast.LENGTH_SHORT).show();
                    showPop(v, i);
                }
            });
            return view;
        }
    }
}