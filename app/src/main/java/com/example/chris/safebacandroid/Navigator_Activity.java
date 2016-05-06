package com.example.chris.safebacandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Steel on 4/25/16.
 */
public class Navigator_Activity extends Activity {

    private ImageView current;
    private double currentBac;
    private String currentName;
    private TextView WheelBac;
    private TextView WelcomeName;
    private int curr;
    private int last;
    private boolean dimensions_set = false;
    private int[][][] regions;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);
        final View touchView = findViewById(R.id.touchView);

        WheelBac = (TextView)findViewById(R.id.wheelBac);
        WelcomeName = (TextView)findViewById(R.id.title_name);

        UserBacTask bacTask = new UserBacTask();
        bacTask.execute((Void)null);

        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (dimensions_set == false) {
                    set_dimensions();
                    dimensions_set = true;
                }
                final int x, y, action = event.getAction();

                switch (action & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN: {
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        display_button(x, y);
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        display_button(x, y);
                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                        hide_buttons();
                        change_activity();
                        break;
                    }

                }

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        UserBacTask bacTask = new UserBacTask();

        bacTask.execute((Void)null);
    }

    /**
     * sets dimensions of wheel buttons
     */
    public void set_dimensions(){
        final View wheel = findViewById(R.id.wheel);
        int[] location = new int[2];
        wheel.getLocationInWindow(location);

        int wheel_x = wheel.getRight()/2;
        int wheel_y = location[1]+(wheel.getBottom()/2);
        int radius_Out = wheel_x;
        int radius_In = radius_Out/3;


        double[] ang_A = {Math.toRadians(0),Math.toRadians(-60),Math.toRadians(-120),
                Math.toRadians(180),Math.toRadians(120),Math.toRadians(60)};
        double[] ang_B = {Math.toRadians(-30),Math.toRadians(-90),Math.toRadians(-150),
                Math.toRadians(150),Math.toRadians(90),Math.toRadians(30)};
        double[] ang_C = {Math.toRadians(30),Math.toRadians(-30),Math.toRadians(-90),
                Math.toRadians(-150),Math.toRadians(150),Math.toRadians(90)};

        regions = new int[6][6][2];
        for (int i = 0; i < 6; i++){

            int[][] temp = {{(int)(wheel_x+(radius_In*Math.cos(ang_A[i]))),(int)(wheel_y+(radius_In*Math.sin(ang_A[i])))},
                    {(int)(wheel_x+(radius_In*Math.cos(ang_B[i]))),(int)(wheel_y+(radius_In*Math.sin(ang_B[i])))},
                    {(int)(wheel_x+(radius_Out*Math.cos(ang_B[i]))),(int)(wheel_y+(radius_Out*Math.sin(ang_B[i])))},
                    {(int)(wheel_x+(radius_Out*Math.cos(ang_A[i]))),(int)(wheel_y+(radius_Out*Math.sin(ang_A[i])))},
                    {(int)(wheel_x+(radius_Out*Math.cos(ang_C[i]))),(int)(wheel_y+(radius_Out*Math.sin(ang_C[i])))},
                    {(int)(wheel_x+(radius_In*Math.cos(ang_C[i]))),(int)(wheel_y+(radius_In*Math.sin(ang_C[i])))}};

            regions[i] = temp;
        }

        regions[0][1][1]-=1;
        regions[2][5][1]+=1;
        regions[3][1][1]+=1;
        regions[5][5][1]-=1;
    }

    /**
     * changes activity based on selected button
     */
    public void change_activity(){
        Intent next_activity;
        if (curr > 0){
            if (curr == 1){
                next_activity = new Intent(this, Profile_Activity.class);
            }else if (curr == 2){
//                next_activity = new Intent(this, Calculator_Activity.class);
            }else if (curr == 3){
                next_activity = new Intent(this, Catalog_Activity.class);
            }else if (curr == 4){
//                next_activity = new Intent(this, Messenger_Activity.class);
            }else if (curr == 5){
                next_activity = new Intent(this, FriendsActivity.class);
            }else if (curr == 6){
                next_activity = new Intent(this, Transportation_Activity.class);
            }else{
                return;
            }
            startActivity(next_activity);
        }
    }

    /**
     * displays selected button
     * @param x x coordinates for checking within boundaries of button
     * @param y y coordinates for checking within boundaries of button
     */
    public void display_button(int x, int y){
        int[] point = {x,y};

        if((x > regions[0][1][0]) && contains(regions[0],point)) {
            curr = 1;
            if (last != curr) {
                hide_buttons();
            }
            current = (ImageView) findViewById(R.id.eSwiv);
            current.setVisibility(View.VISIBLE);
            last = curr;
        }else if(contains(regions[1],point)){
            curr = 2;
            if (last != curr){
                hide_buttons();
            }
            current = (ImageView) findViewById(R.id.neSwiv);
            current.setVisibility(View.VISIBLE);
            last = curr;
        }else if(contains(regions[2],point)){
            curr = 3;
            if (last != curr){
                hide_buttons();
            }
            current = (ImageView) findViewById(R.id.nwSwiv);
            current.setVisibility(View.VISIBLE);
            last = curr;
        }else if((x < regions[3][1][0]) && contains(regions[3],point)){
            curr = 4;
            if (last != curr){
                hide_buttons();
            }
            current = (ImageView) findViewById(R.id.wSwiv);
            current.setVisibility(View.VISIBLE);
            last = curr;
        }else if(contains(regions[4],point)){
            curr = 5;
            if (last != curr){
                hide_buttons();
            }
            current = (ImageView) findViewById(R.id.swSwiv);
            current.setVisibility(View.VISIBLE);
            last = curr;
        }else if(contains(regions[5],point)){
            curr = 6;
            if (last != curr){
                hide_buttons();
            }
            current = (ImageView) findViewById(R.id.seSwiv);
            current.setVisibility(View.VISIBLE);
            last = curr;
        }else{
            curr = 0;
            hide_buttons();
        }
    }

    /**
     * hides unselected buttons
     */
    public void hide_buttons(){
        if(current != null){
            current.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * checks if a point is within boundaries of a shape
     * @param shape shape of button
     * @param pnt mouse click point location
     * @return returns true if point is inside shape
     */
    private boolean contains(int[][] shape, int[] pnt) {
        boolean inside = false;
        int len = shape.length;
        for (int i = 0; i < len; i++) {
            if (intersects(shape[i], shape[(i + 1) % len], pnt)) {
                inside = !inside;
            }
        }
        return inside;
    }

    /**
     * sets the BAC view in center of wheel
     */
    private void setWheelBac(){
        WheelBac.setText(currentBac+"");
        WelcomeName.setText(currentName);
    }

    /**
     * checks if point vector ray intersects given line
     * @param A line 1 for checking intersection
     * @param B line 2 for checking intersection
     * @param P point ray to compare to lines
     * @return returns true if rays intersect
     */
    private boolean intersects(int[] A, int[] B, int[] P) {
        if (A[1] > B[1]) {
            return intersects(B, A, P);
        }
        if (P[1] == A[1] || P[1] == B[1]) {
            P[1] += 0.0001;
        }
        if (P[1] > B[1] || P[1] < A[1] || P[0] > max(A[0], B[0])) {
            return false;
        }
        if (P[0] < min(A[0], B[0])) {
            return true;
        }
        double red = (P[1] - A[1]) / (double) (P[0] - A[0]);
        double blue = (B[1] - A[1]) / (double) (B[0] - A[0]);
        return red >= blue;
    }

    private int max(int a, int b){
        if (a>b){
            return a;
        }else{
            return b;
        }
    }
    private int min(int a, int b){
        if(a<b){
            return a;
        }else{
            return b;
        }
    }

    /**
     * custom task to recieve BAC and name of current user
     */
    private class UserBacTask extends AsyncTask<Void, Void, JSONObject>{

        private JSONObject profile;

        @Override
        protected JSONObject doInBackground(Void... params){
            profile = APICaller.getProfile();
            return profile;
        }

        @Override
        protected void onPostExecute(JSONObject result){

            try {
                currentBac = result.getDouble("bac");
                currentName = result.getString("name");
            }catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            setWheelBac();
        }
    }
}

