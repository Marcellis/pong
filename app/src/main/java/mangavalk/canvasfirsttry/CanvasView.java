package mangavalk.canvasfirsttry;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;

public class CanvasView extends View
{
    //region Variabelen
    Resources r;

    //  Sizes
    float dpHeight;
    float dpWidth;

    float Height;
    float Width;

    //  Screen Boundary's
    float boundaryTop = 10;
    float boundaryBottom = 95;

    //  Pointers
    float moveX1,moveX2;
    float moveY1,moveY2;

    //  Ball
    boolean bounceXdirection, bounceYdirection;
    float bounceX, bounceY;
    float speedX, speedY;

    //  Pedles
    float rectangleHeight1 = 20;
    float rectangleHeight2 = 20;

    //  Score
    int score;
    int Player1Life = 3;
    int Player2Life = 3;

    //   Fps
    long oldTime;

    //  Sound
    boolean musicisplaying = false;
    private SoundPool soundPool,soundPool2;
    private int Sound_Pling,Sound_GameOver,Sound_Music;

    //  Game Status
    boolean gamePlaying = false;
    boolean gameover = false;

    //  Difficulty
    int BotDif = 0;

    //  Style
    int selectedStyle = 0;
    boolean StyleRound = true;

    int Color_PedleLeft = Color.WHITE;
    int Color_PedleRight = Color.WHITE;
    int Color_Ball = Color.RED;
    int Color_ScoreBar = Color.GRAY;
    int Color_Footer = Color.GRAY;
    int Color_Background = Color.BLACK;
    int Color_Score = Color.WHITE;

    //endregion Variabelen

    public CanvasView(Context context) {
        super(context);

        super.setBackgroundColor(Color_Background);
        oldTime = System.currentTimeMillis();

        this.postInvalidate();
    }

