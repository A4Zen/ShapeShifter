package io.github.lewismcgeary.shapeshifter;

import android.os.Bundle;
import android.os.Handler;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements VoiceInputResultsReceiver {

    private VoiceInputRecognizer voiceInputRecognizer;
    private TextView returnedText;
    private ImageView shapeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shapeView = (ImageView) findViewById(R.id.shape_view);
        returnedText = (TextView) findViewById(R.id.returned_text);
        voiceInputRecognizer = new VoiceInputRecognizer(this, this);


        shapeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
                revealMic();
                v.setClickable(false);
            }
        });
    }

    public void startListening(){
        voiceInputRecognizer.startListening();
    }

    public void revealMic(){
        AnimatedVectorDrawableCompat micRevealDrawable;
        micRevealDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.mic_reveal);

        shapeView.setImageDrawable(micRevealDrawable);
        micRevealDrawable.start();
    }

    public void hideMic(){

        AnimatedVectorDrawableCompat micHideDrawable;
        micHideDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.mic_hide);

        shapeView.setImageDrawable(micHideDrawable);
        micHideDrawable.start();
    }

    @Override
    public void shapeIdentified(String shape) {
        hideMic();
        int resourceId = getResources().getIdentifier(shape.concat("_reveal"), "drawable", getPackageName());
        final AnimatedVectorDrawableCompat selectedShapeAVD = AnimatedVectorDrawableCompat.create(this, resourceId);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                shapeView.setImageDrawable(selectedShapeAVD);
                selectedShapeAVD.start();
            }

        }, 1000);
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {

            @Override
            public void run() {
                shapeView.setClickable(true);
            }

        }, 4000);
    }

    @Override
    public void noShapeIdentified(String results) {
        returnedText.setText(results);
        shapeView.setClickable(true);
        hideMic();
    }

    @Override
    public void errorRecognizingSpeech(String errorMessage) {
        returnedText.setText(errorMessage);
        shapeView.setClickable(true);
        hideMic();
    }
}
