package io.github.lewismcgeary.shapeshifter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Lewis on 31/03/2016.
 */
public class VoiceInputRecognizer implements RecognitionListener {
    private SpeechRecognizer speechRecognizer = null;
    private VoiceInputResultsReceiver resultsReceiver;
    private Intent recognizerIntent;
    private HashSet<String> validShapesHashSet;
    private boolean listeningFinished = true;

    public VoiceInputRecognizer(Context context, VoiceInputResultsReceiver resultsReceiver) {
        List<String> validShapesArray = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.valid_shapes)));
        validShapesHashSet = new HashSet<>(validShapesArray);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);
        this.resultsReceiver = resultsReceiver;
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                context.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    public void startListening(){
        speechRecognizer.startListening(recognizerIntent);
        listeningFinished = false;
    }

    public void destroy(){
        speechRecognizer.destroy();
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        listeningFinished = true;
    }

    @Override
    public void onError(int error) {
        String message;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        if(message.equals("No match")) {
            if(listeningFinished) {
                resultsReceiver.errorRecognizingSpeech(message);
            }
        } else {
            resultsReceiver.errorRecognizingSpeech(message);
        }
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        Boolean matchFound = false;
        for (String result : matches){
            result = result.toLowerCase();
            text += result + "\n";
            if (validShapesHashSet.contains(result)) {
                matchFound = true;
                resultsReceiver.shapeIdentified(result);
                break;
            }
        }
        if (!matchFound){
            text += "No matching shape found";
            resultsReceiver.noShapeIdentified(text);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
