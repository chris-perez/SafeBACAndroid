package com.example.chris.safebacandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Steel on 4/25/16.
 */
public class Catalog_Activity extends Activity {

    ImageView current;
    int[] divisions;
    boolean dimensions_set;
    int curr, last;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        final View touchView = findViewById(R.id.touchView);
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
                            display_plate(x, y);
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            x = (int) event.getRawX();
                            y = (int) event.getRawY();
                            display_plate(x, y);
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            hide_plate();


                        if (curr > 0) {
                        }

                            move();
                            break;
                        }
                    }


                return true;

            }
        });
    }


    public void set_dimensions(){

        int[] location = new int[2];
        divisions = new int[5];

        View plate = findViewById(R.id.beer_plate);
        plate.getLocationInWindow(location);
        int plateHeight = plate.getBottom();

        divisions[0] = location[1];
        divisions[1] = location[1] + plateHeight;

        plate = findViewById(R.id.wine_plate);
        plate.getLocationInWindow(location);
        divisions[2] = location[1] + plateHeight;

        plate = findViewById(R.id.cocktail_plate);
        plate.getLocationInWindow(location);
        divisions[3] = location[1] + plateHeight;

        curr = 0;
        last = curr;
    }

    public void display_plate(int x, int y){

        if(y > divisions[0] && y <= divisions[1]){
            curr = 1;
            if (last != curr){
                hide_plate();
            }
            current = (ImageView) findViewById(R.id.beer_plate);
            current.setVisibility(View.VISIBLE);
            last = curr;
        }else if(y > divisions[1] && y <= divisions[2]){
            curr = 2;
            if (last != curr){
                hide_plate();
            }
            current = (ImageView) findViewById(R.id.wine_plate);
            current.setVisibility(View.VISIBLE);
            last = curr;
        }else if (y > divisions[2] && y <= divisions[3]){
            curr = 3;
            if (last != curr){
                hide_plate();
            }
            current = (ImageView) findViewById(R.id.cocktail_plate);
            current.setVisibility(View.VISIBLE);
            last = curr;
        }else{
            curr = 0;
            hide_plate();
        }
    }

    public void hide_plate(){
        if (current != null){
            current.setVisibility(View.INVISIBLE);
        }
    }

    public void move(){

        if (curr > 0) {
            Intent next_activity = new Intent(this, Catalog_Browser_Activity.class);
            Bundle par = new Bundle();
            par.putInt("Label", curr);
            next_activity.putExtras(par);
            startActivity(next_activity);
            finish();
        }
    }


}
