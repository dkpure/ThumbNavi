package cn.seu.dkpure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * A custom widget to show weather information
 * @author dkpure
 *
 */
public class WeatherView extends View {
        private Bitmap         weather_icon;
        private volatile String        temp_str;
        private Paint        text_paint;
        
        WeatherInfoGetter winfo_getter;

        public WeatherView(Context context) {
                super(context);
                // TODO Auto-generated constructor stub
                
                initWeatherView ();
                weather_icon = BitmapFactory.decodeResource(getResources(), R.drawable.weather_icon99);
        }
        
        public WeatherView(Context context, AttributeSet attrs) {
                super(context, attrs);
                
                initWeatherView ();
                weather_icon = BitmapFactory.decodeResource(getResources(), R.drawable.weather_icon99);
        }
        
        /**
         * Do some initialization work.
         */
        private void initWeatherView () {
            temp_str = "N/A";
            text_paint = new Paint();
            text_paint.setColor(Color.WHITE);
            text_paint.setAntiAlias(true);
            if (GlobalParams.RUN_720P)
            	text_paint.setTextSize(70);
            else
            	text_paint.setTextSize(40);
            
            //retrieve weather information from weather_request_url
            winfo_getter = new WeatherInfoGetter();
            winfo_getter.execute(getResources().getString(R.string.fine_weather_request_url));
        }
        
        /**
         * Update the interface with new parameters returned
         * @param resId id of the corresponding weather icon
         * @param temp temperature string
         */
        public void updateView(int resId, String temp) {
                if (temp == null)
                	return;
                
                weather_icon = BitmapFactory.decodeResource(getResources(), resId);
                temp_str = temp;
                
                Log.v("WeatherView", "updateView, temp_str: " + temp_str);
                
                if (null != weather_icon && null != temp_str) {
                        requestLayout(); // invoke onMeasure()
                        invalidate(); // invoke onDraw()
                        animateShow();
                }
        }
        
        /**
         * Using an alpha animation to update the interface
         */
        private void animateShow() {
                Animation a = new AlphaAnimation(0.0f, 1.0f);
            a.setDuration(1000); // 1s
        a.setRepeatCount(0);
        this.setAnimation(a);
        this.startAnimation(a);// show with an AlphaAnimation
        }
        
        private int getRectWidth() {
            int ret = 0;
            
            ret = (int) text_paint.measureText(temp_str);
            
            if (ret < weather_icon.getWidth())
                    ret = weather_icon.getWidth();
            
            return ret;
    }
    
    private int getRectHeight() {
            int ret = 0;
            
            ret = weather_icon.getHeight() + (-(int) text_paint.ascent()) + (int)text_paint.descent();
            return ret;
    }
        
        private int measureWidth(int measureSpec) {
                int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = getRectWidth();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
        }
        
