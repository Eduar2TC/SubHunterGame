package com.eduar2tc.subhunter;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
/*rest imports*/
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.widget.ImageView;
import java.util.Random;

public class SubHunter extends Activity {

    private int numberHorizontalPixels;          //Ancho de pantalla
    private int numberVerticalPixels;            //Largo de pantalla
    private int blockSize;                       // Tamaño de bloque
    private int gridWidth = 40;                  //Anchura por numero de cuadriculas
    private int gridHeight;                      //Altura por numero de bloques
    private float horizontalTouched = -100;      //Coordenada x touched
    private float verticalTouched = -100;        //Coordenada y touched
    private int subHorizontalPosition;          //Coordenada x pos sub
    private int subVerticalPosition;            //Coordenada y sub
    private boolean hit = false;                //Objetivo alcanzado
    private int shotsTaken;                     //Contador de intentos
    private int distanceFromSub;                //distancia del disparo al submarino
    private boolean debugging = false;

    //Objetos necesarios para poder dibujar
    private ImageView gameView;
    private Bitmap blankBitmap;
    private Canvas canvas;
    private Paint paint;

    /*Parte del codigo que dibuja todo. Las cuadriculas, el Sub, el indicador touck y el "BOOM " cuando se atina al submarino*/
    public void draw() {
        this.gameView.setImageBitmap(this.blankBitmap);
        //Cambia la pantalla a color blanca
        this.canvas.drawColor(Color.argb(255,255,255,255));

        //Cambia el color del objeto paint a negro para dibujar las lineas de las cuadriculas
        this.paint.setColor(Color.argb(255, 0, 0, 0));

        //Dibuja Lines verticales de la cuadricula, // X inicio = X fin: incrementan iguales
        for(int i = 0; i < gridWidth; i++){
            canvas.drawLine(
                    this.blockSize * i,
                    0,  // -Inicio de la linea
                    this.blockSize * i,
                           this.numberVerticalPixels,   //stopY Fin de la linea
                    this.paint
            );
        }

        //Dibuja lineas horizontales de la cuadricula, // Y inicio = Y fin: incrementan iguales
        for(int j = 0; j < gridHeight; j++){
            canvas.drawLine(
                    0, // -inicio de la linea
                    this.blockSize * j,
                           this.numberHorizontalPixels, //stopX  -fin de la linea
                    this.blockSize * j,
                    this.paint
            );
        }
        // Dibuja el disparo del usuario
        this.canvas.drawRect(
                        this.horizontalTouched * this.blockSize,
                        this.verticalTouched * this.blockSize,
                        (this.horizontalTouched * this.blockSize) + this.blockSize,
                        (this.verticalTouched * this.blockSize) + this.blockSize,
                        paint );
        //Redimensiona el texto apropiadamente para el score y la distancia del texto
        this.paint.setTextSize(blockSize * 2);
        this.paint.setColor(
                Color.argb(255, 0, 0, 255)
        );
        this.canvas.drawText(
                "Shots Taken: " + this.shotsTaken + "  Distance: " + this.distanceFromSub, this.blockSize,
                this.blockSize * 1.75f,
                this.paint
        );

        Log.d("Debugging", "In draw");
        if (debugging) {
            printDebuggingText();
        }
    }
    /*Este codigo se ejecuta cuando se inicializa el juego. Este ocurre cuando cuando el jugador empieza un nuevo juego o gana un juego*/
    public void newGame(){

        Random random   = new Random();
        this.subHorizontalPosition = random.nextInt(this.gridWidth);
        this.subVerticalPosition = random.nextInt(this.gridHeight);
        this.shotsTaken = 0;
        Log.d("Debugging", "In newGame");
    }
    /*Esta parte del codigo maneja la detección de toques de la pantalla */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        Log.d("Debugging", "In onTouchEvent");
        // Has the player removed their finger from the screen?
        if((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            // Process the player's shot by passing the
            // coordinates of the player's finger to takeShot
            takeShot(motionEvent.getX(), motionEvent.getY());
        }
        return true;
    }

