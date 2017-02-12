package com.example.techlap.piratescrewapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Class extending service as it is a service that will run in background
public class NotificationListener extends Service {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    //private NewMessageNotification Note;
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("NOTI","onBind");
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().getUid()!=null){
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Tasks").child(mAuth.getCurrentUser().getUid());
        //Note = new NewMessageNotification();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

                //showNotification(dataSnapshot.getValue(String.class));
                Task task=dataSnapshot.getValue(Task.class);
                showNotification(task.getTitle(),task.getDesc(),task.getFile());
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }

        return null;
    }

    //When the service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NOTI","onBind");
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().getUid()!=null){
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Tasks").child(mAuth.getCurrentUser().getUid());
            //Note = new NewMessageNotification();
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

                    //showNotification(dataSnapshot.getValue(String.class));
                    Task task=dataSnapshot.getValue(Task.class);
                    if(!task.getSeen())
                    {
                        showNotification(task.getTitle(),task.getDesc(),task.getFile());
                        task.setSeen(true);
                        databaseReference.child(dataSnapshot.getKey()).child("seen").setValue(true);
                    }
                }

                @Override
                public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        //START_NOT_STICKY
        //START_STICKY
        return START_STICKY;
    }

    private void showNotification(String title ,String desc,String file){
        //Creating a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.axis);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.simplifiedcoding.net"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.axis));
        builder.setContentTitle(title);
        builder.setContentText(desc);
        builder.setContentIntent(
                PendingIntent.getActivity(
                        this,
                        0,
                        //these link we go to it when we click on the notification
                        new Intent(Intent.ACTION_VIEW, Uri.parse(file)),
                        PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle()
                .bigText(desc)
                .setBigContentTitle(title)
                //summary appear in the last line of the notification
                .setSummaryText("New Task"));
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
