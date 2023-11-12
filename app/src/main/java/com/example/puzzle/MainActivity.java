package com.example.puzzle;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Arreglo que almacena las piezas del rompecabezas (ImageViews)
    private ImageView[] piezasPuzzle;

    // Arreglo que almacena los Drawables originales de las piezas
    private Drawable[] drawablesOriginales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de las piezas del rompecabezas
        piezasPuzzle = new ImageView[]{
                findViewById(R.id.f1c1), findViewById(R.id.f1c2), findViewById(R.id.f1c3), findViewById(R.id.f1c4), findViewById(R.id.f1c5),
                findViewById(R.id.f2c1), findViewById(R.id.f2c2), findViewById(R.id.f2c3), findViewById(R.id.f2c4), findViewById(R.id.f2c5),
                findViewById(R.id.f3c1), findViewById(R.id.f3c2), findViewById(R.id.f3c3), findViewById(R.id.f3c4), findViewById(R.id.f3c5),
        };

        // Configuración de listeners de eventos táctiles y de arrastre
        setTouchListeners();
        setDragListeners();

        // Inicialización de la posición inicial de las piezas del rompecabezas
        original_click(null);
    }

    // Configuración de listeners de eventos táctiles para las piezas del rompecabezas
    private void setTouchListeners() {
        for (ImageView pieza : piezasPuzzle) {
            pieza.setOnTouchListener(new MyTouchListener());
        }
    }

    // Configuración de listeners de eventos de arrastre para el GridLayout
    private void setDragListeners() {
        GridLayout gridLayout = findViewById(R.id.GridLayout);
        gridLayout.setOnDragListener(new MyDragListener());
    }

    // Método para obtener los Drawables originales de las piezas
    private void obtenerDraw() {
        drawablesOriginales = new Drawable[piezasPuzzle.length];

        for (int i = 0; i < piezasPuzzle.length; i++) {
            drawablesOriginales[i] = piezasPuzzle[i].getDrawable();
        }
    }

    // Método para restaurar la posición inicial de las piezas del rompecabezas
    public void original_click(View v) {
        // Obtener los Drawables originales
        obtenerDraw();

        // Crear una lista de Drawables y mezclarla
        List<Drawable> listaDrawables = new ArrayList<>();
        Collections.addAll(listaDrawables, drawablesOriginales);
        Collections.shuffle(listaDrawables);

        // Asignar los Drawables mezclados a las piezas del rompecabezas
        for (int i = 0; i < piezasPuzzle.length; i++) {
            piezasPuzzle[i].setImageDrawable(listaDrawables.get(i));
        }
    }

    // Clase interna para manejar eventos táctiles en las piezas del rompecabezas
    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // Configurar datos de arrastre
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                // Iniciar el arrastre
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, 0);
                }

                // Hacer la vista invisible durante el arrastre
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    // Clase interna para manejar eventos de arrastre en el GridLayout
    public class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Cambiar el fondo o resaltar el área de destino si es necesario
                    v.setBackgroundResource(R.drawable.border_highlight);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    // Restaurar el fondo original del área de destino
                    v.setBackgroundResource(R.drawable.border_normal);
                    break;
                case DragEvent.ACTION_DROP:
                    // Obtener la vista arrastrada y el contenedor de destino
                    View draggedView = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) draggedView.getParent();
                    GridLayout container = (GridLayout) v;

                    // Obtener las posiciones de las vistas en el GridLayout
                    int indexDragged = owner.indexOfChild(draggedView);
                    int indexTarget = container.indexOfChild(v);

                    // Intercambiar las vistas en el GridLayout
                    owner.removeView(draggedView);
                    container.removeViewAt(indexTarget);
                    owner.addView(draggedView, indexTarget);
                    container.addView(draggedView, indexDragged);

                    // Restaurar el fondo original del área de destino
                    container.setBackgroundResource(R.drawable.border_normal);
                    draggedView.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    // Restaurar el fondo original del área de destino si el arrastre no fue exitoso
                    v.setBackgroundResource(R.drawable.border_normal);
                    if (!event.getResult()) {
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