    public void SetActivity(Activity activity)
    {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        r = getResources();
        float density  = getResources().getDisplayMetrics().density;
        dpHeight = outMetrics.heightPixels / density;
        dpWidth  = outMetrics.widthPixels / density;
        Height = outMetrics.heightPixels;
        Width  = outMetrics.widthPixels;

        //  Set Basic Score And Pedle Position
        moveY1 = 50;
        moveY2 = 50;

        bounceX = bounceY = 40 + (int)(Math.random() * ((60 - 40) + 1));
        bounceY = bounceY = 0 + (int)(Math.random() * ((100 - 0) + 1));

        speedX = 1;
        speedY = 1.1f;

        // Load the sound
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool2 = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        Sound_Pling = soundPool.load(activity, R.raw.pling, 1);
        Sound_GameOver = soundPool.load(activity, R.raw.gameover, 1);
        Sound_Music = soundPool2.load(activity, R.raw.bat_cat, 1);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        if (musicisplaying == false)
        {
            soundPool2.play(Sound_Music, 1, 1, 0, -1, 1);
            musicisplaying = true;
        }

        if (gamePlaying)
        {
            Bot();

            drawPeddleRight(canvas);
            drawPeddleLeft(canvas);

            Bal();
            drawCircle(canvas, bounceX, bounceY);

            //  Draw Score Bar
            ScoreBar(canvas, boundaryTop);
            Footer(canvas, boundaryBottom);

            //  Fps
            long newTime = System.currentTimeMillis();
            DrawFps(canvas, newTime);

            //  Score
            DrawText(canvas, "Life: " + Player1Life, 15);
            DrawText(canvas, "Score: " + score, 45);
            DrawText(canvas, "Life: " + Player2Life, 85);

            oldTime = newTime;

            if (gameover)
            {
                gameover = false;

                //  Sound
                soundPool.play(Sound_GameOver, .5f, .5f, 0, 0, 1);

                //  Show GameOver message
                if ((Player1Life < 0) || (Player2Life < 0))
                {
                    DrawGameOver(canvas);

                    gamePlaying = false;

                    //  New Life
                    Player1Life = 3;
                    Player2Life = 3;
                }

                //  New Ball

                //  Position
                bounceX = 40 + (int)(Math.random() * ((60 - 40) + 1));
                bounceY = 0 + (int)(Math.random() * ((100 - 0) + 1));

                //  Direction
                bounceXdirection = Math.random() >= 0.5f ? true : false;

                //  Speed
                speedX = 1f;
                speedY = 1f;

                //  Respawn time
                this.postInvalidateDelayed( 5000 );
            }
            else
                this.postInvalidate();
        }
        else    //  Show Start Menu
        {
            //  Uhem
            SetStyle(0);

            Rect(canvas, 15, 25, 10, 25, Color.GRAY);
            DrawText(canvas, "Start", 15, 20, 50);

            Rect(canvas, 15+2, 25+2, 65, 80, Color.GRAY);
            DrawText(canvas, "Style:", 70, 20+2, 50);


            Rect(canvas, 25+4, 35+4, 65, 80, selectedStyle == 0 ? Color.RED : Color.GRAY);
            DrawText(canvas, "Retro", 70, 30+4, 50);

            if ((moveX1 >= 65 && moveX1 <= 80) && (moveY1 >= 25+4 && moveY1 <= 35+4))
                selectedStyle = 0;


            Rect(canvas, 35+6, 45+6, 65, 80, selectedStyle == 1 ? Color.RED : Color.GRAY);
            DrawText(canvas, "Neon", 70, 40+6, 50);

            if ((moveX1 >= 65 && moveX1 <= 80) && (moveY1 >= 35+6 && moveY1 <= 45+6))
                selectedStyle = 1;


            Rect(canvas, 45+8, 55+8, 65, 80, selectedStyle == 2 ? Color.RED : Color.GRAY);
            DrawText(canvas, "Red", 70, 50+8, 50);

            if ((moveX1 >= 65 && moveX1 <= 80) && (moveY1 >= 45+8 && moveY1 <= 55+8))
                selectedStyle = 2;


            Rect(canvas, 15+2, 25+2, 45, 60, Color.GRAY);
            DrawText(canvas, "Bot:", 50, 20+2, 50);

            Rect(canvas, 25+4, 35+4, 45, 60, BotDif == 0 ? Color.RED : Color.GRAY);
            DrawText(canvas, "Hard", 50, 30+4, 50);

            if ((moveX1 >= 45 && moveX1 <= 60) && (moveY1 >= 25+4 && moveY1 <= 35+4))
                BotDif = 0;


            Rect(canvas, 35+6, 45+6, 45, 60, BotDif == 1 ? Color.RED : Color.GRAY);
            DrawText(canvas, "Normal", 50, 40+6, 50);

            if ((moveX1 >= 45 && moveX1 <= 60) && (moveY1 >= 35+6 && moveY1 <= 45+6))
                BotDif = 1;


            Rect(canvas, 45+8, 55+8, 45, 60, BotDif == 2 ? Color.RED : Color.GRAY);
            DrawText(canvas, "Easy", 50, 50+8, 50);

            if ((moveX1 >= 45 && moveX1 <= 60) && (moveY1 >= 45+8 && moveY1 <= 55+8))
                BotDif = 2;

            Rect(canvas, 55+10, 65+10, 45, 60, BotDif == 3 ? Color.RED : Color.GRAY);
            DrawText(canvas, "Novice", 50, 60+10, 50);

            if ((moveX1 >= 45 && moveX1 <= 60) && (moveY1 >= 55+10 && moveY1 <= 65+10))
                BotDif = 3;


            Rect(canvas, 65+12, 75+12, 45, 60, BotDif == 4 ? Color.RED : Color.GRAY);
            DrawText(canvas, "Off", 50, 70+12, 50);

            if ((moveX1 >= 45 && moveX1 <= 60) && (moveY1 >= 65+12 && moveY1 <= 75+12))
                BotDif = 4;


            if ((moveX1 >= 15 && moveX1 <= 25) && (moveY1 >= 10 && moveY1 <= 25))//  Start Pressed
            {
                //  Set Style
                SetStyle(selectedStyle);

                //  Set Basic Score And Padle Position
                moveY1 = 50;
                moveY2 = 50;

                bounceX = bounceY = 40 + (int)(Math.random() * ((60 - 40) + 1));
                bounceY = bounceY = 0 + (int)(Math.random() * ((100 - 0) + 1));

                speedX = 1;
                speedY = 1.1f;

                Player1Life = 3;
                Player2Life = 3;

                gamePlaying = true;
            }

            this.postInvalidate();
        }
    }