        private int measureHeight(int measureSpec) {
                int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = getRectHeight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        
        return result;
        }
        
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
        }
        
        @Override
        protected void onDraw (Canvas canvas) {
                super.onDraw(canvas);
                
                int x = getPaddingLeft();
                int y = getPaddingTop() - (int) text_paint.ascent() /*+ (int) text_paint.descent()*/ + weather_icon.getHeight();
                
                if (null != weather_icon)
                        canvas.drawBitmap(weather_icon, getPaddingLeft(), getPaddingTop(), null);// draw weather icon bitmap
                if (null != temp_str)
                        canvas.drawText(temp_str, x, y, text_paint); // draw temperature text
        }
        
        private final int icon_128x128_tab[] =
    	{
        		R.drawable.weather_icon0, R.drawable.weather_icon1, R.drawable.weather_icon2,
        		R.drawable.weather_icon3, R.drawable.weather_icon4, R.drawable.weather_icon99,
        		R.drawable.weather_icon6, R.drawable.weather_icon7, R.drawable.weather_icon8,
        		R.drawable.weather_icon9, R.drawable.weather_icon99, R.drawable.weather_icon99,
        		R.drawable.weather_icon12, R.drawable.weather_icon13, R.drawable.weather_icon14,
        		R.drawable.weather_icon15, R.drawable.weather_icon99, R.drawable.weather_icon17
    	};
        
        private final int icon_720p_tab[] =
        	{
            		R.drawable.weather_icon0_720p, R.drawable.weather_icon1_720p, R.drawable.weather_icon2_720p,
            		R.drawable.weather_icon3_720p, R.drawable.weather_icon4_720p, R.drawable.weather_icon99_720p,
            		R.drawable.weather_icon6_720p, R.drawable.weather_icon7_720p, R.drawable.weather_icon8_720p,
            		R.drawable.weather_icon9_720p, R.drawable.weather_icon99_720p, R.drawable.weather_icon99_720p,
            		R.drawable.weather_icon12_720p, R.drawable.weather_icon13_720p, R.drawable.weather_icon14_720p,
            		R.drawable.weather_icon15_720p, R.drawable.weather_icon99_720p, R.drawable.weather_icon17_720p
        	};
        
        int getIconId(int index) {
        	if (!GlobalParams.RUN_720P)
        		return index < icon_128x128_tab.length ? icon_128x128_tab[index] : R.drawable.weather_icon99;
        	else
        		return index < icon_720p_tab.length ? icon_720p_tab[index] : R.drawable.weather_icon99_720p;
        }
        
        /**
         * An AsyncTask to retrieve weather information and parse it,
         * then update the weather widget
         * @author dkpure
         *
         */
        class WeatherInfoGetter extends AsyncTask <String, Integer, String>{
                private String         _temp1_str;
                private int                _weather_icon_id = R.drawable.weather_icon99;
                 
                @Override
                protected String doInBackground(String... urls) {
                        // TODO Auto-generated method stub
                        return doHttpRequest(urls);
                }
                
                private String doHttpRequest(String... urls) {
                        HttpClient httpClient = DkHttpClient.getHttpClient();
                        
                        try {
                                HttpGet request = new HttpGet(urls[0]);
                                HttpParams params= new BasicHttpParams();
                                HttpConnectionParams.setSoTimeout(params, 50000); // 50s
                                
                                HttpResponse response = httpClient.execute(request);
                                
                                if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                                        Log.e("WeatherInfoGetter", "Network problem!");
                                }
                                
                                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                                
//                                Properties.loadConvert()
//                                Log.v("traffic", content);
                                
                                try {
                                        // The "weather content" we get from Internet is in JASON format
                                        JSONObject json = new JSONObject(content).getJSONObject("weatherinfo");
//                                        JSONArray jsonarr = new JSONObject(content).getJSONArray("results");
//                                        Log.v("traffic", "jason numbers:" + jsonarr.length());
//                                        JSONObject json = jsonarr.getJSONObject(0);
//                                        String title_str = json.getString("title");
//                                        Log.v("traffic", "title_str:"+ title_str);
//                                        title_str = json.getString("description");
//                                        Log.v("traffic", "description_str:"+ title_str);
                                        
                                        String temp1 = json.getString("temp1");
                                        String temp2 = json.getString("temp2");
                                        String img_str = json.getString("img1");
                                        
                                        // Convert temp1 and temp2 to number and compare them, 
                                        // as temp1 is not always smaller than temp2 
                                        String regEx="[^0-9]";
                                        Pattern p = Pattern.compile(regEx);
                                        Matcher m1 = p.matcher(temp1);
                                        Matcher m2 = p.matcher(temp2);
                                        
                                        String num1_str = m1.replaceAll("").trim();
                                        String num2_str = m2.replaceAll("").trim();
                                        
                                        if (Integer.parseInt(num1_str) < Integer.parseInt(num2_str)) {
//                                                _temp1_str = temp1 + "/" + temp2;
                                        	_temp1_str = num1_str + "/" + temp2;
                                        } else {
//                                                _temp1_str = temp2 + "/" + temp1;
                                        	_temp1_str = num2_str + "/" + temp1;
                                        }                                
                                        
                                        Matcher m3 = p.matcher(img_str);
                                        String index_str = m3.replaceAll("").trim();
//                                        Log.v("WeatherInfoGetter", "index_str: " + index_str);
                                        
                                        _weather_icon_id = getIconId(Integer.parseInt(index_str));
                                        return _temp1_str;
                                } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                }
                                
                        }catch(Exception e) {
                                e.printStackTrace();
                        }
                        
                        return null;
                }
                
                @Override
                protected void onPostExecute (String info)  {
//                        ((ThmbNaiviMain) mcontext).populateWeathInfo(_weather_icon_id, _temp1_str);
                        updateView(_weather_icon_id, _temp1_str);
                }
        }
}