import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
  Лабораторная работа №2. Модель Джелинского-Моранды
 **/
public class JelinskiMoranda {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        // Имя файла: из аргумента командной строки или data.txt по умолчанию
        String fileName = (args.length > 0) ? args[0] : "data.txt";

        // Чтение интервалов из файла
        double[] X = readIntervalsFromFile(fileName);
        if (X == null || X.length == 0) {
            System.err.println("Не удалось прочитать данные из файла: " + fileName);
            return;
        }
        int n = X.length;

        // НАХОДИМ СУММУ ИНТЕРВАЛОВ И ВЗВЕШЕННУЮ СУММУ ИНТЕРВАЛОВ
        double sumX = 0.0;
        double sumIX = 0.0;
        for (int i = 0; i < n; i++) {
            sumX  += X[i];
            sumIX += (i + 1) * X[i];
        }

        System.out.println("Файл данных: " + fileName);
        System.out.println("n = " + n);
        System.out.printf(Locale.US, "Сумма интервалов ΣX  = %.0f%n", sumX);
        System.out.printf(Locale.US, "Взвешенная сумма интервалов ΣiX = %.0f%n%n", sumIX);

        // РЕШАЕМ НЕЛИНЕЙНОЕ УРАВНЕНИЕ ДЛЯ B, ИЩЕМ B > n МЕТОДОМ БИСЕКЦИИ
        double B = findB(n, sumX, sumIX);
        System.out.printf(Locale.US, "Общее число ошибок B (вещественное) = %.6f%n", B);

        // БЕРЕМ БЛИЖАЙШЕЕ ЦЕЛОЕ ЗНАЧЕНИЕ ОБЩЕГО ЧИСЛА ОШИБОК B
        int Bint = (int) Math.round(B);

        // РАССЧИТЫВАЕМ ЗНАЧЕНИЕ КОЭФФИЦИЕНТА ПРОПОРЦИОНАЛЬНОСТИ K
        double denom = (Bint + 1) * sumX - sumIX;
        double K = n / denom;

        //НАХОДИМ СРЕДНЕЕ ВРЕМЯ X_{n+1} ДО ПОЯВЛЕНИЯ n+1 ОШИБКИ
        double Xn1 = 1.0 / (K * (Bint - n));

        // НАХОДИМ ВРЕМЯ ДО ОКОНЧАНИЯ ТЕСТИРОВАНИЯ t_k
        double H = 0.0;
        int m = Bint - n;
        for (int i = 1; i <= m; i++) {
            H += 1.0 / i;
        }
        double tk = H / K;

        // ВЫВОД РЕЗУЛЬТАТОВ
        System.out.println("Общее число ошибок B (целое) = " + Bint);
        System.out.printf(Locale.US, "Коэффициент K = %.6f%n", K);
        System.out.printf(Locale.US, "Время до следующей ошибки = %.3f ч%n", Xn1);
        System.out.printf(Locale.US, "Время до окончания тестирования = %.3f ч%n", tk);
    }


     // МЕТОД ДЛЯ ЧТЕНИЯ ИНТЕРВАЛОВ ИЗ ТЕКСТОВОГО ФАЙЛА
    private static double[] readIntervalsFromFile(String fileName) {
        List<Double> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("[,\\s]+");
                for (String p : parts) {
                    if (!p.isEmpty()) {
                        list.add(Double.parseDouble(p.replace(',', '.')));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            return null;
        } catch (NumberFormatException e) {
            System.err.println("Ошибка формата числа в файле: " + e.getMessage());
            return null;
        }

        double[] X = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            X[i] = list.get(i);
        }
        return X;
    }

    // МЕТОД findB ДЛЯ РЕШЕНИЯ УРАВНЕНИЯ ДЛЯ B
    private static double findB(int n, double sumX, double sumIX) {
        double left = n + 0.1;          // B > n
        double right = n + 100.0;       // достаточно большой верхний предел
        double eps = 1e-10;

        for (int iter = 0; iter < 200; iter++) {
            double mid = (left + right) / 2.0;
            double fMid = f(mid, n, sumX, sumIX);

            if (Math.abs(fMid) < eps) {
                return mid;
            }
            if (f(left, n, sumX, sumIX) * fMid < 0) {
                right = mid;
            } else {
                left = mid;
            }
        }
        return (left + right) / 2.0;
    }
    // МЕТОД f ДЛЯ ВЫЧИСЛЕНИЯ ЗНАЧЕНИЯ УРАВНЕНИЯ f(B)=0 ПРИ ПОИСКЕ B
    private static double f(double B, int n, double sumX, double sumIX) {
        double leftSum = 0.0;
        for (int i = 1; i <= n; i++) {
            leftSum += 1.0 / (B - i + 1);
        }
        double right = n * sumX / ((B + 1) * sumX - sumIX);
        return leftSum - right;
    }
}