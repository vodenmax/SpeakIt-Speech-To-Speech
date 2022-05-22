package com.example.ispeak;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView textTest;
    TextToSpeech tts;
    private final String[] translateLocals = {"eng", "zh", "it", "ko", "ja"};
    final List<String> languages = Arrays.asList("English","Chinese","Italian","Korean","Japanese");
    private final Locale[] voiceLocals = {Locale.US, Locale.CHINESE, Locale.ITALIAN, Locale.KOREAN, Locale.JAPANESE};
    private int languageMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textTest = findViewById(R.id.textResult);

        ImageButton speakBtn = findViewById(R.id.speak);
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.my_selected_item, languages);
        adapter.setDropDownViewResource(R.layout.my_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        speakBtn.setOnClickListener(view -> {
            Translate translate=new Translate();
            translate.setOnTranslationCompleteListener(new Translate.OnTranslationCompleteListener() {
                @Override
                public void onStartTranslation() {
                }

                @Override
                public void onCompleted(String text) {

                    textTest.setText(text);
                    tts = new TextToSpeech(getApplicationContext(), i -> {
                        if(i == TextToSpeech.SUCCESS){
                            tts.setLanguage(voiceLocals[languageMode]);
                            tts.setSpeechRate(1.0f);
                            tts.speak(textTest.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {

                }
            });
            translate.execute(textTest.getText().toString(),"ru",translateLocals[languageMode]);
        });
    }

    public void OnMicClick(View view)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(intent, 10);
    }

    public void RemoveText(View view)
    {
        textTest.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null)
        {
            if (requestCode == 10) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                textTest.setText(text.get(0));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        switch(text) {
            case "Chinese":
                languageMode = 1;
                break;
            case "Italian":
                languageMode = 2;
                break;
            case "Korean":
                languageMode = 3;
                break;
            case "Japanese":
                languageMode = 4;
                break;
            default:
                languageMode = 0;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}