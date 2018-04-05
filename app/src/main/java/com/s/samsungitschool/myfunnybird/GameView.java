package com.s.samsungitschool.myfunnybird;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;


public class GameView extends View {
    // Размеры экрана w-ширина, h-высота
    int viewWidth, viewHeight;
    // Набранные очки
    int points = 0;
    // Промежуток времени, за который должно происходить изменение игровой модели
    public int timeInterval = 30;

    Sprite playerBird;
    Sprite enemyBird;

    public GameView(Context context) {
        super(context);

        // Создание спрайта
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player);

        int w = bitmap.getWidth() / 5;
        int h = bitmap.getHeight() / 3;

        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, bitmap);

        // Добавление новых кадров
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (i == 2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j*w, i*h, j*w + w, i*h + h));
            }
        }

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = bitmap.getWidth() / 5;
        h = bitmap.getHeight() / 3;

        firstFrame = new Rect(4*w, 0, 5*w, h);
        enemyBird = new Sprite(2000, 250, -300, 0, firstFrame, bitmap);

        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0 ; j--) {
                if (i == 0 && j == 4) {
                    continue;
                }
                if (i == 2 && j == 0) {
                    continue;
                }
                enemyBird.addFrame(new Rect(j*w, i*h, j*w + w, i*w + h));
            }
        }

        Timer t =new Timer();
        t.start();
    }

    // Метод для рисования
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Отрисовка фона и очков
        canvas.drawARGB(250, 127,199,255);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(55.0f);
        paint.setColor(Color.WHITE);
        canvas.drawText(points + "", viewWidth - 100, 70, paint);

        // Отрисовка птицы на экране

        playerBird.draw(canvas);
        enemyBird.draw(canvas);

    }

    public void update() {

        playerBird.update(timeInterval);
        enemyBird.update(timeInterval);


        // Метод, который не позволит птице игрока вылететь за пределы экрана
        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setVelocityY( -playerBird.getVelocityY() );
            points--;
        } else if (playerBird.getY() < 0) {
            playerBird.setVelocityY( -playerBird.getVelocityY() );
        }

        // Проверка пролета птицы противника
        if (enemyBird.getX() < -enemyBird.getFrameWidth()) {
            teleportEnemy();
            points += 10;
        }

        // Проверка столкновений
        if (enemyBird.intersect(playerBird)) {
            teleportEnemy();
            points -= 40;
        }

        invalidate();
    }

    // Метод возвращения птицы противника после пролета
    public void teleportEnemy() {
        enemyBird.setX(viewWidth + Math.random() * 500);
        enemyBird.setY( Math.random() * (viewHeight - enemyBird.getFrameHeight()) );
    }

    // Обработка нажатия
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();

        if (eventAction == MotionEvent.ACTION_DOWN) {

            if (event.getY() < playerBird.getBoundingBoxRect().top) {
                playerBird.setVelocityY( -Math.abs(playerBird.getVelocityY()) );
                points--;
            } else if (event.getY() > playerBird.getBoundingBoxRect().bottom) {
                playerBird.setVelocityY( Math.abs(playerBird.getVelocityY()) );
                points--;
            }
        }

        return super.onTouchEvent(event);
    }

    class Timer extends CountDownTimer {

        public Timer() {
            super(Integer.MAX_VALUE, timeInterval);
        }

        @Override
        public void onTick(long l) {
            update();
        }

        @Override
        public void onFinish() {

        }
    }

    // Метод, вызывабщийся при изменении игрового поля
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Запоминание размеров экрана
        viewWidth = w;
        viewHeight = h;
    }
}




