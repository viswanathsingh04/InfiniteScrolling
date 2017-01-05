package com.infinitescrolling;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends CommonAppcompatActivity {
    JSONObject jsonObject;
    JSONArray jsonArray;
    ListView list;
    TextView txt_title, txt_des, txt_posted;
    ImageView img_news;
    DetailAsyncTask detailAsyncTask;
    NewsAdapter newsAdapter;
    String next_page_url, prev_page_url;
    String uri = "@drawable/listtext";
    int page = 1;
    protected boolean isEndReached = false;
    public boolean isLoading = false;
    protected int scroll;
    public Integer totalPages;
    private LinearLayout ll_footer;
    private ArrayList<HashMap<String, String>> arrayList, arrayListTemp;
    protected int stateScroll;
    Context context;
    int current_page = 1;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayList = new ArrayList<HashMap<String, String>>();

        list = (ListView) findViewById(R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*Intent intent = new Intent(MainActivity.this, Read.class);
                startActivity(intent);*/
            }
        });

        if (isInternetAvailable) {
            detailAsyncTask = new DetailAsyncTask();
            detailAsyncTask.execute();
        }
    }

    public class DetailAsyncTask extends AsyncTask<String, Void, String> {
        private final String USER_AGENT = "Mozilla/5.0";
        String res = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(MainActivity.this);
        }

        @Override
        protected String doInBackground(String... voids) {

            try {
                String url = "http://news.mindzenmedia.com/api/v1/feeditems";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                res = response.toString();
                System.out.println(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissProgressDialog();
            try {
                HashMap<String, String> hashMap;
                jsonObject = new JSONObject(s);
                next_page_url = jsonObject.getString("next_page_url");
                prev_page_url = jsonObject.getString("prev_page_url");

                jsonArray = jsonObject.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    hashMap = new HashMap<String, String>();
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                    hashMap.put("id", jsonObject2.getString("id"));
                    hashMap.put("title", jsonObject2.getString("title"));
                    hashMap.put("description", jsonObject2.getString("description"));
                    hashMap.put("date", jsonObject2.getString("date"));
                    hashMap.put("image_url", jsonObject2.getString("image_url"));
                    arrayList.add(hashMap);
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            if (arrayList.size() > 0) {
                newsAdapter = new NewsAdapter();
                list.setAdapter(newsAdapter);
                list.setOnScrollListener(new EndlessScrollListenerr());
            } else {
                Toast.makeText(MainActivity.this, "Data Not Available,Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class EndlessScrollListenerr implements OnScrollListener {

        private int visibleThreshold = 3;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListenerr() {
        }

        public EndlessScrollListenerr(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                new DetailAsyncTask().execute();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                final ListView listView = (ListView) view;
            }
        }
    }

    public class NewsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.list, viewGroup, false);
            }
            txt_title = (TextView) view.findViewById(R.id.list_news_title);
            txt_des = (TextView) view.findViewById(R.id.list_news_source);
            txt_posted = (TextView) view.findViewById(R.id.list_news_time);
            img_news = (ImageView) view.findViewById(R.id.list_img);

            txt_title.setText(arrayList.get(i).get("title"));
            txt_des.setText(arrayList.get(i).get("description"));
            txt_posted.setText(arrayList.get(i).get("date"));

            if (TextUtils.isEmpty(arrayList.get(i).get("image_url"))) {
                Picasso.with(getApplicationContext()).cancelRequest(img_news);
                img_news.setImageDrawable(null);
                /*int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                img_news.setImageDrawable(res);*/
            } else {
                Picasso.with(getApplicationContext()).load(arrayList.get(i).get("image_url"))
                        .placeholder(R.drawable.listtext).resize(300, 200).into(img_news);
            }
            return view;
        }
    }

}