    private void SetStyle(int i)
    {
        //  Set Style
        switch (i)
        {
            case 0:
                Color_PedleLeft = Color.WHITE;
                Color_PedleRight = Color.WHITE;
                Color_Ball = Color.RED;
                Color_ScoreBar = Color.GRAY;
                Color_Footer = Color.GRAY;
                Color_Background = Color.BLACK;
                Color_Score = Color.WHITE;
                break;
            case 1:
                Color_PedleLeft = Color.MAGENTA;
                Color_PedleRight = Color.MAGENTA;
                Color_Ball = Color.BLUE;
                Color_ScoreBar = Color.YELLOW;
                Color_Footer = Color.YELLOW;
                Color_Background = Color.CYAN;
                Color_Score = Color.RED;
                break;
            case 2:
                Color_PedleLeft = Color.RED;
                Color_PedleRight = Color.RED;
                Color_Ball = Color.RED;
                Color_ScoreBar = Color.RED;
                Color_Footer = Color.RED;
                Color_Background = Color.LTGRAY;
                Color_Score = Color.BLACK;
                break;
        }

        super.setBackgroundColor(Color_Background);
    }


    private void Bal()
    {
        if (bounceXdirection)
            if (bounceX >= 96 & bounceY >= (moveY1 - (rectangleHeight1/2)) & bounceY <= (moveY1 + (rectangleHeight1/2)))
            {
                bounceXdirection = !bounceXdirection;
                score++;

                // check angle;
                //change speed y;
                speedY = map( (bounceY - moveY1), -(rectangleHeight2/2), (rectangleHeight2/2), -2f, 2f);
                if (speedY <= 0)
                {
                    bounceYdirection = false;
                    speedY = Math.abs(speedY);
                }
                else
                    bounceYdirection = true;
                speedX = 3f - speedY;
                soundPool.play(Sound_Pling, .5f, .5f, 0, 0, 1);
            }
            else if (bounceX <= 96)
                bounceX += speedX;
            else
            {
                //dead
                Player2Life--;

                gameover = true;
            }
        else if (!bounceXdirection)
            if (bounceX <= 4 & bounceY >= (moveY2 - (rectangleHeight2/2)) & bounceY <= (moveY2 + (rectangleHeight2/2)))
            {
                bounceXdirection = !bounceXdirection;
                score++;
                // check angle;
                //change speed y;
                speedY = map( (bounceY - moveY2), -(rectangleHeight2/2), (rectangleHeight2/2), -2f, 2f);
                if (speedY <= 0)
                {
                    bounceYdirection = false;
                    speedY = Math.abs(speedY);
                }
                else
                    bounceYdirection = true;
                speedX = 3f - speedY;
                soundPool.play(Sound_Pling, .5f, .5f, 0, 0, 1);
            }
            else if (bounceX >= 4)
                bounceX -= speedX;
            else
            {
                //dead
                Player1Life--;

                gameover = true;
            }

        //  Y movement
        if (bounceYdirection)
            if (bounceY < boundaryBottom - 1.5f)
                bounceY += speedY;
            else
                bounceYdirection = !bounceYdirection;
        else if (!bounceYdirection)
        {
            if (bounceY > boundaryTop + 1.5f)
                bounceY -= speedY;
            else
                bounceYdirection = !bounceYdirection;
        }
    }

