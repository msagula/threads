package com.example.app4;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GuessByGuessActivity extends AppCompatActivity {

    private final AtomicBoolean running = new AtomicBoolean(false);
    TextView tv;
    public final int SUCCESS = 1;
    public final int NEAR_MISS = 2;
    public final int CLOSE_GUESS = 3;
    public final int COMPLETE_MISS = 4;
    public final int DISASTER = 5;
    public final int RANDOM = 999;

    WorkerThread1 wt1;
    WorkerThread2 wt2;

    HashMap<Integer, String> map = new HashMap<>();
    Integer[] gopherNearMiss = new Integer[8];
    Integer[] gopherCloseGuess = new Integer[16];
    CustomAdapter ca = new CustomAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_layout);

        GridView gridview = findViewById(R.id.gridview);
        gridview.setAdapter(ca);

        tv = findViewById(R.id.textView2);
        TextView title = findViewById(R.id.title);
        title.append("Guess by Guess");

        Button bttn = findViewById(R.id.restartBttn);
        Button bttn2 = findViewById(R.id.switchModes);

        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                wt1.interrupt();
                wt2.interrupt();
                wt1.workerHandler1.removeCallbacksAndMessages(null);
                wt2.workerHandler2.removeCallbacksAndMessages(null);
                mainHandler.removeCallbacksAndMessages(null);

                Intent intent = new Intent(GuessByGuessActivity.this,
                        GuessByGuessActivity.class);
                startActivity(intent);
            }
        });

        bttn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                wt1.interrupt();
                wt2.interrupt();
                wt1.workerHandler1.removeCallbacksAndMessages(null);
                wt2.workerHandler2.removeCallbacksAndMessages(null);
                mainHandler.removeCallbacksAndMessages(null);

                Intent intent = new Intent(GuessByGuessActivity.this,
                        ContinuousActivity.class);
                startActivity(intent);
            }
        });

        for (int i = 0; i < 100; i++) {
            map.put(i, "empty");
        }

        Random rand = new Random();
        int rand_int1 = rand.nextInt(100);

        map.put(rand_int1, "gopher");
        ca.updateImage(rand_int1, 3);

        gopherNearMiss[0] = rand_int1-11; gopherNearMiss[1] = rand_int1-10; gopherNearMiss[2] = rand_int1-9;
        gopherNearMiss[3] = rand_int1-1; gopherNearMiss[4] = rand_int1+1;
        gopherNearMiss[5] = rand_int1+9; gopherNearMiss[6] = rand_int1+10; gopherNearMiss[7] = rand_int1+11;

        gopherCloseGuess[0] = rand_int1-22; gopherCloseGuess[1] = rand_int1-21; gopherCloseGuess[2] = rand_int1-20; gopherCloseGuess[3] = rand_int1-19;gopherCloseGuess[4] = rand_int1-18;
        gopherCloseGuess[5] = rand_int1-12; gopherCloseGuess[6] = rand_int1-2; gopherCloseGuess[7] = rand_int1+8; gopherCloseGuess[8] = rand_int1-8; gopherCloseGuess[9] = rand_int1+2;
        gopherCloseGuess[10] = rand_int1+12; gopherCloseGuess[11] = rand_int1+18; gopherCloseGuess[12] = rand_int1+19; gopherCloseGuess[13] = rand_int1+20; gopherCloseGuess[14] = rand_int1+21;
        gopherCloseGuess[15] = rand_int1+22;

        wt1 = new WorkerThread1();
        wt2 = new WorkerThread2();

        try {  Thread.sleep(100);}
        catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }

        wt1.start();
        wt2.start();

        try {  Thread.sleep(100);}
        catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }
    }

    public Handler mainHandler = new uiHandler();

    @SuppressLint("HandlerLeak")
    private class uiHandler extends Handler {
        public void handleMessage(Message msg) {

            try {  Thread.sleep(500);}
            catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }

            int howFar = howFarFromGopher(msg.arg2);
            if (msg.arg1 == 1) {
                try {   wt2.pauseThread(); }
                catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }

                if (howFar == SUCCESS) {

                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    String s = Integer.toString(msg.arg2);
                    SpannableString str1= new SpannableString(s + "(Success) ");
                    str1.setSpan(new ForegroundColorSpan(Color.RED), 0, str1.length(), 0);
                    builder.append(str1);
                    tv.append(builder);

                    wt1.interrupt();
                    wt2.interrupt();
                    wt1.workerHandler1.removeCallbacksAndMessages(null);
                    wt2.workerHandler2.removeCallbacksAndMessages(null);
                    mainHandler.removeCallbacksAndMessages(null);
                    Toast.makeText(GuessByGuessActivity.this, "Thread 1 wins", Toast.LENGTH_SHORT).show();
                } else {
                    Message m = new Message();
                    m.arg1 = msg.arg2;
                    m.arg2 = howFar;

                    wt1.workerHandler1.sendMessage(m);
                }
                wt2.resumeThread();
            } else {

                try {   wt1.pauseThread(); }
                catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }

                if (howFar == SUCCESS) {

                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    String s = Integer.toString(msg.arg2);
                    SpannableString str1= new SpannableString(s + "(Success) ");
                    str1.setSpan(new ForegroundColorSpan(Color.GREEN), 0, str1.length(), 0);
                    builder.append(str1);
                    tv.append(builder);

                    wt1.interrupt();
                    wt2.interrupt();
                    wt1.workerHandler1.removeCallbacksAndMessages(null);
                    wt2.workerHandler2.removeCallbacksAndMessages(null);
                    mainHandler.removeCallbacksAndMessages(null);
                    Toast.makeText(GuessByGuessActivity.this, "Thread 2 wins", Toast.LENGTH_SHORT).show();
                }
                else {
                    Message m = new Message();
                    m.arg1 = msg.arg2;
                    m.arg2 = howFar;

                    wt2.workerHandler2.sendMessage(m);
                }
                wt1.resumeThread();

            }
        }
    }

    public class WorkerThread1 extends Thread {
        Handler workerHandler1;
        int previousGuessCloseness;
        int previousGuess;
        int newGuess;

        int temp;

        WorkerThread1(){
            //send initial random guess (even number)
            Random rand = new Random();
            final int rand_int1 = rand.nextInt(50);   //0-49

            Message m = new Message();
            m.arg1 = 1; //send which thread it is
            m.arg2 = rand_int1; //send the guess
            mainHandler.sendMessage(m);
        }

        @SuppressLint("HandlerLeak")
        public void run(){

            Looper.prepare();

            workerHandler1 = new Handler() {
                public void handleMessage(Message msg) {
                    if (running.get()) {
                        synchronized (running) {
                            while (running.get()) {
                                try {
                                    running.wait();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                            }
                        }
                    }

                    if(msg.arg2 == DISASTER) {

                        final int temp = msg.arg1;
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                SpannableStringBuilder builder = new SpannableStringBuilder();
                                String s = Integer.toString(temp);
                                SpannableString str1= new SpannableString(s + "(Disaster) ");
                                str1.setSpan(new ForegroundColorSpan(Color.RED), 0, str1.length(), 0);
                                builder.append(str1);
                                tv.append(builder);
                            }
                        });

                        newGuess = calculateNextMove(previousGuess, previousGuessCloseness);
                        Message m = new Message();
                        m.arg1 = 1; //send which thread it is
                        m.arg2 = newGuess; //send the guess
                        mainHandler.sendMessage(m);
                    }
                    else {
                        temp = msg.arg1;
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ca.updateImage(temp, 1);
                                ca.notifyDataSetChanged();

                                SpannableStringBuilder builder = new SpannableStringBuilder();
                                String s = Integer.toString(temp);
                                SpannableString str1= new SpannableString(s + " ");
                                str1.setSpan(new ForegroundColorSpan(Color.RED), 0, str1.length(), 0);
                                builder.append(str1);
                                tv.append(builder);
                            }
                        });

                        previousGuess = msg.arg1;
                        previousGuessCloseness = msg.arg2;

                        final int temp = msg.arg1;

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                map.put(temp, "thread1");
                            }
                        });

                        newGuess = calculateNextMove(previousGuess, previousGuessCloseness);

                        Message m = new Message();
                        m.arg1 = 1; //send which thread it is
                        m.arg2 = newGuess; //send the guess
                        mainHandler.sendMessage(m);

                    }
                }
            };

            Looper.loop();
        }

        void pauseThread() throws InterruptedException
        {
            running.set(true);
        }

        void resumeThread()
        {
            running.set(false);
            synchronized (running) {
                running.notify();
            }
        }
    }

    private class WorkerThread2 extends Thread {

        Handler workerHandler2;
        int previousGuessCloseness;
        int previousGuess;
        int newGuess;

        int temp;

        WorkerThread2(){
            Random rand = new Random();
            final int rand_int1 = rand.nextInt(50) + 50;   //50-59

            Message m = new Message();
            m.arg1 = -1; //send which thread it is
            m.arg2 = rand_int1; //send the guess
            mainHandler.sendMessage(m);
        }

        @SuppressLint("HandlerLeak")
        public void run(){
            Looper.prepare();

            workerHandler2 = new Handler() {
                public void handleMessage(Message msg) {
                    if (running.get()) {
                        synchronized (running) {
                            while (running.get()) {
                                try {
                                    running.wait();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                            }
                        }
                    }

                    if(msg.arg2 == DISASTER) {
                        final int temp = msg.arg1;
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                SpannableStringBuilder builder = new SpannableStringBuilder();
                                String s = Integer.toString(temp);
                                SpannableString str1= new SpannableString(s + "(Disaster) ");
                                str1.setSpan(new ForegroundColorSpan(Color.GREEN), 0, str1.length(), 0);
                                builder.append(str1);
                                tv.append(builder);
                            }
                        });

                        newGuess = calculateNextMove(previousGuess, previousGuessCloseness);
                        Message m = new Message();
                        m.arg1 = -1; //send which thread it is
                        m.arg2 = newGuess; //send the guess
                        mainHandler.sendMessage(m);
                    }
                    else {
                        temp = msg.arg1;

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ca.updateImage(temp, 2);
                                ca.notifyDataSetChanged();

                                SpannableStringBuilder builder = new SpannableStringBuilder();
                                String s = Integer.toString(temp);
                                SpannableString str1= new SpannableString(s + " ");
                                str1.setSpan(new ForegroundColorSpan(Color.GREEN), 0, str1.length(), 0);
                                builder.append(str1);
                                tv.append(builder);
                            }
                        });

                        previousGuess = msg.arg1;
                        previousGuessCloseness = msg.arg2;

                        final int temp = msg.arg1;

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                map.put(temp, "thread2");
                            }
                        });

                        newGuess = calculateNextMove(RANDOM, RANDOM);

                        Message m = new Message();
                        m.arg1 = -1; //send which thread it is
                        m.arg2 = newGuess; //send the guess
                        mainHandler.sendMessage(m);

                    }
                }
            };

            Looper.loop();
        }


        void pauseThread() throws InterruptedException
        {
            running.set(true);
        }

        void resumeThread()
        {
            running.set(false);
            synchronized (running) {
                running.notify();
            }
        }
    }

    public int howFarFromGopher(int guess) {
        for(Integer i : gopherNearMiss){
            if(guess == i) return NEAR_MISS;
        }

        for(Integer i : gopherCloseGuess) {
            if(guess == i) return CLOSE_GUESS;
        }

        if(map.get(guess).equals("gopher")) return SUCCESS;
        if(map.get(guess).equals("thread1") || map.get(guess).equals("thread2")) return  DISASTER;

        else return COMPLETE_MISS;
    }

    public int calculateNextMove(int previousGuess, int howFar){
        Integer[] potentialGopherNM = new Integer[8];
        Integer[] potentialGopherCG = new Integer[16];
        int x = 999;
        int y = 999;

        if(howFar == NEAR_MISS){
            //move to new function??
            potentialGopherNM[0] = previousGuess-11; potentialGopherNM[1] = previousGuess-10; potentialGopherNM[2] = previousGuess-9;
            potentialGopherNM[3] = previousGuess-1; potentialGopherNM[4] = previousGuess+1;
            potentialGopherNM[5] = previousGuess+9; potentialGopherNM[6] = previousGuess+10; potentialGopherNM[7] = previousGuess+11;

            while (x < 0 || x > 99) {
                Random rand = new Random();
                int rand_int1 = rand.nextInt(8);

                x = potentialGopherNM[rand_int1];
            }
            return x;
        }
        else if(howFar == CLOSE_GUESS) {
            potentialGopherCG[0] = previousGuess-22; potentialGopherCG[1] = previousGuess-21; potentialGopherCG[2] = previousGuess-20; potentialGopherCG[3] = previousGuess-19;potentialGopherCG[4] = previousGuess-18;
            potentialGopherCG[5] = previousGuess-12; potentialGopherCG[6] = previousGuess-2; potentialGopherCG[7] = previousGuess+8; potentialGopherCG[8] = previousGuess-8; potentialGopherCG[9] = previousGuess+2;
            potentialGopherCG[10] = previousGuess+12; potentialGopherCG[11] = previousGuess+18; potentialGopherCG[12] = previousGuess+19; potentialGopherCG[13] = previousGuess+20; potentialGopherCG[14] = previousGuess+21;
            potentialGopherCG[15] = previousGuess+22;

            while (y < 0 || y > 99) {
                Random rand = new Random();
                int rand_int1 = rand.nextInt(16);

                y = potentialGopherCG[rand_int1];
            }
            return y;
        }
        else { //COMPLETE_MISS or RANDOM
            Random rand = new Random();
            return rand.nextInt(100);
        }
    }
}
