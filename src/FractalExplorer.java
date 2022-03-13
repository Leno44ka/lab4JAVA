import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;

/**
 * TODO: Соединяющий класс
 * Класс соединяет разные части реализации фрактала
 * путем создания и демонстрации Swing GUI и обработки
 * событий, связаных с взаимодействием с пользователем
 */
public class FractalExplorer {
    //Кол-во пикселей исходя из длины и ширины дисплея
    private int displaySize;

    //Объект для обновления отображения в разных
    //методах в процессе вычисления фрактала.
    private JImageDisplay display;

    //Объект, использующий базовый класс FractalGeneration на будущее
    private FractalGenerator fractal;

    //Диапазон комплексной плоскости, которая выводится на экран.
    private Rectangle2D.Double range;

    //Констуктор для инициализации диапазона и объектов FractalGeneration
    public FractalExplorer(int size) {

        //Размер дисплея
        displaySize = size;

        //Объекты FractalGeneration
        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        display = new JImageDisplay(displaySize, displaySize);

    }

    /**
     * Метод инициализирует Swing GUI с JFrame содержащим
     * объеты JImageDisplay и кнопку для ресета дисплея
     */
    public void createAndShowGUI() {

        //Устанавливаем дисплей для использования java.awt.BorderLayout
        display.setLayout(new BorderLayout());
        JFrame myframe = new JFrame("Fractal Explorer");

        //Добавляем объект ImageDisplay
        myframe.add(display, BorderLayout.CENTER);

        //Кнопка ресета
        JButton resetButton = new JButton("Reset");
        ResetHandler handler = new ResetHandler();
        resetButton.addActionListener(handler);
        myframe.add(resetButton, BorderLayout.SOUTH);

        //Экземпляр MouseHandler
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);

        //Exit кнопка
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Делаем все видимым и запрет изменения размера окна
        myframe.pack();
        myframe.setVisible(true);
        myframe.setResizable(false);
    }

    /**
     * Отображения фрактала. Этот метод зацикливается
     * через каждый пиксель на дисплее и вычисляет количество
     * итераций для соответствующих координат во фрактале
     * Если количество итераций равно -1, установи цвет пикселя.
     * в черный. В противном случае выбери значение в зависимости от количества итераций.
     * Когда все пиксели нарисованы, JImageDisplay обновит цвет для каждого пикселя
     * и если нужно перекрасит
     */
    private void drawFractal() {
        /** Проходимся по всем пикселям на дисплее */
        for (int x = 0; x < displaySize; x++) {
            for (int y = 0; y < displaySize; y++) {

                /** Находим подходящие координаты  в области фрактала */
                double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
                double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);

                /**
                 * Считаем кол-во итераций для координат в области фрактала
                 */
                int iteration = fractal.numIterations(xCoord, yCoord);

                /**
                 * Кол-во -1, перекрашиваем в черный
                 */
                if (iteration == -1) {
                    display.drawPixel(x, y, 0);
                } else {

                    /**
                     * Выбор оттенка на основе кол-ва итераций
                     */
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);

                    /** Обновляем цвет */
                    display.drawPixel(x, y, rgbColor);
                }

            }
        }

        /**
         * Все нарисовали, перекрашиваем JImageDisplay в соотв с текущим изображением
         */
        display.repaint();
    }

    /**
     * Внутренний класс для обработки события ActionListener с помощью кнопки ресет
     */
    private class ResetHandler implements ActionListener {

        /**
         * Обработчик сбрасывает диапазон до начального диапазона, заданного
         * генератором, а затем рисует фрактал.
         */
        public void actionPerformed(ActionEvent e) {
            fractal.getInitialRange(range);
            drawFractal();
        }
    }

    /**
     * Внутренний класс для обработки события MouseListener от дисплея
     */
    private class MouseHandler extends MouseAdapter {

        /**
         * Когда обработчик получает событие щелчка мыши, он отображает пиксельные
         * координаты щелчка в область фрактала, который
         * отображается, а затем вызывает recenterAndZoomRange()
         * метод с координатами, которые были нажаты.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            /** x координата дисплея нажатия кнопки */
            int x = e.getX();
            double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);

            /** y координата дисплея нажатия кнопки */
            int y = e.getY();
            double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);

            /**
             * Вызов recternAndZoomRange() с данными координатами
             */
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            /**
             * Перерисовка фрактала
             */
            drawFractal();
        }
    }

    public static void main(String[] args) {
        FractalExplorer displayExplorer = new FractalExplorer(800);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}