    private void Bot()
    {
        //  Bot -   hard

        switch (BotDif)
        {
            case 0:
                if (bounceX <= 40)  //  Left
                    if (bounceY > moveY2 + .1f + (int)(Math.random() * ((7 - .1f) + 1)))
                        moveY2 += 1f + (int)(Math.random() * ((5 - 1f) + 1));
                    else if (bounceY < moveY2 -  .1f + (int)(Math.random() * ((7 - .1f) + 1)))
                        moveY2 -= 1f + (int)(Math.random() * ((5 - 1f) + 1));

                if (bounceX >= 60)  //  Right
                    if (bounceY > moveY1 + .1f + (int)(Math.random() * ((7 - .1f) + 1)))
                        moveY1 += 1f + (int)(Math.random() * ((5 - 1f) + 1));
                    else if (bounceY < moveY1 -  .1f + (int)(Math.random() * ((7 - .1f) + 1)))
                        moveY1 -= 1f + (int)(Math.random() * ((5 - 1f) + 1));
                break;


                //  Bot -   normal
            case 1:
                if (bounceX <= 40)  //  Left
                    if (bounceY > moveY2 + .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY2 += 1f + (int)(Math.random() * ((3 - 1f) + 1));
                    else if (bounceY < moveY2 -  .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY2 -= 1f + (int)(Math.random() * ((3 - 1f) + 1));

                if (bounceX >= 60)  //  Right
                    if (bounceY > moveY1 + .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY1 += 1f + (int)(Math.random() * ((3 - 1f) + 1));
                    else if (bounceY < moveY1 -  .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY1 -= 1f + (int)(Math.random() * ((3 - 1f) + 1));
                break;
                //  Bot -   easy

            case 2:
                if (bounceX <= 40)  //  Left
                    if (bounceY > moveY2 + .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY2 += .1f + (int)(Math.random() * ((3 - .1f) + 1));
                    else if (bounceY < moveY2 -  .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY2 -= .1f + (int)(Math.random() * ((3 - .1f) + 1));

                if (bounceX >= 60)  //  Right
                    if (bounceY > moveY1 + .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY1 += .1f + (int)(Math.random() * ((3 - .1f) + 1));
                    else if (bounceY < moveY1 -  .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY1 -= .1f + (int)(Math.random() * ((3 - .1f) + 1));
                break;

                //  Bot -   novice

            case 3:
                if (bounceX <= 40)  //  Left
                    if (bounceY > moveY2 + .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY2++;
                    else if (bounceY < moveY2 -  .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY2--;

                if (bounceX >= 60)  //  Right
                    if (bounceY > moveY1 + .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY1++;
                    else if (bounceY < moveY1 -  .1f + (int)(Math.random() * ((10 - .1f) + 1)))
                        moveY1--;
                break;
        }
    }

    private void DrawFps(Canvas canvas, long newTime)
    {
        Paint paint = new Paint();
        paint.setColor(Color_Score);
        paint.setTextSize(30);
        String text = "FPS: " + (1000/(newTime-oldTime));
        canvas.drawText(text, GetRealPixelSize(dpWidth, 5), GetRealPixelSize(dpHeight, 5), paint);
    }

    private void DrawText(Canvas canvas, String text, int x)
    {
        Paint paint = new Paint();
        paint.setColor(Color_Score);
        paint.setTextSize(30);
        canvas.drawText(text, GetRealPixelSize(dpWidth, x), GetRealPixelSize(dpHeight, 5), paint);
    }

    private void DrawText(Canvas canvas, String text, int x, int y, int size)
    {
        Paint paint = new Paint();
        paint.setColor(Color_Score);
        paint.setTextSize(size);
        canvas.drawText(text, GetRealPixelSize(dpWidth, x), GetRealPixelSize(dpHeight, y), paint);
    }

    private void DrawGameOver(Canvas canvas)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.gameover);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int)GetRealPixelSize(dpWidth, 40), (int)GetRealPixelSize(dpHeight, 20), false);
        canvas.drawBitmap(bitmap, (int)GetRealPixelSize(dpWidth, 30), (int)GetRealPixelSize(dpHeight, 40), null);

        //  Which Player Won? Player1Life
        if (Player1Life < 0)
        {
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.player2);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int)GetRealPixelSize(dpWidth, 40), (int)GetRealPixelSize(dpHeight, 20), false);
            canvas.drawBitmap(bitmap, (int)GetRealPixelSize(dpWidth, 30), (int)GetRealPixelSize(dpHeight, 55), null);
        }
        else
        {
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.player1);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int)GetRealPixelSize(dpWidth, 40), (int)GetRealPixelSize(dpHeight, 20), false);
            canvas.drawBitmap(bitmap, (int)GetRealPixelSize(dpWidth, 30), (int)GetRealPixelSize(dpHeight, 55), null);
        }
    }

    float map(float x, float in_min, float in_max, float out_min, float out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    private void ScoreBar(Canvas canvas, float Bottom)
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color_ScoreBar);
        paint.setStyle(Style.FILL);

        //  Style Square
        canvas.drawRect(
                GetRealPixelSize(dpWidth, 0),
                GetRealPixelSize(dpHeight, 0),
                GetRealPixelSize(dpWidth, 100),
                GetRealPixelSize(dpHeight, Bottom), paint);
    }