   /*Se ejecuta cuando el jugador toca la pantalla este calcula la distancia del submarino y determina si se atinó o fallo*/
   void takeShot(float touchX, float touchY){
       Log.d("Debugging", "In takeShot");

       //Incrimenta la variable disparos hechos
       this.shotsTaken++;

       // Convierte las coordenadas float de la pantalla a coordenadas enteras de cuadriculas
       this.horizontalTouched = (int)touchX/ blockSize;
       this.verticalTouched = (int)touchY/ blockSize;

       // El disparo alcanzo al submarino?
       this.hit = (this.horizontalTouched == this.subHorizontalPosition && this.verticalTouched == this.subVerticalPosition);

       // A que distancia vertical y horizontal se encontraba el submarino despues del disparo
       int horizontalGap = (int)this.horizontalTouched - this.subHorizontalPosition;
       int verticalGap = (int)this.verticalTouched - this.subVerticalPosition;

       // Obtencion de la distancia entre los puntos
       this.distanceFromSub = (int)Math.sqrt( ((/*horizontalGap * horizontalGap*/Math.pow(horizontalGap, 2) ) + (verticalGap * verticalGap)) );

       // If there is a hit call boom
       if(this.hit){
           this.boom();
       }
       else{
           this.draw();
       }
   }
    // This code says "BOOM!"
    public void boom(){

        this.gameView.setImageBitmap(this.blankBitmap);

        // Cambia la pantalla a color rojo
        canvas.drawColor(Color.argb(255, 255, 0, 0));

        // Dibuja texto grande
        this.paint.setColor(Color.argb(255, 255, 255, 255));
        this.paint.setTextSize(blockSize * 10);

        this.canvas.drawText("BOOM!", blockSize * 4, blockSize * 14, paint);

        // Dibuja texto de reinicio
        this.paint.setTextSize(blockSize * 2);
        this.canvas.drawText("Take a shot to start again",
                blockSize * 8,
                blockSize * 18, paint);

        // Inicia el nuevo Juego
        this.newGame();
    }
    // IMPRIME TEXTO DE DEBUGG
    public void printDebuggingText(){
        this.paint.setTextSize(blockSize);
        this.canvas.drawText(
                "numberHorizontalPixels = "  + numberHorizontalPixels,
                50,
                blockSize * 3,
                this.paint
        );

        this.canvas.drawText(
                "numberVerticalPixels = " + numberVerticalPixels,
                50,
                blockSize * 4,
                this.paint
        );

        this.canvas.drawText("blockSize = " + blockSize,
                50,
                this.blockSize * 5,
                this.paint
        );

        this.canvas.drawText("gridWidth = " + gridWidth,
                50,
                this.blockSize * 6,
                this.paint
        );

        this.canvas.drawText("gridHeight = " + gridHeight,
                50,
                this.blockSize * 7,
                this.paint
        );

        this.canvas.drawText("horizontalTouched = " +  this.horizontalTouched,
                50,
                this.blockSize * 8,
                this.paint
        );

        this.canvas.drawText("verticalTouched = " + this. verticalTouched,
                50,
                this.blockSize * 9,
                this.paint
        );

        this.canvas.drawText("subHorizontalPosition = "  + this.subHorizontalPosition,
                50,
                this.blockSize * 10,
                this.paint
        );

        this.canvas.drawText("subVerticalPosition = " + this.subVerticalPosition,
                50,
                this.blockSize * 11,
                this.paint
        );

        this.canvas.drawText("hit = " + this.hit,
                50,
                this.blockSize * 12,
                this.paint
        );

        this.canvas.drawText("shotsTaken = " + this.shotsTaken,
                50,
                this.blockSize * 13,
                this.paint
        );

        this.canvas.drawText("debugging = " + this.debugging,
                50,
                this.blockSize * 12,
                this.paint
        );
    }
    public void initializeObjetsForDrawing(){
        // Initialize all the objects ready for drawing
        this.blankBitmap = Bitmap.createBitmap(
                            numberHorizontalPixels,
                            numberVerticalPixels,
                            Bitmap.Config.ARGB_8888
        );
        this.canvas = new Canvas(this.blankBitmap);
        this.gameView = new ImageView(this);
        this.paint = new Paint();
        setContentView(gameView); /*Cambia nuestro objeto ImageView a al view principal*/
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtiene la resolucion de pantalla del dispositivo
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Inicializacion de las variables basado en el tama�o de la pantalla
        this.numberHorizontalPixels = size.x;
        this.numberVerticalPixels = size.y;
        this.blockSize = numberHorizontalPixels / gridWidth;     //(1813 / 40) = 45.325   -- Numero de bloques en anchura
        gridHeight = numberVerticalPixels / blockSize;      //(1080 / 45) = 24 --- Numero de bloques en altura
        this.initializeObjetsForDrawing();
        Log.d("Debuggin", "In onCreate");
        this.newGame();
        this.draw();
    }
}