    private void Footer(Canvas canvas, float Top)
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color_Footer);
        paint.setStyle(Style.FILL);

        //  Style Square
        canvas.drawRect(
                GetRealPixelSize(dpWidth, 0),
                GetRealPixelSize(dpHeight, Top),
                GetRealPixelSize(dpWidth, 100),
                GetRealPixelSize(dpHeight, 100), paint);
    }

    private void drawPeddleRight(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color_PedleRight);
        paint.setStyle(Style.FILL);

        float rectangleWidth = 2;
        rectangleHeight1 = 20;

        moveY1 = moveY1 <= (boundaryBottom - (rectangleHeight1 / 2)) ? moveY1 : (boundaryBottom - (rectangleHeight1 / 2));
        moveY1 = moveY1 >= (rectangleHeight1 / 2) + boundaryTop ? moveY1 : (rectangleHeight1 / 2) + boundaryTop;

        float xPosition = 97;
        float yPosition = moveY1;

        //  Style Square/Round
        if (!StyleRound)
            canvas.drawRect(
                    GetRealPixelSize(dpWidth, xPosition),
                    GetRealPixelSize(dpHeight, yPosition - (rectangleHeight1 / 2)),
                    GetRealPixelSize(dpWidth, xPosition + (rectangleWidth)),
                    GetRealPixelSize(dpHeight, yPosition + (rectangleHeight1 / 2)), paint);
        else
        {
            RectF rect=new RectF(
                    GetRealPixelSize(dpWidth, xPosition),
                    GetRealPixelSize(dpHeight, yPosition - (rectangleHeight1 / 2)),
                    GetRealPixelSize(dpWidth, xPosition + (rectangleWidth)),
                    GetRealPixelSize(dpHeight, yPosition + (rectangleHeight1 / 2)));

            canvas.drawRoundRect(rect, 20,20, paint);
        }
    }

    private void Rect(Canvas canvas, float Top, float Bottom, float Left, float Right, int Color)
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color);
        paint.setStyle(Style.FILL);

        //  Style Square
        canvas.drawRect(
                GetRealPixelSize(dpWidth, Left),
                GetRealPixelSize(dpHeight, Top),
                GetRealPixelSize(dpWidth, Right),
                GetRealPixelSize(dpHeight, Bottom), paint);
    }

    private void drawPeddleLeft(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color_PedleLeft);
        paint.setStyle(Style.FILL);

        float rectangleWidth = 2;
        rectangleHeight2 = 20;

        moveY2 = moveY2 <= (boundaryBottom - (rectangleHeight2 / 2)) ? moveY2 : (boundaryBottom - (rectangleHeight2 / 2));
        moveY2 = moveY2 >= (rectangleHeight2 / 2) + boundaryTop ? moveY2 : (rectangleHeight2 / 2) + boundaryTop;

        float xPosition = 3;
        float yPosition = moveY2;

        //  Style Square/Round
        if (!StyleRound)
            canvas.drawRect(
                    GetRealPixelSize(dpWidth, xPosition - (rectangleWidth)),
                    GetRealPixelSize(dpHeight, yPosition - (rectangleHeight2 / 2)),
                    GetRealPixelSize(dpWidth, xPosition),
                    GetRealPixelSize(dpHeight, yPosition + (rectangleHeight2 / 2)), paint);
        else
        {
            RectF rect=new RectF(
                    GetRealPixelSize(dpWidth, xPosition - (rectangleWidth)),
                    GetRealPixelSize(dpHeight, yPosition - (rectangleHeight2 / 2)),
                    GetRealPixelSize(dpWidth, xPosition),
                    GetRealPixelSize(dpHeight, yPosition + (rectangleHeight2 / 2)));

            canvas.drawRoundRect(rect, 20,20, paint);
        }
    }

    private void drawCircle(Canvas canvas, float xPosition, float yPosition) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color_Ball);
        paint.setStyle(Style.FILL);
        canvas.drawCircle(GetRealPixelSize(dpWidth, xPosition), GetRealPixelSize(dpHeight, yPosition), GetRealPixelSize(dpWidth, 1), paint);
    }

    private float GetRealPixelSize(float dpWH, float DIP)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (dpWH * ( DIP / 100 )), r.getDisplayMetrics());
    }

    public void sendMove(float x, float y)
    {
        if (!gamePlaying)
        {
            moveX1 = (x / Width) * 100;
            moveY1 = (y / Height) * 100;
        }
        else if (x / Width >= 0.6f)
        {
            moveX1 = (x / Width) * 100;
            moveY1 = (y / Height) * 100;
        }
        else if (x / Width <= 0.4f)
        {
            moveX2 = (x / Width) * 100;
            moveY2 = (y / Height) * 100;
        }
    }

    public void SetGameState(boolean b)
    {
        gamePlaying = false;
    }
}